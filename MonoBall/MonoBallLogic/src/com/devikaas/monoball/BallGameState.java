package com.devikaas.monoball;

import com.devikaas.monoball.ingame.model.BallGameModel;
import com.devikaas.monoball.ingame.view.BallGameView;

import owg.engine.GameState;

public class BallGameState implements GameState {
	BallGameModel model;
	BallGameView view;
	
	public BallGameState() {
		model = new BallGameModel();
		view = new BallGameView(model);
	}

	@Override
	public void step() {
		model.step();
	}

	@Override
	public void render() {
		view.render();
	}
}
