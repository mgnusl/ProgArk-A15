package com.devikaas.monoball;

import static owg.engine.Engine.*;


import com.devikaas.monoball.ingame.controller.Controller;
import com.devikaas.monoball.ingame.controller.SystemKeyController;
import owg.engine.Engine;
import owg.engine.GameState;
import owg.engine.graphics.ColorF;
import owg.engine.graphics.MatrixStack;
import owg.engine.graphics.Sprite2D;
import owg.engine.util.Compass;
import owg.engine.util.V3F;

public class MenuGameState implements GameState {
	private float viewLeft;
	private float viewTop;

	private float viewWidth;
	private float viewHeight;

	//Scales of buttons and logo
	private float imageScale;
	private float logoScale;


	// Used to dynamically rotate logo.
	private float logoRotation = 0.0f;
	private float logoRotationSpeed = 0.0021f;
	private float logoMaxAngle = 0.1f;

	// Used to dynamically scale logo. logoMinScale will be set according to logoMaxScale.
	// logoMaxScale will be changed to accomodate actual logoScale
	private float logoMinScale;
	private float logoMaxScale = 0.12f;
	private float logoScaleSpeed = 0.0011f;

	//Stores position and size of buttons, to allow easy interaction
	private V3F playPos;
	private V3F quitPos;
	private V3F playSize;
	private V3F quitSize;

	
	//Position of mouse/touch pointer in view
	private V3F viewSpacePointer;

    private Controller sysController;
	
	public MenuGameState() {
		// subImage = 0;
		// font = new SpriteFontRenderer(sprites().get("font"), 1, 1);
		viewSpacePointer = new V3F();
		// audioLib().get("clank").play(1, 0, 1);

        sysController = new SystemKeyController();


		float screenAspect = (float)scene().getWidth()/scene().getHeight();
		viewHeight = 320;
		viewWidth = viewHeight*screenAspect;

		//Scales buttons and logo according to width of screen
		imageScale = viewWidth / 2 / sprites().get("play").getWidth();
		logoScale = viewWidth / 1.2f / sprites().get("logo").getWidth();

		//Sets max and min scale values based on the size, and original logoMaxScale
		logoMinScale = logoScale * (1 - logoMaxScale);
		logoMaxScale = logoScale * (1 + logoMaxScale);

		//Sets button positions based on screen size, and scales according to screen width.
		playPos = new V3F(viewWidth/2, viewHeight/2, 0);
		playSize = new V3F(	sprites().get("play").getWidth() * imageScale,
				sprites().get("play").getHeight() * imageScale, 0);

		//Sets position of quit button below play button
		quitPos = new V3F(playPos.x(), playPos.y() + playSize.y()*1.1f, 0);
		quitSize = playSize;
	}
    @Override
    public void step() {

		// Turns rotation if outside of max or min values, and rotates logo
		if(logoRotation > logoMaxAngle || logoRotation < - logoMaxAngle)
			logoRotationSpeed = -logoRotationSpeed;

		logoRotation += logoRotationSpeed;

		// Turns scaling if outside of max or min values, and rescales the logo
		if(logoScale > logoMaxScale || logoScale < logoMinScale)
			logoScaleSpeed = - logoScaleSpeed;

		logoScale += logoScaleSpeed;


    	//Example: fixed camera height, stretched to fit the screen with the correct aspect ratio

        viewSpacePointer.x(pointer().getLastPointerX()*(float)viewWidth/scene().getWidth());
        viewSpacePointer.y(pointer().getLastPointerY()*(float)viewHeight/scene().getHeight());

		//Handles touch and pointer interactions
        if(pointer().isPointerButtonBeingPressed()){
			//Play Clicked
			if(viewSpacePointer.x() > playPos.x() - playSize.x() / 2 &&
					viewSpacePointer.x() < playPos.x() + playSize.x() / 2 &&
					viewSpacePointer.y() > playPos.y() - playSize.y() / 2&&
					viewSpacePointer.y() < playPos.y() + playSize.y() / 2){
				scene().setState(new BallGameState());

			//Quit Clicked
			}else if(viewSpacePointer.x() > quitPos.x() - quitSize.x() / 2 &&
					viewSpacePointer.x() < quitPos.x() + quitSize.x() / 2 &&
					viewSpacePointer.y() > quitPos.y() - quitSize.y() / 2&&
					viewSpacePointer.y() < quitPos.y() + quitSize.y() / 2){
				Engine.exit(0);
			}
		}

        sysController.step();
    }

    @Override
    public void render() {
    	//Routine work that should be done at beginning of each render, for example in the camera view:
    	
    	//Clear the screen
        glUtil().clearScreen(ColorF.GREEN);
        
        //Set an orthographic projection
        MatrixStack projection = glUtil().projectionMatrix();
        projection.identity();
        projection.ortho(viewLeft, viewLeft+viewWidth, viewTop+viewHeight, viewTop, -1, 1);
        
        //Reset the model transformation matrix
        MatrixStack modelview = glUtil().modelviewMatrix();
        modelview.identity();
        

		glUtil().setColor(ColorF.WHITE);

		//Draws Background
		Sprite2D bg = sprites().get("background");
		bg.render(0, new V3F(0, 0, 0), Compass.NORTHWEST, viewWidth / bg.getWidth(), viewHeight / bg.getHeight(), 0);

		//Draws play and quit buttons
		sprites().get("play").render(0, playPos, Compass.CENTER, imageScale, imageScale, 0);
		sprites().get("quit").render(0, quitPos, Compass.CENTER, imageScale, imageScale,0);
		//Draws logo
		sprites().get("logo").render(0, new V3F(viewWidth / 2, viewHeight / 6, 0), Compass.CENTER, logoScale , logoScale, logoRotation);
    }
}