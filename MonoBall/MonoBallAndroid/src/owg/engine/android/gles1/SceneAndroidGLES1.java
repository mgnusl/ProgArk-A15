package owg.engine.android.gles1;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Environment;
import android.view.Window;
import android.view.WindowManager;
import owg.engine.AssetProducer;
import owg.engine.Engine;
import owg.engine.Scene;
import owg.engine.android.*;
import owg.engine.android.audio.SoundPoolAudioLib;
import owg.engine.audio.AudioLib;
import owg.engine.graphics.SpriteLib;
import owg.engine.input.KeyboardHandler;
import owg.engine.input.PointerHandler;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class SceneAndroidGLES1 extends Scene implements GLSurfaceView.Renderer {
	private final boolean DEBUG = false;
    private GLSurfaceView canvas;
    private GLES1Util glUtil;

    private long lastStepTime;
    private Long vbrGameClock;
    
    public int getWidth() {
    	return canvas.getWidth();
    }
    public int getHeight() {
    	return canvas.getHeight();
    }    
	public SceneAndroidGLES1(Activity activity)
    	{
    	super();
    	//Disable title bar
    	activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
    	activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
    	
    	canvas = new GLSurfaceView(activity);
    	
    	canvas.setEGLConfigChooser(true);
    	
        canvas.setRenderer(this);
        canvas.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        canvas.setFocusable(true);
        canvas.setFocusableInTouchMode(true);
        canvas.requestFocus();
        activity.setContentView(canvas);
    	}
    
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
    	if(DEBUG)
    		gl = new DebugGLES1(gl);
    	if(!Engine.isInitializationComplete()) {
	    	AssetProducer assets = new AssetsAndroid(canvas.getContext());
            String rootPath = Environment.getExternalStorageDirectory().toString();

	    	glUtil = new GLES1Util(gl);
	    	animator = new AnimatorAndroid(Engine.getDefaultTickRate(), canvas);
	    	SpriteLib sprites = new SpriteLib(glUtil, assets);
	    	
	    	KeyboardHandler keyboard = new KeyboardHandlerAndroid(canvas);
	    	PointerHandler pointer = new PointerHandlerAndroid(canvas);
	    	
	    	AudioLib audioLib = new SoundPoolAudioLib(assets);

            vbrGameClock = null;
	    	
	    	Engine.initializationComplete(sprites, glUtil, keyboard, pointer, assets, audioLib, rootPath);
    	}
    	else {
    		//OpenGL context has been recreated. Need to reload sprites...
    		glUtil.setup(gl);
    		Engine.sprites().reloadAll();
    	}
    }
    @Override
    public void onDrawFrame(GL10 gl) {
    	if(DEBUG)
    		gl = new DebugGLES1(gl);
    	glUtil.setGL(gl);
    	glUtil.step();


        if(Engine.useVariableFrameRate()) {
            if(vbrGameClock == null) {
                step();
                vbrGameClock = System.currentTimeMillis();
            }
            //The time between game ticks in milliseconds
            int tickMs = 1000/Engine.getDefaultTickRate();
            //Prevent accumulation of too much lag, in a way that is consistent with constant rate behaviour
            long minTime = System.currentTimeMillis()-(1000*animator.getUpdateFPSFrames())/Engine.getDefaultTickRate();
            if(vbrGameClock < minTime)
                vbrGameClock = minTime;

            long dt = System.currentTimeMillis() - vbrGameClock;
            while(dt >= tickMs) {
                dt -= tickMs;
                vbrGameClock += tickMs;
                step();
            }
        }
        else
            step();

        if(state != null) {
            state.render();
        }
    }

    private void step() {
        lastStepTime = System.currentTimeMillis();
        if(state != null) {
            state.step();
        }

        Engine.keyboard().resetPressReleaseState();
        Engine.pointer().resetPressReleaseState();
        Engine.audioLib().updateFading();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
    	if(DEBUG)
    		gl = new DebugGLES1(gl);
    	gl.glViewport(0, 0, width, height);
    	for(DimensionListener d : dimensionListeners)
    		d.dimensionChanged(this, width, height);
    }
	@Override
	public void setPreferredSize(int width, int height)
		{
		canvas.setMinimumWidth(width);
		canvas.setMinimumHeight(height);
		}

    @Override
    public long getLastStepTime() {
        return lastStepTime;
    }
}
