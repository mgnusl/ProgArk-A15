package owg.engine.android.audio;

import java.io.IOException;

import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import owg.engine.AssetProducer;
import owg.engine.android.AssetsAndroid;
import owg.engine.audio.MusicState;

public class MusicStateAndroid extends MusicState implements OnCompletionListener {
	final boolean looping;
	volatile boolean isPlaying;
	private String fileName;
	private int loopStart;
	private MediaPlayer mediaPlayer;
	private AssetProducer assets;
	
	public MusicStateAndroid(MediaPlayer mediaPlayer, AssetProducer assets,
			String fileName, float volume, boolean looping, int loopStart) {
		super();
		this.mediaPlayer = mediaPlayer;
		this.assets = assets;
		this.fileName = fileName;
		this.volume = volume;
		this.looping = looping;
		this.loopStart = loopStart;
		this.isPlaying = false;
	}

	@Override
	public void updateVolume(float fade) {
		mediaPlayer.setVolume(fade*volume, fade*volume);
	}

	@Override
	public void play() {
		try {
			mediaPlayer.reset();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			AssetFileDescriptor fd = ((AssetsAndroid)assets).getAssetManager().openFd(MusicState.MUSIC_FOLDER+fileName);
			mediaPlayer.setDataSource(fd.getFileDescriptor(), fd.getStartOffset(), fd.getLength());
			fd.close();
			mediaPlayer.prepare();
			if(loopStart == 0 || !looping) {
				mediaPlayer.setOnCompletionListener(null);
				mediaPlayer.setLooping(looping);
			}
			else { 
				mediaPlayer.setOnCompletionListener(this);
				mediaPlayer.setLooping(false);
			}
			isPlaying = true;
			mediaPlayer.start();
		} catch (IOException e) {
			System.err.println("Could not play music: "+this);
			e.printStackTrace();
		}
	}
	
	public int getPosition() {
		return mediaPlayer.getCurrentPosition();
	}

	@Override
	public void stop() {
		isPlaying = false;
		mediaPlayer.stop();
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		if(!isPlaying)//Do nothing if playback has been stopped by the application.
			return;
		assert mp == mediaPlayer;
		//onCompletion becomes unreliable if we don't reset the MediaPlayer between each round.
		mediaPlayer.reset();
		mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		AssetFileDescriptor fd;
		try {
			fd = ((AssetsAndroid)assets).getAssetManager().openFd(MusicState.MUSIC_FOLDER+fileName);
			mediaPlayer.setDataSource(fd.getFileDescriptor(), fd.getStartOffset(), fd.getLength());
			fd.close();
			mediaPlayer.prepare();
			mediaPlayer.setLooping(false);
			mediaPlayer.setOnCompletionListener(this);
			mediaPlayer.seekTo(loopStart);
			mediaPlayer.start();
		} catch (IOException e) {
			System.err.println("Could not repeat music: "+this+" at "+loopStart);
			e.printStackTrace();
		}
	}
	
	@Override
	public String toString() {
		return fileName;
	}
}
