package com.devikaas.monoball.ingame.controller;

/**
 * Facade for possible game inputs.
 * Created by Stan on 18.03.14.
 */


import com.devikaas.monoball.ingame.controller.Controller;
import com.devikaas.monoball.ingame.model.BallGameModel;

import java.util.ArrayList;
import java.util.List;
public class InputController implements Controller{

    static final InputController INSTANCE = new InputController();
    List<Controller> controllers = new ArrayList<>();
    int stepCount = 0;
    String inputLog = "";
    BallGameModel ballGameModel;


    public InputController(){

    }
    public void setGameModel(BallGameModel ballGameModel){

        this.ballGameModel = ballGameModel;
    }

    public static InputController getInstance() {
        return INSTANCE;
    }

    @Override
    public void step(){
        for (Controller c : controllers) {
            c.step();
        }
        stepCount++;
    }

    public void moveBall(float x){

        ballGameModel.setX(x);
        inputLog += stepCount+"!moveBall:"+x+";";
    }

    public void registerController(Controller c){
        controllers.add(c);
    }

    public String inputLog(){
        return inputLog;
    }
}
