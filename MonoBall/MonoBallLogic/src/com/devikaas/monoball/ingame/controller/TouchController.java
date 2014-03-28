package com.devikaas.monoball.ingame.controller;

import com.devikaas.monoball.ingame.model.BallGameModel;
import owg.engine.Engine;
import owg.engine.util.Calc;

public class TouchController implements Controller {
    InputController inputController = InputController.getInstance();
    private int stepCounter = 0;
    public boolean prevDirectionLeft = false;
    public boolean prevDirectionRight = false;

    public TouchController() {

    }

    @Override
    public void step() {
        if(Engine.pointer().isPointerButtonDown()) {
            int width = Engine.scene().getWidth();
            // float normalSpeed = (-1+2*Engine.pointer().getLastPointerX()/width)*3;
            // float normalSpeed = (Engine.pointer().getLastPointerX() > width/2 ? 1 : -1);

            // Right direction
            float speed;
            if ((Engine.pointer().getLastPointerX() > width/2)) {
                if (prevDirectionRight) {
                    stepCounter++;
                    speed = (float) Math.log(stepCounter) / 2;

                } else {
                    stepCounter = 1;
                    prevDirectionRight = true;
                    prevDirectionLeft = false;
                    speed = 1;
                }

            } else {
                if (prevDirectionLeft) {
                    stepCounter++;
                    speed = -(float)Math.log(stepCounter) / 2;

                } else {
                    stepCounter = 1;
                    prevDirectionLeft = true;
                    prevDirectionRight = false;
                    speed = -1;
                }
            }

            inputController.moveBall(speed);

        } else if (Engine.pointer().isPointerButtonBeingReleased()) {
            stepCounter = 0;
        }
    }
}
