package owg.engine.audio;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

import owg.engine.AssetProducer;

public abstract class AudioLib
{
	/**Sound clip map*/
	private HashMap<String,AudioClip> sounds;

	/**A count of all active sound loader threads. 
	 * This is used internally in order to wait for music to finish loading before playback.
	 * Note that playback will fail if there are no loader threads and the music still hasn't been loaded.*/
	private AtomicInteger numLoaderThreads;

	/**The current fading level. Normally 1, decreases towards 0 at the rate described by {@link #fadeRate}.*/
	private float fade;
	/**The current fading rate. The fading level is decreased by 1/fadeRate per step. 
	 * If -1, no fading is performed.
	 * If 0, the music is stopped as soon as possible.*/
	private int fadeRate;
	/**The currently playing music. If null, no music is playing.*/
	private MusicState currentMusic;
	/**The next music to be played. May be null.*/
	private MusicState nextMusic;

	public AudioLib(AssetProducer assets)
	{
		initMixer();
		sounds = new HashMap<String,AudioClip>();
		numLoaderThreads = new AtomicInteger(0);
		fade = 1;
		fadeRate = -1;
		loadDir(assets, "sounds");
	}
	/**Initialize the mixer. The actual actions necessary here depends entirely on the implementation.*/
	protected abstract void initMixer();

	/**Returns whether the indicated sound is loaded into the hashmap, 
	 * optionally waiting for any currently running loader threads to complete if the sound clip does not currently exist.*/
	public final boolean exists(String name, boolean waitForLoaderThreads)
	{
		if(waitForLoaderThreads)
			waitForLoaderThreads(name);
		return sounds.containsKey(name);
	}
	/**Remove and delete the sound with the indicated name. Deleting a sound that does not exist is an error.
	 * The sound will be stopped if it is playing.*/
	public final synchronized void delete(String name)
	{
		AudioClip s = sounds.get(name);
		if(s==null)
			throw new RuntimeException("Cannot delete a sound that does not exist: "+name);
		s.dispose();
		sounds.remove(name);
	}
	/**Stop all sounds in the hashmap.*/
	public synchronized void stopAll()
	{
		Iterator<Entry<String, AudioClip>> it = sounds.entrySet().iterator();
		while (it.hasNext()) 
		{
			Map.Entry<String, AudioClip> pairs = it.next();
			pairs.getValue().stop();
		}
	}
	/**Waits for any audio loader threads to continue reading from the disk until the indicated clips have been loaded.
	 * The clips should be sound names, without extension or directory. May not be null.*/
	public final void waitForLoaderThreads(String... clips)
	{
		boolean waiting = false;
		//If there are any loader threads, wait until music has been loaded or there are no threads left.
		while (numLoaderThreads.get() > 0)
		{
			//Test if music has been loaded
			boolean ok = true; 
			for(int i = 0; i<clips.length; i++)
			{
				if(!exists(clips[i], false))
					ok = false;
			}
			if(ok)//Music has been loaded, continue
				break;

			//Wait for a loader thread to complete and try again
			if(!waiting)
			{
				System.out.print("Audio: Waiting for audio clips to load... ");
				waiting = true;
			}
			synchronized (numLoaderThreads)
			{
				try
				{
					numLoaderThreads.wait();
				}
				catch (InterruptedException e)
				{
					System.err.println("Unexpected interrupt in music player: Program terminated while loading music?");
					e.printStackTrace();
				}
			}
		}
		if(waiting)
			System.out.println("All audio clips loaded, continuing. ");
	}

	/**Load the given directory into the hashmap in a different thread.
	 * @param dirName The directory name relative to the jar file, bat file or project folder.
	 * @param l A listener to be notified when all sounds have been loaded.
	 * Please be aware that this call will come from the loader thread.
	 * If null, no action events will be fired.
	 */
	public final void loadDirAsync(final AssetProducer assets, final String dirName, final ActionListener l)
	{
		System.out.print("Load sound dir asynchronous: "+dirName+
				" ... # loader threads is now: "+numLoaderThreads.incrementAndGet()+"... ");

		final String[] fileNames = assets.listAssets(dirName);
		if(fileNames != null)
		{
			Thread t = new Thread() 
			{	
				@Override
				public void run()
				{
					for (String file : fileNames)
					{
						if(file.toLowerCase(Locale.ENGLISH).endsWith(".ogg") || file.toLowerCase(Locale.ENGLISH).endsWith(".wav"))
							try {
								load(assets, dirName+'/'+file, file);
							} catch (IOException e) {
								System.err.println("Could not load "+file+"... ");
								e.printStackTrace();
							}
					}
					if(l != null)
						l.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, dirName));

					System.out.println("... done. # loader threads is now: "+numLoaderThreads.decrementAndGet());
					synchronized (numLoaderThreads)
					{
						numLoaderThreads.notifyAll();
					}
				}
			};
			t.start();
		}
		else 
		{
			System.err.println(dirName +" is not a directory!");
		}
	}

	/**Load the given directory into the hashmap.
	 * @return An array of strings used to address the loaded sounds in the hashmap. Will never be null.*/
	public final String[] loadDir(AssetProducer assets, String dirName)
	{
		String[] fileNames = assets.listAssets(dirName);
		System.out.print("Load sound dir: "+dirName+" ... ");
		if(fileNames != null) 
		{
			ArrayList<String> keys = new ArrayList<String>();
			for (String file : fileNames)
			{
				if(file.toLowerCase(Locale.ENGLISH).endsWith(".ogg") || file.toLowerCase(Locale.ENGLISH).endsWith(".wav"))
					try {
						keys.add(load(assets, dirName+'/'+file, file));
					} catch (IOException e) {
						System.err.println("Could not load "+file+"... ");
						e.printStackTrace();
					}
			}
			System.out.println("... done.");
			String[] result = new String[keys.size()];
			keys.toArray(result);
			return result;
		}
		else 
		{
			System.err.println(dirName +" is not a directory!");
			return new String[0];
		}
	}
	/**Load the given sound into the hashmap.
	 * @return The key used to address the loaded sound.
	 * Will be null if the sound is not an audio file(i.e. not having .ogg or .wav extension).
	 * If the file has a valid extension but cannot be loaded, a RuntimeException is thrown.*/
	public final String load(AssetProducer assets, String file, String simpleName) throws IOException
	{
		//Find the name without the file extension.
		String key,format;
		int pos = simpleName.length()-1;
		while (simpleName.charAt(pos)!='.' && pos>0) pos-=1;
		if (pos == 0)
			return null;
		key = simpleName.substring(0, pos);
		format = simpleName.substring(pos+1,simpleName.length());

		//load and insert into HashMap
		System.out.print(simpleName+'('+key+')' + " ");
		try
		{
			sounds.put(key,loadWavOgg(assets, file, format.toLowerCase(Locale.ENGLISH)));
		}
		catch (Exception e)
		{throw new RuntimeException("Missing or corrupt audio", e);}
		return key;
	}

	/**Load an ogg vorbis file or RIFF waveform file with PCM encoding as an AudioClip. 
	 * For wav, 8(unsigned) and 16(signed) bits depth must be supported.*/
	protected abstract AudioClip loadWavOgg(AssetProducer assets, String file, String extension) throws IOException;
	/**Optional. Should release any resources that may not be automatically released upon shutdown.*/
	public abstract void dispose();

	/**Return a loaded AudioClip with the given name.
	 * The name should be given without a file extension or path name.*/
	public final synchronized AudioClip get(String name)
	{
		return sounds.get(name);
	}
	/**Should be called each step, to update music fading.*/
	public final void updateFading() {
		//If stopped, go to next music(impose 1 step delay if music is stopping.)
		if(currentMusic == null && nextMusic != null) {
			currentMusic = nextMusic;
			currentMusic .play();
			nextMusic = null;
			fade = 1f;
			fadeRate = -1;
		}
		//Fade out, stop when completely faded out
		if(fadeRate != -1 && currentMusic != null) {
			if(fadeRate <= 0)
				fade = 0;
			else
				fade -= 1f/fadeRate;
			if(fade <= 0) {
				currentMusic.stop();
				currentMusic = null;
			}
		}
		//Finally, update volume
		if(currentMusic != null) {
			currentMusic.updateVolume(fade);
		}
		
		step();
	}
	/**Optional method, executed each step.*/
	protected void step() {
		//Optional
	}
	
	/**Play a MIDI file.
	 * @param assets The asset producer from which to derive the input stream.
	 * @param fileName The name of the midi file. Should be relative to the assets/music/ folder. 
	 * The file extension must be included.
	 * @param volume The initial volume of the new music.
	 * @param looping Whether the midi file should loop.
	 * @param loopStart The point(in milliseconds) that the player will seek back to after completion, if looping.
	 * This is typically used to avoid repeating the song intro. Use 0 to seek to the beginning of the song.
	 * @param fadePrevious If music is already playing, it will fade out over this number of steps.
	 * If 0, the previous music is stopped as soon as possible.<br/>
	 * If the music is already fading out, then the fading rate will be updated.<br/>
	 * <br/>
	 * The new MIDI file will begin playback as soon as the previous music has stopped.
	 */
	public final void playMusic(AssetProducer assets, String fileName, float volume,
			boolean looping, int loopStart, int fadePrevious) {
		if(fileName.endsWith(".ogg"))
			nextMusic = getVorbisState(assets, fileName, volume, looping, loopStart);
		else
			nextMusic = getMIDIState(assets, fileName, volume, looping, loopStart);
		fadeRate = fadePrevious;
	}
	protected abstract MusicState getMIDIState(AssetProducer assets, String fileName, float volume,
			boolean looping, int loopStart);

	protected abstract MusicState getVorbisState(AssetProducer assets, String fileName, float volume, 
			boolean looping, int loopStart);


	/**
	 * Stop any currently playing music. It is safe to call this while music is not playing.
	 * @param fadeOut The number of steps over which to fade the currently playing music out.
	 * If music is already fading out, then the fade-out rate will be updated.
	 * If 0, it will stop immediately.
	 */
	public final void stopMusic(int fadeOut) {
		this.fadeRate = Math.max(0, fadeOut);
	}
}
