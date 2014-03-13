package owg.engine.util;

import java.io.IOException;
import java.io.InputStream;

/**
 * This class exists to add a name to an input stream.
 * The name should be such that it can be used to recreate the stream(e.g. a file path).
 */
public class NamedInputStream extends InputStream {
	public final String name;
	public final InputStream input;
	
	/**
	 * Construct a named stream from the input stream.
	 * @param input The underlying stream. 
	 * Any operations on this stream will simply delegate a call to the same method on the input.
	 * @param name The name of the stream. The name should be such that it can be used to recreate the stream
	 * (e.g. a file path).
	 */
	public NamedInputStream(InputStream input, String name) {
		this.name = name;
		this.input = input;
	}

	@Override
	public int read() throws IOException {
		return input.read();
	}
	@Override
	public int read(byte[] buffer) throws IOException {
		return input.read(buffer);
	}
	@Override
	public int read(byte[] buffer, int byteOffset, int byteCount)
			throws IOException {
		return input.read(buffer, byteOffset, byteCount);
	}
	@Override
	public int available() throws IOException {
		return input.available();
	}
	@Override
	public void close() throws IOException {
		input.close();
	}
	@Override
	public void mark(int readlimit) {
		input.mark(readlimit);
	}
	@Override
	public boolean markSupported() {
		return input.markSupported();
	}
	@Override
	public synchronized void reset() throws IOException {
		input.reset();
	}
	@Override
	public long skip(long byteCount) throws IOException {
		return input.skip(byteCount);
	}
	
	
	@Override
	public String toString() {
		return name;
	}
	public String getName() {
		return name;
	}
	public InputStream getInput() {
		return input;
	}
}
