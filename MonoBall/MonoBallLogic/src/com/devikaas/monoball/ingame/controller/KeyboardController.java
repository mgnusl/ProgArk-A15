package com.devikaas.monoball.ingame.controller;

import com.devikaas.monoball.ingame.model.BallGameModel;
import owg.engine.Engine;
import owg.engine.input.VirtualKey;

/**
 * Created by bvx89 on 18/03/14.
 */
public class KeyboardController implements Controller {

    InputController inputController = InputController.getInstance();
    public KeyboardController() {
    }

    public interface Command extends Runnable {

    }

    @Override
    public void step() {
        float x = 0;
        if(Engine.keyboard().isDown(VirtualKey.VK_LEFT)) {
           x = -1f;
          inputController.moveBall(x);
        }

        if(Engine.keyboard().isDown(VirtualKey.VK_RIGHT)) {
           x = 1f;
           inputController.moveBall(x);
        }
    }
}
