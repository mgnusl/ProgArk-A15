package com.devikaas.monoball;

import static owg.engine.Engine.*;


import com.devikaas.monoball.ingame.controller.Controller;
import com.devikaas.monoball.ingame.controller.SystemKeyController;
import com.devikaas.monoball.ingame.model.BallGameModel;
import com.devikaas.monoball.ingame.model.Player;
import owg.engine.Engine;
import owg.engine.GameState;
import owg.engine.graphics.ColorF;
import owg.engine.graphics.MatrixStack;
import owg.engine.graphics.Sprite2D;
import owg.engine.graphics.SpriteFontRenderer;
import owg.engine.util.Compass;
import owg.engine.util.V3F;

public class GameOverState implements GameState {
    private float viewLeft = 0;
    private float viewTop = 0;

    private float viewWidth;
    private float viewHeight;

    //Scales of buttons and logo
    private float imageScale;

    // Used to dynamically rotate result.
    private float resultRotation = 0.0f;
    private float resultRotationSpeed = 0.015f;
    private float resultMaxAngle = 0.1f;

    //Stores position and size of buttons, to allow easy interaction
    private V3F playPos;
    private V3F playSize;


    //Position of mouse/touch pointer in view
    private V3F viewSpacePointer;

    private Controller sysController;
    private BallGameModel model;
    private SpriteFontRenderer font;
    private Player player1;
    private Player player2;
    String result = "";

    public GameOverState(Player player1,Player player2) {

        this.player1 = player1;
        this.player2 = player2;
        font = new SpriteFontRenderer(sprites().get("font"), 1, 1);
        viewSpacePointer = new V3F();

        sysController = new SystemKeyController();

        float screenAspect = (float)scene().getWidth()/scene().getHeight();
        viewHeight = 320;
        viewWidth = viewHeight*screenAspect;

        // Reset viewport
        glUtil().viewport(0, 0, scene().getWidth(), scene().getHeight());
        glUtil().scissor(0, 0, scene().getWidth(), scene().getHeight());
        glUtil().setScissorEnabled(false);

        //Scales buttons and logo according to width of screen
        imageScale = viewWidth / 2 / sprites().get("play").getWidth();

        //Sets button positions based on screen size, and scales according to screen width.
        playPos = new V3F(viewWidth/2, viewHeight/2+100, 0);
        playSize = new V3F(	sprites().get("play").getWidth() * imageScale,
                sprites().get("play").getHeight() * imageScale, 0);

        //Find result of game
		if(player1.getScore() > player2.getScore()){
            result = "Player1 won!";
        }else if(player1.getScore() < player2.getScore()){
            result = "Player2 won!";
        }else{
            result = "Tie!";
        }
    }
    @Override
    public void step() {

        // Turns rotation if outside of max or min values, and rotates logo
        if(resultRotation > resultMaxAngle || resultRotation < - resultMaxAngle)
            resultRotationSpeed = -resultRotationSpeed;

        resultRotation += resultRotationSpeed;

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
        Sprite2D backgroundTile = sprites().get("background");
        int bgReplicates = (int)Math.ceil(viewHeight / (viewWidth / backgroundTile.getWidth() * backgroundTile.getHeight()));
        float aspect = viewWidth / backgroundTile.getWidth();
        
        for (int i = 0; i < bgReplicates; i++) {
            backgroundTile.render(0,
                    new V3F(0, i * (backgroundTile.getHeight() * aspect), 0),
                    Compass.NORTHWEST,
                    aspect,
                    aspect,
                    0);
        }

        //Draws play button
        sprites().get("play").render(0, playPos , Compass.CENTER, imageScale, imageScale, 0);

        //Render text
        font.render("Game over!",viewWidth/2, 40,Compass.CENTER,1.0f,1.0f,0);
        font.render(result, viewWidth/2, 100,Compass.CENTER,0.80f,1.250f,resultRotation);
        font.render("Player 1: " + player1.getScore(), viewWidth/2, 170,Compass.CENTER,0.75f,0.75f,0);
        font.render("Player 2: " + player2.getScore(), viewWidth/2, 180,Compass.CENTER,0.75f,0.75f,0);
        font.render("Play again?", viewWidth/2, 220,Compass.CENTER,0.45f,0.65f,0);


    }
}