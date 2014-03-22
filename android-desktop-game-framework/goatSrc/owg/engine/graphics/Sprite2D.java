package owg.engine.graphics;

import java.io.IOException;

import owg.engine.Engine;
import owg.engine.util.Compass;
import owg.engine.util.V3F;

/**A container for arrays of 2D textures. Note: Currently only supports power-of-two textures on all hardware.*/
public abstract class Sprite2D {
	/**Enable the 2D texture at the given index on the GL defined by GLUtil.
	 * If 2D textures are already enabled, then this will replace the currently bound texture.*/
	public abstract void enable(int subImage);
	/**Disable 2D textures on the GL defined by GLUtil.*/
	public abstract void disable();
	/**Enable, render and disable the texture with the given index.<br/>
	 * <br/>
	 * The texture will be rendered on the x/y plane, at the given location.
	 * The location actually defines where the point on the image defined by compassOrientation will appear.<br/>
	 * Example: {@link Compass#NORTHWEST} will render the upper-left pixel at the given location.
	 * {@link Compass#CENTER} will render the image with its center position at the given location. <br/>
	 * <br/>
	 * The rectangle will have a size equal to {@link #getWidth()}*xScale, {@link #getHeight()}*yScale 
	 * in the current modelview transformation.<br/>
	 * <br/>
	 * A counterclockwise rotation of angle(radians) is applied around the position 
	 * prior to scaling and translation.*/
	public void render(int subImage, V3F location, Compass orientation, float xScale, float yScale, float angle) {
    	enable(subImage);
    	MatrixStack m = Engine.glUtil().modelviewMatrix();
    	m.push();
    	m.translatef(location.x(), location.y(), location.z());
    	m.scalef(getWidth()*xScale/2, getHeight()*yScale/2, 1);
    	m.rotatef(angle, 0, 0, -1);
    	m.translatef(-(orientation.dx*2-1), -(orientation.dy*2-1), 0);
    	Engine.glUtil().unitSquare.render();
    	m.pop();
    	disable();
	}
	/**Return the sprite width. All textures in the sprite have the same size.*/
	public abstract int getWidth();
	/**Return the sprite height. All textures in the sprite have the same size.*/
	public abstract int getHeight();
	/**Return the number of textures in the sprite.*/
	public abstract int getNumFrames();
	/**Instruct the Sprite2D object to reload the texture from file.
	 * @throws IOException If reloading fails.*/
	public abstract void reload() throws IOException;
}
