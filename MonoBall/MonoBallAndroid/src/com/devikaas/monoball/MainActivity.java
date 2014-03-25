package com.devikaas.monoball;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import owg.engine.Engine;
import owg.engine.EntryPoint;
import owg.engine.GameState;

public class MainActivity extends Activity  {
    private MainActivity instance;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        instance = this;

        System.out.println("onCreate");


        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {

                Intent gameIntent = new Intent(MainActivity.this, GameActivity.class);
                MainActivity.this.startActivity(gameIntent);
                MainActivity.this.finish();
            }
        }, 1000);

        // Engine.initializeEngine(Engine.TargetPlatform.AndroidGLES1, instance);
    }

    /*
    @Override
    public GameState getInitialState() {

        return new MenuGameState();
    }
    */
}
