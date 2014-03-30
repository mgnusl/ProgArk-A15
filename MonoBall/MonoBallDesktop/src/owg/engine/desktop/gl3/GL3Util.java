package owg.engine.desktop.gl3;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GL3;
import javax.media.opengl.glu.GLU;

import owg.engine.AssetProducer;
import owg.engine.Engine;
import owg.engine.desktop.GLDesktopUtil;
import owg.engine.desktop.PolygonModelDesktopVBOF;
import owg.engine.graphics.ColorF;
import owg.engine.graphics.PolygonModelF;
import owg.engine.graphics.Primitive;
import owg.engine.input.VirtualKey;
import owg.engine.util.Calc;
import owg.engine.util.MatrixStackGeneral;

public class GL3Util extends GLDesktopUtil<GL3> {
	/**Reference to a GLU instance, used to access auxilliary OpenGL functions not part of the spec.*/
	private GLU glu;
	
	/**Equivalent of OpenGL1 matrix stack*/
	private final MatrixStackGeneral modelview = new MatrixStackGeneral(16, "Modelview");
	/**Equivalent of OpenGL1 matrix stack*/
	private final MatrixStackGeneral projection = new MatrixStackGeneral(16, "Projection");
	/**Equivalent of OpenGL1 matrix stack*/
	private final MatrixStackGeneral texture = new MatrixStackGeneral(16, "Modelview");
	
	/**GL3 core spec does not have any default rendering capabilities, so we need to define our own color.*/
	private ColorF.ColorFMutable currentColor;
	
	/**GL3 core spec does not have any default rendering capabilities, so we need to define our own shader.*/
	private int defaultShaderProgram;
	/**White texture to use when texturing is disabled.*/
	private int whiteTexture;
	

	public GL3Util(GL3 gl, AssetProducer assets) {
		super(gl);
		this.glu = new GLU();
		
		int[] tBuf = new int[1];
		gl.glGenTextures(1, tBuf, 0);
		whiteTexture = tBuf[0];
		
		gl.glBindTexture(GL.GL_TEXTURE_2D, whiteTexture);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_REPEAT);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_REPEAT);
		gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);
		gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
		
		ByteBuffer pBuf = asBuffer(new byte[]{(byte)255, (byte)255, (byte)255, (byte)255});
		gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGBA, 1, 1, 0, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, pBuf);
		
		currentColor = ColorF.WHITE.getMutableCopy();
		try {
			defaultShaderProgram = loadShaderFromFile(
					ClassLoader.getSystemClassLoader().getResourceAsStream("shaders/default.vert"), 
					ClassLoader.getSystemClassLoader().getResourceAsStream("shaders/default.frag"));
		}
		catch (IOException e) {
			throw new RuntimeException("Failed to load default shader... ", e);
		}
	}
	
	public int loadShaderFromFile(InputStream vFile, InputStream fFile) throws IOException {
		checkError("Purge: loadShader: "+vFile+", "+fFile);
		
		String vString = Calc.readFileString(vFile);
		final int vShader = gl.glCreateShader(GL2.GL_VERTEX_SHADER);
		gl.glShaderSource(vShader, 1, new String[]{vString}, new int[]{vString.length()},0);
		gl.glCompileShader(vShader);
		vString = null;
		checkError("Vertex shader: "+vFile);
		
		String fString = Calc.readFileString(fFile);
		final int fShader = gl.glCreateShader(GL2.GL_FRAGMENT_SHADER);
		gl.glShaderSource(fShader, 1, new String[]{fString}, new int[]{fString.length()},0);
		gl.glCompileShader(fShader);
		fString = null;
		checkError("Fragment shader: "+fFile);
		
		final int shaderProgram = gl.glCreateProgram();
		gl.glAttachShader(shaderProgram, vShader);
		gl.glAttachShader(shaderProgram, fShader);
		gl.glLinkProgram(shaderProgram);
		gl.glValidateProgram(shaderProgram);
		
		int[] status = new int[1];
        gl.glGetProgramiv(shaderProgram, GL2.GL_LINK_STATUS, status, 0);
        if (status[0] != 1) {
        	int[] logSize = new int[1];
            gl.glGetProgramiv(shaderProgram, GL2.GL_INFO_LOG_LENGTH, logSize, 0);
            int size = logSize[0];
            System.err.println("Program link error: ");
            if (size > 0) {
                ByteBuffer byteBuffer = ByteBuffer.allocate(size);
                IntBuffer none = IntBuffer.allocate(1);
                gl.glGetProgramInfoLog(shaderProgram, size, none, byteBuffer);
                for (byte b : byteBuffer.array()) {
                    System.err.print((char) b);
                }
            } else {
                System.out.println("Unknown");
            }
            return -1;
        }
		
		//Delete shader objects; The binary will be kept in the program object.
		gl.glDeleteShader(vShader);
		gl.glDeleteShader(fShader);
		if(checkError("GLSL Shader Program: "+vFile+", "+fFile))
			return -1;
		
		return shaderProgram;
	}
	
	public ColorF getColor() {
		return currentColor;
	}

	@Override
	public GL3 getGL() {
		return gl;
	}
	@Override
	public boolean checkError(String message) {
		if(!gl.getContext().isCurrent()) {
			System.err.println("Error: Cannot check for errors when context is not current: "+message);
			new Throwable().printStackTrace();
			return true;
		}
		else {
			int error = gl.glGetError();
			if(error!=GL2.GL_NO_ERROR) {
				System.err.println(message+": "+glu.gluErrorString(error));
				new Throwable().printStackTrace();
				return true;
			}
		}
		return false;
	}
	
	@Override
	public void clearScreen(ColorF color) {
		gl.glClearColor(color.get(0), color.get(1), color.get(2), color.get(3));
		gl.glClear(GL3.GL_COLOR_BUFFER_BIT);
	}
	@Override
	public void setColor(ColorF color) {
		currentColor.set(color);
	}

	@Override
	protected void stepImpl() {
		checkError("Purge: stepImpl");
		//Press F11 to reload default shader at runtime.
		if(Engine.keyboard().isPressed(VirtualKey.VK_F11)) {
			try {
				int oldProgram = defaultShaderProgram;
				defaultShaderProgram = loadShaderFromFile(
						Engine.assets().open("shaders/vDefault.glsl"), 
						Engine.assets().open("shaders/fDefault.glsl"));
				if(defaultShaderProgram == -1)
					defaultShaderProgram = oldProgram;
				else
					gl.glDeleteProgram(oldProgram);
				System.out.println("Successfully reloaded default shader.");
			} catch (IOException e) {
				System.err.println("Could not reload default shader... ");
				e.printStackTrace();
			}
		}
	}

	@Override
	public FloatBuffer asBuffer(float[] data) {
		return FloatBuffer.wrap(data);
	}

	@Override
	public IntBuffer asBuffer(int[] data) {
		return IntBuffer.wrap(data);
	}

	@Override
	public ShortBuffer asBuffer(short[] data) {
		return ShortBuffer.wrap(data);
	}

	@Override
	public ByteBuffer asBuffer(byte[] data) {
		return ByteBuffer.wrap(data);
	}

	@Override
	public MatrixStackGeneral modelviewMatrix() {
		return modelview;
	}

	@Override
	public MatrixStackGeneral textureMatrix() {
		return texture;
	}

	@Override
	public MatrixStackGeneral projectionMatrix() {
		return projection;
	}

	public int getDefaultShaderProgram() {
		return defaultShaderProgram;
	}

	@Override
	public PolygonModelF<GL3> genModel(String name, boolean dynamicDraw, Primitive faceMode, boolean useNormal,
			boolean useColor, boolean useAlpha, boolean useTexCoord,
			boolean use3dTexCoord) {
		return new PolygonModelDesktopVBOF<GL3>(name, dynamicDraw?GL.GL_DYNAMIC_DRAW:GL.GL_STATIC_DRAW, 
				faceMode.value(), useNormal, useColor, useAlpha, useTexCoord, use3dTexCoord);
	}
	
	@Override
	public void enableTexture2D(int texture) {
		gl.glBindTexture(GL3.GL_TEXTURE_2D, texture);
	}
	@Override
	public void disableTexture2D() {
		gl.glBindTexture(GL3.GL_TEXTURE_2D, whiteTexture);
	}
}
