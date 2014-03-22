package com.devikaas.monoball.ingame.view;

import static owg.engine.Engine.glUtil;
import owg.engine.Engine;
import owg.engine.graphics.ColorF;
import owg.engine.graphics.MatrixStack;
import com.devikaas.monoball.ingame.model.CameraModel;
/**Performs initial setup for the viewport, projection and modelview matrix.<br/>
 * Also clears the screen prior to drawing.<br/>
 * This view should be rendered first!*/
public class CameraView implements Renderable {

	private CameraModel model;
	
	public CameraView(CameraModel model) {
		this.model = model;
	}
	
	@Override
	public void render() {
		float myAspect = (float)model.getWidth()/model.getHeight();
		float screenAspect = (float)Engine.scene().getWidth()/Engine.scene().getHeight();
		int vx, vy, vw, vh;
		
		if(myAspect < screenAspect) {
			vh = Engine.scene().getHeight();
			vw = (int)(myAspect*vh);
		} else {
			vw = Engine.scene().getWidth();
			vh = (int)(vw/myAspect);
		}
		
		vx = (Engine.scene().getWidth()-vw)/2;
		vy = (Engine.scene().getHeight()-vh)/2;
		
		glUtil().viewport(vx, vy, vw, vh);
		glUtil().scissor(vx, vy, vw, vh);
		glUtil().setScissorEnabled(false);
		//Clear the screen
		glUtil().clearScreen(ColorF.BLACK);
		//Clear the game area of the window
		glUtil().setScissorEnabled(true);
        glUtil().clearScreen(model.getClearColor());
        
        
        //Set an orthographic projection
        MatrixStack projection = glUtil().projectionMatrix();
        projection.identity();
        projection.ortho(model.getLocation().x(), model.getLocation().x()+model.getWidth(),
        		model.getLocation().y()+model.getHeight(), model.getLocation().y(), -1, 1);
        
        //Reset the model transformation matrix
        MatrixStack modelview = glUtil().modelviewMatrix();
        modelview.identity();
	}

}
