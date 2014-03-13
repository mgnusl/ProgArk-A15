package owg.engine.desktop.audio;

import javax.sound.sampled.SourceDataLine;
public interface AudioOutputLine extends SourceDataLine {
	public final static int PCM_SIGNED_SHORT = 16;
	public final static int PCM_UNSIGNED_BYTE = 8;
	
	public final static int BUFFER_SIZE_DEFAULT = -1;
	
	public int getChannels();
	public int getSampleRate();
	public int getBitsPerSample();
	public int getBufferSize();

	/**Set the line's volume(0-1) in the future.
	 * Please note that these messages must be sent sequentially:
	 * A volume event for the far future will make it impossible to schedule any further volume changes before this.
	 * @param latency The number of microseconds into the future when the volume change must happen.
	 * @param newVolume The new volume */
	public void addVolumeEvent(long latency, float newVolume);
	/**Set the line's volume(0-1) immediately.
	 * This will destroy any pending volume events.*/
	public void setVolume(float volume);
	/**Return the current volume(0-1).*/
	public float getVolume();
	
	public void start();
	
	public int write(byte[] bytes, int offset, int length);
	
	public void close();
}
