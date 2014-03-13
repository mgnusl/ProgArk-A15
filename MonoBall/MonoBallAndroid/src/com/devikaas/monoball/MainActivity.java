package com.devikaas.monoball;

import android.app.Activity;
import android.os.Bundle;
import owg.engine.Engine;
import owg.engine.EntryPoint;
import owg.engine.GameState;

public class MainActivity extends Activity implements EntryPoint {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Engine.initializeEngine(Engine.TargetPlatform.AndroidGLES1, this);
        System.out.println("onCreate");
    }

    @Override
    public GameState getInitialState() {
        System.out.println("Initial state");
        return new MenuGameState();
    }
}
