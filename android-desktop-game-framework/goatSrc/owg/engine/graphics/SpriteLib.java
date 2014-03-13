package owg.engine.graphics;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

import owg.engine.AssetProducer;
import owg.engine.util.NamedInputStream;

public class SpriteLib {
	private static SpriteLib instance;
	public static SpriteLib sprites() {
		return instance;
	}
	
    private HashMap<String, Sprite2D> sprites;
    
	/**Singleton constructor*/
    public SpriteLib(GLUtil<?> glUtil, AssetProducer assets) {
    	assert instance == null;
    	instance = this;
    	
        sprites = new HashMap<String, Sprite2D>();
        loadDir(glUtil, assets, "textures");
    }
    /**Loads images from a relative path in the "assets" directory.
     * Note that the textures folder is automatically loaded at initialization.*/
    public void loadDir(GLUtil<?> glUtil, AssetProducer assets, String dirName)
    	{
    	for (final String fname : assets.listAssets(dirName)) {
    	    final int lastDot = fname.lastIndexOf('.');
    	    
    	    //Only attempt to load files that have an extension.
    	    if(lastDot != -1) {
                final String extension = fname.substring(lastDot+1, fname.length()).toLowerCase(Locale.ENGLISH);
                //Only attempt to load png files.
                if(extension.equals("png")) {
	                final String nameWithoutExtension = fname.substring(0, lastDot);
	                int lastUnderscore = nameWithoutExtension.lastIndexOf('_');
	                
	                
	                int numXFrames = 1;
	                int numYFrames = 1;
	                String simpleName = nameWithoutExtension;
	                //If name ends with underscore and number, then assume it is an image strip with that number of frames.
	                //If name ends two sets of underscore and number, then assume it is a grid: _xframes_yframes.
	                
	                //Get the last number
	                int parseIntR = -1;
	                if(lastUnderscore != -1) {
	                	String frameCountString = nameWithoutExtension.substring(lastUnderscore+1, nameWithoutExtension.length());
	                	try {
	                		parseIntR = Integer.parseInt(frameCountString);
	                		simpleName = nameWithoutExtension.substring(0, lastUnderscore);
	                	}
	                	catch (NumberFormatException e) {}
	                }
	                if(parseIntR != -1) {
						lastUnderscore = simpleName.lastIndexOf('_');
						//Get the second-to-last number
						int parseIntL = -1;
						if(lastUnderscore != -1) {
							String frameCountString = simpleName.substring(lastUnderscore+1, simpleName.length());
							
							try {
								parseIntL = Integer.parseInt(frameCountString);
								simpleName = simpleName.substring(0, lastUnderscore);
							}
							catch (NumberFormatException e) {}
	 	                }
						//If we found two numbers
						if(parseIntL != -1) {
							numXFrames = parseIntL;
							numYFrames = parseIntR;
						}
						else {//One number
							numXFrames = parseIntR;
						}
	                }
	                
	                try {
		                NamedInputStream file = assets.open(dirName+'/'+fname);
		                Sprite2D texture = glUtil.loadSprite2D(simpleName, file, numXFrames, numYFrames);
		                
		                if(texture != null) {
		                	if(sprites.containsKey(simpleName))
		                		System.err.println("Warning: "+fname+
		                				" overwrites existing sprite with simple name: "+simpleName);
		                	sprites.put(simpleName, texture);
		                }
	                }
	                catch (IOException e) {
	                	System.err.println("Warning: Could not open image file: "+fname);
	                	new Throwable().printStackTrace();
	                }
                }
    	    }
    	}
    }
    public Sprite2D get(String name) {
    	Sprite2D spr = sprites.get(name);
    	if(spr == null)
    		throw new RuntimeException("No such sprite: "+name);
    	return sprites.get(name);
    }
	public void reloadAll()
		{
		Iterator<Sprite2D> it = sprites.values().iterator();
		Sprite2D spr;
		while((spr = it.next())!=null) {
			try
				{
				spr.reload();
				}
			catch (IOException e)
				{
				System.err.println("Failed to reload sprite: "+spr);
				e.printStackTrace();
				sprites.remove(spr);
				}
		}
	}
}
