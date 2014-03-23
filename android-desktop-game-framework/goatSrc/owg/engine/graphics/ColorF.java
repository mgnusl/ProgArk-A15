package owg.engine.graphics;

import owg.engine.util.Calc;
/**Provides a well-organized way to handle OpenGL float colors.
 * The basic implementation is immutable, so its instances may be recklessly shared.*/
public class ColorF {
	public static final ColorF 
	        BLACK = 	new ColorF(new float[]{0,0,0,1}),
			WHITE = 	new ColorF(new float[]{1,1,1,1}),
			DKGRAY = 	new ColorF(new float[]{0.25f,0.25f,0.25f,1}),
			GRAY = 		new ColorF(new float[]{0.5f,0.5f,0.5f,1}),
			LTGRAY = 	new ColorF(new float[]{0.75f,0.75f,0.75f,1}),
			RED = 		new ColorF(new float[]{1,0,0,1}),
			GREEN = 	new ColorF(new float[]{0,1,0,1}),
			BLUE = 		new ColorF(new float[]{0,0,1,1}),
			CYAN = 		new ColorF(new float[]{0,1,1,1}),
			MAGENTA = 	new ColorF(new float[]{1,0,1,1}),
			YELLOW = 	new ColorF(new float[]{1,1,0,1}),
			ORANGE = 	new ColorF(new float[]{1,0.5f,0,1}),
			RUST = 		new ColorF(new float[]{0x8b/255f,0x23/255f,0x0a/255f,1});
	
	/**Parses the integer as RGB bytes. 
	 * The three (unsigned) least significant bytes of the integer are normalized and returned in a new float array.
	 * The resulting array will have 4 elements. The alpha component defaults to 1.*/
	public static float[] rgbToFloat(int hexColor)
		{
		float[] color = new float[4];
		color[3] = 1.0f;
		rgbToFloat(color, hexColor);
		return color;
		}
	/**Parses the integer as RGB bytes. 
	 * The three (unsigned) least significant bytes of the integer are normalized and inserted into the color array.*/
	public static void rgbToFloat(float[] color, int hexColor)
		{
		color[0] = (float)((hexColor&0xFF0000)>>16)/0xFF;
		color[1] = (float)((hexColor&0x00FF00)>> 8)/0xFF;
		color[2] = (float)((hexColor&0x0000FF)    )/0xFF;
		}
	
	/**Parses the integer as ARGB bytes, where A is the most significant byte and B is the least significant byte. 
	 * The four bytes of the integer are normalized and returned in a new float array.
	 * The resulting array will have 4 elements.*/
	public static float[] argbToFloat(int hexColor)
		{
		float[] color = new float[4];
		argbToFloat(color, hexColor);
		return color;
		}
	/**Parses the integer as ARGB bytes, where A is the most significant byte and B is the least significant byte.
	 * The four bytes of the integer are normalized and returned in the color array.*/
	public static void argbToFloat(float[] color, int hexColor)
		{
		color[0] = (float)((hexColor&0x00FF0000)>>16)/0xFF;
		color[1] = (float)((hexColor&0x0000FF00)>> 8)/0xFF;
		color[2] = (float)((hexColor&0x000000FF)    )/0xFF;
		color[3] = (float)((hexColor&0xFF000000)>>24)/0xFF;
		}
	/**Encodes the 4 element float color into an integer as ARGB, 
	 * where A is the most significant byte and B is the least significant byte.*/
	public static int floatToARGB(float[] color) {
		return	(((int)(color[3]*255)&0xFF)<<24) |
				(((int)(color[0]*255)&0xFF)<<16) |
				(((int)(color[1]*255)&0xFF)<< 8) |
				(((int)(color[2]*255)&0xFF));
	}
	/**Represents a mutable ColorF. <br/>
	 * This is useful for objects that have a color attribute that needs to change over time.*/
	public static class ColorFMutable extends ColorF {
		/**A public view of the 4-element backing array for the color.*/
		public final float[] c = data;
		
		public ColorFMutable() {
			super();
		}
		
		public ColorFMutable(float[] color) {
			super(color);
		}
		public ColorFMutable(float r, float g, float b, float a) {
			super(r,g,b,a);
		}
		/**
		 * @param color The 32 bit integer color, formatted as ARGB,
		 * where A is the most significant byte and B is the least significant byte.
		 * @param useAlpha Whether to use the Alpha byte from the integer. If false, the alpha is set to 1.
		 */
		public ColorFMutable(int color, boolean useAlpha) {
			super(color, useAlpha);
		}
		public ColorFMutable(byte r, byte g, byte b, byte a) {
			super(r,g,b,a);
		}
		public ColorFMutable(ColorF srcColor) {
			super(srcColor.data);
		}
		
		public void setRed(byte r) {
			data[0] = (r&0xFF)/255f;
		}
		public void setGreen(byte g) {
			data[1] = (g&0xFF)/255f;
		}
		public void setBlue(byte b) {
			data[2] = (b&0xFF)/255f;
		}
		public void setAlpha(byte a) {
			data[3] = (a&0xFF)/255f;
		}
		
		public void setRed(float r) {
			data[0] = r;
		}
		public void setGreen(float g) {
			data[1] = g;
		}
		public void setBlue(float b) {
			data[2] = b;
		}
		public void setAlpha(float a) {
			data[3] = a;
		}
		
		public void set(float r, float g, float b, float a) {
			Calc.fill(data, r, g, b, a);
		}
		public void set(byte r, byte g, byte b, byte a) {
			Calc.fill(data, (r&0xFF)/255f, (g&0xFF)/255f, (b&0xFF)/255f, (a&0xFF)/255f);
		}
		
		public void set(float[] src) {
			System.arraycopy(src, 0, data, 0, 4);
		}
		
		public void setARGB(int argb) {
			argbToFloat(data, argb);
		}
		public void setRGB(int rgb) {
			rgbToFloat(data, rgb);
		}
		
		public void set(ColorF srcColor) {
			data[0] = srcColor.data[0];
			data[1] = srcColor.data[1];
			data[2] = srcColor.data[2];
			data[3] = srcColor.data[3];
		}
	}

	/**The backing array for the color. Is not exposed in immutable ColorF objects.*/
	protected final float[] data;
	
	public ColorF(float[] color) {
		data = color.clone();
	}
	public ColorF(float r, float g, float b, float a) {
		data = new float[]{r,g,b,a};
	}
	/**
	 * @param color The 32 bit integer color, formatted as ARGB,
	 * where A is the most significant byte and B is the least significant byte.
	 * @param useAlpha Whether to use the Alpha byte from the integer. If false, the alpha is set to 1.
	 */
	public ColorF(int color, boolean useAlpha) {
		if(useAlpha)
			data = argbToFloat(color);
		else
			data = rgbToFloat(color);
	}
	public ColorF(byte r, byte g, byte b, byte a) {
		data = new float[]{(r&0xFF)/255f,(g&0xFF)/255f,(b&0xFF)/255f,(a&0xFF)/255f};
	}
	
	public ColorF() {
		data = new float[4];
	}
	
	public float getRed() {
		return data[0];
	}
	public float getGreen() {
		return data[1];
	}
	public float getBlue() {
		return data[2];
	}
	public float getAlpha() {
		return data[3];
	}
	public int getARGB() {
		return floatToARGB(data);
	}
	/**@return A new 4 element float array representing the color.*/
	public float[] getFloat() {
		return data.clone();
	}
	/**
	 * @param index The index of the desired color component; Red = 0, Green = 1, Blue = 2, Alpha = 3.
	 * @return The color component with the indicated index.
	 */
	public float get(int index) {
		return data[index];
	}
	
	public ColorFMutable getMutableCopy() {
		return new ColorFMutable(data);
	}
	@Override
	public String toString() {
		return "r: "+data[0]+" g: "+data[1]+" b: "+data[2]+" a: "+data[3];
	}
}
