package com.devikaas.monoball.ingame.view;

import owg.engine.Engine;

import com.devikaas.monoball.ingame.model.SpriteModel;
/**Displays an object implementing the SpriteModel interface.*/
public class SpriteView implements Renderable {
	
	private SpriteModel model;
	/**Constructs a new sprite view, bound to the indicated model.*/
	public SpriteView(SpriteModel model) {
		this.model = model;
	}
	/**Sets the current sprite model. Will affect future render calls.*/
	public void setModel(SpriteModel model) {
		this.model = model;
	}
	/**Renders the view with the current sprite model.*/
	@Override
	public void render(float alpha) {
		Engine.sprites().get(model.getSprite()).render(model.getSubimage(), model.getSpriteLocation(alpha), model.getOrientation(), model.getXScale(), model.getYScale(), model.getAngle());
	}
}
