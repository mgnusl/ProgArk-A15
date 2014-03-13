package com.devikaas.monoball;

import java.awt.*;
import owg.engine.Engine;
import owg.engine.EntryPoint;
import owg.engine.GameState;

/**
 * Created with IntelliJ IDEA.
 * User: bvx89
 * Date: 13/03/14
 * Time: 15:32
 * To change this template use File | Settings | File Templates.
 */
public class MainFrame extends Frame implements EntryPoint{
    public static void main(String[] args) {
        Frame f = new MainFrame();
    }


    public MainFrame() {
        this.setTitle("MonoBall Desktop");
        Engine.initializeEngine(Engine.TargetPlatform.Desktop, this);
        this.pack();
        this.setVisible(true);

    }

    @Override
    public GameState getInitialState() {
        return new MenuGameState();
    }
}
