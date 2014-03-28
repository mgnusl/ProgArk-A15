package com.devikaas.monoball.ingame.controller;

import com.devikaas.monoball.ingame.model.BallGameModel;
import owg.engine.Engine;

public class TouchController implements Controller {
    InputController inputController = InputController.getInstance();

    public TouchController() {

    }

    @Override
    public void step() {
        if(Engine.pointer().isPointerButtonDown()) {
            int width = Engine.scene().getWidth();
            // float normalSpeed = (-1+2*Engine.pointer().getLastPointerX()/width)*3;
            float normalSpeed = (Engine.pointer().getLastPointerX() > width/2 ? 1 : -1) * 2f;

            inputController.moveBall(normalSpeed);

        }
    }
}
