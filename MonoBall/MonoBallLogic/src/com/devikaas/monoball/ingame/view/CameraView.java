package com.devikaas.monoball.ingame.view;

import static owg.engine.Engine.glUtil;
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
		//Clear the screen
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
