package owg.engine.graphics;

import owg.engine.Engine;
import owg.engine.util.Compass;


public class SpriteFontRenderer
{
	public static final float[] xOffs = {1, 1, 0.5f, 0, 0, 0, 0.5f, 1, 0.5f};
	public static final float[] yOffs = {0.5f, 0, 0, 0, 0.5f, 1, 1, 1, 0.5f};

	private PolygonModelF<?> box;
	private Sprite2D font;
	private float lineSeparation;
	private float charSeparation;

	public SpriteFontRenderer(Sprite2D font, float charSeparation, float lineSeparation)
	{
		this.font = font;
		this.charSeparation = charSeparation;
		this.lineSeparation = lineSeparation;
		this.box = PrimitiveFactory.genSquare(Engine.glUtil(), 0, 0, font.getWidth(), font.getHeight(), 1, 1);
	}
	public void render(String string, float x, float y)
	{
		render(string, x, y, Compass.NORTHWEST, 1f, 1f, 0);
	}
	public void render(String string, float x, float y, Compass orientation, float xScale, float yScale, float angle)
	{
		GLUtil<?> glUtil = Engine.glUtil();
		MatrixStack m = glUtil.modelviewMatrix();
		m.push();
		
		m.translatef(x, y, 0);
		m.scalef(xScale, yScale, 1);
		m.rotatef(angle, 0, 0, -1);
		
		String[] strings = string.split("\n", -1);
		float yOff = -(getStringHeight(strings.length)-(lineSeparation-1)*font.getHeight())*orientation.dy;
		m.translatef(0, yOff, 0);
		for(int py = 0; py<strings.length; py++)
		{
			m.push();
			m.translatef(-(getStringWidth(strings[py])-(charSeparation-1)*font.getWidth())*orientation.dx, 0, 0);
			for(int px = 0; px<strings[py].length(); px++) 
			{
				char c = strings[py].charAt(px);
				font.enable(c);
				box.render();
				m.translatef(getCharWidth(), 0, 0);
			}
			m.pop();
			m.translatef(0, getLineHeight(), 0);
		}
		font.disable();
		m.pop();
	}
	public float getStringHeight(String string)
	{
		return getStringHeight(string.split("\n", -1).length);
	}

	public float getStringHeight(int lines)
	{
		return lines*getLineHeight();
	}

	public float getLineHeight()
	{
		return font.getHeight()*lineSeparation;
	}

	public float getStringWidth(String string)
	{
		return getStringWidth(string.split("\n", -1));
	}

	public float getStringWidth(String[] strings)
	{
		float maxWidth = 0;
		for(int i = 0; i<strings.length; i++)
		{
			maxWidth = Math.max(maxWidth, getCharWidth()*strings[i].length());
		}
		return maxWidth;
	}

	public float getCharWidth()
	{
		return font.getWidth()*charSeparation;
	}
}
