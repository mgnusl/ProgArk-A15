package owg.engine.android.audio;

import owg.engine.audio.AudioClip;

/**unused*/
public class SoundPoolAudioClip implements AudioClip {
	SoundPoolAudioLib audioLib;
	int soundID;
	int streamID;
	
	float volume;
	float pan;
	float frequency;
	
	public SoundPoolAudioClip(SoundPoolAudioLib audioLib, int soundID) {
		this.audioLib = audioLib;
		this.soundID = soundID;
		this.streamID = -1;
	}
	@Override
	public void play(float volume, float pan, float frequency) {
		if(streamID != -1)
			stop();
		this.volume = volume;
		this.pan = pan;
		this.frequency = frequency;
		streamID = audioLib.soundPool.play(soundID, volume*Math.min(1, 1-pan), volume*Math.min(1, 1+pan), 1, 0, frequency);
	}
	@Override
	public void loop(float volume, float pan, float frequency) {
		if(streamID != -1)
			stop();
		this.volume = volume;
		this.pan = pan;
		this.frequency = frequency;
		streamID = audioLib.soundPool.play(soundID, volume*Math.min(1, 1-pan), volume*Math.min(1, 1+pan), 1, -1, frequency);
	}
	@Override
	public void stop()
		{
		audioLib.soundPool.stop(streamID);
		}
	@Override
	public void setVolume(float volume)
		{
		this.volume = volume;
		audioLib.soundPool.setVolume(streamID, volume*Math.min(1, 1-pan), volume*Math.min(1, 1+pan));
		}
	@Override
	public void setPan(float pan)
		{
		this.pan = pan;
		audioLib.soundPool.setVolume(streamID, volume*Math.min(1, 1-pan), volume*Math.min(1, 1+pan));
		}
	@Override
	public void setFrequency(float frequency)
		{
		this.frequency = frequency;
		audioLib.soundPool.setRate(streamID, frequency);
		}
	@Override
	public void dispose()
		{
		audioLib.soundPool.unload(soundID);
		}
	@Override
	public SoundPoolAudioClip clone() {
		return new SoundPoolAudioClip(audioLib, soundID);
	}
}
