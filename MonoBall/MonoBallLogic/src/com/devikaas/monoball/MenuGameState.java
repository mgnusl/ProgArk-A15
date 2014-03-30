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
	private float viewLeft = 0;
	private float viewTop = 0;

	private float viewWidth;
	private float viewHeight;

	//Scales of buttons and logo
	private float imageScale;
	private float logoScale;

	//Stores position and size of buttons, to allow easy interaction
	private V3F playPos;
	private V3F quitPos;
	private V3F playSize;
	private V3F quitSize;


    // Reference to sprites
    private Sprite2D backgroundTile;
    private Sprite2D logo;
    private Sprite2D play;
    private Sprite2D quit;

    private int bgReplicates;


	
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

        // Get references to sprites
        backgroundTile = sprites().get("background");
        logo = sprites().get("title");
        quit = sprites().get("quit");
        play = sprites().get("play");

		//Scales buttons and logo according to width of screen
		imageScale = viewWidth / 2 / play.getWidth();
		logoScale = viewWidth / 1.2f / logo.getWidth();

		//Sets button positions based on screen size, and scales according to screen width.
		playPos = new V3F(viewWidth/2, viewHeight/2, 0);
		playSize = new V3F(	play.getWidth() * imageScale,
				            play.getHeight() * imageScale, 0);

		//Sets position of quit button below play button
		quitPos = new V3F(playPos.x(), playPos.y() + playSize.y()*1.1f, 0);
		quitSize = playSize;

        bgReplicates = (int)Math.ceil(viewHeight / (viewWidth / backgroundTile.getWidth() * backgroundTile.getHeight()));
        System.out.println("Bg replicates:" + bgReplicates);

	}
    @Override
    public void step() {

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

            System.out.println("click!");
            System.out.println("X: " + viewSpacePointer.x());
            System.out.println("Y: " + viewSpacePointer.y());
        }

        sysController.step();
    }

    @Override
    public void render() {
    	//Routine work that should be done at beginning of each render, for example in the camera view:
    	
    	//Clear the screen
        glUtil().clearScreen(ColorF.BLACK);
        
        //Set an orthographic projection
        MatrixStack projection = glUtil().projectionMatrix();
        projection.identity();
        projection.ortho(viewLeft, viewLeft+viewWidth, viewTop+viewHeight, viewTop, -1, 1);
        
        //Reset the model transformation matrix
        MatrixStack modelview = glUtil().modelviewMatrix();
        modelview.identity();
        

		glUtil().setColor(ColorF.WHITE);

        float aspect = viewWidth / backgroundTile.getWidth();
        for (int i = 0; i < bgReplicates; i++) {
            backgroundTile.render(0,
                    new V3F(0, i * (backgroundTile.getHeight() * aspect), 0),
                    Compass.NORTHWEST,
                    aspect,
                    aspect,
                    0);
        }


		//Draws play and quit buttons
		play.render(0, playPos, Compass.CENTER, imageScale, imageScale, 0);
		quit.render(0, quitPos, Compass.CENTER, imageScale, imageScale,0);
		//Draws logo
		logo.render(0, new V3F(viewWidth / 2, viewHeight / 6, 0), Compass.CENTER, logoScale , logoScale, 0);
    }
}