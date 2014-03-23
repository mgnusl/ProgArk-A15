package com.devikaas.monoball;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
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

        getWindow().getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);

        System.out.println("onCreate");
    }

    @Override
    public GameState getInitialState() {
        System.out.println("Initial state");
        return new MenuGameState();
    }
}
