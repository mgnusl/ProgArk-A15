package owg.engine.desktop.audio;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;

import owg.engine.AssetProducer;
import owg.engine.audio.MusicState;
import owg.engine.util.Triplet;

public class OggStateDesktop extends MusicState {
	private final boolean looping;
	private AssetProducer assets;
	private String fileName;
	private int loopStart;
	private JavaSoundAudioLib audioLib;
	
	private Thread decoder;
	private volatile int position;
	private volatile boolean isPlaying;
	private int rate;

	public OggStateDesktop(JavaSoundAudioLib audioLib, AssetProducer assets,
			String fileName, float volume, boolean looping, int loopStart) {
		super();
		this.assets = assets;
		this.audioLib = audioLib;
		this.fileName = fileName;
		this.volume = volume;
		this.looping = looping;
		this.loopStart = loopStart;
	}
	@Override
	public void updateVolume(float fade) {
		audioLib.musicLine.setVolume(fade*volume);
	}

	@Override
	public void play() {
		audioLib.setSynthEnabled(false);
		if(isPlaying)
			throw new IllegalStateException("play() cannot be called while music is already playing...");
		
		//Open
		final OggInputStream ois;
		try {
			ois = new OggInputStream(assets.open(MUSIC_FOLDER+fileName));
		} catch (IOException e) {
			System.err.println("Failed to open ogg file: "+OggStateDesktop.this);
			e.printStackTrace();
			return;
		}
		
		final int BUFSIZE = 1024;
		
		decoder = new Thread(){
			public void run() {
				LinkedList<Triplet<byte[], Integer, Integer>> loopBuffer = 
						looping?
							new LinkedList<Triplet<byte[],Integer,Integer>>()
						:
							null;
				
				try {
					//Stream
					while(isPlaying && ois.available() > 0) {
						byte[] buf = new byte[BUFSIZE];
						int read;
						//Dupe to stereo if necessary
						if (ois.getFormat()==OggInputStream.FORMAT_MONO16) {
							read = ois.read(buf, 0, buf.length/2);
							int j = read-2;
							for(int i = read*2-2; i>=0; i-=4) {
								buf[i] = buf[j];
								buf[i+2] = buf[j];
								buf[i+1] = buf[j+1];
								buf[i+3] = buf[j+1];
								j-=2;
							}
							read *= 2;
						}
						else 
							read = ois.read(buf);
						
						int ppos = position;
						position += read/4;
						if(framesToMillis(position) > loopStart && looping) {
							final int start = ppos<=loopStart?millisToFrames(getPosition()-loopStart)*4:0;
							loopBuffer.add(new Triplet<byte[], Integer, Integer>(buf, start, read-start));
						}
						audioLib.waveWriter.offer(buf, 0, read);
					}
				}
				catch (IOException e) {
					System.err.println("Failed to decode ogg file: "+OggStateDesktop.this);
					e.printStackTrace();
				}
				//Close
				try {
					ois.close();
				} catch (IOException e) {
					System.err.println("Failed to close ogg file: "+OggStateDesktop.this);
					e.printStackTrace();
				}
				
				if(looping) {
					//Loop
					position = 4*millisToFrames(loopStart);
					Iterator<Triplet<byte[], Integer, Integer>> loopIterator = loopBuffer.iterator();
					Triplet<byte[], Integer, Integer> buf;
					while(isPlaying) {
						if(loopIterator.hasNext())
							buf = loopIterator.next();
						else { //Rewind
							loopIterator = loopBuffer.iterator();
							buf = loopIterator.next();
							position = 4*millisToFrames(loopStart);
						}
						position += buf.c/4;
						audioLib.waveWriter.offer(buf.a, buf.b, buf.c);
					}
				}
			}
		};
		rate = ois.getRate();
		if(rate != 44100)
			System.err.println("Warning: Unexpected Ogg Vorbis sample rate: "+rate+"(expected: 44100)");
		isPlaying = true;
		position = 0;
		decoder.start();
	}
	public int getPosition() {
		return framesToMillis(position);
	}

	private int framesToMillis(int frames) {
		return (frames*1000) / rate;
	}
	private int millisToFrames(int millseconds) {
		return (rate*millseconds) / 1000;
	}
	@Override
	public void stop() {
		if(isPlaying) {
			isPlaying = false;
			audioLib.waveWriter.clear();
		}
	}

	@Override
	public String toString() {
		return fileName;
	}

}
