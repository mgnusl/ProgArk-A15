package owg.engine.audio;

public abstract class MusicState {
	public final static String MUSIC_FOLDER = "music/";
	protected float volume;
	public final void setVolume(float volume) {
		this.volume = volume;
	}
	public abstract void updateVolume(float fade);
	public abstract void play();
	public abstract void stop();
}
