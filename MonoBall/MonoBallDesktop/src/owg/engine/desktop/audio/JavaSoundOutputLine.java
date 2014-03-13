package owg.engine.desktop.audio;

import java.util.LinkedList;
import java.util.Queue;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Control;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.Control.Type;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import owg.engine.util.Pair;


public class JavaSoundOutputLine implements AudioOutputLine {
	private int channels;
	private int sampleRate;
	private int bitsPerSample;
	private int bufferSize;
	
	private float volume;
	
	private AudioFormat format;
	private SourceDataLine line;
	
	private long lastScheduledVolumeChange;
	private Queue<Pair<Long, Float>> volumeChangeQueue; 
	
	/**Ability to control music volume.*/
	private FloatControl musicVolumeControl;

	public JavaSoundOutputLine(int channels, int sampleRate, int bitsPerSample, int bufferSize) throws LineUnavailableException {
		this.channels = channels;
		this.sampleRate = sampleRate;
		this.bitsPerSample = bitsPerSample;

		if(bufferSize == -1)
			bufferSize = (50*channels*sampleRate*bitsPerSample/8)/1000;
		this.bufferSize = bufferSize;
		//Set up the output line
		format = new AudioFormat(sampleRate,bitsPerSample, channels, bitsPerSample==16, false);
		line = AudioSystem.getSourceDataLine(format);
		line.open(format,bufferSize);
		
		volume = 1;
		volumeChangeQueue = new LinkedList<Pair<Long,Float>>();
		lastScheduledVolumeChange = Long.MIN_VALUE;
		
		if (line.isControlSupported(javax.sound.sampled.FloatControl.Type.MASTER_GAIN)) {
			musicVolumeControl = ((FloatControl)line.getControl(javax.sound.sampled.FloatControl.Type.MASTER_GAIN));
		}
		else {
			System.err.println("Warning: No volume control support for: "+line);
		}
		
	}
	
	@Override
	public int getChannels() {
		return channels;
	}

	@Override
	public int getSampleRate() {
		return sampleRate;
	}

	@Override
	public int getBitsPerSample() {
		return bitsPerSample;
	}

	@Override
	public int getBufferSize() {
		return bufferSize;
	}

	@Override
	public void start() {
		line.start();
	}

	@Override
	public void addVolumeEvent(long latency, float newVolume) {
		synchronized(volumeChangeQueue) {
			long time = line.getMicrosecondPosition()+latency;
			if(time <= lastScheduledVolumeChange || latency < 0)
				return;//Discard
			volumeChangeQueue.offer(new Pair<Long, Float>(time, newVolume));
			lastScheduledVolumeChange = time;
		}
	}
	@Override
	public void setVolume(float newVolume) {
		synchronized(volumeChangeQueue) {
			volumeChangeQueue.clear();
			lastScheduledVolumeChange = Long.MIN_VALUE;
		}
		volume = newVolume;
		if(musicVolumeControl != null)
			musicVolumeControl.setValue(musicVolumeControl.getMinimum()+
					volume*0.95f*(musicVolumeControl.getMaximum()-musicVolumeControl.getMinimum()));
	}
	@Override
	public float getVolume() {
		return volume;
	}

	@Override
	public int write(byte[] bytes, int offset, int length) {
		int read = line.write(bytes, offset, length);

		synchronized(volumeChangeQueue) {
			Pair<Long, Float> nextVolumeChange = volumeChangeQueue.peek();
			if(nextVolumeChange != null && line.getMicrosecondPosition() >= nextVolumeChange.a) {
				volume = nextVolumeChange.b;
				if(musicVolumeControl != null)
					musicVolumeControl.setValue(musicVolumeControl.getMinimum()+
							volume*0.95f*(musicVolumeControl.getMaximum()-musicVolumeControl.getMinimum()));
				volumeChangeQueue.remove();
			}
		}
		return read;
	}

	@Override
	public void close() {
		line.stop();
		line.close();
	}

	@Override
	public void open(AudioFormat arg0) throws LineUnavailableException {
		line.open(arg0);
	}

	@Override
	public void open(AudioFormat arg0, int arg1)
			throws LineUnavailableException {
		line.open(arg0, arg1);
	}

	@Override
	public int available() {
		return line.available();
	}

	@Override
	public void drain() {
		line.drain();
	}

	@Override
	public void flush() {
		line.flush();
	}

	@Override
	public AudioFormat getFormat() {
		return line.getFormat();
	}

	@Override
	public int getFramePosition() {
		return line.getFramePosition();
	}

	@Override
	public float getLevel() {
		return line.getLevel();
	}

	@Override
	public long getLongFramePosition() {
		return line.getLongFramePosition();
	}

	@Override
	public long getMicrosecondPosition() {
		return line.getMicrosecondPosition();
	}

	@Override
	public boolean isActive() {
		return line.isActive();
	}

	@Override
	public boolean isRunning() {
		return line.isRunning();
	}

	@Override
	public void stop() {
		line.stop();
	}

	@Override
	public void addLineListener(LineListener arg0) {
		line.addLineListener(arg0);
	}

	@Override
	public Control getControl(Type arg0) {
		return line.getControl(arg0);
	}

	@Override
	public Control[] getControls() {
		return line.getControls();
	}

	@Override
	public javax.sound.sampled.Line.Info getLineInfo() {
		return line.getLineInfo();
	}

	@Override
	public boolean isControlSupported(Type arg0) {
		return line.isControlSupported(arg0);
	}

	@Override
	public boolean isOpen() {
		return line.isOpen();
	}

	@Override
	public void open() throws LineUnavailableException {
		line.open();
	}

	@Override
	public void removeLineListener(LineListener arg0) {
		line.removeLineListener(arg0);
	}
}
