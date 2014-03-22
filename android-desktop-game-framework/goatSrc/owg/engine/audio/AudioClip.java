package owg.engine.audio;

public interface AudioClip
	{
	/**Loop the sound until it is stopped, with the given preset parameters*/
	public void loop(float volume, float pan, float frequency);
	/**Play the sound once, with the given preset parameters*/
	public void play(float volume, float pan, float frequency);
	/**Stop the sound, if it is playing.*/
	public void stop();
	/**Set the volume. Values between 0(silent) and 1(normal volume) must be supported.*/
	public void setVolume(float volume);
	/**Set the pan. -1 is left, 1 is right.*/
	public void setPan(float pan);
	/**Set the relative frequency. Values between 0.5(half speed) and 2(double speed) must be supported.*/
	public void setFrequency(float frequency);
	/**Stop the sound if it is playing, and release any native resources held by the clip.*/
	public void dispose();
	/**Returns a soft copy. The returned instance will have a distinct playback state, 
	 * but it shares the audio data. It should never be explicitly disposed, leaving it to the GC is sufficient.*/
	public AudioClip clone();
	}
