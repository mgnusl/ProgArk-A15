package com.devikaas.monoball;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import owg.engine.Engine;
import owg.engine.EntryPoint;
import owg.engine.GameState;

public class MainActivity extends Activity implements EntryPoint  {
    private MainActivity instance;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        instance = this;

        Engine.initializeEngine(Engine.TargetPlatform.AndroidGLES1, instance);
    }


    @Override
    public GameState getInitialState() {
        return new SplashState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

}
