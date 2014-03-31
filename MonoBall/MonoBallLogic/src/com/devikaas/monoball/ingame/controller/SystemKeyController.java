package com.devikaas.monoball.ingame.controller;

import owg.engine.Engine;
import owg.engine.input.VirtualKey;

/**
 * Controller implementation which handles the escape/back button to exit the game
 */
public class SystemKeyController implements Controller {
    @Override
    public void step() {
        if (Engine.keyboard().isPressed(VirtualKey.VK_ESCAPE)) {
            Engine.exit(0);
        }
    }
}
