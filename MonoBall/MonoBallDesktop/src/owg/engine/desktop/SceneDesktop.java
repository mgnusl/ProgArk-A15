package owg.engine.desktop;

import java.awt.Container;
import java.awt.Dimension;
import java.util.ArrayList;

import javax.media.opengl.GL;
import javax.media.opengl.GL3;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLCanvas;
import owg.engine.Engine;
import owg.engine.Scene;
import owg.engine.audio.AudioLib;
import owg.engine.desktop.audio.JavaSoundAudioLib;
import owg.engine.desktop.gl3.GL3Util;
import owg.engine.graphics.GLUtil;
import owg.engine.graphics.SpriteLib;
import owg.engine.input.KeyboardHandler;
import owg.engine.input.PointerHandler;

public class SceneDesktop extends Scene implements GLEventListener {
	public static final Dimension DEFAULT_SIZE = new Dimension(640, 480);
    private GLCanvas canvas;
    private GLUtil<GL3> glUtil;    
	
    public int getWidth() {
    	return canvas.getWidth();
    }
    public int getHeight() {
    	return canvas.getHeight();
    }    
    public SceneDesktop(Container container)
    	{
    	super();
    	dimensionListeners = new ArrayList<Scene.DimensionListener>();
    	canvas = new GLCanvas();
    	canvas.setPreferredSize(DEFAULT_SIZE);
        canvas.addGLEventListener(this);
        container.add(canvas);
    	}
	@Override
	public void display(GLAutoDrawable drawable) {
		glUtil.setGL((GL3) drawable.getGL());
		glUtil.step();
		if(state != null) {
			state.step();
			state.render();
		}
		Engine.keyboard().resetPressReleaseState();
		Engine.pointer().resetPressReleaseState();
		Engine.audioLib().updateFading();
	}
	@Override
	public void dispose(GLAutoDrawable arg0) {
		// Nop
	}
	@Override
	public void init(GLAutoDrawable drawable) {
		AssetsDesktop assets = new AssetsDesktop();
		
    	AWTFocusHandler f = new AWTFocusHandler();
    	
		GL gl = drawable.getGL();
	//	if(gl.isGL3() || gl.isGL4bc())
			glUtil = new GL3Util((GL3)drawable.getGL(), assets);
	//	else
	//		throw new RuntimeException("No GLUtil implementation for: "+gl+"...");
		
		animator = new AnimatorDesktop(Engine.getDefaultFPS(), drawable);
		SpriteLib sprites = new SpriteLib(glUtil, assets);
		
		KeyboardHandler keyboard = new KeyboardHandlerDesktop(f);
    	PointerHandler pointer = new PointerHandlerDesktop(f, canvas);
    	
    	AudioLib audioLib = new JavaSoundAudioLib(assets);
		
    	Engine.initializationComplete(sprites, glUtil, keyboard, pointer, assets, audioLib);
	}
	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
		for(DimensionListener d : dimensionListeners)
    		d.dimensionChanged(this, w, h);
	}
	@Override
	public void setPreferredSize(int width, int height)
		{
		canvas.setPreferredSize(new Dimension(width, height));
		}
}
