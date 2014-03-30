package com.devikaas.monoball;

import owg.engine.Engine;
import owg.engine.GameState;
import owg.engine.graphics.ColorF;
import owg.engine.graphics.MatrixStack;
import owg.engine.graphics.Sprite2D;
import owg.engine.util.Compass;
import owg.engine.util.V3F;

import static owg.engine.Engine.glUtil;
import static owg.engine.Engine.scene;
import static owg.engine.Engine.sprites;

public class SplashState implements GameState {
	private Sprite2D splash;
    private ColorF background = new ColorF(0x387a96, false);

	private float viewLeft = 0;
	private float viewTop = 0;

	private float viewWidth;
	private float viewHeight;
	private boolean rendered = false;

    private int stepsCounter = 0;
    private final int stepLimit;

	public SplashState(){
		viewHeight = scene().getHeight();
		viewWidth = scene().getWidth();

		splash = sprites().get("logo");
        stepLimit = Engine.getDefaultTickRate();
	}

	@Override
	public void step() {
		if(rendered && (stepsCounter++ >= stepLimit)){
			sprites().loadAssets();
			Engine.scene().setState(new MenuGameState());
		}
	}

	@Override
	public void render() {

		glUtil().clearScreen(background);
		MatrixStack projection = glUtil().projectionMatrix();
		projection.identity();
		projection.ortho(viewLeft, viewLeft+viewWidth, viewTop+viewHeight, viewTop, -1, 1);

		//Reset the model transformation matrix
		MatrixStack modelview = glUtil().modelviewMatrix();
		modelview.identity();

		glUtil().setColor(ColorF.WHITE);


		splash.render(0,
				new V3F(viewWidth / 2, viewHeight / 2, 0),
				Compass.CENTER,
				1,
				1,
				0);
		rendered = true;
	}
}