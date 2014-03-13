package owg.engine.graphics;

/**Provides a cross-platform, type safe reference point for OpenGL primitive rendering modes.
 * The GLUtil implementation must provide the native value for each enum!*/
public enum Primitive {
	POINTS,
	LINES, 
	LINE_STRIP, 
	LINE_LOOP,
	TRIANGLES, 
	TRIANGLE_STRIP,
	TRIANGLE_FAN;
	
	int value = -1;
	public int value() {
		return value;
	}
}
