package com.devikaas.monoball.ingame.controller;

import com.devikaas.monoball.ingame.model.BallGameModel;
import owg.engine.Engine;
import owg.engine.input.VirtualKey;

/**
 * Created by bvx89 on 18/03/14.
 */
public class SystemKeyController implements Controller {
    @Override
    public void step() {
        if (Engine.keyboard().isPressed(VirtualKey.VK_ESCAPE)) {
            Engine.exit(0);
        }
    }
}
