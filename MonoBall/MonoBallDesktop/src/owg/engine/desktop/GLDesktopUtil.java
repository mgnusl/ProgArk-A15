package owg.engine.desktop;

import java.io.IOException;

import javax.media.opengl.GL;
import owg.engine.graphics.BlendMode;
import owg.engine.graphics.GLUtil;
import owg.engine.graphics.Primitive;
import owg.engine.graphics.Sprite2D;
import owg.engine.graphics.BlendMode.BlendOp;
import owg.engine.util.NamedInputStream;
/**Implements common functionality for all desktop OpenGLs*/
public abstract class GLDesktopUtil<GLT extends GL> extends GLUtil<GLT> {
	
	protected GLDesktopUtil(GLT gl) {
		super(gl);
		gl.glDisable(GL.GL_CULL_FACE);
		gl.glEnable(GL.GL_BLEND);
		setBlendMode(BlendMode.NORMAL);
	}
	
	@Override
	protected int valueOf(BlendOp op) {
		switch (op) {
		case DST_COLOR:
			return GL.GL_DST_COLOR;
		case ONE:
			return GL.GL_ONE;
		case ONE_MINUS_DST_COLOR:
			return GL.GL_ONE_MINUS_DST_COLOR;
		case ONE_MINUS_SRC_ALPHA:
			return GL.GL_ONE_MINUS_SRC_ALPHA;
		case ONE_MINUS_SRC_COLOR:
			return GL.GL_ONE_MINUS_SRC_COLOR;
		case SRC_ALPHA:
			return GL.GL_SRC_ALPHA;
		case SRC_COLOR:
			return GL.GL_SRC_COLOR;
		case ZERO:
			return GL.GL_ZERO;
		}
		throw new RuntimeException("Blend operand: "+op+" not implemented in "+this);
	}
	
	@Override
	protected int valueOf(Primitive p) {
		switch (p) {
		case LINES:
			return GL.GL_LINES;
		case LINE_LOOP:
			return GL.GL_LINE_LOOP;
		case LINE_STRIP:
			return GL.GL_LINE_STRIP;
		case POINTS:
			return GL.GL_POINTS;
		case TRIANGLES:
			return GL.GL_TRIANGLES;
		case TRIANGLE_FAN:
			return GL.GL_TRIANGLE_FAN;
		case TRIANGLE_STRIP:
			return GL.GL_TRIANGLE_STRIP;
		}
		throw new RuntimeException("Primitive rendering mode: "+p+" not implemented in "+this);
	}
	
	@Override
	public void setBlendMode(BlendMode bm) {
		gl.glBlendFunc(bm.src.value(), bm.dst.value());
	}
	@Override
	protected Sprite2D loadSprite2D(String simpleName, NamedInputStream file, int numXFrames, int numYFrames) throws IOException {
		return new Sprite2DDesktop(this, simpleName, file, numXFrames, numYFrames);
	}
	
	@Override
	public void setLineSmoothing(boolean b) {
		if(b)
			gl.glEnable(GL.GL_LINE_SMOOTH);
		else
			gl.glDisable(GL.GL_LINE_SMOOTH);
	}
	@Override
	public void setLineWidth(float w) {
		gl.glLineWidth(w);
	}
}
