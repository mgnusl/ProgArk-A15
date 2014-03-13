package com.devikaas.monoball;

import owg.engine.Engine;
import owg.engine.GameState;
import owg.engine.graphics.ColorF;

public class MenuGameState implements GameState {
    @Override
    public void step() {

    }

    @Override
    public void render() {
        Engine.glUtil().clearScreen(ColorF.RED);
    }
}
