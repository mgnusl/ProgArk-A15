package com.devikaas.monoball.ingame.controller;

import com.devikaas.monoball.ingame.model.BallGameModel;
import owg.engine.Engine;
import owg.engine.input.VirtualKey;

/**
 * Created by bvx89 on 18/03/14.
 */
public class KeyboardController implements Controller {
    public BallGameModel mBallGameModel;

    public KeyboardController(BallGameModel ballGameModel) {
        mBallGameModel = ballGameModel;
    }

    @Override
    public void step() {
        if(Engine.keyboard().isDown(VirtualKey.VK_LEFT))//TODO controller
            mBallGameModel.setX(-1f);
        if(Engine.keyboard().isDown(VirtualKey.VK_RIGHT)) {
            mBallGameModel.setX(1f);
        }
    }
}
