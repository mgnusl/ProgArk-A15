package owg.engine.android.gles1;

import android.opengl.GLES10;
import android.opengl.GLU;
import owg.engine.graphics.*;
import owg.engine.graphics.BlendMode.BlendOp;
import owg.engine.graphics.ColorF.ColorFMutable;
import owg.engine.util.Calc;
import owg.engine.util.NamedInputStream;

import javax.microedition.khronos.opengles.GL10;
import java.io.IOException;
import java.nio.*;

public class GLES1Util extends GLUtil<GL10> {
	/**OpenGL1 modelview matrix stack*/
	private final GLES1MatrixStack modelview = new GLES1MatrixStack(this, "Modelview", GLES10.GL_MODELVIEW);
	/**OpenGL1 projection matrix stack*/
	private final GLES1MatrixStack projection = new GLES1MatrixStack(this, "Projection", GLES10.GL_PROJECTION);
	/**OpenGL1 texture matrix stack*/
	private final GLES1MatrixStack texture = new GLES1MatrixStack(this, "Modelview", GLES10.GL_TEXTURE);
	
	private ColorFMutable currentColor;
	private BlendMode blendMode;
	
	@Override
	public GL10 getGL() {
		return gl;
	}
	@Override
	public boolean checkError(String message) {
		int error = gl.glGetError();
		if(error!=GL10.GL_NO_ERROR) {
			System.err.println(message+": "+GLU.gluErrorString(error));
			new Throwable().printStackTrace();
			return true;
		}
		return false;
	}
	
	public GLES1Util(GL10 gl) {
		super(gl);
		blendMode = BlendMode.NORMAL;
		currentColor = ColorF.WHITE.getMutableCopy();
		setup(gl);
	}
	/**Call when a new OpenGL context has been created.*/
	public void setup(GL10 gl) {
		this.gl = gl;
		gl.glDisable(GL10.GL_CULL_FACE);
		gl.glEnable(GL10.GL_BLEND);
		gl.glShadeModel(GLES10.GL_SMOOTH);
		setBlendMode(blendMode);
		gl.glColor4f(currentColor.c[0], currentColor.c[1], currentColor.c[2], currentColor.c[3]);
	}
	
	@Override
	protected int valueOf(BlendOp op) {
		switch (op) {
		case DST_COLOR:
			return GL10.GL_DST_COLOR;
		case ONE:
			return GL10.GL_ONE;
		case ONE_MINUS_DST_COLOR:
			return GL10.GL_ONE_MINUS_DST_COLOR;
		case ONE_MINUS_SRC_ALPHA:
			return GL10.GL_ONE_MINUS_SRC_ALPHA;
		case ONE_MINUS_SRC_COLOR:
			return GL10.GL_ONE_MINUS_SRC_COLOR;
		case SRC_ALPHA:
			return GL10.GL_SRC_ALPHA;
		case SRC_COLOR:
			return GL10.GL_SRC_COLOR;
		case ZERO:
			return GL10.GL_ZERO;
		}
		throw new RuntimeException("Blend operand: "+op+" not implemented in "+this);
	}
	
	@Override
	protected int valueOf(Primitive p) {
		switch (p) {
		case LINES:
			return GL10.GL_LINES;
		case LINE_LOOP:
			return GL10.GL_LINE_LOOP;
		case LINE_STRIP:
			return GL10.GL_LINE_STRIP;
		case POINTS:
			return GL10.GL_POINTS;
		case TRIANGLES:
			return GL10.GL_TRIANGLES;
		case TRIANGLE_FAN:
			return GL10.GL_TRIANGLE_FAN;
		case TRIANGLE_STRIP:
			return GL10.GL_TRIANGLE_STRIP;
		}
		throw new RuntimeException("Primitive rendering mode: "+p+" not implemented in "+this);
	}


	@Override
	public void clearScreen(ColorF color) {
		gl.glClearColor(color.get(0), color.get(1), color.get(2), color.get(3));
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
	}
	@Override
	public void setColor(ColorF color) {
		currentColor.set(color);
		gl.glColor4f(color.get(0), color.get(1), color.get(2), color.get(3));
	}
	@Override
	public void stepImpl() {
		//NOP
	}
	@Override
	public MatrixStack modelviewMatrix() {
		gl.glMatrixMode(modelview.getMatrixMode());
		return modelview;
	}
	@Override
	public MatrixStack textureMatrix() {
		gl.glMatrixMode(texture.getMatrixMode());
		return texture;
	}
	@Override
	public MatrixStack projectionMatrix() {
		gl.glMatrixMode(projection.getMatrixMode());
		return projection;
	}
	@Override
	public PolygonModelF<GL10> genModel(String name, boolean dynamicDraw,
			Primitive faceMode, boolean useNormal, boolean useColor,
			boolean useAlpha, boolean useTexCoord, boolean use3dTexCoord) {
		return new PolygonModelAndroidVAF(faceMode.value(), useNormal, useColor, useAlpha, useTexCoord, use3dTexCoord);
	}
	@Override
	public FloatBuffer asBuffer(float[] data) {
		//Create a direct buffer with the native endianness.
		FloatBuffer result = ByteBuffer.allocateDirect(data.length*Calc.bytesPerFloat).order(
				ByteOrder.nativeOrder()).asFloatBuffer();
		result.put(data);
		return result;
	}
	@Override
	public IntBuffer asBuffer(int[] data) {
		//Create a direct buffer with the native endianness.
		IntBuffer result = ByteBuffer.allocateDirect(data.length*Calc.bytesPerFloat).order(
				ByteOrder.nativeOrder()).asIntBuffer();
		result.put(data);
		return result;
	}
	@Override
	public ShortBuffer asBuffer(short[] data) {
		//Create a direct buffer with the native endianness.
		ShortBuffer result = ByteBuffer.allocateDirect(data.length*Calc.bytesPerShort).order(
				ByteOrder.nativeOrder()).asShortBuffer();
		result.put(data);
		return result;
	}
	@Override
	public ByteBuffer asBuffer(byte[] data) {
		//Create a direct byte buffer.
		ByteBuffer result = ByteBuffer.allocateDirect(data.length);
		result.put(data);
		return result;
	}
	@Override
	public void setBlendMode(BlendMode bm) {
		this.blendMode = bm;
		gl.glBlendFunc(bm.src.value(), bm.dst.value());
	}

	@Override
	protected Sprite2D loadSprite2D(String simpleName, NamedInputStream file, int numXFrames, int numYFrames) throws IOException {
		return new Sprite2DAndroidGLES1(this, simpleName, file, numXFrames, numYFrames);
	}
	@Override
	public void enableTexture2D(int texture) {
		gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glBindTexture(GL10.GL_TEXTURE_2D, texture);
	}
	@Override
	public void disableTexture2D() {
		gl.glDisable(GL10.GL_TEXTURE_2D);
		gl.glBindTexture(GL10.GL_TEXTURE_2D, 0);
	}
	@Override
	public void setLineSmoothing(boolean b) {
		if(b)
			gl.glEnable(GL10.GL_LINE_SMOOTH);
		else
			gl.glDisable(GL10.GL_LINE_SMOOTH);
	}
	@Override
	public void setLineWidth(float w) {
		gl.glLineWidth(w);
	}
	
	@Override
	public void viewport(int x, int y, int w, int h) {
		gl.glViewport(x, y, w, h);
	}
    
	@Override
	public void scissor(int x, int y, int w, int h) {
		gl.glScissor(x, y, w, h);
	}

	@Override
	public void setScissorEnabled(boolean b) {
		if(b)
			gl.glEnable(GL10.GL_SCISSOR_TEST);
		else
			gl.glDisable(GL10.GL_SCISSOR_TEST);
	}

}
