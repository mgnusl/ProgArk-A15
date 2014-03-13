package owg.engine.util;


/**
 * This is a rigid 3D vector class for abstracting the representation and computing of a wide range of values.
 * This implementation is mutable, so the user has to be aware that reckless handling may cause unintentional overwrites.
 * Many of the methods return the vector on which the call was made, so the calls can be chained.
 */
public class V3F
{
	private static final float DEFAULT_ERROR_THRESHOLD = Float.MIN_VALUE*10;
	
	public static final char[] COMPONENT_NAMES = {'x','y','z'};
	public static final byte X_INDEX = 0;
	public static final byte Y_INDEX = 1;
	public static final byte Z_INDEX = 2;
	
	public static int getIndexForChar(char c)
	{
		if(c == 'x') return 0;
		if(c == 'y') return 1;
		if(c == 'z') return 2;
		throw new IllegalArgumentException(c+" is not a vector component name... ");
	}


	public final float[] p = new float[3];
	/**
	 * Construct with zero length
	 */
	public V3F()
	{
		this(0,0,0);
	}
	/**
	 * Construct with the specified components
	 */
	public V3F(float x, float y, float z)
	{
		p[0]=x;
		p[1]=y;
		p[2]=z;
	}
	/**
	 * Construct from the float array, starting from the offset 
	 */
	public V3F(float[] vector, int offset)
	{
		this(vector[offset],vector[offset+1],vector[offset+2]);
	}
	/**
	 * Set all components
	 * @return this vector
	 */
	public V3F set(float x,float y,float z)
	{
		p[0]=x;
		p[1]=y;
		p[2]=z;
		return this;
	}
	/**
	 * Set all components
	 * @return this vector
	 */
	public V3F set(V3F source)
	{
		p[0]=source.p[0];
		p[1]=source.p[1];
		p[2]=source.p[2];
		return this;
	}

	/**Calculate the length of the vector. */
	public float len()
	{
		return (float)Math.sqrt(Math.pow(p[0],2)+Math.pow(p[1],2)+Math.pow(p[2],2));
	}
	/**Calculate the squared length. This method is fast because it has no square root computation. */
	public float sqLen()
	{
		return p[0]*p[0]+p[1]*p[1]+p[2]*p[2];
	}
	/**Returns the manhattan length.*/
	public float manLen()
	{
		return Math.abs(p[0])+Math.abs(p[1])+Math.abs(p[2]);
	}

	/**Calculate the distance from this to other, that is, length of the hypothetical vector this.sub(other)*/
	public float distance(V3F other)
	{
		return (float)Math.sqrt(Math.pow(p[0]-other.p[0],2)+Math.pow(p[1]-other.p[1],2)+Math.pow(p[2]-other.p[2],2));
	}
	/**Calculate the squared length of this-other. This method is fast because it has no square root computation.*/
	public float sqDistance(V3F other)
	{
		return (p[0]-other.p[0])*(p[0]-other.p[0])+(p[1]-other.p[1])*(p[1]-other.p[1])+(p[2]-other.p[2])*(p[2]-other.p[2]);
	}
	/**Calculate the manhattan length of this-other.*/
	public float manDistance(V3F other)
	{
		return Math.abs(p[0]-other.p[0])+Math.abs(p[1]-other.p[1])+Math.abs(p[2]-other.p[2]);
	}
	/**Calculate the angle(radians) between this and other*/
	public float angle(V3F other)
	{
		return (float)Calc.acos(dot(other)/(len()*other.len()));
	}

	/**
	 * Make the length equal to 1 while keeping the direction.
	 * Note that a zero-length vector defaults to (1,0,0).
	 * @return this vector
	 */
	public V3F normalize()
	{
		double d = Math.sqrt(Math.pow(p[0],2)+Math.pow(p[1],2)+Math.pow(p[2],2));
		if (d==0)
		{
			p[0]=1;
			p[1]=0;
			p[2]=0;
		}
		else
		{
			p[0]/=d;
			p[1]/=d;
			p[2]/=d;
		}
		return this;
	}
	/**
	 * Multiply all components with the value
	 * @return this vector
	 */
	public V3F multiply(float m)
	{
		p[0]*=m;
		p[1]*=m;
		p[2]*=m;
		return this;
	}

	/**
	 * Optimal reversal of vector. Functionally equivalent to multiply(-1)
	 * @return This vector
	 */
	public V3F reverse()
	{
		p[0] = -p[0];
		p[1] = -p[1];
		p[2] = -p[2];
		return this;
	}
	/**
	 * Multiply all components with the respective values
	 * @return this vector
	 */
	public V3F multiply(float mx, float my, float mz)
	{
		p[0]*=mx;
		p[1]*=my;
		p[2]*=mz;
		return this;
	}
	/**
	 * Add the components to the current values
	 * @return this vector
	 */
	public V3F add(float x,float y,float z)
	{
		p[0]+=x;
		p[1]+=y;
		p[2]+=z;
		return this;
	}
	/**
	 * Add the vector's components to the current values. 
	 * As a convenience, this method treats a null vector as a zero-length vector.
	 * If this behaviour is potentially problematic, then {@link #add(V3F)} should be used.
	 * @return this vector
	 */
	public V3F addAllowNull(V3F v)
	{
		if(v!=null)
		{
			p[0]+=v.p[0];
			p[1]+=v.p[1];
			p[2]+=v.p[2];
		}
		return this;
	}

	/**
	 * Add the vector's components to the current values
	 * @return this vector
	 */
	public V3F add(V3F v)
	{
		p[0]+=v.p[0];
		p[1]+=v.p[1];
		p[2]+=v.p[2];
		return this;
	}
	/**
	 * Subtract the vector's components from the current values
	 * @return this vector
	 */
	public V3F sub(V3F v)
	{
		p[0]-=v.p[0];
		p[1]-=v.p[1];
		p[2]-=v.p[2];
		return this;
	}
	/**
	 * Add the other vector, multiplied with the value, to this vector.
	 * @return this vector
	 */
	public V3F add(V3F v,float multiplicator)
	{
		p[0]+=multiplicator*v.p[0];
		p[1]+=multiplicator*v.p[1];
		p[2]+=multiplicator*v.p[2];
		return this;
	}

	/**
	 * Set this vector to the cross product of the specified vectors.
	 * @return this vector
	 */
	public V3F setCross(V3F v1, V3F v2)
	{
		if(v1==this) return setCross(v2);
		else if(v2 == this) return setCross(v1).reverse();
		p[0]=v1.p[1]*v2.p[2]-v1.p[2]*v2.p[1];
		p[1]=v1.p[2]*v2.p[0]-v1.p[0]*v2.p[2];
		p[2]=v1.p[0]*v2.p[1]-v1.p[1]*v2.p[0];
		return this;
	}
	/**
	 * Set this vector to the cross product of this and the specified vector.
	 * @return this vector
	 */
	public V3F setCross(V3F v2)
	{
		float x=p[0], y=p[1], z=p[2];
		p[0]=y*v2.p[2]-z*v2.p[1];
		p[1]=z*v2.p[0]-x*v2.p[2];
		p[2]=x*v2.p[1]-y*v2.p[0];
		return this;
	}
	/**
	 * Return a new vector which is the cross product of this and the specified vector
	 */
	public V3F createCross(V3F v)
	{
		float x,y,z;
		x=p[1]*v.p[2]-p[2]*v.p[1];
		y=p[2]*v.p[0]-p[0]*v.p[2];
		z=p[0]*v.p[1]-p[1]*v.p[0];
		return new V3F(x,y,z);
	}
	/**
	 * Make this vector the average of this and the specified vector(weight=0 -> this, weight=1 -> other)
	 * @return this vector
	 */
	public V3F setAverage(V3F other,float weight)
	{
		p[0]=(p[0]*(1-weight)+other.p[0]*weight);
		p[1]=(p[1]*(1-weight)+other.p[1]*weight);
		p[2]=(p[2]*(1-weight)+other.p[2]*weight);
		return this;
	}

	/**
	 * Make this vector the average of this and the specified components(weight=0 -> this, weight=1 -> components)
	 * @return this vector
	 */
	public V3F setAverage(float x, float y, float z, float weight)
	{
		p[0]=(p[0]*(1-weight)+x*weight);
		p[1]=(p[1]*(1-weight)+y*weight);
		p[2]=(p[2]*(1-weight)+z*weight);
		return this;
	}
	/**
	 * Return the dot product of this and the specified vector
	 */
	public float dot(V3F v)
	{
		return p[0]*v.p[0]+p[1]*v.p[1]+p[2]*v.p[2];
	}
	/**
	 * Return the dot product of this and the specified vector
	 */
	public float dot(float x,float y,float z)
	{
		return p[0]*x+p[1]*y+p[2]*z;
	}
	/**
	 * Return the x-component
	 */
	public float x()
	{
		return p[0];
	}
	/**
	 * Return the y-component
	 */
	public float y()
	{
		return p[1];
	}
	/**
	 * Return the z-component
	 */
	public float z()
	{
		return p[2];
	}
	/**
	 * Set the x-component
	 * @return this vector
	 */
	public V3F x(float x)
	{
		p[0]=x;
		return this;
	}
	/**
	 * Set the y-component
	 * @return this vector
	 */
	public V3F y(float y)
	{
		p[1]=y;
		return this;
	}
	/**
	 * Set the z-component
	 * @return this vector
	 */
	public V3F z(float z)
	{
		p[2]=z;
		return this;
	}
	/**Insert 3 elements into the given array starting from the indicated index,
	 * representing the x, y and z value of this vector.*/
	public void toFloat(float[] vertices, int i)
	{
		vertices[i+0] = (float)p[0];
		vertices[i+1] = (float)p[1];
		vertices[i+2] = (float)p[2];
	}
	/**
	 * Return a double array representing this vector
	 */
	public double[] toDouble()
	{
		double[] result = {p[0],p[1],p[2]};
		return result;
	}
	/**
	 * Return a signed byte array representing this vector
	 */
	public byte[] toByte()
	{
		byte[] result = {(byte)p[0],(byte)p[1],(byte)p[2]};
		return result;
	}
	/**
	 * Return a signed short array representing this vector
	 */
	public short[] toShort()
	{
		short[] result = {(short)p[0],(short)p[1],(short)p[2]};
		return result;
	}
	/**
	 * Make a copy
	 */
	@Override
	public V3F clone()
	{
		return new V3F(p[0],p[1],p[2]);
	}
	/**
	 * Print the coordinates. 
	 */
	@Override
	public  String toString()
	{
		return "x:"+p[0]+", y:"+p[1]+", z:"+p[2]+" ";
	}
	/**
	 * Parse a string into a V3D. The String should correspond to the output from {@link V3F#toString()}
	 * @param str A string in the form<br/> 
	 * x:[x],y:[y],z:[z]<br/>
	 * where [x] is the numerical x coordinate value and soforth. It is valid to change the component order. 
	 * Alternatively, the identifiers (x:) may be omitted, in which case the x-y-z order is implicit.
	 * The values must appear in order in any case.
	 * @return A V3D object with the values defined in the string.
	 */
	public static V3F parseV3D(String str) throws IllegalArgumentException
	{
		String[] in = str.split(",");
		if(in.length < 3)
			throw new IllegalArgumentException("Vector must have 3 components. Found: "+in.length+" in "+str);
		V3F vec = new V3F();
		try
		{
			for(int i = 0; i<3; i++)
			{
				int pos = in[i].lastIndexOf(':');
				if(pos == -1)
					vec.p[i] = Float.parseFloat(in[i]);
				else
					vec.p[getIndexForChar(in[i].charAt(0))] = Float.parseFloat(in[i].substring(pos+1));
			}
			return vec;
		}
		catch (NumberFormatException e)
		{
			throw new IllegalArgumentException("Error in parsing vector: "+str, e);
		}
	}
	/**
	 * Accelerate in the current direction by an absolute value. 
	 * If length is under the negative of the value, or the vector's length is already zero, then the vector is set to zero.
	 * @return this vector*/
	public V3F accelerate(float d)
	{
		float sp = len();
		if(sp!=0)
		{
			if (sp<=-d)
			{
				p[0]=0; p[1]=0; p[2]=0;
				return this;
			}
			d=1+d/sp;
			p[0]*=d; p[1]*=d; p[2]*=d;
		}
		return this;
	}
	/**
	 * Accelerate towards becoming identical to other by an absolute value.
	 * If the vector is already identical to, or will go beyond the other vector's value, then it is set to be identical to it.
	 * @return this vector*/
	public V3F accelerateTowards(V3F other, float d)
	{
		float sp = distance(other);
		if (sp<=d)
		{
			p[0]=other.p[0]; p[1]=other.p[1]; p[2]=other.p[2];
			return this;
		}
		d=d/sp;
		p[0]=p[0]*(1-d)+other.p[0]*(d); 
		p[1]=p[1]*(1-d)+other.p[1]*(d); 
		p[2]=p[2]*(1-d)+other.p[2]*(d);
		return this;
	}
	/**Returns whether this vector's components are equal to the other vector's values, using bit-by-bit comparison.
	 * Always returns false for non-V3F objects.*/
	@Override
	public boolean equals(Object other)
	{
		if(other instanceof V3F)
			return (((V3F)other).p[0]==p[0] && ((V3F)other).p[1]==p[1] && ((V3F)other).p[2]==p[2]);
		return false;
	}
	/**Returns whether the manhattan difference between this and the other vector is less than the maximum error.
	 * Always returns false for non-V3F objects.*/
	public boolean equals(Object other, float maxError)
	{
		if(other instanceof V3F)
			return manDistance((V3F) other)<maxError;
		return false;
	}
	/**Checks if this vector is a multiple of the other vector.
	 * Always returns false if exactly one vector is zero.
	 * Returns true if both vectors are zero.*/
	public boolean isMultipleOf(V3F other)
	{
		if(isZero() ^ other.isZero())
			return false;
		if(isZero() && other.isZero())
			return true;
		float m = other.p[0]/p[0];
		return (Math.abs(other.p[1]-p[1]*m) < DEFAULT_ERROR_THRESHOLD && Math.abs(other.p[2]-p[2]*m) < DEFAULT_ERROR_THRESHOLD);
	}
	/**Checks if all components are exactly, bit-by-bit, zero.*/
	public boolean isZero()
	{
		return p[0]==0 && p[1]==0 && p[2]==0;
	}
	/**Sets this vector to the vector from 'from' to 'to', that is, to.sub(from), 
	 * then adds a multiple of orthogonalVector so that the
	 * resulting vector is orthogonal to the orthogonalVector.<br/>
	 * orthogonalVector should be of length 1, and should not be parallel to the vector between from and to.<br/> 
	 * This method does not perform any normalization.<br/>
	 * <br/>
	 * By example, passing a vertical vector as orthogonalVector will result in a horizontal vector, 
	 * regardless of the from and to vectors.
	 * @return this vector*/
	public V3F setFromToOrthogonal(V3F from, V3F to, V3F orthogonalVector)
	{
		set(to);
		sub(from);
		float d = -dot(orthogonalVector);
		add(orthogonalVector,d);
		return this;
	}
	/**Returns the distance from this vector to the other vector, 
	 * projected onto the plane specified by the normal and the location of this vector.<br/>
	 * By example, passing a Z vector as the normal will cause only the X/Y components to be included in the computation.
	 * The method is valid for arbitrary normals, not only axis-aligned ones.*/
	public float distancePlane(V3F other, V3F normal)
	{
		V3F projection = other.clone().sub(this);
		projection.add(normal, -projection.dot(normal));
		return projection.len();
	}
	/**Postmultiply this vector to a float[3][3], replacing the contents of this vector. The matrix is unchanged.
	 * @return this vector
	 * @see Calc#rotationMatrixPostMultiplyOpt(float[][], float[])*/
	public V3F postMultiply(float[][] matrix)
	{
		Calc.rotationMatrixPostMultiplyOpt(matrix, p);
		return this;
	}
}