package owg.engine.android.gles1;

import java.io.IOException;
import java.nio.ByteBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import owg.engine.Engine;
import owg.engine.graphics.GLUtil;
import owg.engine.graphics.Sprite2D;
import owg.engine.util.NamedInputStream;

public class Sprite2DAndroidGLES1 extends Sprite2D {
	final GLUtil<GL10> glUtil;
	final String simpleName, fileName;
	final int[] textures;
	int width, height;
	final int numXFrames, numYFrames;
	
	/**Constructor that will attempt to load an image from the indicated file.
	 * @see GLUtil#loadSprite2D*/
    public Sprite2DAndroidGLES1(GLUtil<GL10> glUtil, String simpleName, NamedInputStream file, int numXFrames, int numYFrames) throws IOException {
    	System.out.println("Loading texture "+simpleName+'('+numXFrames+','+numYFrames+')');
    	//Store the reference so the gl instance doesn't have to be explicitly specified each time we want to use the texture
    	this.glUtil = glUtil;
    	//Try to load the file
    	this.fileName = file.name;
    	this.simpleName = simpleName;
    	this.numXFrames = numXFrames;
    	this.numYFrames = numYFrames;
    	textures = new int[numXFrames*numYFrames];
    	
    	reload(file);
    }

    @Override
    public void enable(int subImage) {
    	GL10 gl = glUtil.getGL();
		gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[subImage]);
    }
    @Override
    public void disable() {
    	glUtil.disableTexture2D();
    }

    @Override
    public int getWidth() {
    	return width;
    }

    @Override
    public int getHeight() {
    	return height;
    }
    
    @Override
    public int getNumFrames() {
    	return textures.length;
    }

	@Override
	public void reload() throws IOException {
		reload(Engine.assets().open(fileName));
	}
	private void reload(NamedInputStream file) throws IOException
		{
		Bitmap bmp = BitmapFactory.decodeStream(file);
		
    	//Set to default values if anything failed
    	if(bmp == null) {
    		throw new IOException("Warning: Failed to decode image: "+fileName);
    	}
    	//Set basic properties
    	width = bmp.getWidth()/numXFrames;
    	height = bmp.getHeight()/numYFrames;
    	
    	//Generate texture objects
    	GL10 gl = glUtil.getGL();
		gl.glGenTextures(textures.length, textures, 0);
		
		//Set up temporary storage for bitmap(wasteful, but it will do for now)
		int[] tmp = new int[width*height];
    	ByteBuffer buffer = ByteBuffer.allocateDirect(width*height*4);
    	
    	//Load each subimage as into its texture object
    	for(int y = 0; y<numYFrames; y++) {
    		for(int x = 0; x<numXFrames; x++) {
	    		//Get 32 bit int array from bitmap
	    		bmp.getPixels(tmp, 0, width, x*width, y*height, width, height);
	    		//Convert to byte array
	    		for(int j = 0; j<tmp.length; j++) {
	    			int c = tmp[j];
	    			buffer.put((byte)(( c>>>16 )&0xFF));
	    			buffer.put((byte)(( c>>> 8 )&0xFF));
	    			buffer.put((byte)(( c     )&0xFF));
	    			buffer.put((byte)(( c>>>24 )&0xFF));
	    		}
	    		buffer.rewind();
	    		
	    		//Bind texture object and set properties
				gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[y*numXFrames+x]);
				gl.glTexParameterx(GL10.GL_TEXTURE_2D,GL10.GL_TEXTURE_MIN_FILTER,GL10.GL_LINEAR);
				gl.glTexParameterx(GL10.GL_TEXTURE_2D,GL10.GL_TEXTURE_MAG_FILTER,GL10.GL_NEAREST);
				gl.glTexParameterx(GL10.GL_TEXTURE_2D,GL10.GL_TEXTURE_WRAP_S,GL10.GL_CLAMP_TO_EDGE);
				gl.glTexParameterx(GL10.GL_TEXTURE_2D,GL10.GL_TEXTURE_WRAP_T,GL10.GL_CLAMP_TO_EDGE);
				//Load byte array into the texture object
				gl.glTexImage2D(GL10.GL_TEXTURE_2D, 0, GL10.GL_RGBA, width, height, 0, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, buffer);
				buffer.rewind();
    		}
    	}
    	//Reset GL state
    	gl.glBindTexture(GL10.GL_TEXTURE_2D, 0);
		}
}
