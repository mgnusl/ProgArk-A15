package owg.engine.desktop;

import java.awt.Container;
import java.awt.Dimension;
import java.util.ArrayList;

import javax.media.opengl.DebugGL3;
import javax.media.opengl.GL3;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
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
    	GLCapabilities caps = new GLCapabilities(GLProfile.get(GLProfile.GL3));
    	canvas = new GLCanvas(caps);
    	canvas.setPreferredSize(DEFAULT_SIZE);
        canvas.addGLEventListener(this);
        container.add(canvas);
    	}
	@Override
	public void display(GLAutoDrawable drawable) {
		glUtil.setGL(new DebugGL3((GL3) drawable.getGL()));
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
    	
    	System.out.println("GLSL version: "+drawable.getGL().glGetString(GL3.GL_SHADING_LANGUAGE_VERSION));
		glUtil = new GL3Util(new DebugGL3((GL3)drawable.getGL()), assets);
		
		animator = new AnimatorDesktop(Engine.getDefaultTickRate(), drawable);
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
