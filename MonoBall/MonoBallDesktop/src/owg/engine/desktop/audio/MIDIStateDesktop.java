package owg.engine.desktop.audio;

import java.io.IOException;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import owg.engine.AssetProducer;
import owg.engine.audio.MusicState;

public class MIDIStateDesktop extends MusicState {
	private final boolean looping;
	private String fileName;
	private int loopStart;
	private JavaSoundAudioLib audioLib;
	private Sequence sequence;
	
	public MIDIStateDesktop(JavaSoundAudioLib audioLib, AssetProducer assets,
			String fileName, float volume, boolean looping, int loopStart) {
		super(); //thanks for asking
		this.audioLib = audioLib;
		this.fileName = fileName;
		this.volume = volume;
		this.looping = looping;
		this.loopStart = loopStart;
		try {
			sequence = MidiSystem.getSequence(assets.open(MUSIC_FOLDER+fileName));
		} catch (InvalidMidiDataException e) {
			System.err.println("Corrupt MIDI data: "+this);
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("Could not load MIDI file: "+this);
			e.printStackTrace();
		}
	}
	@Override
	public void updateVolume(float fade) {
		audioLib.musicLine.addVolumeEvent(
				audioLib.synth.getLatency(), 
				fade*volume);
	}

	@Override
	public void play() {
		audioLib.setSynthEnabled(true);
		audioLib.synth.playSequence(sequence, looping, loopStart);
	}
	
	public int getPosition() {
		return audioLib.synth.getPosition();
	}

	@Override
	public void stop() {
		if(audioLib.synth.getSequence() == sequence)
		audioLib.synth.stopSequence();
		
	}
	
	@Override
	public String toString() {
		return fileName;
	}

}
