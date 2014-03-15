package owg.engine.desktop.audio;

import java.io.IOException;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Soundbank;
import javax.sound.sampled.SourceDataLine;

import com.sun.media.sound.SF2SoundbankReader;
import com.sun.media.sound.SoftSynthesizer;

public class MIDISynth {
	/**The software gervill midi synth*/
	SoftSynthesizer synth;
	/**The software sequencer*/
	Sequencer sequencer;
	/**Whether the synth is currently open for MIDI input*/
	boolean isOpen;
	
	public MIDISynth(String sf2) {
		try {
			//Construct a midi environment with the Gervill software synth
			Soundbank bank = new SF2SoundbankReader().getSoundbank(ClassLoader.getSystemClassLoader().getResourceAsStream(sf2));
			synth = new SoftSynthesizer();
			synth.loadAllInstruments(bank);
			sequencer = MidiSystem.getSequencer(false);
			isOpen = false;
		} catch (InvalidMidiDataException e) {
			System.err.println("Failed to decode SoundFont...");
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("Failed to read SoundFont file...");
			e.printStackTrace();
		} catch (MidiUnavailableException e) {
			System.err.println("Failed to open Gervill Midi Synthesizer");
			e.printStackTrace();
		}
	}
	/**Returns the internal MIDI synth latency, in microseconds*/
	public long getLatency() {
		return synth.getLatency();
	}
	
	public void open(final SourceDataLine musicLine) {
		try {
			if(!isOpen) {
				synth.open(musicLine, null);
				sequencer.getTransmitter().setReceiver(synth.getReceiver());
				sequencer.open();
				isOpen = true;
			}
		} catch (MidiUnavailableException e) {
			System.err.println("Failed to open Gervill Midi Synthesizer");
			e.printStackTrace();
		}
	}
	public void close() {
		if(isOpen) {
			synth.close();
			sequencer.close();
			isOpen = false;
		}
	}
	/**Play the indicated sequence.
	 * Optionally, the sequence may loop, in which case the loop will restart at loopStartMillis.*/
	public void playSequence(Sequence sequence, boolean looping, long loopStartMillis) {
		if(!isOpen)
			throw new IllegalStateException("Cannot play sequence: "+sequence+" before MIDI synth has been opened...");
		stopSequence();
		
		try {
			sequencer.setSequence(sequence);
			sequencer.setLoopCount(looping?Sequencer.LOOP_CONTINUOUSLY:0);
			sequencer.setLoopStartPoint(msToTicks(loopStartMillis, sequence.getResolution(), sequencer.getTempoInBPM()));
			sequencer.start();
		} catch (InvalidMidiDataException e) {
			System.err.println("Corrupt MIDI data: "+this);
			e.printStackTrace();
		}
	}
	/**Stop the currently playing sequence, if any.*/
	public void stopSequence() {
		if(!isOpen)
			throw new IllegalStateException("Cannot stop sequence when MIDI synth is not open...");
		if(sequencer.isRunning())
			sequencer.stop();
	}
	
	/**Convert milliseconds to MIDI ticks for the given tempo and tick resolution*/
	public static long msToTicks(long ms, int resolution, float tempoInBPM) {
		return (long) ((ms*resolution*tempoInBPM)/60000L);
	}
	/**Convert MIDI ticks to milliseconds for the given tempo and tick resolution*/
	public static long ticksToMS(long ticks, int resolution, float tempoInBPM) {
		return (long) ((60000L*ticks)/(resolution*tempoInBPM));
	}
	/**Return current playback position in milliseconds*/
	public int getPosition() {
		if(sequencer.isRunning())
			return (int) ticksToMS(
					sequencer.getTickPosition(), 
					sequencer.getSequence().getResolution(), 
					sequencer.getTempoInBPM());
		else
			return 0;
	}
	/**Returns the currently playing sequence, or null if none.*/
	public Sequence getSequence() {
		if(sequencer.isRunning())
			return sequencer.getSequence();
		else
			return null;
	}
}
