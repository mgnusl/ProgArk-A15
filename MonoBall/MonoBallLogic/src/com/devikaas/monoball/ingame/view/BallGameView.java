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
		camera.render();
		new SpriteView(model.getBall()).render();//TODO
		map.render();
		hud.render();
	}
	
	/**Returns the minimum y value in game world space, that the current projection covers.*/
	public float getVisibleRangeMinY() {
		return model.getCamera().getLocation().y();
	}
	/**Returns the maximum y value in game world space, that the current projection covers.*/
	public float getVisibleRangeMaxY() {
		return model.getCamera().getLocation().y()+model.getCamera().getHeight();
	}
}
