package owg.engine.android.audio;

import java.io.IOException;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import owg.engine.AssetProducer;
import owg.engine.android.AssetsAndroid;
import owg.engine.audio.AudioClip;
import owg.engine.audio.AudioLib;
import owg.engine.audio.MusicState;

public class SoundPoolAudioLib extends AudioLib
{
	/**Soundpool*/
	SoundPool soundPool;
	/**MediaPlayer(streaming)*/
	MediaPlayer mediaPlayer;

	public SoundPoolAudioLib(final AssetProducer assets)
	{
		super(assets);
	}

	@Override
	protected void initMixer()
	{
		System.out.println("Creating SoundPool");
		soundPool = new SoundPool(16, AudioManager.STREAM_MUSIC, 0);
		mediaPlayer = new MediaPlayer();
		mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
	}
	public synchronized void dispose()
	{
		mediaPlayer.stop();
		mediaPlayer.release();
	}

	@Override
	protected AudioClip loadWavOgg(AssetProducer assets, String file, String extension) throws IOException
	{
		int soundID = soundPool.load(((AssetsAndroid)assets).getAssetManager().openFd(file), 1);
		System.out.println("Loaded "+file+" as "+soundID);
		SoundPoolAudioClip soundClip = new SoundPoolAudioClip(this, soundID);
		return soundClip;
	}
	
	@Override
	public MusicState getMIDIState(AssetProducer assets, String fileName,
			float volume, boolean looping, int loopStart) {
		return new MusicStateAndroid(mediaPlayer, assets, fileName, volume, looping, loopStart);
	}
	@Override
	public MusicState getVorbisState(AssetProducer assets, String fileName,
			float volume, boolean looping, int loopStart) {
		return new MusicStateAndroid(mediaPlayer, assets, fileName, volume, looping, loopStart);
	}
}