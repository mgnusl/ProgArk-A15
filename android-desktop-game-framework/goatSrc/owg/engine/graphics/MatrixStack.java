package owg.engine.graphics;

import owg.engine.util.V3F;

/**Abstraction for OpenGL matrix stack operations.*/
public interface MatrixStack {
	/**Set to the identity matrix(all 1s)*/
	public abstract void identity();

	/**Set all elements to the given values*/
	public abstract void set(float m0, float m1, float m2, float m3, float m4,
			float m5, float m6, float m7, float m8, float m9, float m10,
			float m11, float m12, float m13, float m14, float m15);

	/**Copy the given array into the matrix*/
	public abstract void set(float[] m);

	/**Ascends to the next matrix.
	 * The new matrix will be a copy of the previous one.
	 * The matrix can be restored with a call to pop().*/
	public abstract void push();

	/**Descends to the previous matrix, corresponding to a previous call to push().*/
	public abstract void pop();

	/**Rotates the current matrix by the given angle in radians around the given axis.*/
	public abstract void rotatef(float radians, float x, float y, float z);

	/**Translates the current matrix by the given amount in euclidian space.*/
	public abstract void translatef(float x, float y, float z);

	/**Scales the matrix by the given amount in euclidian space.*/
	public abstract void scalef(float x, float y, float z);

	/**Multiplies the current matrix by the given matrix.
	 * The multiplier matrix is post-multiplied.*/
	public abstract void mult(float[] multiplier);

	/**
	//glFrustum()
	 */
	public abstract void frustum(float l, float r, float b, float t, float n,
			float f);

	/**
	//gluPerspective
	 */
	public abstract void perspective(float fovY, float aspect, float zNear,
			float zFar);

	/**
	//glOrtho()
	 */
	public abstract void ortho(float l, float r, float b, float t, float n,
			float f);
	/**Get name of matrix*/
	public abstract String toString();
	
	/**gluLookAt*/
	public void lookAt( V3F eyePosition3D,
			V3F center3D, V3F upVector3D);
}