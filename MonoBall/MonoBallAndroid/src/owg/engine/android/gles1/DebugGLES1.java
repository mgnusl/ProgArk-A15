package owg.engine.android.gles1;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLU;

public class DebugGLES1 implements GL10 {
	GL10 gl;

	public DebugGLES1(GL10 gl) {
		this.gl = gl;
		checkError();
	}

	private void checkError() {
		int error = gl.glGetError();
		if(error!= GL_NO_ERROR) {
			System.err.println("DebugGLES1: "+GLU.gluErrorString(error));
			new Throwable().printStackTrace();
		}
	}

	public void glActiveTexture(int texture) {
		gl.glActiveTexture(texture);
		checkError();
	}

	public void glAlphaFunc(int func, float ref) {
		gl.glAlphaFunc(func, ref);
		checkError();
	}

	public void glAlphaFuncx(int func, int ref) {
		gl.glAlphaFuncx(func, ref);
		checkError();
	}

	public void glBindTexture(int target, int texture) {
		gl.glBindTexture(target, texture);
		checkError();
	}

	public void glBlendFunc(int sfactor, int dfactor) {
		gl.glBlendFunc(sfactor, dfactor);
		checkError();
	}

	public void glClear(int mask) {
		gl.glClear(mask);
		checkError();
	}

	public void glClearColor(float red, float green, float blue, float alpha) {
		gl.glClearColor(red, green, blue, alpha);
		checkError();
	}

	public void glClearColorx(int red, int green, int blue, int alpha) {
		gl.glClearColorx(red, green, blue, alpha);
		checkError();
	}

	public void glClearDepthf(float depth) {
		gl.glClearDepthf(depth);
		checkError();
	}

	public void glClearDepthx(int depth) {
		gl.glClearDepthx(depth);
		checkError();
	}

	public void glClearStencil(int s) {
		gl.glClearStencil(s);
		checkError();
	}

	public void glClientActiveTexture(int texture) {
		gl.glClientActiveTexture(texture);
		checkError();
	}

	public void glColor4f(float red, float green, float blue, float alpha) {
		gl.glColor4f(red, green, blue, alpha);
		checkError();
	}

	public void glColor4x(int red, int green, int blue, int alpha) {
		gl.glColor4x(red, green, blue, alpha);
		checkError();
	}

	public void glColorMask(boolean red, boolean green, boolean blue,
			boolean alpha) {
		gl.glColorMask(red, green, blue, alpha);
		checkError();
	}

	public void glColorPointer(int size, int type, int stride, Buffer pointer) {
		gl.glColorPointer(size, type, stride, pointer);
		checkError();
	}

	public void glCompressedTexImage2D(int target, int level,
			int internalformat, int width, int height, int border,
			int imageSize, Buffer data) {
		gl.glCompressedTexImage2D(target, level, internalformat, width, height,
				border, imageSize, data);
		checkError();
	}

	public void glCompressedTexSubImage2D(int target, int level, int xoffset,
			int yoffset, int width, int height, int format, int imageSize,
			Buffer data) {
		gl.glCompressedTexSubImage2D(target, level, xoffset, yoffset, width,
				height, format, imageSize, data);
		checkError();
	}

	public void glCopyTexImage2D(int target, int level, int internalformat,
			int x, int y, int width, int height, int border) {
		gl.glCopyTexImage2D(target, level, internalformat, x, y, width, height,
				border);
		checkError();
	}

	public void glCopyTexSubImage2D(int target, int level, int xoffset,
			int yoffset, int x, int y, int width, int height) {
		gl.glCopyTexSubImage2D(target, level, xoffset, yoffset, x, y, width,
				height);
		checkError();
	}

	public void glCullFace(int mode) {
		gl.glCullFace(mode);
		checkError();
	}

	public void glDeleteTextures(int n, int[] textures, int offset) {
		gl.glDeleteTextures(n, textures, offset);
		checkError();
	}

	public void glDeleteTextures(int n, IntBuffer textures) {
		gl.glDeleteTextures(n, textures);
		checkError();
	}

	public void glDepthFunc(int func) {
		gl.glDepthFunc(func);
		checkError();
	}

	public void glDepthMask(boolean flag) {
		gl.glDepthMask(flag);
		checkError();
	}

	public void glDepthRangef(float zNear, float zFar) {
		gl.glDepthRangef(zNear, zFar);
		checkError();
	}

	public void glDepthRangex(int zNear, int zFar) {
		gl.glDepthRangex(zNear, zFar);
		checkError();
	}

	public void glDisable(int cap) {
		gl.glDisable(cap);
		checkError();
	}

	public void glDisableClientState(int array) {
		gl.glDisableClientState(array);
		checkError();
	}

	public void glDrawArrays(int mode, int first, int count) {
		gl.glDrawArrays(mode, first, count);
		checkError();
	}

	public void glDrawElements(int mode, int count, int type, Buffer indices) {
		gl.glDrawElements(mode, count, type, indices);
		checkError();
	}

	public void glEnable(int cap) {
		gl.glEnable(cap);
		checkError();
	}

	public void glEnableClientState(int array) {
		gl.glEnableClientState(array);
		checkError();
	}

	public void glFinish() {
		gl.glFinish();
		checkError();
	}

	public void glFlush() {
		gl.glFlush();
		checkError();
	}

	public void glFogf(int pname, float param) {
		gl.glFogf(pname, param);
		checkError();
	}

	public void glFogfv(int pname, float[] params, int offset) {
		gl.glFogfv(pname, params, offset);
		checkError();
	}

	public void glFogfv(int pname, FloatBuffer params) {
		gl.glFogfv(pname, params);
		checkError();
	}

	public void glFogx(int pname, int param) {
		gl.glFogx(pname, param);
		checkError();
	}

	public void glFogxv(int pname, int[] params, int offset) {
		gl.glFogxv(pname, params, offset);
		checkError();
	}

	public void glFogxv(int pname, IntBuffer params) {
		gl.glFogxv(pname, params);
		checkError();
	}

	public void glFrontFace(int mode) {
		gl.glFrontFace(mode);
		checkError();
	}

	public void glFrustumf(float left, float right, float bottom, float top,
			float zNear, float zFar) {
		gl.glFrustumf(left, right, bottom, top, zNear, zFar);
		checkError();
	}

	public void glFrustumx(int left, int right, int bottom, int top, int zNear,
			int zFar) {
		gl.glFrustumx(left, right, bottom, top, zNear, zFar);
		checkError();
	}

	public void glGenTextures(int n, int[] textures, int offset) {
		gl.glGenTextures(n, textures, offset);
		checkError();
	}

	public void glGenTextures(int n, IntBuffer textures) {
		gl.glGenTextures(n, textures);
		checkError();
	}

	public int glGetError() {
		int err = gl.glGetError();
		checkError();
		return err;
	}

	public void glGetIntegerv(int pname, int[] params, int offset) {
		gl.glGetIntegerv(pname, params, offset);
		checkError();
	}

	public void glGetIntegerv(int pname, IntBuffer params) {
		gl.glGetIntegerv(pname, params);
		checkError();
	}

	public String glGetString(int name) {
		String str =  gl.glGetString(name);
		checkError();
		return str;
	}

	public void glHint(int target, int mode) {
		gl.glHint(target, mode);
		checkError();
	}

	public void glLightModelf(int pname, float param) {
		gl.glLightModelf(pname, param);
		checkError();
	}

	public void glLightModelfv(int pname, float[] params, int offset) {
		gl.glLightModelfv(pname, params, offset);
		checkError();
	}

	public void glLightModelfv(int pname, FloatBuffer params) {
		gl.glLightModelfv(pname, params);
		checkError();
	}

	public void glLightModelx(int pname, int param) {
		gl.glLightModelx(pname, param);
		checkError();
	}

	public void glLightModelxv(int pname, int[] params, int offset) {
		gl.glLightModelxv(pname, params, offset);
		checkError();
	}

	public void glLightModelxv(int pname, IntBuffer params) {
		gl.glLightModelxv(pname, params);
		checkError();
	}

	public void glLightf(int light, int pname, float param) {
		gl.glLightf(light, pname, param);
		checkError();
	}

	public void glLightfv(int light, int pname, float[] params, int offset) {
		gl.glLightfv(light, pname, params, offset);
		checkError();
	}

	public void glLightfv(int light, int pname, FloatBuffer params) {
		gl.glLightfv(light, pname, params);
		checkError();
	}

	public void glLightx(int light, int pname, int param) {
		gl.glLightx(light, pname, param);
		checkError();
	}

	public void glLightxv(int light, int pname, int[] params, int offset) {
		gl.glLightxv(light, pname, params, offset);
		checkError();
	}

	public void glLightxv(int light, int pname, IntBuffer params) {
		gl.glLightxv(light, pname, params);
		checkError();
	}

	public void glLineWidth(float width) {
		gl.glLineWidth(width);
		checkError();
	}

	public void glLineWidthx(int width) {
		gl.glLineWidthx(width);
		checkError();
	}

	public void glLoadIdentity() {
		gl.glLoadIdentity();
		checkError();
	}

	public void glLoadMatrixf(float[] m, int offset) {
		gl.glLoadMatrixf(m, offset);
		checkError();
	}

	public void glLoadMatrixf(FloatBuffer m) {
		gl.glLoadMatrixf(m);
		checkError();
	}

	public void glLoadMatrixx(int[] m, int offset) {
		gl.glLoadMatrixx(m, offset);
		checkError();
	}

	public void glLoadMatrixx(IntBuffer m) {
		gl.glLoadMatrixx(m);
		checkError();
	}

	public void glLogicOp(int opcode) {
		gl.glLogicOp(opcode);
		checkError();
	}

	public void glMaterialf(int face, int pname, float param) {
		gl.glMaterialf(face, pname, param);
		checkError();
	}

	public void glMaterialfv(int face, int pname, float[] params, int offset) {
		gl.glMaterialfv(face, pname, params, offset);
		checkError();
	}

	public void glMaterialfv(int face, int pname, FloatBuffer params) {
		gl.glMaterialfv(face, pname, params);
		checkError();
	}

	public void glMaterialx(int face, int pname, int param) {
		gl.glMaterialx(face, pname, param);
		checkError();
	}

	public void glMaterialxv(int face, int pname, int[] params, int offset) {
		gl.glMaterialxv(face, pname, params, offset);
		checkError();
	}

	public void glMaterialxv(int face, int pname, IntBuffer params) {
		gl.glMaterialxv(face, pname, params);
		checkError();
	}

	public void glMatrixMode(int mode) {
		gl.glMatrixMode(mode);
		checkError();
	}

	public void glMultMatrixf(float[] m, int offset) {
		gl.glMultMatrixf(m, offset);
		checkError();
	}

	public void glMultMatrixf(FloatBuffer m) {
		gl.glMultMatrixf(m);
		checkError();
	}

	public void glMultMatrixx(int[] m, int offset) {
		gl.glMultMatrixx(m, offset);
		checkError();
	}

	public void glMultMatrixx(IntBuffer m) {
		gl.glMultMatrixx(m);
		checkError();
	}

	public void glMultiTexCoord4f(int target, float s, float t, float r, float q) {
		gl.glMultiTexCoord4f(target, s, t, r, q);
		checkError();
	}

	public void glMultiTexCoord4x(int target, int s, int t, int r, int q) {
		gl.glMultiTexCoord4x(target, s, t, r, q);
		checkError();
	}

	public void glNormal3f(float nx, float ny, float nz) {
		gl.glNormal3f(nx, ny, nz);
		checkError();
	}

	public void glNormal3x(int nx, int ny, int nz) {
		gl.glNormal3x(nx, ny, nz);
		checkError();
	}

	public void glNormalPointer(int type, int stride, Buffer pointer) {
		gl.glNormalPointer(type, stride, pointer);
		checkError();
	}

	public void glOrthof(float left, float right, float bottom, float top,
			float zNear, float zFar) {
		gl.glOrthof(left, right, bottom, top, zNear, zFar);
		checkError();
	}

	public void glOrthox(int left, int right, int bottom, int top, int zNear,
			int zFar) {
		gl.glOrthox(left, right, bottom, top, zNear, zFar);
		checkError();
	}

	public void glPixelStorei(int pname, int param) {
		gl.glPixelStorei(pname, param);
		checkError();
	}

	public void glPointSize(float size) {
		gl.glPointSize(size);
		checkError();
	}

	public void glPointSizex(int size) {
		gl.glPointSizex(size);
		checkError();
	}

	public void glPolygonOffset(float factor, float units) {
		gl.glPolygonOffset(factor, units);
		checkError();
	}

	public void glPolygonOffsetx(int factor, int units) {
		gl.glPolygonOffsetx(factor, units);
		checkError();
	}

	public void glPopMatrix() {
		gl.glPopMatrix();
		checkError();
	}

	public void glPushMatrix() {
		gl.glPushMatrix();
		checkError();
	}

	public void glReadPixels(int x, int y, int width, int height, int format,
			int type, Buffer pixels) {
		gl.glReadPixels(x, y, width, height, format, type, pixels);
		checkError();
	}

	public void glRotatef(float angle, float x, float y, float z) {
		gl.glRotatef(angle, x, y, z);
		checkError();
	}

	public void glRotatex(int angle, int x, int y, int z) {
		gl.glRotatex(angle, x, y, z);
		checkError();
	}

	public void glSampleCoverage(float value, boolean invert) {
		gl.glSampleCoverage(value, invert);
		checkError();
	}

	public void glSampleCoveragex(int value, boolean invert) {
		gl.glSampleCoveragex(value, invert);
		checkError();
	}

	public void glScalef(float x, float y, float z) {
		gl.glScalef(x, y, z);
		checkError();
	}

	public void glScalex(int x, int y, int z) {
		gl.glScalex(x, y, z);
		checkError();
	}

	public void glScissor(int x, int y, int width, int height) {
		gl.glScissor(x, y, width, height);
		checkError();
	}

	public void glShadeModel(int mode) {
		gl.glShadeModel(mode);
		checkError();
	}

	public void glStencilFunc(int func, int ref, int mask) {
		gl.glStencilFunc(func, ref, mask);
		checkError();
	}

	public void glStencilMask(int mask) {
		gl.glStencilMask(mask);
		checkError();
	}

	public void glStencilOp(int fail, int zfail, int zpass) {
		gl.glStencilOp(fail, zfail, zpass);
		checkError();
	}

	public void glTexCoordPointer(int size, int type, int stride, Buffer pointer) {
		gl.glTexCoordPointer(size, type, stride, pointer);
		checkError();
	}

	public void glTexEnvf(int target, int pname, float param) {
		gl.glTexEnvf(target, pname, param);
		checkError();
	}

	public void glTexEnvfv(int target, int pname, float[] params, int offset) {
		gl.glTexEnvfv(target, pname, params, offset);
		checkError();
	}

	public void glTexEnvfv(int target, int pname, FloatBuffer params) {
		gl.glTexEnvfv(target, pname, params);
		checkError();
	}

	public void glTexEnvx(int target, int pname, int param) {
		gl.glTexEnvx(target, pname, param);
		checkError();
	}

	public void glTexEnvxv(int target, int pname, int[] params, int offset) {
		gl.glTexEnvxv(target, pname, params, offset);
		checkError();
	}

	public void glTexEnvxv(int target, int pname, IntBuffer params) {
		gl.glTexEnvxv(target, pname, params);
		checkError();
	}

	public void glTexImage2D(int target, int level, int internalformat,
			int width, int height, int border, int format, int type,
			Buffer pixels) {
		gl.glTexImage2D(target, level, internalformat, width, height, border,
				format, type, pixels);
		checkError();
	}

	public void glTexParameterf(int target, int pname, float param) {
		gl.glTexParameterf(target, pname, param);
		checkError();
	}

	public void glTexParameterx(int target, int pname, int param) {
		gl.glTexParameterx(target, pname, param);
		checkError();
	}

	public void glTexSubImage2D(int target, int level, int xoffset,
			int yoffset, int width, int height, int format, int type,
			Buffer pixels) {
		gl.glTexSubImage2D(target, level, xoffset, yoffset, width, height,
				format, type, pixels);
		checkError();
	}

	public void glTranslatef(float x, float y, float z) {
		gl.glTranslatef(x, y, z);
		checkError();
	}

	public void glTranslatex(int x, int y, int z) {
		gl.glTranslatex(x, y, z);
		checkError();
	}

	public void glVertexPointer(int size, int type, int stride, Buffer pointer) {
		gl.glVertexPointer(size, type, stride, pointer);
		checkError();
	}

	public void glViewport(int x, int y, int width, int height) {
		gl.glViewport(x, y, width, height);
		checkError();
	}
}
