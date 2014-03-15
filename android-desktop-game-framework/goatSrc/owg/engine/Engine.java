package owg.engine;

import java.lang.reflect.InvocationTargetException;

import owg.engine.audio.AudioLib;
import owg.engine.graphics.GLUtil;
import owg.engine.graphics.SpriteLib;
import owg.engine.input.KeyboardHandler;
import owg.engine.input.PointerHandler;

/**Delegates resonsibility for generating the singleton instances, and stores them for later access*/
public class Engine {
	/**Specifies the supported target platforms for the engine.
	 * The individual enums list the most important prerequisites for the platform.*/
    public enum TargetPlatform {
    	/**Android Java/GLES1. Requires the android support libraries(2.0 or above).<br/> 
    	 * The {@link EntryPoint} instance must be an android.app.Activity*/
    	AndroidGLES1,
    	/**Desktop Java/OpenGL. Requires AWT and JOGL.<br/> 
    	 * The {@link EntryPoint} instance must be a java.awt.Container*/
    	Desktop;
    }
    private static EntryPoint entryPoint;
    private static TargetPlatform platform;
    private static Scene scene;
    
    private static boolean isInitialized = false;
    
    private static SpriteLib sprites;
    private static GLUtil<?> glUtil;
    private static KeyboardHandler keyboard;
    private static PointerHandler pointer;
    private static AssetProducer assets;
    private static AudioLib audioLib;
	
	public static int getDefaultFPS() {
		return 30;
	}
    
	/**Call to initialize the engine for the given platform
	 * The {@link EntryPoint} must conform to the type specified by the {@link TargetPlatform}.<br/>
	 * {@link owg.engine.EntryPoint#getInitialState()} will be called as the final step of the initialization.*/
    public static void initializeEngine(final TargetPlatform platform, final EntryPoint entryPoint) {
    	if(Engine.entryPoint != null)
    		throw new RuntimeException("Double engine initialization is not allowed!");
    	Engine.entryPoint = entryPoint;
    	Engine.platform = platform;
    	System.out.println("Initializing engine for platform: "+platform);
    	try {
	    	if(platform == TargetPlatform.Desktop) {
	    		scene = (Scene) Class.forName("owg.engine.desktop.SceneDesktop").getConstructors()[0].newInstance(entryPoint);
            } else if(platform == TargetPlatform.AndroidGLES1) {
	    		scene = (Scene) Class.forName("owg.engine.android.gles1.SceneAndroidGLES1").getConstructors()[0].newInstance(entryPoint);
                System.out.println("Scene started");
            } else {
	    		throw new RuntimeException("Unknown entry point implementation: "+entryPoint+"...");
            }
    	} catch (ClassCastException e) {
    		System.err.println("Entry point for "+platform+" cannot be "+entryPoint);
    		e.printStackTrace();
    	} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
    }
    /**Returns whether the initialization has completed.*/
    public static boolean isInitializationComplete() {
    	return isInitialized;
    }
    
    /**Called from the Scene when it has received a GL instance from the window manager,
     * and all singleton instances have been initialized for the first time. 
     * The implementation must supply instances of all required singletons.*/
    public static void initializationComplete(
    		SpriteLib sprites, 
    		GLUtil<?> glUtil,
    	    KeyboardHandler keyboard,
    	    PointerHandler pointer,
    	    AssetProducer assets,
    	    AudioLib audioLib) {
    	
    	Engine.assets = assets;
    	
    	Engine.glUtil = glUtil;
    	Engine.sprites = sprites;
    	
    	Engine.keyboard = keyboard;
    	Engine.pointer = pointer;
    	
    	Engine.audioLib = audioLib;
    	
    	assert Engine.platform != null;
    	assert Engine.entryPoint != null;
    	assert Engine.assets != null;
    	
    	assert Engine.scene != null;
    	assert Engine.scene.getAnimator() != null;
    	assert Engine.glUtil != null;
    	assert Engine.sprites != null;
    	
    	assert Engine.keyboard != null;
    	assert Engine.pointer != null;
    	
    	assert Engine.audioLib != null;

    	System.out.println("All engine systems initialized, starting game.");
    	
    	scene.setState(Engine.entryPoint.getInitialState());
    	scene.getAnimator().start();
    	isInitialized = true;
	}
    
    public static void exit(int code) {
    	System.exit(code);
    }

    public static EntryPoint entryPoint() {
    	return entryPoint;
    }
    public static TargetPlatform targetPlatform() {
    	return platform;
    }
    public static SpriteLib sprites() {
    	return SpriteLib.sprites();
    }
    public static GLUtil<?> glUtil() {
    	return glUtil;
    }
    public static Scene scene() {
    	return scene;
    }
    public static KeyboardHandler keyboard() {
    	return keyboard;
    }
    public static PointerHandler pointer() {
    	return pointer;
    }
    public static AssetProducer assets() {
    	return assets;
    }
    public static AudioLib audioLib() {
    	return audioLib;
    }
}
