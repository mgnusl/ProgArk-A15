package owg.engine.desktop.audio;

import owg.engine.audio.AudioClip;

/**
 * This class is designed to contain and play back sound sample data, 
 * in a way that is uncompromisingly cross-platform and plug-and-play.
 * 
 * It provides methods for loading from wav and ogg files.
 * Format support is limited to: 
 * 8 bit unsigned PCM
 * 16 bit signed PCM
 * 
 * Any sample rate is supported, but the clip is expected to deliver data to a single 16 bit fixed rate AudioLine stream.
 * Both mono and stereo formats are supported.
 * 
 * The class provides methods for operations that should be expected, i.e. 
 * frequency changes, panning, volume, looping etc.
 * */
public class JavaSoundClip implements AudioClip
{
	/**The asset name used to load the sound*/
	final String fname;
	/**The frame rate, in Hz. Note that a frame may contain more than one sample.*/
	final int frameRate;
	/**Bits per sample. Should be 8 or 16.*/
	final int bitsPerSample;
	/**Number of channels. Should be 1 or 2, i.e. mono or stereo*/
	final int channels;
	/**The raw byte data array*/
	final byte[] dataArray;
	/**The total number of frames. A frame contains 'channels' number of samples.*/
	final int numFrames;
	/**The playback frequency with which the sound will be played at its intended speed, 
	 * with respect to the provided mixer's format*/
	final float stdFrequency;
	/**The ouput mixer. The mixer is responsible for fetching data and mixing samples.*/
	final AudioMixer myLine;

	/**The current playback position, in frames. 
	 * This is accurate to the buffer length of the output Audioline.*/
	float currentFramePos;
	/**Whether the clip is playing*/
	boolean isPlaying;
	/**Whether the clip will loop, if it is also playing*/
	boolean isLooping;

	/**The current playback speed. stdFrequency provides a default value.*/
	float frequency;
	/**The current pan. -1 is completely left, 1 is completely right, 0 is centered.*/
	float pan;
	/**The current gain. May take any value, but 0 is completely silent and 1 is the original volume.*/
	float gain;

	/**The current left channel gain. May take any value, but 0 is completely silent and 1 is the original volume.*/
	float lGain;
	/**The current right channel gain. May take any value, but 0 is completely silent and 1 is the original volume.*/
	float rGain;


	/**The AudioLine uses a linked list implementation to keep track of which sounds are playing.
	 * These variables should only be modified by AudioLine. 8D */
	JavaSoundClip listNext,listPrev;

	/**An instance that is notified when the sound reaches the end.*/
	JavaSoundStopListener stopListener;

	public JavaSoundClip(String fname, byte[] dataArray, int sampleRate, int bitsPerSample, int channels, AudioMixer line)
	{
		this.fname = fname;
		this.channels = channels;
		this.dataArray = dataArray;
		this.frameRate = sampleRate;
		this.bitsPerSample = bitsPerSample;
		numFrames = dataArray.length*8/channels/bitsPerSample;

		myLine = line;
		stdFrequency = (float)sampleRate/line.frameRate;
		gain = 1;
		pan = 0;
		lGain = 1;
		rGain = 1;
		frequency=stdFrequency;

		currentFramePos=0;
		isPlaying=false;
		isLooping=false;

		listNext=null;
		listPrev=null;
	}

	/**This will create a clone of the clip. The clone will use the same data array and thus take up very little memory.*/
	@Override
	public JavaSoundClip clone()
	{
		return new JavaSoundClip(fname, dataArray, frameRate, bitsPerSample, channels, myLine);
	}
	/**Start playing from 0*/
	public synchronized void play()
	{
		currentFramePos = 0;
		if(!isPlaying)
			myLine.play(this);
		isPlaying=true;
	}
	/**Stop and set position to 0*/
	public synchronized void stop()
	{
		if(isPlaying)
		{
			currentFramePos = 0;
			isPlaying=false;
		}
	}
	/**Set frequency. 1 will always be the original speed of the loaded sample.*/
	public void setFrequency(float frequency)
	{
		this.frequency = stdFrequency*frequency;
	}
	/**Set pan from -1 to 1, where -1 is left and +1 is right.*/
	public void setPan(float pan)
	{
		this.pan=pan;
		updateVolume();
	}
	/**Set gain. Ordinary values would lie between 0 (no sound) and 1 (original volume), but any values are permitted.*/
	public void setVolume(float gain)
	{
		this.gain = gain;
		updateVolume();
	}
	/**Compute the left/right gain based on gain and pan. (Saves a couple of cpu cycles in the mixer).*/
	private void updateVolume() 
	{
		lGain = gain*Math.min(1, 1-pan);
		rGain = gain*Math.min(1, 1+pan);
	}
	/**Set wether to loop playback when the sound playback reaches the end.*/
	public void setLooping(boolean looping)
	{
		isLooping = looping;
	}
	/**Get length in seconds, not taking frequency change into account.*/
	public float getSecondLength()
	{
		int bytes = dataArray.length;
		int bytesPerFrame = bitsPerSample*channels/8;
		int framesPerSecond = frameRate;
		return (bytes/bytesPerFrame)/framesPerSecond;
	}
	/**Specify an instance that is notified when the sound reaches the end.*/
	public synchronized void setStopListener(JavaSoundStopListener l)
	{
		this.stopListener = l;
	}

	@Override
	public void loop(float volume, float pan, float frequency)
		{
		this.setLooping(true);
		this.setVolume(volume);
		this.setPan(pan);
		this.setFrequency(frequency);
		this.play();
		}

	@Override
	public void play(float volume, float pan, float frequency)
		{
		this.setLooping(false);
		this.setVolume(volume);
		this.setPan(pan);
		this.setFrequency(frequency);
		this.play();
		}
	@Override
	public void dispose()
		{
		stop();
		}

}