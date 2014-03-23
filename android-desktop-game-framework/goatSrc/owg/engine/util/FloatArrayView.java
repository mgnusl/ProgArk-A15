package owg.engine.util;

/**
 * This object specifies a view into an array of floats. The backing array may be shared with other objects.
 * The object is similar to a FloatBuffer, but its position is immutable and is always backed by a Java array.
 * */
public class FloatArrayView {
	/**The backing array*/
	public final float[] data;
	/**The position of the first element in the rawData array*/
	public final int index;
	/**The number of elements to use in the rawData array*/
	public final int length;
	
	/**Create a view into the entire array.*/
	public FloatArrayView(float[] data) {
		this.data = data;
		index = 0;
		length = data.length;
	}
	/**Create a view into the specified indices of the array.*/
    public FloatArrayView(float[] rawData, int position, int length) {
    	this.data = rawData;
    	this.index = position;
    	this.length = length;
    	}
    
    public FloatArrayView clone() {
    	return new FloatArrayView(data.clone(), index, length);
    }
}
