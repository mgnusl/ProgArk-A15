package owg.engine.android.gles1;

import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLU;

import owg.engine.graphics.MatrixStack;
import owg.engine.util.Calc;
import owg.engine.util.V3F;

public class GLES1MatrixStack implements MatrixStack {
	private GLES1Util glUtil;
	private int matrixMode;
	private String name;

	public GLES1MatrixStack(GLES1Util glUtil, String name, int matrixMode) {
		this.glUtil = glUtil;
		this.name = name;
		this.matrixMode = matrixMode;
	}
	
	private final GL10 gl() {
		return glUtil.getGL();
	}
	
	@Override
	public void identity() {
		gl().glLoadIdentity();
	}

	@Override
	public void set(float m0, float m1, float m2, float m3, float m4, float m5,
			float m6, float m7, float m8, float m9, float m10, float m11,
			float m12, float m13, float m14, float m15) {
		gl().glLoadMatrixf(new float[]{m0,m1,m2,m3,m4,m5,m6,m7,m8,m9,m10,m11,m12,m13,m14,m15}, 0);
	}

	@Override
	public void set(float[] m) {
		gl().glLoadMatrixf(m, 0);
	}

	@Override
	public void push() {
		gl().glPushMatrix();
	}

	@Override
	public void pop() {
		gl().glPopMatrix();
	}

	@Override
	public void rotatef(float radians, float x, float y, float z) {
		gl().glRotatef(radians*Calc.toDeg, x, y, z);
	}

	@Override
	public void translatef(float x, float y, float z) {
		gl().glTranslatef(x, y, z);
	}

	@Override
	public void scalef(float x, float y, float z) {
		gl().glScalef(x, y, z);
	}

	@Override
	public void mult(float[] multiplier) {
		gl().glMultMatrixf(multiplier, 0);
	}

	@Override
	public void frustum(float l, float r, float b, float t, float n, float f) {
		gl().glFrustumf(l, r, b, t, n, f);
	}

	@Override
	public void perspective(float fovY, float aspect, float zNear, float zFar) {
		GLU.gluPerspective(gl(), fovY, aspect, zNear, zFar);
	}

	@Override
	public void ortho(float l, float r, float b, float t, float n, float f) {
		gl().glOrthof(l, r, b, t, n, f);
	}

	public int getMatrixMode() {
		return matrixMode;
	}
	
	@Override
	public String toString() {
		return name;
	}

	@Override
	public void lookAt(V3F eye, V3F center, V3F up) {
		GLU.gluLookAt(glUtil.getGL(), 
				eye.x(), eye.y(), eye.z(), 
				center.x(), center.y(), center.z(), 
				up.x(), up.y(), up.z());
	}

}
