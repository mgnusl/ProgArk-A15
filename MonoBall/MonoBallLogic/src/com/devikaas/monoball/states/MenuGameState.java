package com.devikaas.monoball.states;

import static owg.engine.Engine.*;

import com.devikaas.monoball.model.map.AssetMapGenerator;
import owg.engine.GameState;
import owg.engine.graphics.ColorF;
import owg.engine.graphics.MatrixStack;
import owg.engine.graphics.SpriteFontRenderer;
import owg.engine.util.Compass;
import owg.engine.util.V3F;

public class MenuGameState implements GameState {
	SpriteFontRenderer font;
	
	float subImage;

	private float viewLeft;
	private float viewTop;
	
	private float viewWidth;
	private float viewHeight;
	
	//Position of mouse/touch pointer in view
	private V3F viewSpacePointer;

    private AssetMapGenerator amg;
    private final static int SEED = 1;
	
	public MenuGameState() {
		subImage = 0;
		font = new SpriteFontRenderer(sprites().get("font"), 1, 1);
		viewSpacePointer = new V3F();
		audioLib().get("clank").play(1, 0, 1);

        amg = new AssetMapGenerator(SEED);
    }


    @Override
    public void step() {
    	subImage = (subImage+10f/30)%4; //10 fps animation
    	
    	//Example: fixed camera height, stretched to fit the screen with the correct aspect ratio
        float screenAspect = (float)scene().getWidth()/scene().getHeight();
        viewHeight = 320;
        viewWidth = viewHeight*screenAspect;
        
        viewSpacePointer.x(pointer().getLastPointerX()*(float)viewWidth/scene().getWidth());
        viewSpacePointer.y(pointer().getLastPointerY()*(float)viewHeight/scene().getHeight());
    }

    @Override
    public void render() {
    	//Routine work that should be done at beginning of each render, for example in the camera view:
    	
    	//Clear the screen
        glUtil().clearScreen(ColorF.RED);
        
        //Set an orthographic projection
        MatrixStack projection = glUtil().projectionMatrix();
        projection.identity();
        projection.ortho(viewLeft, viewLeft+viewWidth, viewTop+viewHeight, viewTop, -1, 1);
        
        //Reset the model transformation matrix
        MatrixStack modelview = glUtil().modelviewMatrix();
        modelview.identity();
        
        //Render objects on the screen:
        glUtil().setColor(ColorF.WHITE);
        sprites().get("heliStrip").render((int)subImage, viewSpacePointer, Compass.CENTER, 1, 1, 0);
        
        glUtil().setColor(new ColorF(0xFF8000, false));
        font.render("omfg\ntext", 0, 0);
    }
}
