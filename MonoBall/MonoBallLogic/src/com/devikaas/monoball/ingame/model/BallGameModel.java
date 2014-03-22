package com.devikaas.monoball.ingame.model;

import owg.engine.util.V3F;

import com.devikaas.monoball.ingame.model.map.CollidableList;
import com.devikaas.monoball.ingame.model.map.MapModel;
import com.devikaas.monoball.ingame.model.map.Row;
/**A fully specified instance of the game model.*/
public class BallGameModel {
	private final CameraModel cameraModel;
	private final MapModel mapModel;
	private final BallModel ballModel;
	
	private final CollidableList collisionHandler;
	
	private final V3F gravity;
    private final int SEED = 7;
	
	public BallGameModel() {
		final float w = MapModel.MAP_WIDTH+64;
		final float h = w*16f/9;
		cameraModel = new CameraModel(new V3F(MapModel.MAP_X-32, -160, 0), w, h);
		mapModel = new MapModel(this, new AssetMapGenerator(SEED));
		collisionHandler = new CollidableList(mapModel);
		gravity = new V3F(0, 1, 0);
		ballModel = new BallModel(this, new V3F(MapModel.MAP_X+MapModel.MAP_WIDTH/2, 0, 0), Row.ROW_HEIGHT/2-1);
		
		collisionHandler.addCollidable(ballModel);
		
		cameraModel.setVerticalSpeed(1);
	}
	/**Returns the game camera model. 
	 * This camera defines the borders where a player will lose a life if they fall outside.*/
	public CameraModel getCamera() {
		return cameraModel;
	}
	public BallModel getBall() {
		return ballModel;
	}
	public MapModel getMap() {
		return mapModel;
	}

    public void setX(float x) {
        gravity.x(x);
    }

    public void reverse() {
        gravity.reverse();
    }

	public void step() {

		cameraModel.step();
		mapModel.step();
		ballModel.step();
		collisionHandler.step();
	}
	public V3F getGravity() {
		return gravity;
	}
}
