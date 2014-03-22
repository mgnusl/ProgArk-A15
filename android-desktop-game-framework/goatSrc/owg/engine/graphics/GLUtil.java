package owg.engine.graphics;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.LinkedList;
import java.util.Queue;

import owg.engine.util.NamedInputStream;


/**This abstract class defines abstractions for basic operations that require the GL.*/
public abstract class GLUtil<GL> {
	/**Reusable renderable primitives. In general, they fill the space between -1 and 1 on each relevant axis.<br/>
	 * 1-Dimensional objects will exist on the x axis while 2-Dimensional objects will exist on the XY-plane.*/
	public final PolygonModelF<GL> unitSquare;
	/**Provides a very simple interface for rendering geometry with low complexity.*/
	public final ImmediateModeWrapper<GL> immediateMode;
	
	/**Queue for enqueuing runnables for execution on the next step.*/
	protected final Queue<Runnable> runnableQueue;
	/**The GL instance.*/
	protected GL gl;

	
	
	/**Singleton constructor*/
	public GLUtil(GL gl) {
		this.gl = gl;
		this.runnableQueue = new LinkedList<Runnable>();
		
		//Retrieve graphics enum values from implementation
		for (Primitive p : Primitive.values())
			p.value = valueOf(p);
		for (BlendMode.BlendOp p : BlendMode.BlendOp.values())
			p.value = valueOf(p);
		
		//We now have enough information to generate models
		unitSquare = PrimitiveFactory.genSquare(this,  -1, -1,  	1, 1,  		1 ,1);
		
		immediateMode = new ImmediateModeWrapper<GL>(this);
	}
	/**Get the native OpenGL value for the indicated primitive rendering mode*/
	protected abstract int valueOf(Primitive p);
	/**Get the native OpenGL value for the indicated blending operand*/
	protected abstract int valueOf(BlendMode.BlendOp func);

	/**Get the GL instance. The implementation must further specify what kind of GL this is.*/
	public abstract GL getGL();
	/**Check for OpenGL errors. If an error is encountered, the message is printed along with it.
	 * @return Whether an error was found.*/
	public abstract boolean checkError(String message);
	/**Enqueue an operation to be executed on the next step. <br/>
	 * Useful for doing this from other threads where the GL context is not current.*/
	public final void invokeLater(Runnable r) {
		synchronized(runnableQueue) {
			runnableQueue.offer(r);
		}
	}
	
	/**Should be called every step on the thread that owns the GL context. <br/>
	 * Will execute default behaviour and dispatch a call to {@link #stepImpl()}*/
	public final void step() {
		synchronized(runnableQueue) {
			while(!runnableQueue.isEmpty()) {
				Runnable r = runnableQueue.poll();
				r.run();
			}
		}
		stepImpl();
	}
	

	/**Perform routine operations on every game step.*/
	protected abstract void stepImpl();
	
	/**Load a 2D sprite. A 2D sprite is a sequence of equally sized textures.<br/>
	 * By default, sprite sheets are automatically loaded from the textures directory.<br/>
	 * See {@link SpriteLib} for details.
	 * @param simpleName The name that is given to the sprite in the application. 
	 * This name may used to provide meaningful debug output, be displayed in the user interface, 
	 * or be used as a hash map key.
	 * @param file The named resource stream to load. PNG must be supported. 
	 * This should be a relative filename path relative to the assets directory.
	 * @param numXFrames The number of frames in the source file in the X direction
	 * @param numYFrames The number of lines of frames in the source file in the Y direction 
	 * @return The sprite object.
	 * @throws IOException If the sprite could not be loaded from the file*/
	protected abstract Sprite2D loadSprite2D(String simpleName, NamedInputStream file, int numXFrames, int numYFrames) throws IOException;
	
	public abstract PolygonModelF<GL> genModel(String name, boolean dynamicDraw, Primitive faceMode, boolean useNormal, boolean useColor, boolean useAlpha, boolean useTexCoord, boolean use3DTexCoord);
	
	/**Return a buffer containing the data from the array.<br/>
	 * The returned buffer must be usable by OpenGL for rendering, 
	 * so a direct buffer may be required if DMA will be used.*/
	public abstract FloatBuffer asBuffer(float[] data);
	/**Return a buffer containing the data from the array.<br/>
	 * The returned buffer must be usable by OpenGL for rendering, 
	 * so a direct buffer may be required if DMA will be used.*/
	public abstract IntBuffer asBuffer(int[] data);
	/**Return a buffer containing the data from the array.<br/>
	 * The returned buffer must be usable by OpenGL for rendering, 
	 * so a direct buffer may be required if DMA will be used.*/
	public abstract ShortBuffer asBuffer(short[] data);
	/**Return a buffer containing the data from the array.<br/>
	 * The returned buffer must be usable by OpenGL for rendering, 
	 * so a direct buffer may be required if DMA will be used.*/
	public abstract ByteBuffer asBuffer(byte[] data);

	/**Clear the entire canvas with the indicated color.*/
	public abstract void clearScreen(ColorF colorF);
	/**Set the current color used for drawing.<br/>
	 * Normally, texture color is multiplied by this value, or,
	 * if there is no texture, the output color is set to this value.*/
	public abstract void setColor(ColorF colorF);
	
	/**Returns a matrix stack for the traditional modelview matrix.<br/>
	 * If applicable, calling this method will set the current matrix mode to operate on the modelview matrix.<br/>
	 * In practice, this means that, as a general contract, 
	 * references to the object returned <i>previous</i> calls to this method should never be reused.*/
	public abstract MatrixStack modelviewMatrix();
	/**Returns a matrix stack for the traditional texture matrix.<br/>
	 * If applicable, calling this method will set the current matrix mode to operate on the texture matrix.<br/>
	 * In practice, this means that, as a general contract, 
	 * references to the object returned <i>previous</i> calls to this method should never be reused.*/
	public abstract MatrixStack textureMatrix();
	/**Returns a matrix stack for the traditional projection matrix.<br/>
	 * If applicable, calling this method will set the current matrix mode to operate on the projection matrix.<br/>
	 * In practice, this means that, as a general contract, 
	 * references to the object returned <i>previous</i> calls to this method should never be reused.*/
	public abstract MatrixStack projectionMatrix();
	
	/**Update the current GL instance, in case it changes in the application.<br/> 
	 * The user does not ever have to call this.*/
	public void setGL(GL gl) {
		this.gl = gl;
	}
	/**Sets the current blend mode on the GL.*/
	public abstract void setBlendMode(BlendMode blendMode);
	
	/**Enable a 2D texture on the GL.*/
	public abstract void enableTexture2D(int texture);
	/**Disable 2D textures on the GL.*/
	public abstract void disableTexture2D();
	
	/**Enable or disable smooth lines. Affects line-based primitives. The actual effect is implementation-dependent.*/
	public abstract void setLineSmoothing(boolean b);
	/**Set line width. Affects line-based primitives. The actual effect is implementation-dependent.*/
	public abstract void setLineWidth(float w);
	/**Renders a rectangle with the given size, 
	 * with its upper-left corner at the given position on the x/y plane.
	 * May cause the Modelview matrix to become active.*/
	public void drawRect(float x, float y, float width, float height) {
		modelviewMatrix().push();
		modelviewMatrix().translatef(x+width/2f, y+height/2f, 0);
		modelviewMatrix().scalef(width/2f, height/2f, 1);
		unitSquare.render();
		modelviewMatrix().pop();
	}
	/**Sets the viewport into the indicated rectangle on the game window.
	 * The viewport is a transformation from device coordinates into window coordinates.
	 * All rendered shapes will be transformed into this area of the window.*/
	public abstract void viewport(int x, int y, int w, int h);
	/**Restricts rendering to the indicated rectangle on the game window.
	 * Scissoring simply masks away all pixels outside the rectangle.*/
	public abstract void scissor(int x, int y, int w, int h);
	/**Enables or disables the scissor test.
	 * @see #scissor(int, int, int, int)*/
	public abstract void setScissorEnabled(boolean b);
}
