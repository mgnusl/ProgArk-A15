package owg.engine.graphics;

/**
 * This class defines static methods for generating various mathematically defined models.
 */
public class PrimitiveFactory {
	/**Generates a model of an axis aligned rectangle filling the indicated area on the X/Y plane.<br/>
	 * The minimum texture coordinates will always be 0, but the maximum texture coordinates may be set to the indicated values.*/
	public static<GLT> PolygonModelF<GLT> genSquare(GLUtil<GLT> glUtil, float x0, float y0, float x1, float y1, float textureHRepeat, float textureVRepeat)
	{
		PolygonModelF<GLT> o = glUtil.genModel("Square", false, Primitive.TRIANGLE_STRIP, 
				true, false, false, true, false);
		
		o.addVertex(new VertexFloat(
				new float[]{x0,y1,0},
				new float[]{0,0,1},
				null,
				new float[]{0,textureVRepeat}
				));
		o.addVertex(new VertexFloat(
				new float[]{x0,y0,0},
				new float[]{0,0,1},
				null,
				new float[]{0,0}
				));
		o.addVertex(new VertexFloat(
				new float[]{x1,y1,0},
				new float[]{0,0,1},
				null,
				new float[]{textureHRepeat,textureVRepeat}
				));
		o.triangle(false);
		o.addVertex(new VertexFloat(
				new float[]{x1,y0,0},
				new float[]{0,0,1},
				null,
				new float[]{textureHRepeat,0}
				));
		o.addIndex(o.getIndex()-1);
		o.end(glUtil);
		return o;
	}
}
