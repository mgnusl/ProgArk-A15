package owg.engine.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.ref.WeakReference;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

/**
 * This is a class providing static methods for doing various common computations, 
 * typically useful for 2D and 3D euclidian/radial coordinate system conversions and the like.
 */
public class Calc
{
	/**lol.*/
	public final static int bytesPerByte = 1;
	public final static int bytesPerFloat = Float.SIZE/Byte.SIZE;
	public final static int bytesPerShort = Short.SIZE/Byte.SIZE;
	public final static int bytesPerInt = Integer.SIZE/Byte.SIZE;

	/**Multiply degrees with this constant to get radians*/
	public static final float toRad = (float)(Math.PI/180);
	/**Multiply radians with this constant to get degrees*/
	public static final float toDeg = (float)(180/Math.PI);
	/**PI rounded to a 32 bit float.*/
	public static final float PI = (float)Math.PI;

	/**Calculate the distance from the origin to (x,y)*/
	public static double dist(double x,double y)
	{
		return Math.sqrt(x*x+y*y);
	}

	/**Calculate the squared distance from the origin to (x,y) (cheap)*/
	public static double sqDist(double x, double y)
	{
		return x*x+y*y;
	}
	/**Calculate the distance from the (x1,y1) to (x2,y2)*/
	public static double dist(double x1,double y1,double x2,double y2)
	{
		return Math.sqrt((x2-x1)*(x2-x1)+(y2-y1)*(y2-y1));
	}
	/**Calculate the distance from the origin to (x,y,z)*/
	public static double dist(double x,double y,double z)
	{
		return Math.sqrt(x*x+y*y+z*z);
	}
	/**Calculate the distance from the (x1,y1,z1) to (x2,y2,z2)*/
	public static double dist(double x1,double y1,double z1,double x2,double y2,double z2)
	{
		return Math.sqrt((x2-x1)*(x2-x1)+(y2-y1)*(y2-y1)+(z2-z1)*(z2-z1));
	}
	/**Compute a somewhat accurate approximation to euclidian distance, using fast integer arithmetic.
	 * Note that this is not useful for very small numbers unless they are scaled.*/
	public static int approxIntegerDistance(int dx, int dy)
	{
		long min, max, approx;

		if ( dx < 0 ) dx = -dx;
		if ( dy < 0 ) dy = -dy;

		if ( dx < dy )
		{
			min = dx;
			max = dy;
		} 
		else 
		{
			min = dy;
			max = dx;
		}

		approx = ( max * 1007 ) + ( min * 441 );
		if ( max < ( min << 4 ))
			approx -= ( max * 40 );

		// add 512 for proper rounding
		return (int)(( approx + 512 ) >> 10 );
	}
	/**
	 * Calculate the direction from (x1,y1) to (x2,y2). 
	 * The resulting radian angle is given in a counter-clockwise fashion in a left-handed 2D coordinate space, 
	 * with 0 pointing to the east(x axis).
	 */
	public static double dir(double x1, double y1, double x2, double y2)
	{
		double dx = x2-x1;
		double dy = y2-y1;
		double dist = dist(dx,dy);
		dx/=dist;
		dy/=dist;
		if(dx==0 && dy==0)
			return 0;
		if(dx>=0 && dy<=0)
		{
			dy*=-1;
			return Math.atan(dy/dx);
		}
		if(dx<0 && dy<=0)
		{
			dx*=-1;
			dy*=-1;
			return Math.PI-Math.atan(dy/dx);
		}
		if(dx>=0 && dy>0)
		{
			return 2*Math.PI-Math.atan(dy/dx);
		}
		if(dx<0 && dy>0)
		{
			dx*=-1;
			return Math.PI+Math.atan(dy/dx);
		}
		return 0;
	}
	/**Approximation to the sine. Up to 25% faster than Math.cos.*/
	public static float fastSin(float angle)
	{
		return fastCos(PI/2-angle);
	}
	/**Approximation to the cosine. Up to 25% faster than Math.cos.*/
	public static float fastCos(final float angle)
	{
		final float z = Calc.cyclic(2f*angle/PI, 4f);
		if(z < 1f)
			return fc(z);
		else if(z < 2f) 
			return -fc(2f-z);
		else if(z < 3f)
			return -fc(z-2f);
		else
			return fc(4f-z);
	}
	/**Approximation for first half-quadrant of cosine function.*/
	private static float fc(final float z)
	{
		return 1f-z*z*((2f-PI/4)-z*z*(1f-PI/4f));
	}

	/**Force the given number into the interval by adding or subtracting the size of the range*/
	public static int cyclic(int number,int min, int max)
	{
		number = (number-min)%(max-min);
		if (number<0) number+=(max-min);
		return number+min;
	}
	/**Force the given number into the interval by adding or subtracting the size of the range*/
	public static double cyclic(double number,double min, double max)
	{
		number = (number-min)%(max-min);
		if (number<0) number+=(max-min);
		return number+min;
	}
	/**Force the given number into the interval by adding or subtracting the size of the range. Min is implicitly 0.*/
	public static int cyclic(int number, int max)
	{
		return (number % max + max) % max;
	}
	/**Force the given number into the interval by adding or subtracting the size of the range. Min is implicitly 0.*/
	public static float cyclic(float number, float max)
	{
		return (number % max + max) % max;
	}
	/**Calculate the shortest difference(never above Math.PI, never below -Math.PI) between any 2 angles*/
	public static double dirDiff(double dir1, double dir2)
	{
		double diff = dir2-dir1;
		while (diff<-Math.PI)
			diff+=2*Math.PI;
		while (diff>Math.PI)
			diff-=2*Math.PI;
		return diff;
	}
	/**Create a concatenated X*Y*Z euler rotation matrix, putting the result in the indicated destination
	 * Optional y-mirroring*/
	public static void makeXYZMatrixOpt(float rX,float rY,float rZ, boolean mirror, float[][] destination)
	{
		//generate units
		float cx = (float) Math.cos(rX);
		float sx = (float) Math.sin(rX);
		float cy = (float) Math.cos(rY);
		float sy = (float) Math.sin(rY);
		float cz = (float) Math.cos(rZ);
		float sz = (float) Math.sin(rZ);
		if(mirror) {sy*=-1; sz*=-1;}
		destination[0][0] = cy*cz;
		destination[0][1] = -cy*sz;
		destination[0][2] = sy;
		destination[1][0] = cx*sz + sy*cz*sx;
		destination[1][1] = cx*cz - sx*sy*sz;
		destination[1][2] = - sx*cy;
		destination[2][0] = sx*sz - sy*cz*cx;
		destination[2][1] = sx*cz + cx*sy*sz;
		destination[2][2] = cx*cy;
	}
	/**Create a concatenated X*Y*Z euler rotation matrix, allocating the result in a new matrix
	 * Optional y-mirroring*/
	public static float[][] makeXYZMatrix(float rX,float rY,float rZ, boolean mirror)
	{
		//generate units
		float cx = (float) Math.cos(rX);
		float sx = (float) Math.sin(rX);
		float cy = (float) Math.cos(rY);
		float sy = (float) Math.sin(rY);
		float cz = (float) Math.cos(rZ);
		float sz = (float) Math.sin(rZ);
		if(mirror) {sy*=-1; sz*=-1;}

		float[][] destination = new float[3][3];
		destination[0][0] = cy*cz;
		destination[0][1] = -cy*sz;
		destination[0][2] = sy;
		destination[1][0] = cx*sz + sy*cz*sx;
		destination[1][1] = cx*cz - sx*sy*sz;
		destination[1][2] = - sx*cy;
		destination[2][0] = sx*sz - sy*cz*cx;
		destination[2][1] = sx*cz + cx*sy*sz;
		destination[2][2] = cx*cy;
		return destination;
	}
	/**Returns the base-2 logarithm for the number.*/
	public static double log2(double d)
	{
		return Math.log(d)/Math.log(2);
	}
	/**Returns the integer logarithm for n. 
	 * Note that the floating point variant {@link #log2(double)} might be faster depending on hardware/JVM.*/
	public static int binlog( int n ) // returns 0 for bits=0
	{
	    int log = 0;
	    if( ( n & 0xffff0000 ) != 0 ) { n >>>= 16; log = 16; }
	    if( n >= 256 ) { n >>>= 8; log += 8; }
	    if( n >= 16  ) { n >>>= 4; log += 4; }
	    if( n >= 4   ) { n >>>= 2; log += 2; }
	    return log + ( n >>> 1 );
	}
	/**Construct a 3x3 rotation matrix around the r axis, 
	 * counterclockwise seen from the positive direction in left-handed space.*/
	public static float[][] makeEulerMatrix(float[] r, float f)
	{
		float c = (float)Math.cos(f);
		float s = (float)Math.sin(f);
		float[][] m = new float[][]{
				{c+r[0]*r[0]*(1-c)		,r[0]*r[1]*(1-c)-r[2]*s	,r[0]*r[2]*(1-c)+r[1]*s },
				{r[1]*r[0]*(1-c)+r[2]*s	,c+r[1]*r[1]*(1-c)		,r[1]*r[2]*(1-c)-r[0]*s	},
				{r[2]*r[0]*(1-c)-r[1]*s	,r[2]*r[1]*(1-c)+r[0]*s	,c+r[2]*r[2]*(1-c)		}};
		return m;
	}


	/**Construct a 3x3 rotation matrix around the Z axis, 
	 * counterclockwise seen from the positive direction in left-handed space.*/
	public static float[][] makeZMatrix(float rZ)
	{
		float cz = (float) Math.cos(-rZ);
		float sz = (float) Math.sin(-rZ);

		return new float[][]{
				{cz,-sz,0},
				{sz, cz,0},
				{0,0,1}
		};
	}
	/**Construct a 3x3 rotation matrix around the X axis, 
	 * counterclockwise seen from the positive direction in left-handed space.*/
	public static float[][] makeXMatrix(float rX)
	{
		float cz = (float) Math.cos(-rX);
		float sz = (float) Math.sin(-rX);

		return new float[][]{
				{1, 0, 0},
				{0, cz, -sz},
				{0, sz, cz}
		};
	}

	/**
	 * Postmultiply a float[3] to a float[3][3], return a new, resulting float[3]
	 */
	public static float[] rotationMatrixPostMultiply(float[][] m, float[] v)
	{
		return new float[]{
				v[0]*m[0][0]+v[1]*m[0][1]+v[2]*m[0][2],
				v[0]*m[1][0]+v[1]*m[1][1]+v[2]*m[1][2],
				v[0]*m[2][0]+v[1]*m[2][1]+v[2]*m[2][2]
		};
	}
	/**
	 * Postmultiply a float[3] to a float[3][3], replacing the contents of the vector.
	 * The matrix is unchanged.
	 */
	public static void rotationMatrixPostMultiplyOpt(float[][] m, float[] v)
	{
		float x=v[0];
		float y=v[1];
		float z=v[2];
		v[0]=x*m[0][0]+y*m[0][1]+z*m[0][2];
		v[1]=x*m[1][0]+y*m[1][1]+z*m[1][2];
		v[2]=x*m[2][0]+y*m[2][1]+z*m[2][2];
	}
	/**
	 * Postmultiply a float[3] to a float[9], replacing the contents of the vector.
	 * The matrix is unchanged.
	 */
	public static void rotationMatrixPostMultiplyOpt(float[] m, float[] v)
	{
		float x=v[0];
		float y=v[1];
		float z=v[2];
		v[0]=x*m[0]+y*m[1]+z*m[2];
		v[1]=x*m[3]+y*m[4]+z*m[5];
		v[2]=x*m[6]+y*m[7]+z*m[8];
	}
	/**
	 * Premultiply a float[3] to a float[3][3], return a new, resulting float[3]
	 */
	public static float[] rotationMatrixPreMultiply(float[] v,float[][] m)
	{
		return new float[]{
				v[0]*m[0][0]+v[1]*m[1][0]+v[2]*m[2][0],
				v[0]*m[0][1]+v[1]*m[1][1]+v[2]*m[2][1],
				v[0]*m[0][2]+v[1]*m[1][2]+v[2]*m[2][2]
		};
	}

	/**Multiply the matrices m1 x m2, putting the result in m1*/
	public static void matrixMultiply3OptFirst(float[][]m1, float[][]m2)
	{
		float m100=m1[0][0],m101=m1[0][1],m102=m1[0][2];
		float m110=m1[1][0],m111=m1[1][1],m112=m1[1][2];
		float m120=m1[2][0],m121=m1[2][1],m122=m1[2][2];

		m1[0][0]=m100*m2[0][0]+m101*m2[1][0]+m102*m2[2][0];
		m1[0][1]=m100*m2[0][1]+m101*m2[1][1]+m102*m2[2][1];
		m1[0][2]=m100*m2[0][2]+m101*m2[1][2]+m102*m2[2][2];

		m1[1][0]=m110*m2[0][0]+m111*m2[1][0]+m112*m2[2][0];
		m1[1][1]=m110*m2[0][1]+m111*m2[1][1]+m112*m2[2][1];
		m1[1][2]=m110*m2[0][2]+m111*m2[1][2]+m112*m2[2][2];

		m1[2][0]=m120*m2[0][0]+m121*m2[1][0]+m122*m2[2][0];
		m1[2][1]=m120*m2[0][1]+m121*m2[1][1]+m122*m2[2][1];
		m1[2][2]=m120*m2[0][2]+m121*m2[1][2]+m122*m2[2][2];
	}

	/**Multiply the matrices m1 x m2, putting the result in m2*/
	public static void matrixMultiply3OptLast(float[][]m1, float[][]m2)
	{
		float m200=m2[0][0],m201=m2[0][1],m202=m2[0][2];
		float m210=m2[1][0],m211=m2[1][1],m212=m2[1][2];
		float m220=m2[2][0],m221=m2[2][1],m222=m2[2][2];

		m2[0][0]=m1[0][0]*m200+m1[0][1]*m210+m1[0][2]*m220;
		m2[0][1]=m1[0][0]*m201+m1[0][1]*m211+m1[0][2]*m221;
		m2[0][2]=m1[0][0]*m202+m1[0][1]*m212+m1[0][2]*m222;

		m2[1][0]=m1[1][0]*m200+m1[1][1]*m210+m1[1][2]*m220;
		m2[1][1]=m1[1][0]*m201+m1[1][1]*m211+m1[1][2]*m221;
		m2[1][2]=m1[1][0]*m202+m1[1][1]*m212+m1[1][2]*m222;

		m2[2][0]=m1[2][0]*m200+m1[2][1]*m210+m1[2][2]*m220;
		m2[2][1]=m1[2][0]*m201+m1[2][1]*m211+m1[2][2]*m221;
		m2[2][2]=m1[2][0]*m202+m1[2][1]*m212+m1[2][2]*m222;
	}
	/**Compute m1 x m2 for two 3x3 matrices*/
	public static float[][] matrixMultiply3(float[][]m1, float[][]m2)
	{
		return new float[][]{
				{m1[0][0]*m2[0][0]+m1[0][1]*m2[1][0]+m1[0][2]*m2[2][0],m1[0][0]*m2[0][1]+m1[0][1]*m2[1][1]+m1[0][2]*m2[2][1],m1[0][0]*m2[0][2]+m1[0][1]*m2[1][2]+m1[0][2]*m2[2][2]},
				{m1[1][0]*m2[0][0]+m1[1][1]*m2[1][0]+m1[1][2]*m2[2][0],m1[1][0]*m2[0][1]+m1[1][1]*m2[1][1]+m1[1][2]*m2[2][1],m1[1][0]*m2[0][2]+m1[1][1]*m2[1][2]+m1[1][2]*m2[2][2]},
				{m1[2][0]*m2[0][0]+m1[2][1]*m2[1][0]+m1[2][2]*m2[2][0],m1[2][0]*m2[0][1]+m1[2][1]*m2[1][1]+m1[2][2]*m2[2][1],m1[2][0]*m2[0][2]+m1[2][1]*m2[1][2]+m1[2][2]*m2[2][2]}
		};
	}
	/**Compute left x right for two 4x4 matrices, formatted as a 16 element array.*/
	public static float[] multMatrixFlat4x4(float[] left, float[] right) 
	{
		float[] tmp = new float[16];

		for (int i = 0; i < 4; i++) 
		{
			tmp[i*4+0] =
					(left[i*4+0] * right[0*4+0]) +
					(left[i*4+1] * right[1*4+0]) +
					(left[i*4+2] * right[2*4+0]) +
					(left[i*4+3] * right[3*4+0]) ;

			tmp[i*4+1] =
					(left[i*4+0] * right[0*4+1]) +
					(left[i*4+1] * right[1*4+1]) +
					(left[i*4+2] * right[2*4+1]) +
					(left[i*4+3] * right[3*4+1]) ;

			tmp[i*4+2] =
					(left[i*4+0] * right[0*4+2]) +
					(left[i*4+1] * right[1*4+2]) +
					(left[i*4+2] * right[2*4+2]) +
					(left[i*4+3] * right[3*4+2]) ;

			tmp[i*4+3] =
					(left[i*4+0] * right[0*4+3]) +
					(left[i*4+1] * right[1*4+3]) +
					(left[i*4+2] * right[2*4+3]) +
					(left[i*4+3] * right[3*4+3]) ;
		}
		return tmp;
	}

	/**
	 * Parses a string to an array of floats. Must be separated with whitespaces.
	 */
	public static float[] getFloatArray(String line)
	{
		String[] split = line.split(" ");
		float[] c = new float[split.length];
		for(int i=0; i<split.length; i++)
		{
			c[i] = Float.parseFloat(split[i]);
		}
		return c;
	}

	public static int countOccurrences(String haystack, char needle)
	{
		int count = 0;
		for (int i=0; i < haystack.length(); i++)
		{
			if (haystack.charAt(i) == needle)
			{
				count++;
			}
		}
		return count;
	}
	/**@return The number multiplied by -1 if mirror is true, the unmodified number if mirror is false.*/
	public static double mirror(double d, boolean mirror)
	{
		return d*(mirror?-1:1);
	}
	/**Transpose a 3x3 float rotation matrix.*/
	public static float[][] transpose3(float[][] a)
	{
		return new float[][]{
				{a[0][0],a[1][0],a[2][0]},
				{a[0][1],a[1][1],a[2][1]},
				{a[0][2],a[1][2],a[2][2]}};
	}
	/**Clamp the number into the bounds. Equivalent to: Math.max(Math.min(number, maximum), minimum)*/
	public static double clamp(double number, double minimum, double maximum)
	{
		if (number<minimum)
			return minimum;
		if (number>maximum)
			return maximum;
		return number;
	}
	/**Clamp the number into the bounds. Equivalent to: Math.max(Math.min(number, maximum), minimum)*/
	public static float clamp(float number, float minimum, float maximum)
	{
		if (number<minimum)
			return minimum;
		if (number>maximum)
			return maximum;
		return number;
	}
	/**Clamp the number into the bounds. Equivalent to: Math.max(Math.min(number, maximum), minimum)*/
	public static int clamp(int number, int minimum, int maximum)
	{
		if (number<minimum)
			return minimum;
		if (number>maximum)
			return maximum;
		return number;
	}

	/**Clamp the number into the bounds. Equivalent to: Math.max(Math.min(number, maximum), minimum)
	 * @param clamped A byte array where we will increment a value if the number was clamped. It is left unaltered otherwise.
	 * @param index The index to use in the byte array.*/
	public static double clamp(double number, double minimum, double maximum, byte[] clamps, byte index)
	{
		if (number<minimum)
		{clamps[index]++;
		return minimum;}
		if (number>maximum)
		{clamps[index]++;
		return maximum;}
		return number;
	}

	/**Failsafe version of arccos. Defaults to the closest valid radian angle if the number is out of bounds.*/
	public static double acos(double d)
	{
		if (d<=-1)
			return  Math.PI;
		else if (d>=1)
			return  0;
		return Math.acos(d);
	}
	/**Deeper cloning of 2D array*/
	public static float[][] clone2D(float[][] src)
	{
		float[][] result = new float[src.length][];
		for(int i=0; i<result.length; i++)
		{
			result[i] = src[i].clone();
		}
		return result;
	}
	/**Manhattan distance*/
	public static double manDist(double x1, double y1, double x2, double y2)
	{
		return Math.abs(x2-x1)+Math.abs(y2-y1);
	}
	/**Fill a float array with four elements.*/
	public static void fill(float[] array, float a,float b,float c,float d)
	{
		array[0]=a;
		array[1]=b;
		array[2]=c;
		array[3]=d;
	}
	/**Return the minimum value of three elements.*/
	public static double min(double a, double b, double c)
	{
		if(a<=b && a<=c)
			return a;
		else if (b<=c)
			return b;
		return c;
	}
	/**Return the maximum value of three elements.*/
	public static double max(double a, double b, double c)
	{
		if(a>=b && a>=c)
			return a;
		else if (b>=c)
			return b;
		return c;
	}
	/**Return the minimum value of three elements.*/
	public static float min(float a, float b, float c)
	{
		if(a<=b && a<=c)
			return a;
		else if (b<=c)
			return b;
		return c;
	}
	/**Return the maximum value of three elements.*/
	public static float max(float a, float b, float c)
	{
		if(a>=b && a>=c)
			return a;
		else if (b>=c)
			return b;
		return c;
	}
	/**Compute SHA-1 hash
	 * @throws NoSuchAlgorithmException If there is no SHA-1 algorithm on the system.*/
	public static String SHAsum(byte[] convertme) throws NoSuchAlgorithmException 
	{
		MessageDigest md = MessageDigest.getInstance("SHA-1");
		return new String(md.digest(convertme));
	}
	/**Strip the extension if it exists*/
	public static String stripExtension(String fname)
	{
		int i = fname.lastIndexOf('.');
		if(i==-1)
			return fname;
		return fname.substring(0, i);
	}
	/**Converts the java integer to a four element byte array, formatted as "big endian".*/
	public static byte[] IntToBytes(int value)
	{
		return new byte[]{(byte)((value&0xFF000000)>>>24),(byte)((value&0x00FF0000)>>>16),(byte)((value&0x0000FF00)>>8),(byte)(value&0x000000FF)};
	}
	/**Converts a four element byte array, formatted as "big endian", to a java integer.*/
	public static int BytesToInt(byte[] value)
	{
		return ((value[0]&0xFF)<<24) | ((value[1]&0xFF)<<16) | ((value[2]&0xFF)<<8) | ((value[3]&0xFF));
	}
	/**When invoked, this method will multiply each pair of elements in the 4-element arrays.
	 * The result is stored in dstValue(the first array).*/
	public static void multiplyEach4f(float[] dstValue, float[] multiplier)
	{
		dstValue[0] *= multiplier[0];
		dstValue[1] *= multiplier[1];
		dstValue[2] *= multiplier[2];
		dstValue[3] *= multiplier[3];
	}
	/**Prints the string value of each object in a list.*/
	public static void println(Object... args)
	{
		for(int i=0; i<args.length-1; i++)
		{
			System.out.print(args[i]+", ");
		}
		if(args.length>0)
			System.out.println(args[args.length-1]);
	}
	/**Given the previous and current value for a timer, 
	 * returns whether the value has passed the specified value during the current time step.
	 * This test is safe to use for timers that increase at a non-uniform pace.*/
	public static boolean passed(float previous, float current, float value)
	{
		return current >= value && previous < value;
	}
	/**Returns a value that increases and decreases between 0 and max, at the same pace as the value.*/
	public static float patrol(float value, float max)
	{
		float bar = value%max;
		if((int)(value/max)%2 == 0)
			return bar;
		else
			return max-bar;
	}
	/**Returns a value that increases and decreases between 0 and max, at the same pace as the value.*/
	public static int patrol(int value, int max)
	{
		int bar = value%max;
		if((int)(value/max)%2 == 0)
			return bar;
		else
			return max-bar;
	}

	/**Filter the array on the given regex. Returns a new array that contains each element from the original list if and only if
	 * the element matches the regex, that is list[j] is retained iff list[j].matches(regex).
	 * If regex is null, the original reference to the list is returned.*/
	public static String[] filter(String[] list, String regex)
	{
		if(regex == null)
			return list;
		ArrayList<String> matches = new ArrayList<String>();
		for(String s : list)
		{
			if(s.matches(regex))
				matches.add(s);
		}
		String[] result = new String[matches.size()];
		matches.toArray(result);
		return result;
	}
	/**
	 * @param start The lowermost value to put in the list, inclusive
	 * @param end The upper bound of values to put in the list, exclusive
	 * @return A list containing integers from start to end-1, inclusive
	 */
	public static ArrayList<Integer> getRangeElements(int start, int end)
	{
		int size = end-start;
		ArrayList<Integer> result = new ArrayList<Integer>(size);
		for(int i = start; i<end; i++)
			result.add(i);
		return result;
	}
	/**Returns a number with the same sign as the input value, 
	 * but with an absolute value that is at least as great as the minimum value.
	 * The minimum value should be a positive number.*/
	public static double absMax(double value, double minimum)
	{
		if(value>=0)
			return Math.max(value, minimum);
		else
			return Math.min(value, -minimum);
	}
	/**Equals method that is safe to use for potential nulls.
	 * @return boy==null?girl==null:boy.equals(girl).*/
	public static boolean equals(Object boy, Object girl)
	{
		if(boy == null)
			return girl == null;
		else
			return boy.equals(girl);
	}
	/**Returns the first index of the item in the list of weak references, or -1 if it was not found.
	 * @see ArrayList#indexOf(Object)*/
	public static<T> int indexOfWeakReference(ArrayList<WeakReference<T>> list, T item)
	{
		for(int i = 0; i<list.size(); i++)
		{
			if((item == null && list.get(i) == null) || (item != null && item.equals(list.get(i))))
				return i;
		}
		return -1;
	}

	/**Useful for even powers, this method will return the result of the power function, 
	 * multiplied by the original sign of the input number.*/
	public static double signedPow(double number, double power)
	{
		if(number < 0)
			return -Math.pow(-number, power);
		else
			return Math.pow(number, power);
	}
	public static String[] readFileArray(InputStream s) throws IOException {
		ArrayList<String> sb = new ArrayList<String>(16);
		Reader r = new InputStreamReader(s);
		BufferedReader br = new BufferedReader(r);
		String in;
		while((in = br.readLine())!=null) {
			sb.add(in);
		}
		String[] result = new String[sb.size()];
		sb.toArray(result);
		return result;
	}

	public static String readFileString(InputStream s) throws IOException {
		StringBuilder sb = new StringBuilder(256);
		Reader r = new InputStreamReader(s);
		BufferedReader br = new BufferedReader(r);
		String in;
		while((in = br.readLine())!=null) {
			sb.append(in);
			sb.append('\n');
		}
		return sb.toString();
	}
}
