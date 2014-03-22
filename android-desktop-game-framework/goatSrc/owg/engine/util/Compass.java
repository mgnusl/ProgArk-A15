package owg.engine.util;

/**Provides an abstraction for cardinal directions on the X/Y plane, in a left-handed coordinate space.<br/>
 * The following conventions are used throughout the project:<br/>
 * <br/>
 * East is determined to be in the direction of (1,0), west in the direction of (-1,0), 
 * north in the direction of (0,-1) and south in the direction of (0,1).<br/>
 * <br/>
 * Where an ordering is implied, east is determined to be 0, 
 * and the other directions follow in a counter-clockwise fashion.*/
public enum Compass {
	EAST(1f, 0.5f), 
	NORTHEAST(1f, 0f), 
	NORTH(0.5f, 0f), 
	NORTHWEST(0f, 0f), 
	WEST(0f, 0.5f), 
	SOUTHWEST(0f, 1f), 
	SOUTH(0.5f, 1f), 
	SOUTHEAST(1f, 1f), 
	CENTER(0.5f, 0.5f);
	/**The normalized coordinate component, clamped to the range[(0,0), (1,1)].<br/>
	 * For example, CENTER is (0.5, 0.5), NORTHEAST is (1,0), SOUTH is (0.5, 1).*/
	public final float dx, dy;
	Compass(float dx, float dy) {
		this.dx = dx;
		this.dy = dy;
	}
}
