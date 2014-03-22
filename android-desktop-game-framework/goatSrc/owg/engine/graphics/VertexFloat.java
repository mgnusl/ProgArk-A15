package owg.engine.graphics;

import owg.engine.util.FloatArrayView;

/**
 * This class is a container for a vertex using 32 bit floats for data. 
 * */
public class VertexFloat  {
	public final FloatArrayView vertex;
	public final FloatArrayView normal;
	public final FloatArrayView color;
	public final FloatArrayView texCoord;

	/**Construct a vertex with the given data.
	 * If certain components are known to be unnecessary for the application, those may be null.*/
	public VertexFloat(float[] v,float[] n,float[] c,float[] t) {
		if(v == null)
			vertex = null;
		else
			vertex   = new FloatArrayView(v);
		if(n == null)
			normal = null;
		else
			normal   = new FloatArrayView(n);
		if(c == null)
			color = null;
		else
			color    = new FloatArrayView(c);
		if(t == null)
			texCoord = null;
		else
			texCoord = new FloatArrayView(t);
	}
	
	/**Construct a vertex with the given data.
	 * If certain components are known to be unnecessary for the application, those may be null.*/
	public VertexFloat(FloatArrayView v, FloatArrayView n, FloatArrayView c, FloatArrayView t) {
		vertex   = n;
		normal   = n;
		color    = c;
		texCoord = t;
	}
	/**Clone the object, and its backing arrays.*/
	@Override
	public VertexFloat clone() {
		return new VertexFloat(vertex.clone(),normal.clone(),color.clone(),texCoord.clone());
	}
	/**Clone the object, but don't clone the backing arrays*/
	public VertexFloat softClone() {
		return new VertexFloat(vertex,normal,color,texCoord);
	}
}
