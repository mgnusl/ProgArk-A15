package owg.engine.graphics;

import java.util.Arrays;
/**Provides an interface similar to OpenGL1's immediate mode.<br/>
 * This exists solely for the programmer's convenience and should only be used for debugging or 
 * the occasional, low-poly model where performance is not an issue.*/
public class ImmediateModeWrapper<GLT>
	{
	private static int callCounter;
	
	GLUtil<GLT> glUtil;
	float[] currentColor;
	float[] currentNormal;
	float[] currentTexCoord;
	PolygonModelF<GLT> model;
	public ImmediateModeWrapper(GLUtil<GLT> glUtil)
		{
		this.glUtil = glUtil;
		}
	/**Begins a new primitive rendering sequence. 
	 * The caller must specify which vertex attributes will be used.
	 * The normal, color and texture coordinate states are reset to their default values upon a call to this method.*/
	public void glBegin(Primitive faceMode, 
			boolean useNormal, boolean useColor, boolean useAlpha, boolean useTexCoord, boolean use3DTexCoord) {
		model = glUtil.genModel("ImmediateMode"+(callCounter++), false, faceMode, useNormal, useColor, useAlpha, useTexCoord, use3DTexCoord);
		currentNormal = new float[]{0,0,1};
		currentColor = new float[useAlpha?4:3];
		Arrays.fill(currentColor, 1);
		currentTexCoord = new float[use3DTexCoord?3:2];
	}
	
	public void glNormal3f(float x, float y, float z)
		{
		currentNormal[0] = x;
		currentNormal[1] = y;
		currentNormal[2] = z;
		}
	public void glNormal3fv(float[] v, int offset)
		{
		glNormal3f(v[offset], v[offset+1], v[offset+2]);
		}
	public void glNormal3d(double x, double y, double z)
		{
		glNormal3f((float)x, (float)y, (float)z);
		}
	public void glNormal3dv(double[] v, int offset)
		{
		glNormal3d(v[offset], v[offset+1], v[offset+2]);
		}
	
	public void glColor4f(float r, float g, float b, float a)
		{
		currentColor[0] = r;
		currentColor[1] = g;
		currentColor[2] = b;
		if (currentColor.length > 3)
			currentColor[3] = a;
		}
	public void glColor4fv(float[] v, int offset)
		{
		glColor4f(v[offset], v[offset+1], v[offset+2], v[offset+3]);
		}
	public void glColor4d(double r, double g, double b, double a)
		{
		glColor4f((float)r, (float)g, (float)b, (float)a);
		}
	public void glColor4dv(double[] v, int offset)
		{
		glColor4d(v[offset], v[offset+1], v[offset+2], v[offset+3]);
		}
	public void glColor3f(float r, float g, float b)
		{
		glColor4f(r, g, b, 1);
		}
	public void glColor3fv(float[] v, int offset)
		{
		glColor3f(v[offset], v[offset+1], v[offset+2]);
		}
	public void glColor3d(double r, double g, double b)
		{
		glColor3f((float)r, (float)g, (float)b);
		}
	public void glColor3dv(double[] v, int offset)
		{
		glColor3d(v[offset], v[offset+1], v[offset+2]);
		}
	
	public void glTexCoord3f(float s, float t, float r)
		{
		currentTexCoord[0] = s;
		currentTexCoord[1] = t;
		if(currentTexCoord.length > 2)
			currentTexCoord[2] = r;
		}
	public void glTexCoord3fv(float[] v, int offset)
		{
		glTexCoord3f(v[offset], v[offset+1], v[offset+2]);
		}
	public void glTexCoord3d(double s, double t, double r)
		{
		glTexCoord3f((float)s, (float)t, (float)r);
		}
	public void glTexCoord3dv(double[] v, int offset)
		{
		glTexCoord3d(v[offset], v[offset+1], v[offset+2]);
		}
	public void glTexCoord2f(float s, float t)
		{
		glTexCoord3f(s, t, 0);
		}
	public void glTexCoord2fv(float[] v, int offset)
		{
		glTexCoord2f(v[offset], v[offset+1]);
		}
	public void glTexCoord2d(double s, double t)
		{
		glTexCoord2f((float)s, (float)t);
		}
	public void glTexCoord2dv(double[] v, int offset)
		{
		glTexCoord2d(v[offset], v[offset+1]);
		}
	
	public void glVertex3f(float x, float y, float z)
		{
		model.addIndex(model.getIndex());
		model.addVertex(
				new VertexFloat(new float[]{x,y,z}, currentNormal.clone(), currentColor.clone(), currentTexCoord.clone()));
		}
	public void glVertex3fv(float[] v, int offset)
		{
		glVertex3f(v[offset], v[offset+1], v[offset+2]);
		}
	public void glVertex3d(double x, double y, double z)
		{
		glVertex3f((float)x, (float)y, (float)z);
		}
	public void glVertex3dv(double[] v, int offset)
		{
		glVertex3d(v[offset], v[offset+1], v[offset+2]);
		}
	public void glVertex2f(float x, float y)
		{
		glVertex3f(x, y, 0);
		}
	public void glVertex2fv(float[] v, int offset)
		{
		glVertex2f(v[offset], v[offset+1]);
		}
	public void glVertex2d(double x, double y)
		{
		glVertex2f((float)x, (float)y);
		}
	public void glVertex2dv(double[] v, int offset)
		{
		glVertex2d(v[offset], v[offset+1]);
		}
	/**End the primitive rendering sequence and retrieve the underlying model.
	 * Will not render anything to the current framebuffer.<br/>
	 * It is illegal to call this method prior to 
	 * {@link #glBegin(Primitive, boolean, boolean, boolean, boolean, boolean)},
	 * or to call this method multiple times in succession without starting a new sequence between.*/
	public PolygonModelF<GLT> endAndGetModel()
		{
		model.end(glUtil);
		PolygonModelF<GLT> m = model;
		model = null;
		return m;
		}
	/**The the primitive rendering sequence and render the result to the current framebuffer.<br/>
	 * It is illegal to call this method prior to 
	 * {@link #glBegin(Primitive, boolean, boolean, boolean, boolean, boolean)},
	 * or to call this method multiple times in succession without starting a new sequence between.*/
	public void end()
		{
		model.end(glUtil);
		model.render();
		model.dispose();
		model = null;
		}
	}
