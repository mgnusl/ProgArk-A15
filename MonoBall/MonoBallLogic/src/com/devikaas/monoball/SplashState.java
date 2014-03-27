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

/**
 * Created by oknak_000 on 3/27/14.
 */
public class SplashState implements GameState {
	private Sprite2D splash;

	private float viewLeft;
	private float viewTop;

	private float viewWidth;
	private float viewHeight;
	private int wait = 5;

	public SplashState(){
		float screenAspect = (float)scene().getWidth()/scene().getHeight();
		viewHeight = 320;
		viewWidth = viewHeight*screenAspect;

		// Get references to sprites
		//splash = sprites().get("splash");
	}

	@Override
	public void step() {
		wait--;
		if(wait == 0){
			sprites().loadAssets();
			Engine.scene().setState(new MenuGameState());
		}
	}

	@Override
	public void render() {
		glUtil().clearScreen(ColorF.BLACK);
		MatrixStack projection = glUtil().projectionMatrix();
		projection.identity();
		projection.ortho(viewLeft, viewLeft+viewWidth, viewTop+viewHeight, viewTop, -1, 1);

		//Reset the model transformation matrix
		MatrixStack modelview = glUtil().modelviewMatrix();
		modelview.identity();

		glUtil().setColor(ColorF.WHITE);

		Sprite2D logo = sprites().get("logo");
		float scale = viewWidth * 0.9f / logo.getWidth();
		logo.render(0,
				new V3F(viewWidth / 2, viewHeight / 2, 0),
				Compass.CENTER,
				scale,
				scale,
				0);
	}





}
