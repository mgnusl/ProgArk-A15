package com.devikaas.monoball.ingame.controller;

/**
 * Facade for possible game inputs.
 * Records input events.
 * Created by Stan on 18.03.14.
 */


import com.devikaas.monoball.ingame.controller.Controller;
import com.devikaas.monoball.ingame.model.BallGameModel;

import java.util.ArrayList;
import java.util.List;
public class InputController implements Controller{
	/***/
    private List<Controller> controllers = new ArrayList<>();
    private static final int MIN_ALLOC = 1024 * 1024;
    private StringBuilder inputLog = new StringBuilder(MIN_ALLOC);
    BallGameModel ballGameModel;

    private int stepCounter = 0;


    public InputController(){

    }
    public void setGameModel(BallGameModel ballGameModel){

        this.ballGameModel = ballGameModel;
    }

    @Override
    public void step(){
        for (Controller c : controllers) {
            c.step();
        }
        stepCounter++;
    }

    public void moveBall(float x){

        ballGameModel.setX(x);
        inputLog.append(stepCounter);
        inputLog.append("!moveBall:");
        inputLog.append(x);
        inputLog.append(';');
    }

    public void registerController(Controller c){
        controllers.add(c);
    }

    public String inputLog(){
        return inputLog.toString();
    }
}
