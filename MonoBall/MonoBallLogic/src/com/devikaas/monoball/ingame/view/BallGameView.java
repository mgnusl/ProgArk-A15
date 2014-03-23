package com.devikaas.monoball.ingame.view;

import owg.engine.Engine;

import com.devikaas.monoball.ingame.model.BallGameModel;
import owg.engine.graphics.ColorF;
import owg.engine.graphics.Sprite2D;
import owg.engine.util.Calc;
import owg.engine.util.Compass;
import owg.engine.util.V3F;

import static owg.engine.Engine.sprites;

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
		float tickMs = 1000/Engine.getDefaultTickRate();
		long currentTime = System.currentTimeMillis();
		float alpha = (currentTime-Engine.scene().getLastStepTime())/tickMs;
		camera.render(alpha);

		float camy = model.getCamera().getInterpolatedLocation(alpha).y();

		Sprite2D BG = sprites().get("backgroundTile");
		float bgy = (float)Calc.cyclic(camy * 0.2f, camy-model.getCamera().getHeight(), camy);

		BG.render(0,
				new V3F(0, bgy, 0),
				Compass.NORTHWEST,
				model.getCamera().getWidth() / BG.getWidth(),
				model.getCamera().getHeight() / BG.getHeight(),
				0);
		BG.render(0,
				new V3F(0, model.getCamera().getHeight()+bgy, 0),
				Compass.NORTHWEST, model.getCamera().getWidth() / BG.getWidth(),
				model.getCamera().getHeight() / BG.getHeight(),
				0);

		new SpriteView(model.getBall()).render(alpha);
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
