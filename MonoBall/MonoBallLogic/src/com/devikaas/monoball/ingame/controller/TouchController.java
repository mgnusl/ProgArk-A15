package com.devikaas.monoball.ingame.controller;

import owg.engine.Engine;
/**Controller implementation for pointing device input*/
public class TouchController implements Controller {
    InputController inputController;
    private int stepCounter = 0;
    public boolean prevDirectionLeft = false;
    public boolean prevDirectionRight = false;

    public TouchController(InputController i) {
    	inputController = i;
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
