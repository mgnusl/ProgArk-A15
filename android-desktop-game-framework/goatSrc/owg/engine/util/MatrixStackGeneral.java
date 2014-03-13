package owg.engine.util;

import java.util.Arrays;

import owg.engine.graphics.MatrixStack;

/**
 * Adapted from: http://www.songho.ca/opengl/gl_matrix.html<br/>
 * Original implementations by Song Ho Ahn<br/>
 * Heavily edited by the Oddwarg<br/>
 * <br/>
 * This class provides reimplementations of the algortihms used by the deprecated OpenGL matrix stacks.<br/>
 * */

public class MatrixStackGeneral implements MatrixStack
{
	private final int matrixSize = 16;
	private int depth, maxDepth;
	private float[][] matrix;
	private String name;


	public MatrixStackGeneral(int maxDepth, String name)
	{
		this.maxDepth = maxDepth;
		this.depth = 0;
		this.matrix = new float[maxDepth][matrixSize];
		identity();
	}
	/* (non-Javadoc)
	 * @see owg.engine.desktop.gl3.MatrixStack#identity()
	 */
	@Override
	public void identity()
	{
		set(	1,0,0,0,
				0,1,0,0,
				0,0,1,0,
				0,0,0,1);
	}
	/* (non-Javadoc)
	 * @see owg.engine.desktop.gl3.MatrixStack#set(float, float, float, float, float, float, float, float, float, float, float, float, float, float, float, float)
	 */
	@Override
	public void set(float m0 , float m1 , float m2 , float m3 , 
			float m4 , float m5 , float m6 , float m7 , 
			float m8 , float m9 , float m10, float m11, 
			float m12, float m13, float m14, float m15)
	{
		matrix[depth][ 0] = m0;
		matrix[depth][ 1] = m1;
		matrix[depth][ 2] = m2;
		matrix[depth][ 3] = m3;
		matrix[depth][ 4] = m4;
		matrix[depth][ 5] = m5;
		matrix[depth][ 6] = m6;
		matrix[depth][ 7] = m7;
		matrix[depth][ 8] = m8;
		matrix[depth][ 9] = m9;
		matrix[depth][10] = m10;
		matrix[depth][11] = m11;
		matrix[depth][12] = m12;
		matrix[depth][13] = m13;
		matrix[depth][14] = m14;
		matrix[depth][15] = m15;
	}
	/* (non-Javadoc)
	 * @see owg.engine.desktop.gl3.MatrixStack#set(float[])
	 */
	@Override
	public void set(float[] m)
	{
		System.arraycopy(m, 0, matrix[depth], 0, matrixSize);
	}
	/* (non-Javadoc)
	 * @see owg.engine.desktop.gl3.MatrixStack#push()
	 */
	@Override
	public void push()
	{
		depth++;
		if(depth >= maxDepth)
			throw new IllegalStateException("Matrix stack overflow: "+this);
		System.arraycopy(matrix[depth-1], 0, matrix[depth], 0, matrixSize);
	}
	/* (non-Javadoc)
	 * @see owg.engine.desktop.gl3.MatrixStack#pop()
	 */
	@Override
	public void pop()
	{
		depth--;
		if(depth < 0)
			throw new IllegalStateException("Matrix stack underflow: "+this);
	}
	/* (non-Javadoc)
	 * @see owg.engine.desktop.gl3.MatrixStack#rotatef(float, float, float, float)
	 */
	@Override
	public void rotatef(float radians, float x, float y, float z)
	{
		mult(makeEulerMatrix4x4f(radians, x, y, z));
	}
	/* (non-Javadoc)
	 * @see owg.engine.desktop.gl3.MatrixStack#translatef(float, float, float)
	 */
	@Override
	public void translatef(float x, float y, float z)
	{
		float[] m = {
				1, 0, 0, x,
				0, 1, 0, y,
				0, 0, 1, z,
				0, 0, 0, 1
		};
		mult(m);
	}
	/* (non-Javadoc)
	 * @see owg.engine.desktop.gl3.MatrixStack#scalef(float, float, float)
	 */
	@Override
	public void scalef(float x, float y, float z)
	{
		float[] m = {
				x, 0, 0, 0,
				0, y, 0, 0,
				0, 0, z, 0,
				0, 0, 0, 1
		};
		mult(m);
	}
	/* (non-Javadoc)
	 * @see owg.engine.desktop.gl3.MatrixStack#mult(float[])
	 */
	@Override
	public void mult(float[] multiplier)
	{
		matrix[depth] = Calc.multMatrixFlat4x4(matrix[depth], multiplier);
	}

	/**Get the inverse matrix.*/
	public float[] getInverse()
	{
		float[] m = matrix[depth].clone();
		if(this.matrixSize == 4)
			invert2x2f(m);
		else if(this.matrixSize == 9)
			invert3x3f(m);
		else if(this.matrixSize == 16)
			invert4x4f(m);
		return m;
	}
	/**gluLookAt*/
	public void lookAt( V3F eyePosition3D,
			V3F center3D, V3F upVector3D)
	{
		V3F forward, side, up;
		float[] multiplier = new float[16];
		//------------------
		forward = new V3F(	center3D.p[0] - eyePosition3D.p[0],
				center3D.p[1] - eyePosition3D.p[1],
				center3D.p[2] - eyePosition3D.p[2]);
		forward.normalize();
		//------------------
		//Side = forward x up
		side = forward.createCross(upVector3D);
		side.normalize();
		//------------------
		//Recompute up as: up = side x forward
		up = side.createCross(forward);
		//------------------
		multiplier[0] = side.p[0];
		multiplier[4] = side.p[1];
		multiplier[8] = side.p[2];
		multiplier[12] = 0.0f;
		//------------------
		multiplier[1] = up.p[0];
		multiplier[5] = up.p[1];
		multiplier[9] = up.p[2];
		multiplier[13] = 0.0f;
		//------------------
		multiplier[2] = -forward.p[0];
		multiplier[6] = -forward.p[1];
		multiplier[10] = -forward.p[2];
		multiplier[14] = 0.0f;
		//------------------
		multiplier[3] = multiplier[7] = multiplier[11] = 0.0f;
		multiplier[15] = 1.0f;
		//------------------
		transpose4x4f(multiplier);
		mult(multiplier);
		translatef(-eyePosition3D.p[0], -eyePosition3D.p[1], -eyePosition3D.p[2]);
	}


	/* (non-Javadoc)
	 * @see owg.engine.desktop.gl3.MatrixStack#frustum(float, float, float, float, float, float)
	 */
	@Override
	public void frustum(float l, float r, float b, float t, float n, float f)
	{
		float[] mat = matrix[depth];
		mat[0]  = 2 * n / (r - l);
		mat[2]  = (r + l) / (r - l);
		mat[5]  = 2 * n / (t - b);
		mat[6]  = (t + b) / (t - b);
		mat[10] = -(f + n) / (f - n);
		mat[11] = -(2 * f * n) / (f - n);
		mat[14] = -1;
		mat[15] = 0;
	}

	/* (non-Javadoc)
	 * @see owg.engine.desktop.gl3.MatrixStack#perspective(float, float, float, float)
	 */
	@Override
	public void perspective(float fovY, float aspect, float zNear, float zFar)
	{
		float height = 2*zNear/(float)Math.tan(fovY/2);	// tangent of half fovY

		float[] mat = matrix[depth];
		mat[0]  = height/aspect;
		mat[2]  = 0;
		mat[5]  = height;
		mat[6]  = 0;
		mat[10] = (zFar + zNear) / (zNear - zFar);
		mat[11] = (2 * zFar * zNear) / (zNear - zFar);
		mat[14] = -1;
		mat[15] = 0;
	}

	/* (non-Javadoc)
	 * @see owg.engine.desktop.gl3.MatrixStack#ortho(float, float, float, float, float, float)
	 */
	@Override
	public void ortho(float l, float r, float b, float t, float n, float f)
	{
		float[] mat = matrix[depth];
		mat[0]  = 2 / (r - l);
		mat[5]  = 2 / (t - b);
		mat[10] = -2 / (f - n);
		mat[15] = 1;
		
		mat[3]  = -(r + l) / (r - l);
		mat[7]  = -(t + b) / (t - b);
		mat[11] = -(f + n) / (f - n);
	}
	//0 1 2 3
	//4 5 6 7
	//8 9 1011
	//12131415

	@Override
	public String toString()
	{
		return name;
	}

	/**Generate a rotation matrix*/
	public static float[] makeEulerMatrix4x4f(float f, float rx, float ry, float rz)
	{
		float c = (float)Math.cos(f);
		float s = (float)Math.sin(f);
		return new float[]{
				c+rx*rx*(1-c)		,rx*ry*(1-c)-rz*s	,rx*rz*(1-c)+ry*s , 0,
				ry*rx*(1-c)+rz*s	,c+ry*ry*(1-c)		,ry*rz*(1-c)-rx*s	, 0,
				rz*rx*(1-c)-ry*s	,rz*ry*(1-c)+rx*s	,c+rz*rz*(1-c)		, 0,
				0						,0						,0						, 1};
	}

	/**
	//inverse of 2x2 matrix
	//If cannot find inverse, set identity matrix
	 */
	public static void invert2x2f(float[] m)
	{
		float determinant = m[0] * m[3] - m[1] * m[2];
		if(Math.abs(determinant) <= 0.00001f)
		{
			Arrays.fill(m, 1.0f);
		}

		float tmp = m[0];   // copy the first element
		float invDeterminant = 1.0f / determinant;
		m[0] =  invDeterminant * m[3];
		m[1] = -invDeterminant * m[1];
		m[2] = -invDeterminant * m[2];
		m[3] =  invDeterminant * tmp;
	}

	/**
	//inverse 3x3 matrix
	//If cannot find inverse, set identity matrix
	 */
	public static void invert3x3f(float[] m)
	{
		float determinant, invDeterminant;
		float[] tmp = new float[9];

		tmp[0] = m[4] * m[8] - m[5] * m[7];
		tmp[1] = m[2] * m[7] - m[1] * m[8];
		tmp[2] = m[1] * m[5] - m[2] * m[4];
		tmp[3] = m[5] * m[6] - m[3] * m[8];
		tmp[4] = m[0] * m[8] - m[2] * m[6];
		tmp[5] = m[2] * m[3] - m[0] * m[5];
		tmp[6] = m[3] * m[7] - m[4] * m[6];
		tmp[7] = m[1] * m[6] - m[0] * m[7];
		tmp[8] = m[0] * m[4] - m[1] * m[3];

		// check determinant if it is 0
		determinant = m[0] * tmp[0] + m[1] * tmp[3] + m[2] * tmp[6];
		if(Math.abs(determinant) <= 0.00001f)
		{
			Arrays.fill(m, 1.0f); // cannot inverse, make it identity matrix
		}

		// divide by the determinant
		invDeterminant = 1.0f / determinant;
		m[0] = invDeterminant * tmp[0];
		m[1] = invDeterminant * tmp[1];
		m[2] = invDeterminant * tmp[2];
		m[3] = invDeterminant * tmp[3];
		m[4] = invDeterminant * tmp[4];
		m[5] = invDeterminant * tmp[5];
		m[6] = invDeterminant * tmp[6];
		m[7] = invDeterminant * tmp[7];
		m[8] = invDeterminant * tmp[8];
	}


	/**
	//inverse 4x4 matrix
	 */
	public static void invert4x4f(float[] m)
	{
		// If the 4th row is [0,0,0,1] then it is affine matrix and
		// it has no projective transformation.
		if(m[12] == 0 && m[13] == 0 && m[14] == 0 && m[15] == 1)
			invertAffine4x4f(m);
		else
		{
			invertGeneral4x4f(m);
		}
	}
	/**
	//Invert a flat 4x4 affine transformation matrix
	 */
	private static void invertAffine4x4f(float[] m)
	{
		// R^-1
		float[] r = {m[0],m[1],m[2], m[4],m[5],m[6], m[8],m[9],m[10]};
		invert3x3f( r );
		m[0] = r[0];  m[1] = r[1];  m[2] = r[2];
		m[4] = r[3];  m[5] = r[4];  m[6] = r[5];
		m[8] = r[6];  m[9] = r[7];  m[10]= r[8];

		// -R^-1 * T
		float x = m[3];
		float y = m[7];
		float z = m[11];
		m[3]  = -(r[0] * x + r[1] * y + r[2] * z);
		m[7]  = -(r[3] * x + r[4] * y + r[5] * z);
		m[11] = -(r[6] * x + r[7] * y + r[8] * z);

		// last row should be unchanged (0,0,0,1)
		//m[12] = m[13] = m[14] = 0.0f;
		//m[15] = 1.0f;
	}

	/**
	//compute the inverse of a general 4x4 matrix using Cramer's Rule
	//If cannot find inverse, return indentity matrix
	//M^-1 = adj(M) / det(M)
	 */
	private static void invertGeneral4x4f(float[] m) {
		// get cofactors of minor matrices
		float cofactor0 = getCofactor(m[5],m[6],m[7], m[9],m[10],m[11], m[13],m[14],m[15]);
		float cofactor1 = getCofactor(m[4],m[6],m[7], m[8],m[10],m[11], m[12],m[14],m[15]);
		float cofactor2 = getCofactor(m[4],m[5],m[7], m[8],m[9], m[11], m[12],m[13],m[15]);
		float cofactor3 = getCofactor(m[4],m[5],m[6], m[8],m[9], m[10], m[12],m[13],m[14]);

		// get determinant
		float determinant = m[0] * cofactor0 - m[1] * cofactor1 + m[2] * cofactor2 - m[3] * cofactor3;
		if(Math.abs(determinant) <= 0.00001f) {
			Arrays.fill(m, 1.0f);
		}

		// get rest of cofactors for adj(M)
		float cofactor4 = getCofactor(m[1],m[2],m[3], m[9],m[10],m[11], m[13],m[14],m[15]);
		float cofactor5 = getCofactor(m[0],m[2],m[3], m[8],m[10],m[11], m[12],m[14],m[15]);
		float cofactor6 = getCofactor(m[0],m[1],m[3], m[8],m[9], m[11], m[12],m[13],m[15]);
		float cofactor7 = getCofactor(m[0],m[1],m[2], m[8],m[9], m[10], m[12],m[13],m[14]);

		float cofactor8 = getCofactor(m[1],m[2],m[3], m[5],m[6], m[7],  m[13],m[14],m[15]);
		float cofactor9 = getCofactor(m[0],m[2],m[3], m[4],m[6], m[7],  m[12],m[14],m[15]);
		float cofactor10= getCofactor(m[0],m[1],m[3], m[4],m[5], m[7],  m[12],m[13],m[15]);
		float cofactor11= getCofactor(m[0],m[1],m[2], m[4],m[5], m[6],  m[12],m[13],m[14]);

		float cofactor12= getCofactor(m[1],m[2],m[3], m[5],m[6], m[7],  m[9], m[10],m[11]);
		float cofactor13= getCofactor(m[0],m[2],m[3], m[4],m[6], m[7],  m[8], m[10],m[11]);
		float cofactor14= getCofactor(m[0],m[1],m[3], m[4],m[5], m[7],  m[8], m[9], m[11]);
		float cofactor15= getCofactor(m[0],m[1],m[2], m[4],m[5], m[6],  m[8], m[9], m[10]);

		// build inverse matrix = adj(M) / det(M)
		// adjugate of M is the transpose of the cofactor matrix of M
		float invDeterminant = 1.0f / determinant;
		m[0] =  invDeterminant * cofactor0;
		m[1] = -invDeterminant * cofactor4;
		m[2] =  invDeterminant * cofactor8;
		m[3] = -invDeterminant * cofactor12;

		m[4] = -invDeterminant * cofactor1;
		m[5] =  invDeterminant * cofactor5;
		m[6] = -invDeterminant * cofactor9;
		m[7] =  invDeterminant * cofactor13;

		m[8] =  invDeterminant * cofactor2;
		m[9] = -invDeterminant * cofactor6;
		m[10]=  invDeterminant * cofactor10;
		m[11]= -invDeterminant * cofactor14;

		m[12]= -invDeterminant * cofactor3;
		m[13]=  invDeterminant * cofactor7;
		m[14]= -invDeterminant * cofactor11;
		m[15]=  invDeterminant * cofactor15;
	}

	/**Transpose the input 4x4 matrix*/
	public static void transpose4x4f(float[] m) {
		swapElements(m, 1, 4);
		swapElements(m, 2, 8);
		swapElements(m, 3, 12);
		swapElements(m, 6, 9);
		swapElements(m, 7, 13);
		swapElements(m, 11, 14);
	}

	/**
	//compute cofactor of 3x3 minor matrix without sign
	//input params are 9 elements of the minor matrix
	//NOTE: The caller must know its sign.
	 */
	private static float getCofactor(	float m0, float m1, float m2,
			float m3, float m4, float m5,
			float m6, float m7, float m8) {
		return 	m0 * (m4 * m8 - m5 * m7) -
				m1 * (m3 * m8 - m5 * m6) +
				m2 * (m3 * m7 - m4 * m6);
	}

	/**Swap 2 elements i,j in the array m*/
	public static void swapElements(float[] m, int i, int j) {
		float tmp = m[i];
		m[i] = m[j];
		m[j] = tmp;
	}
	/**Returns the backing array for the matrix.
	 * Altering this array will directly affect the matrix.*/
	public float[] getArray() {
		return matrix[depth];
	}
}
