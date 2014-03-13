package owg.engine.desktop.audio;


/**
 * This class wraps a single SourceDataLine and continously feeds it with sound data in the form of bytes.
 * The AudioLine is a pure java software sample mixer which gets playback audio data from StupidClips.
 * The clips should always be given a reference to the AudioLine instance upon creation, as the classes are tightly bound.
 * 
 * A clip cannot be played on multiple AudioLines due to the naïve linked list implementation. 
 * This should however not be a problem. 
 * 
 * The AudioLine runs in a separate thread which continously fills a small buffer with a mix of whichever clips are
 * playing at the time.
 * 
 * Sound data is expected to be signed, little endian. However conversion from unsigned 8 bit takes place on the fly.
 * */
public class AudioMixer extends Thread
{
	/**The audio output. Details of what goes on beyond this is left to the implementation.*/
	final AudioOutputLine line;
	/**The total number of frames that have been put in the buffer since the start of the program. 
	 * A frame contains 2 samples if this is a stereo output.*/
	long totalBufferFrames;
	/**The number of bytes in the SourceDataLine's buffer.*/
	final int bufByteSize;
	/**The number of frames that are put into the buffer at a time.
	 * As such, from 1 to 2 times this number of frames will pass 
	 * from a sound play is called until the data is sent to the output.*/
	final int frameLag;
	/**The number of frames per second.*/
	final int frameRate;
	/**The number of bits per sample. Will be 16 or 8.*/
	final int bitsPerSample;
	/**Number of channels. Will be equal to 2 or 1.*/
	final int channels;

	/**First element in list of clips being played back.*/
	JavaSoundClip listFirst;

	volatile boolean isRunning = true;

	/**Construct a new AudioLine with the given sample rate. 
	 * Strange sample rates may throw Exceptions. 
	 * Otherwise, this should be supported by any computer with sound.*/
	public AudioMixer(AudioOutputLine line)
	{
		this.line = line;
		this.frameRate = line.getSampleRate();
		this.bitsPerSample = line.getBitsPerSample();
		this.channels = line.getChannels();
		
		//Start with the empty list for playback
		listFirst=null;

		bufByteSize = line.getBufferSize();
		frameLag = bufByteSize*8/bitsPerSample/channels;
		line.start();
		totalBufferFrames=0;

		//The program should not keep alive because of the AudioLine. 
		setDaemon(true);
		//Start processing.
		start();
	}

	@Override
	public void run()
	{
		//Write continously
		while(isRunning)
		{
			//This works because write blocks until all the data has been written.
			//The data can only be written when there is enough free space in the buffer.
			if(bitsPerSample == 16)
			{
				write16(frameLag);
			}
			else 
			{
				write8(frameLag);
			}
		}
	}

	private void write16(int frames)
	{
		//Do the mixing in a 32 bit buffer(one element per channel).
		int[] buf = new int[channels];
		//Put into flat byte array for transfer
		byte[] bytes = new byte[frames*channels*2];

		//This simply refers to the data array for each individual sample we are processing below.
		byte[] add;
		//The playback position in the data array
		int pos;
		//The actual, processed, signed amplitude
		int value;
		//Do 32 bit mixing with 16 bit input, clamp to 16 bits
		for(int i=0; i<frames*channels; i+=channels)
		{
			//Process each sample that is playing now
			JavaSoundClip c = listFirst;
			while (c!=null)
			{
				add = c.dataArray;
				pos = c.bitsPerSample/8*c.channels*(int)c.currentFramePos;

				//Get left amplitude
				if(c.bitsPerSample==16)
					value = joinShort(add[pos+1],add[pos]);
				else
					value = ((add[pos]&0xFF)<<8)+Short.MIN_VALUE;
				//Put left channel in the buffer
				buf[0]  += (int)(c.lGain*value);

				if(c.channels==2)
				{
					//Get right amplitude
					if(c.bitsPerSample==16)
						value = joinShort(add[pos+3],add[pos+2]);
					else
						value = ((add[pos+1]&0xFF)<<8)+Short.MIN_VALUE;
				}
				//Put right channel in the buffer
				buf[channels-1]  += (int)(c.rGain*value);

				//Increment
				c.currentFramePos+=c.frequency;

				//This gets the next sample in any case:
				if(!c.isPlaying)//It was stopped by the program.
				{
					c = remove(c);
				}
				else if(c.currentFramePos>=c.numFrames)//Reached the end
				{
					if(c.isLooping && c.isPlaying)//Loop around
					{
						c.currentFramePos=0;
						c = c.listNext;
					}
					else//Stop dead
					{
						c.isPlaying=false;
						JavaSoundClip s = c;
						c = remove(c);
						if(s.stopListener!=null)
							s.stopListener.soundStopped(s);
					}
				}
				else//Nothing in particular
					c = c.listNext;
			}
			//Clamp the values to signed 16 bit and divide into bytes
			if(channels == 2) 
			{
				if (buf[1]>Short.MAX_VALUE) buf[1] = Short.MAX_VALUE;
				if (buf[1]<Short.MIN_VALUE) buf[1] = Short.MIN_VALUE;
				bytes[i*2+2] = (byte) (buf[1]&0x00FF);
				bytes[i*2+3] = (byte)((buf[1]&0xFF00)>>8);
				buf[1] = 0;
			}
			else
				buf[0] = (int)(buf[0]/2);//Halve because both channels were put here
				
			if (buf[0]>Short.MAX_VALUE) buf[0] = Short.MAX_VALUE;
			if (buf[0]<Short.MIN_VALUE) buf[0] = Short.MIN_VALUE;
			bytes[i*2  ] = (byte) (buf[0]&0x00FF);
			bytes[i*2+1] = (byte)((buf[0]&0xFF00)>>8);
			buf[0] = 0;
		}
		//Take a note that we sent more frames to the output 
		totalBufferFrames+=frames;

		//Actually write. This blocks until it's done and we're ready for the next batch.
		line.write(bytes, 0, bytes.length);
	}

	private void write8(int frames)
	{
		//Do the mixing in a 16 bit buffer.
		short[] buf = new short[channels];
		byte[] bytes = new byte[frames*channels];

		//This simply refers to the data array for each individual sample we are processing below.
		byte[] add;
		//The playback position in the data array
		int pos;
		//The actual, processed, signed amplitude
		short value;

		//Do 16 bit mixing with 8 bit input, clamp to 8 bits
		for(int i=0; i<bytes.length; i+=channels)
		{
			//Process each sample that is playing now
			JavaSoundClip c = listFirst;
			while (c!=null)
			{
				add = c.dataArray;
				pos = c.bitsPerSample/8*c.channels*(int)c.currentFramePos;

				//Get left amplitude
				if(c.bitsPerSample == 16)
					value = (short) (add[pos+1]);//Discard lsb
				else
					value = (short) ((add[pos]&0xFF)+Byte.MIN_VALUE);//Convert to signed
				//Put left channel in the buffer
				buf[0]  += (short) (c.lGain*value*(c.pan>0?1-c.pan:1));

				if(c.channels==2)
				{
					//Get right amplitude
					if(c.bitsPerSample == 16)
						value = (byte) (add[pos+3]);//Discard lsb
					else
						value = (byte) ((add[pos+1]&0xFF)+Byte.MIN_VALUE);//Convert to signed
				}
				//Put right channel in the buffer
				buf[channels-1]  += (short) (c.rGain*value*(c.pan>0?1:1+c.pan));

				//Increment
				c.currentFramePos+=c.frequency;

				//This gets the next sample in any case:
				if(!c.isPlaying)//It was stopped by the program.
				{
					c = remove(c);
				}
				else if(c.currentFramePos>=c.numFrames)//Reached the end
				{
					if(c.isLooping && c.isPlaying)//Loop around
					{
						c.currentFramePos=0;
						c = c.listNext;
					}
					else//Stop dead
					{
						c.isPlaying=false;
						JavaSoundClip s = c;
						c = remove(c);
						if(s.stopListener!=null)
							s.stopListener.soundStopped(s);
					}
				}
				else//Nothing in particular
					c = c.listNext;
			}
			//Clamp the values to unsigned 8 bit
			if(channels == 2)
			{
				if (buf[1]<=Byte.MIN_VALUE) bytes[i+1] = 0;
				else if (buf[1]>=Byte.MAX_VALUE) bytes[i+1] = (byte) 255;
				else bytes[i+1] = (byte)(buf[1]+128);
				buf[1] = 0;
			}
			else
				buf[0] = (short)(buf[0]/2);//Halve because both channels were put here
			
			if (buf[0]<=Byte.MIN_VALUE) bytes[i] = 0;
			else if (buf[0]>=Byte.MAX_VALUE) bytes[i] = (byte) 255;
			else bytes[i] = (byte)(buf[0]+128);
			buf[0] = 0;
		}
		//Take a note that we sent more frames to the output 
		totalBufferFrames+=frames;
		//Actually write. This blocks until it's done and we're ready for the next batch.
		line.write(bytes, 0, bytes.length);
	}

	/**Stop the thread and close the output line.*/
	public void dispose() {
		isRunning = false;
		line.close();
	}

	/**Remove the clip from the playback list.
	 * @return The next clip in the list. May be null.*/
	private JavaSoundClip remove(JavaSoundClip clip)
	{
		clip.isPlaying=false;
		//Remove from list
		if(clip==listFirst)
		{
			listFirst = clip.listNext;
			if(listFirst!=null)
				listFirst.listPrev = null;
		}
		else
		{
			if (clip.listPrev!=null)
				clip.listPrev.listNext = clip.listNext;
			if (clip.listNext!=null)
				clip.listNext.listPrev = clip.listPrev;
		}
		JavaSoundClip r = clip.listNext;
		clip.listNext=null;
		clip.listPrev=null;
		return r;
	}
	/**Start playback of the clip next time a buffer write operation happens.*/
	public synchronized void play(JavaSoundClip clip)
	{
		//Insert at beginning of list.
		if(listFirst!=null)
			listFirst.listPrev = clip;
		clip.listNext = listFirst;
		listFirst=clip;
	}
	/**Join 2 unsigned bytes into a signed short.*/
	public static short joinShort(byte msb, byte lsb)
	{
		return (short) (((msb&0xFF) << 8) | (lsb&0xFF));
	}
}