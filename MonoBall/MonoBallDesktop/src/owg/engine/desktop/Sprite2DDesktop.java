package owg.engine.desktop;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;
import javax.media.opengl.GL;

import owg.engine.Engine;
import owg.engine.graphics.GLUtil;
import owg.engine.graphics.Sprite2D;
import owg.engine.util.NamedInputStream;

public class Sprite2DDesktop extends Sprite2D {
	final GLUtil<? extends GL> glUtil;
	final String name, fileName;
	final int[] textures;
	int width, height;
	final int numXFrames, numYFrames;
	

	/**Constructor that will attempt to load an image from the indicated file.
	 * @see GLUtil#loadSprite2D*/
    public Sprite2DDesktop(GLUtil<? extends GL> glUtil, String simpleName, NamedInputStream file, int numXFrames, int numYFrames) throws IOException {
    	System.out.println("Loading texture "+simpleName+'('+numXFrames+','+numYFrames+')');
    	//Store the reference so the gl instance doesn't have to be explicitly specified each time we want to use the texture
    	this.glUtil = glUtil;
    	textures = new int[numXFrames*numYFrames];
    	this.name = simpleName;
    	this.fileName = file.name;
    	this.numXFrames = numXFrames;
    	this.numYFrames = numYFrames;
    	reload(file);
    }
    @Override
    public void enable(int subImage) {
    	glUtil.enableTexture2D(textures[subImage]);
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
	private void reload(NamedInputStream file) throws IOException {
		//Try to load the file
    	BufferedImage image;
		image = ImageIO.read(file);
		if (image.getType() != BufferedImage.TYPE_4BYTE_ABGR) {
			BufferedImage tmp = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
			Graphics g = tmp.getGraphics();
			g.drawImage(image, 0, 0, null);
			g.dispose();
			image.flush();
			image = tmp;
		}
		
    	//Set basic properties
    	width = image.getWidth()/numXFrames;
    	height = image.getHeight()/numYFrames;
    	
    	//Generate texture objects
    	GL gl = glUtil.getGL();
		gl.glGenTextures(textures.length, textures, 0);
		
		//Set up temporary storage for bitmap
		byte[] data = new byte[width*height*4];
    	ByteBuffer buffer = ByteBuffer.wrap(data);
    	
    	//Load each subimage as into its texture object
    	for(int y = 0; y<numYFrames; y++) {
    		for(int x = 0; x<numXFrames; x++) {
	    		//Get byte array from bitmap
	    		image.getRaster().getDataElements(x*width, y*height, width, height, data);
	    		
	    		//Bind texture object and set properties
				gl.glBindTexture(GL.GL_TEXTURE_2D, textures[y*numXFrames+x]);
				gl.glTexParameteri(GL.GL_TEXTURE_2D,GL.GL_TEXTURE_MIN_FILTER,GL.GL_LINEAR);
				gl.glTexParameteri(GL.GL_TEXTURE_2D,GL.GL_TEXTURE_MAG_FILTER,GL.GL_NEAREST);
				gl.glTexParameteri(GL.GL_TEXTURE_2D,GL.GL_TEXTURE_WRAP_S,GL.GL_CLAMP_TO_EDGE);
				gl.glTexParameteri(GL.GL_TEXTURE_2D,GL.GL_TEXTURE_WRAP_T,GL.GL_CLAMP_TO_EDGE);
				//Load byte array into the texture object
				gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGBA, width, height, 0, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, buffer);
				buffer.rewind();
    		}
    	}
    	//Reset GL state
    	glUtil.disableTexture2D();
	}
}
