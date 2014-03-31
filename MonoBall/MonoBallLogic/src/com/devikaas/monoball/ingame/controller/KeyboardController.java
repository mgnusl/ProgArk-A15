package com.devikaas.monoball.ingame.controller;

import owg.engine.Engine;
import owg.engine.input.VirtualKey;

/**Controller implementation for keyboard (arrow key) input*/
public class KeyboardController implements Controller {

    InputController inputController;
    public KeyboardController(InputController i) {
    	this.inputController = i;
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
