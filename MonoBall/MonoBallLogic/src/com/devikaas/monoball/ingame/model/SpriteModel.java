package com.devikaas.monoball.ingame.model;

import owg.engine.util.Compass;
import owg.engine.util.V3F;
/**Should be implemented by objects that are represented with a single sprite on the screen.*/
public interface SpriteModel {
	/**Returns the name of the sprite to render.
	 * @see owg.engine.graphics.SpriteLib#get(String)*/
	public String getSprite();
	/**Returns the subimage of the sprite to render. Must lie within the valid frame range of {@link #getSprite()}.*/
	public int getSubimage();
	/**Returns the location where the sprite will be rendered.
	 * @param alpha The interpolation factor the the location, from previous(0) to current(1) position*/
	public V3F getSpriteLocation(float alpha);
	/**Returns the location of the origin of the sprite. By example,<br/>
	 * Northwest will cause the upper left corner of the sprite to appear at {@link #getSpriteLocation()}.<br/>
	 * Center will cause the center of the sprite to appear at {@link #getSpriteLocation()}.<br/>
	 * Northwest will cause the lower right corner of the sprite to appear at {@link #getSpriteLocation()}<br/>.
	 * Note that the object is also scaled and rotated around the origin.*/
	public Compass getOrientation();
	/**Returns the scale in the x direction, used for rendering the sprite. 1 represents no scaling.*/
	public float getXScale();
	/**Returns the scale in the y direction, used for rendering the sprite. 1 represents no scaling.*/
	public float getYScale();
	/**Returns the counterclockwise angle, used for rendering the sprite. 0 represents no rotation.*/
	public float getAngle();
}
