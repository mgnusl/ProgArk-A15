package com.devikaas.monoball;

import com.devikaas.monoball.ingame.controller.Controller;
import com.devikaas.monoball.ingame.controller.InputController;
import com.devikaas.monoball.ingame.controller.KeyboardController;
import com.devikaas.monoball.ingame.controller.ReplayController;
import com.devikaas.monoball.ingame.controller.SystemKeyController;
import com.devikaas.monoball.ingame.controller.TouchController;
import com.devikaas.monoball.ingame.model.BallGameModel;
import com.devikaas.monoball.ingame.model.Player;
import com.devikaas.monoball.ingame.model.Steppable;
import com.devikaas.monoball.ingame.view.BallGameView;

import owg.engine.GameState;

import java.util.ArrayList;
import java.util.List;

public class BallGameState implements GameState {
	BallGameModel model;
	BallGameView view;
    List<Controller> controllers = new ArrayList<>();
    InputController inputController = InputController.getInstance();
    Player playerOne;
	
	public BallGameState() {
		model = new BallGameModel();
		view = new BallGameView(model);

        // Add all controllers
        inputController.setGameModel(model);
        inputController.registerController(new TouchController());
        inputController.registerController(new SystemKeyController());
        inputController.registerController(new KeyboardController());
        //inputController.registerController(new ReplayController(""));

        Player one = new Player(model, "Arne");
        Player two = new Player(model, "Paul");

        model.addPlayer(one);
        model.addPlayer(two);

        model.setGameRunning(true);
	}

	@Override
	public void step() {
        // Reset model X-celeration
        model.setX(0);
        inputController.step();

        model.step();
	}

	@Override
	public void render() {
		view.render();
	}
}
