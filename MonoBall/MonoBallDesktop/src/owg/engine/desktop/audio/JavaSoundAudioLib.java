package owg.engine.desktop.audio;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Locale;

import javax.sound.sampled.LineUnavailableException;
import owg.engine.AssetProducer;
import owg.engine.audio.AudioClip;
import owg.engine.audio.AudioLib;
import owg.engine.audio.MusicState;
import owg.engine.util.NamedInputStream;

public class JavaSoundAudioLib extends AudioLib
{
	/**Audio output lines, separate sound effects and music*/
	AudioOutputLine sfxLine, musicLine;
	/**Stable Java sound mixer implementation*/
	AudioMixer mixer;

	/**Wrapper for the midi synthesizer*/
	MIDISynth synth;
	/**A bounded, queued output buffer for waveform music bytes to be written to the output*/
	QueuedWaveformOutput waveWriter;

	public JavaSoundAudioLib(AssetProducer assets)
	{
		super(assets);
	}

	@Override
	protected void initMixer()
	{
		final int rate = 44100;
		try {
			sfxLine = new JavaSoundOutputLine(2, rate, 
					AudioOutputLine.PCM_SIGNED_SHORT, AudioOutputLine.BUFFER_SIZE_DEFAULT);
			musicLine = new JavaSoundOutputLine(2, rate, 
					AudioOutputLine.PCM_SIGNED_SHORT, AudioOutputLine.BUFFER_SIZE_DEFAULT);
			musicLine.start();
		} catch (LineUnavailableException e) {
			throw new RuntimeException("Failed to initialuze audio output", e);
		}
		mixer = new AudioMixer(sfxLine);
		
		waveWriter = new QueuedWaveformOutput(8);
		synth = new MIDISynth("musix/A320U.sf2");
	}

	@Override
	public synchronized void dispose()
	{
		mixer.dispose();
	}

	@Override
	protected AudioClip loadWavOgg(AssetProducer assets, String file, String extension) throws IOException
	{
		if(extension.equals("ogg"))
			return loadOgg(assets.open(file));
		else
			return loadWav(assets.open(file));
	}

	public AudioClip loadWav(NamedInputStream file) throws IOException
	{
		final boolean DEBUG = false;
		if(DEBUG)
			System.out.println("Loading RIFF WAVE file: "+file);

		final String riff;
		final int fileSize;
		final String wave;

		int sampleRate = 0;
		short bitsPerSample = 0;
		short channels = 0;

		byte[] data = new byte[4];
		ByteBuffer buf = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);
		int read = 0;

		//Confirm that this is RIFF file format
		read = file.read(data);									// 00 - "RIFF"
		if(read != 4) 
			throw new IOException("Unexpected end of file: "+file+"... Read "+read+" bytes, expected 4");
		riff = new String(data).toUpperCase(Locale.ENGLISH);
		if(!riff.equals("RIFF"))
			throw new IOException("Unsupported format: "+riff+"(expected: RIFF)");

		//Get file size
		read = file.read(data);
		if(read != 4) 
			throw new IOException("Unexpected end of file: "+file+"... Read "+read+" bytes, expected 4");
		fileSize = buf.getInt();
		if(DEBUG)
			System.out.println("File size: "+fileSize);

		//Read file
		data = new byte[fileSize];
		buf = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);
		read = file.read(data);
		if(read != fileSize) 
			throw new IOException("Unexpected end of file: "+file+"... Read "+read+" bytes, expected "+fileSize);
		file.close();

		wave = getString4(buf);
		if(!wave.equals("WAVE"))
			throw new IOException("File: "+file+" is not a WAVE file... Found: "+wave);

		//Read all chunks before the data chunk(although we only care about the fmt and data chunks)
		//Note: The WAVE specification states that the fmt chunk must come before the data chunk.
		String chunkName;
		int chunkLength;
		while(true) {
			if(buf.remaining() < 8)
				throw new IOException("Error in WAVE file: "+file+": File does not have a data chunk!");
			chunkName = getString4(buf);
			chunkLength = buf.getInt();

			if(chunkName.equals("fmt ")) {
				if(DEBUG)
					System.out.println("Located fmt chunk.");
				//Fail if unknown fmt chunk
				if(chunkLength != 14 && chunkLength != 16 && chunkLength != 18)
					throw new IOException("Unsupported fmt chunk length in WAVE file: "+file+
							"... Found: "+chunkLength+", expected: 14, 16 or 18");

				//Fail if not PCM
				short encoding = buf.getShort(); //2
				if(encoding != 0x0001)
					throw new IOException("File: "+file+" Unsupported encoding: "+encoding+
							"(expected: 0x0001(PCM))");

				//Fail if not Mono or Stereo
				channels = buf.getShort();
				if(channels != 1 && channels != 2) {
					throw new IOException("File: "+file+" Unsupported channel count: "+channels+
							"(expected: 1 or 2)");
				}

				//Support any sample rate.
				sampleRate = buf.getInt();

				//nAvgBytesPerSec is duplicate data, discard.
				buf.getInt();

				//Get the block align. This is the frame size, in bytes.
				int nBlockAlign = buf.getShort();

				if(chunkLength == 14) {
					//No explicit bit depth, extract from nBlockAlign
					bitsPerSample = (short) (nBlockAlign*16/channels);
				}
				else {
					//Read explicitly defined bit depth
					bitsPerSample = buf.getShort(); //16
					//Check that it is consistent with nBlockAlign
					if(nBlockAlign != channels*bitsPerSample/8)
						throw new IOException("File: "+file+" Frame size: "+nBlockAlign+
								" is inconsistent with bit depth: "+bitsPerSample+" and channel count: "+channels);
				}

				if(chunkLength == 18) {
					//Garbage data, don't care
					buf.getShort();
				}


				if(bitsPerSample != 8 && bitsPerSample != 16) {
					throw new IOException("File: "+file+" Unsupported bit depth: "+bitsPerSample+
							"(expected: 8 or 16)");
				}

				if(DEBUG)
					System.out.println("Format: " +
							"Channels="+channels+
							", bitDepth="+bitsPerSample+
							", sampleRate="+sampleRate);
			}
			else if(chunkName.equals("data"))
				break;
			else { 
				if(DEBUG)
					System.out.println("Skipping chunk: "+chunkName+" with length: "+chunkLength);
				buf.position(buf.position()+chunkLength);
			}
		}

		if(DEBUG)
			System.out.println("Located data chunk. Length: "+chunkLength);

		byte[] dataArray =  new byte[chunkLength];
		buf.get(dataArray);

		if(DEBUG)
			System.out.println(String.valueOf(buf.remaining())+" remaining bytes after data chunk.");

		//Discard all bytes after data chunk.
		data = null;
		buf = null;

		//Wrap the array and format data into a clip object
		JavaSoundClip clip = new JavaSoundClip(file.name, dataArray, sampleRate, bitsPerSample, channels, mixer);
		return clip;
	}

	private static String getString4(ByteBuffer buf) {
		byte[] str = new byte[4];
		buf.get(str);
		return new String(str);
	}
	public AudioClip loadOgg(NamedInputStream file) throws IOException 
	{
		//Create decompressor stream
		OggInputStream oggInput = new OggInputStream(file);
		//Decompress into a byte stream
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream(1024*256);
		byteOut.reset();
		byte copyBuffer[] = new byte[1024*4];
		boolean done = false;
		while (!done) 
		{
			int bytesRead = oggInput.read(copyBuffer, 0, copyBuffer.length);
			byteOut.write(copyBuffer, 0, bytesRead);
			done = (bytesRead != copyBuffer.length || bytesRead < 0);
		}
		//Convert to array
		byte[] dataArray = byteOut.toByteArray();
		//Wrap the array and format data into a clip object
		JavaSoundClip clip = new JavaSoundClip(file.name, dataArray,oggInput.getRate(),16,(oggInput.getFormat()==OggInputStream.FORMAT_STEREO16?2:1),mixer);
		oggInput.close();
		return clip;
	}

	@Override
	public MusicState getMIDIState(AssetProducer assets, String fileName,
			float volume, boolean looping, int loopStart) {
		return new MIDIStateDesktop(this, assets, fileName, volume, looping, loopStart);
	}
	@Override
	public MusicState getVorbisState(AssetProducer assets, String fileName,
			float volume, boolean looping, int loopStart) {
		return new OggStateDesktop(this, assets, fileName, volume, looping, loopStart);
	}
	/**Call to enable(open) or disable(close) the MIDI synthesizer.
	 * The synth must be enabled to play back MIDI files.
	 * The synth must be disabled if music(ogg vorbis) should be streamed from disk.*/
	void setSynthEnabled(boolean b) {
		if(b) {
			waveWriter.close();
			synth.open(musicLine);
		}
		else {
			synth.close();
			waveWriter.open(musicLine);
		}
	}
}
