package owg.engine.desktop.audio;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import javax.sound.sampled.SourceDataLine;

import owg.engine.util.Triplet;

public class QueuedWaveformOutput {
	/**A service to handle writing to the audio output while the decoder is working*/
	Thread musicWriter;
	/**A bounded queue to buffer up streaming audio output*/
	BlockingQueue<Triplet<byte[], Integer, Integer>> musicQueue;
	/**Whether the music writer thread should await further input to the queue and write it to the output.*/
	volatile boolean isMusicWriterRunning;
	
	/***
	 * Construct a new wave data output queue. The writer is not automatically started.
	 * @param maxNumBuffers The maximum number of byte arrays to queue up for output.
	 * Large numbers will cause a high memory footprint. Small numbers will involve a risk of audio glitches.
	 * Latency is not affected by this number.
	 */
	public QueuedWaveformOutput(int maxNumBuffers) {
		musicWriter = null;
		musicQueue = new ArrayBlockingQueue<Triplet<byte[], Integer, Integer>>(maxNumBuffers);
		isMusicWriterRunning = false;
	}

	/**Open the waveform output writer on the given SourceDataLine, if it is not already open. */
	public void open(final SourceDataLine musicLine) {
		if(!isMusicWriterRunning) {
			musicWriter = new Thread() {
				public void run() {
					while(isMusicWriterRunning) {
						try {
							Triplet<byte[], Integer, Integer> bytes = musicQueue.take();
							
							musicLine.write(bytes.a, bytes.b, bytes.c);
						} catch (Exception e) {
							System.out.println("Music writing stopped by application");
						}
					}
				}
			};
			musicQueue.clear();
			isMusicWriterRunning = true;
			musicWriter.start();
		}
	}
	/**Close the waveform output writer, if it is open. This will allow other objects to use the SourceDataLine.*/
	public void close() {
		if(isMusicWriterRunning) {
			try {
				isMusicWriterRunning = false;
				musicQueue.clear();
				musicWriter.interrupt();
				musicWriter.join();
			} catch (InterruptedException e) {
				System.err.println("Could not terminate music writer thread");
				e.printStackTrace();
			}
			musicWriter = null;
		}
	}
	/**Clear any already queued buffers on the output.*/
	public void clear() {
		musicQueue.clear();
	}
	/**Put data in the output queue. The data will be written as soon as it is needed.
	 * The call will block until there is space in the queue, if it is full.*/
	public void offer(byte[] buf, int offset, int length) {
		try {
			musicQueue.put(new Triplet<byte[], Integer, Integer>(buf, offset, length));
		} catch (InterruptedException e) {
			System.out.println("Music writing stopped by application");
		}
	}
}
