package com.devikaas.monoball.ingame.view;

import com.devikaas.monoball.ingame.model.BallGameModel;
/**Specifies a view of the entire game model.*/
public class BallGameView {
	private CameraView camera;
	private HUDView hud;
	private MapView map;
	
	private BallGameModel model;

	/**Creates a new game view attached to the indicated game model.*/
	public BallGameView(BallGameModel model) {
		this.model = model;
		
		camera = new CameraView(model.getCamera());
		map = new MapView(this, model.getMap());
		hud = new HUDView(model);
	}
	/**Renders the entire view.*/
	public void render() {
		float alpha = 1;
		camera.render(alpha);
		new SpriteView(model.getBall()).render(alpha);//TODO
		map.render(alpha);
		hud.render(alpha);
	}
	
	/**Returns the minimum y value in game world space, that the current projection covers.*/
	public float getVisibleRangeMinY(float alpha) {
		return model.getCamera().getInterpolatedLocation(alpha).y();
	}
	/**Returns the maximum y value in game world space, that the current projection covers.*/
	public float getVisibleRangeMaxY(float alpha) {
		return model.getCamera().getInterpolatedLocation(alpha).y()+model.getCamera().getHeight();
	}
}
