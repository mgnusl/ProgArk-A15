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
    private List<Controller> controllers = new ArrayList<>();
    private int stepCount = 0;
    private static final int MIN_ALLOC = 1024 * 1024;
    private StringBuilder inputLog = new StringBuilder(MIN_ALLOC);
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
        inputLog.append("!moveBall:");
        inputLog.append(x);
        inputLog.append(";");
    }

    public void registerController(Controller c){
        controllers.add(c);
    }

    public String inputLog(){
        return inputLog.toString();
    }
}
