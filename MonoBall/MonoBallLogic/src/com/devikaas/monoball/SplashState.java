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
/**This state is active before the game is fully loaded.*/
public class SplashState implements GameState {
	//The graphic to draw on the splash screen
	private Sprite2D splash;
	//The background color
    private ColorF background = new ColorF(0x387a96, false);
    //The location and size of coordinate space where we render the menu
	private float viewLeft = 0;
	private float viewTop = 0;
	private float viewWidth;
	private float viewHeight;
	//Whether the first frame has been rendered. Don't load before the first render, or there will be a black screen.
	private boolean rendered = false;
	//Whether the resources have been loaded. Don't load multiple times.
	private boolean loaded = false;
	
	//The time at the start of the loading. Display the screen for at least 1 second.
    private long startTime;
    

	public SplashState(){
		viewHeight = scene().getHeight();
		viewWidth = scene().getWidth();

		splash = sprites().get("logo");
		startTime = System.currentTimeMillis();
	}

	@Override
	public void step() {
		if(rendered && !loaded) {
			sprites().loadAssets();
			loaded = true;
		}
		if(System.currentTimeMillis()-startTime > 1000){
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