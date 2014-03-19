package com.devikaas.monoball.ingame.controller;

import com.devikaas.monoball.ingame.model.BallGameModel;
import com.devikaas.monoball.ingame.model.Steppable;
import owg.engine.Engine;

public class TouchController implements Controller {
    public BallGameModel mBallGameModel;

    public TouchController(BallGameModel ballGameModel) {
        mBallGameModel = ballGameModel;
    }

    @Override
    public void step() {
        if(Engine.pointer().isPointerButtonDown()) {
            int width = Engine.scene().getWidth();

            float normalSpeed = (-1+2*Engine.pointer().getLastPointerX()/width);
            mBallGameModel.setX(normalSpeed);

        }
    }
}
