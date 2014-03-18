package com.devikaas.monoball;

import com.devikaas.monoball.ingame.controller.Controller;
import com.devikaas.monoball.ingame.controller.KeyboardController;
import com.devikaas.monoball.ingame.controller.SystemKeyController;
import com.devikaas.monoball.ingame.controller.TouchController;
import com.devikaas.monoball.ingame.model.BallGameModel;
import com.devikaas.monoball.ingame.model.Steppable;
import com.devikaas.monoball.ingame.view.BallGameView;

import owg.engine.GameState;

import java.util.ArrayList;
import java.util.List;

public class BallGameState implements GameState {
	BallGameModel model;
	BallGameView view;
    List<Controller> controllers = new ArrayList<>();

	
	public BallGameState() {
		model = new BallGameModel();
		view = new BallGameView(model);

        // Add all controllers
        controllers.add(new TouchController(model));
        controllers.add(new KeyboardController(model));
        controllers.add(new SystemKeyController());

	}

	@Override
	public void step() {
        // Reset model X-celeration
        model.setX(0);

        for (Controller c : controllers) {
            c.step();
        }

        model.step();
	}

	@Override
	public void render() {
		view.render();
	}
}
