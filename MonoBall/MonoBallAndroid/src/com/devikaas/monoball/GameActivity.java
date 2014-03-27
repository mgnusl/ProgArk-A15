package com.devikaas.monoball;

import android.app.Activity;
import android.os.Bundle;
import owg.engine.Engine;
import owg.engine.EntryPoint;
import owg.engine.GameState;

/**
 * Created by bvx89 on 24/03/14.
 */
public class GameActivity extends Activity implements EntryPoint {

    private GameActivity instance;

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);

        instance = this;

        Engine.initializeEngine(Engine.TargetPlatform.AndroidGLES1, instance);

    }



    @Override
    public GameState getInitialState() {
        return new SplashState();
    }
}
