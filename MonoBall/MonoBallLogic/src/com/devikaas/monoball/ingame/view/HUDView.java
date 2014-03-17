package com.devikaas.monoball.ingame.view;

import owg.engine.Engine;
import owg.engine.graphics.SpriteFontRenderer;

import com.devikaas.monoball.ingame.model.BallGameModel;

/**Displays textual info about the game*/
public class HUDView implements Renderable {
	private SpriteFontRenderer font;
	private BallGameModel model;

	public HUDView(BallGameModel model) {
		this.model = model;
		font = new SpriteFontRenderer(Engine.sprites().get("font"), 1, 1);
	}

	@Override
	public void render() {
		//TODO stuff
	}

}
