package owg.engine.graphics;

/**Provides a cross-platform, type safe reference point for blend modes.
 * The GLUtil implementation must provide the value of the functions and operands!*/
public enum BlendMode {
	REPLACE(BlendOp.ONE, BlendOp.ZERO),
	NORMAL(BlendOp.SRC_ALPHA, BlendOp.ONE_MINUS_SRC_ALPHA),
	NORMAL_PRE(BlendOp.ONE, BlendOp.ONE_MINUS_SRC_ALPHA),
	ADD(BlendOp.SRC_ALPHA, BlendOp.ONE),
	INVERT(BlendOp.ONE_MINUS_DST_COLOR, BlendOp.ONE_MINUS_SRC_COLOR),
	MULTIPLY(BlendOp.DST_COLOR, BlendOp.ONE_MINUS_SRC_ALPHA),
	REVERSE_MULTIPLY(BlendOp.ZERO, BlendOp.ONE_MINUS_SRC_COLOR);
	
	public enum BlendOp {
		SRC_ALPHA, ONE_MINUS_SRC_ALPHA, SRC_COLOR, ONE_MINUS_SRC_COLOR, DST_COLOR, ONE_MINUS_DST_COLOR, ONE, ZERO;
		
		int value = -1;
		public int value() {
			return value;
		}
	}
	
	public final BlendOp src, dst;
	
	BlendMode(BlendOp src, BlendOp dst) {
		this.src = src;
		this.dst = dst;
	}
}
