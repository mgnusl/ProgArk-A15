package owg.engine.desktop;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import javax.media.opengl.*;

import owg.engine.desktop.gl3.GL3Util;
import owg.engine.graphics.GLUtil;
import owg.engine.graphics.MatrixStack;
import owg.engine.graphics.PolygonModelF;
import owg.engine.graphics.VertexFloat;
import owg.engine.util.Calc;
import owg.engine.util.MatrixStackGeneral;
import owg.engine.util.V3F;



/**
 * This object can be used to build and store VBOs in a conceptually simple and general way.
 * This implementation uses 32 bit floats for vertex data and is compatible with GL2 and GL3
 */
public class PolygonModelDesktopVBOF<GLT extends GL> implements PolygonModelF<GLT>
{
	private ArrayList<VertexFloat> vertices;
	private ArrayList<Integer> indices;
	private int index;
	boolean vboOutdated;
	/**Face type*/
	ArrayList<Integer> faceModes;
	ArrayList<Integer> faceModeIndices;
	/**Update type*/
	int updateMode;
	/**Number of vertices*/
	int totalNumVerts;
	/**Number of indices*/
	int totalNumInds;
	/**Pointer to the index of the interleaved VBO*/
	int vbo = -1;
	boolean vboBound = false;
	float[] vData;
	FloatBuffer vBuf;
	/**Pointer to the index of the IBO*/
	int ibo = -1;
	boolean iboBound = false;
	int[] iData;
	IntBuffer iBuf;
	/**The total space of a single vertex with all its data*/
	int vertexStride;
	/**Whether we're using another VBO for vertex data*/
	private boolean remoteData;
	
	private String name;

	/**The position of the coordinate data in the vertex data*/
	int vertexPointer;
	/**The position of the normal data in the vertex data*/
	int normalPointer;
	boolean useNormal;
	/**The position of the color data in the vertex data*/
	int colorPointer;
	boolean useColor;
	boolean useAlpha;
	int numColorComponents;
	/**The position of the texture data in the vertex data*/
	int texCoordPointer;
	boolean useTexCoord;
	boolean use3DTexCoord;
	int numTexCoordComponents;
	private GLUtil<GLT> glUtil;
	/**Construct a new object which uses the vertex data from dataSrc
	 * Index data must be specified.*/
	public PolygonModelDesktopVBOF(PolygonModelDesktopVBOF<GLT> dataSrc,int faceMode)
	{
		remoteData = true;
		faceModes = new ArrayList<Integer>();
		faceModeIndices = new ArrayList<Integer>();
		faceModes.add(faceMode);
		this.updateMode = dataSrc.updateMode;
		this.useNormal  = dataSrc.useNormal;
		this.useColor   = dataSrc.useColor;
		this.useAlpha   = dataSrc.useAlpha;
		this.useTexCoord= dataSrc.useTexCoord;
		this.use3DTexCoord = dataSrc.use3DTexCoord;

		vertices = null;
		indices  = new ArrayList<Integer>();


		vertexPointer  = dataSrc.vertexPointer;
		normalPointer  = dataSrc.normalPointer;
		colorPointer   = dataSrc.colorPointer;
		numColorComponents = dataSrc.numColorComponents;
		texCoordPointer= dataSrc.texCoordPointer;
		numTexCoordComponents=dataSrc.numTexCoordComponents;

		vbo = dataSrc.vbo;
		totalNumVerts = dataSrc.totalNumVerts;
		vData = dataSrc.vData;
		vBuf = dataSrc.vBuf;

		vertexStride=dataSrc.vertexStride;
		vboOutdated=false;
	}
	/**Construct an empty object with the given settings
	 * Note: updateMode applies only to vertex data, not index data.*/
	public PolygonModelDesktopVBOF(String name, int updateMode, int faceMode, boolean useNormal, boolean useColor, boolean useAlpha, boolean useTexCoord,boolean use3DTexCoord) 
	{
		this.name = name;
		remoteData = false;
		faceModes = new ArrayList<Integer>();
		faceModeIndices = new ArrayList<Integer>();
		faceModes.add(faceMode);
		this.updateMode = updateMode;
		this.useNormal  = useNormal;
		this.useColor   = useColor;
		this.useAlpha   = useAlpha;
		this.useTexCoord= useTexCoord;
		this.use3DTexCoord = use3DTexCoord;

		vertices = new ArrayList<VertexFloat>();
		indices  = new ArrayList<Integer>();
	}
	/* (non-Javadoc)
	 * @see graphics.HPR#addVertex(graphics.Vertex)
	 */
	@Override
	public void addVertex(VertexFloat v)
	{
		vertices.add(v);
		index++;
	}
	/* (non-Javadoc)
	 * @see graphics.HPR#triangle(boolean)
	 */
	@Override
	public void triangle(boolean reverse)
	{
		if(reverse)
		{
			indices.add(index-1);
			indices.add(index-2);
			indices.add(index-3);
		}
		else
		{
			indices.add(index-3);
			indices.add(index-2);
			indices.add(index-1);
		}
	}
	/* (non-Javadoc)
	 * @see graphics.HPR#triangle(int, int, int)
	 */
	@Override
	public void triangle(int i1, int i2, int i3)
	{
		indices.add(i1);
		indices.add(i2);
		indices.add(i3);
	}
	/* (non-Javadoc)
	 * @see graphics.HPR#quad(boolean)
	 */
	@Override
	public void quad(boolean reverse)
	{
		if(reverse)
		{
			if(faceModes.get(faceModeIndices.size())==GL.GL_TRIANGLES)
			{
				indices.add(index-2);
				indices.add(index-3);
				indices.add(index-4);
				indices.add(index-3);
				indices.add(index-2);
				indices.add(index-1);
			}
			else
			{
				indices.add(index-1);
				indices.add(index-2);
				indices.add(index-3);
				indices.add(index-4);
			}
		}
		else
		{
			if(faceModes.get(faceModeIndices.size())==GL.GL_TRIANGLES)
			{
				indices.add(index-4);
				indices.add(index-3);
				indices.add(index-2);
				indices.add(index-1);
				indices.add(index-2);
				indices.add(index-3);
			}
			else
			{
				indices.add(index-4);
				indices.add(index-3);
				indices.add(index-2);
				indices.add(index-1);
			}
		}
	}
	/* (non-Javadoc)
	 * @see graphics.HPR#quad(int, int, int, int)
	 */
	@Override
	public void quad(int i1, int i2, int i3, int i4)
	{
		if(faceModes.get(faceModeIndices.size())==GL.GL_TRIANGLES)
		{
			indices.add(i2);
			indices.add(i3);
			indices.add(i4);
			indices.add(i3);
			indices.add(i2);
			indices.add(i1);
		}
		else
		{
			indices.add(i1);
			indices.add(i2);
			indices.add(i3);
			indices.add(i4);
		}
	}
	/* (non-Javadoc)
	 * @see graphics.HPR#addIndex(int)
	 */
	@Override
	public void addIndex(int index)
	{
		indices.add(index);
	}
	/* (non-Javadoc)
	 * @see graphics.HPR#getIndex()
	 */
	@Override
	public int getIndex()
	{
		return index;
	}
	/* (non-Javadoc)
	 * @see graphics.HPR#requestUpdate()
	 */
	@Override
	public void requestUpdate()
	{
		vboOutdated=true;
	}

	/* (non-Javadoc)
	 * @see graphics.HPR#addSequence(int)
	 */
	@Override
	public void addSequence(int newFaceMode)
	{
		faceModes.add(newFaceMode);
		faceModeIndices.add(indices.size());
	}

	@Override
	public void addSequence(int terminatingIndex, int newFaceMode)
	{
		faceModes.add(newFaceMode);
		faceModeIndices.add(terminatingIndex);
	}

	/* (non-Javadoc)
	 * @see graphics.HPR#end()
	 */
	@Override
	public void end(GLUtil<GLT> glUtil)
	{
		this.glUtil = glUtil;
		GL gl = glUtil.getGL();

		glUtil.checkError("Error occured before generating: "+this);
		faceModeIndices.add(iData==null?indices.size():iData.length);
		int[] bufferContainer = new int[1];//Used when generating buffers.
		//Calculate component offsets and stuff
		if(!remoteData)
		{
			vertexPointer  = 0;
			vertexStride   = 3;
			normalPointer  = vertexStride*Calc.bytesPerFloat;
			if(useNormal)
				vertexStride+=3;
			colorPointer   = vertexStride*Calc.bytesPerFloat;
			if(useColor) {
				vertexStride+=3;
				numColorComponents = 3;
				if(useAlpha) {
					vertexStride+=1;
					numColorComponents++;
				}
			}
			else
				numColorComponents = 0;
			
			texCoordPointer= vertexStride*Calc.bytesPerFloat;
			if(useTexCoord) {
				vertexStride+=2;
				numTexCoordComponents=2;
				if(use3DTexCoord) {
					vertexStride+=1;
					numTexCoordComponents++;
				}
			}
			else
				numTexCoordComponents = 0;

			// generate VBO handle
			gl.glGenBuffers(1, bufferContainer,0);
			vbo = bufferContainer[0];
			if(vData == null) {
				//Create buffers and interleave data
				totalNumVerts = vertices.size();
				vData = new float[totalNumVerts*vertexStride];
				vBuf = glUtil.asBuffer(vData);
				for(int i=0; i<totalNumVerts; i++) {
					VertexFloat v = vertices.get(i);
					vBuf.put(v.vertex.data, v.vertex.index, v.vertex.length);
					if (useNormal)
						vBuf.put(v.normal.data, v.normal.index, 3);
					if (useColor) {
						//Accomodate for missing alpha
						if(useAlpha && v.color.length==3) {
							vBuf.put(v.color.data, v.color.index, 3);
							vBuf.put(1f);
						}
						else
							vBuf.put(v.color.data, v.color.index, numColorComponents);
					}
					if (useTexCoord) {
						vBuf.put(v.texCoord.data, v.texCoord.index, numTexCoordComponents);
					}
				}
				vBuf.rewind();
			}
			else
			{
				//Data is prepacked
				totalNumVerts = vData.length/vertexStride;
				vBuf = glUtil.asBuffer(vData);
			}
			
			vertices=null;//don't need it anymore
			vertexStride*=Calc.bytesPerFloat;
		}

		//generate IBO handle
		gl.glGenBuffers(1, bufferContainer,0);
		ibo = bufferContainer[0];

		if(iData==null)
		{
			totalNumInds = indices.size();
			iData = new int[totalNumInds];
			iBuf = glUtil.asBuffer(iData);
			for(int i=0; i<totalNumInds; i++)
			{
				iBuf.put(indices.get(i));
			}
			iBuf.rewind();
		}
		else
		{
			totalNumInds = iData.length;
			iBuf = glUtil.asBuffer(iData);
		}
		indices=null;//don't need it anymore

		bindBuffers();
		vboOutdated=false;
		glUtil.checkError("Error in generating: "+this);
	}
	/* (non-Javadoc)
	 * @see graphics.HPR#getVData()
	 */
	@Override
	public float[] getVData()
	{
		return vData;
	}

	private void bindBuffers()
	{
		GL gl = glUtil.getGL();
		int numBytes;
		if(!remoteData)
		{
			// transfer data to VBO
			numBytes = vData.length * Calc.bytesPerFloat;
			gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vbo);
			gl.glBufferData(GL.GL_ARRAY_BUFFER, numBytes, vBuf, updateMode);
			gl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);
			vboBound = true;
		}
		// transfer data to IBO
		numBytes = iData.length * Calc.bytesPerInt;
		gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, ibo);
		gl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, numBytes, iBuf, GL.GL_STATIC_DRAW);
		gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, 0);
		iboBound = true;
	}

	/* (non-Javadoc)
	 * @see graphics.HPR#render()
	 */
	@Override
	public void render() 
	{
		GL gl = glUtil.getGL();
		if(ibo==0)
			System.err.println("VBO not finished!");
		if(vboOutdated)
		{
			gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vbo);
			gl.glBufferData(GL.GL_ARRAY_BUFFER, vData.length*Calc.bytesPerFloat, vBuf, updateMode);
			gl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);
			vboOutdated=false;
		}

		
		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, vbo);
		gl.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, ibo);
		
		if(gl.isGL3()) {
			GL3Util gl3Util = ((GL3Util) glUtil);
			GL3 gl3 = (GL3)gl;
			int shader = gl3Util.getDefaultShaderProgram();
			gl3.glUseProgram(shader);
			
			int globalColorLocation = gl3.glGetUniformLocation( shader, "global_color" );
			gl3.glUniform4fv(globalColorLocation, 1, gl3Util.getColor().getFloat(), 0);
			
			int colorCountLoc = gl3.glGetUniformLocation( shader, "color_count" );
			int normalCountLoc = gl3.glGetUniformLocation( shader, "normal_count" );
			int texCoordCountLoc = gl3.glGetUniformLocation( shader, "texcoord_count" );

            int projectionMatrixLocation = gl3.glGetUniformLocation( shader, "projection_matrix" );
            int modelviewMatrixLocation = gl3.glGetUniformLocation( shader, "modelview_matrix" );
            int textureMatrixLocation = gl3.glGetUniformLocation( shader, "texture_matrix" );
            
            float[] pm = gl3Util.projectionMatrix().getArray().clone();
            MatrixStackGeneral.transpose4x4f(pm);
            float[] mm = gl3Util.modelviewMatrix().getArray().clone();
            MatrixStackGeneral.transpose4x4f(mm);
            float[] tm = gl3Util.textureMatrix().getArray().clone();
            MatrixStackGeneral.transpose4x4f(tm);
            gl3.glUniformMatrix4fv(projectionMatrixLocation, 1, false, pm, 0);
            gl3.glUniformMatrix4fv(modelviewMatrixLocation, 1, false, mm, 0);
            gl3.glUniformMatrix4fv(textureMatrixLocation, 1, false, tm, 0);
            
            
            int vertexLoc = gl3.glGetAttribLocation( shader, "vertex_position" );
            gl3.glEnableVertexAttribArray( vertexLoc );
            gl3.glVertexAttribPointer( vertexLoc, 3, GL.GL_FLOAT, false, vertexStride, vertexPointer );
            
            int normalLoc = gl3.glGetAttribLocation( shader, "vertex_normal" );
            if(normalLoc != -1) {
	            gl3.glEnableVertexAttribArray( normalLoc );
	            gl3.glVertexAttribPointer( normalLoc, 3, GL.GL_FLOAT, false, vertexStride, normalPointer );
            }
            gl3.glUniform1i(normalCountLoc, useNormal?3:0);
            
        	int colorLoc = gl3.glGetAttribLocation( shader, "vertex_color" );
        	if(colorLoc != -1) {
	            gl3.glEnableVertexAttribArray( colorLoc );
	            gl3.glVertexAttribPointer( colorLoc, Math.max(1,numColorComponents), GL.GL_FLOAT, false, vertexStride, colorPointer );
        	}
            gl3.glUniform1i(colorCountLoc, numColorComponents);
        	
            int texCoordLoc = gl3.glGetAttribLocation( shader, "vertex_texcoord" );
        	if(texCoordLoc != -1) {
	            gl3.glEnableVertexAttribArray( texCoordLoc );
	            gl3.glVertexAttribPointer( texCoordLoc, Math.max(1,numTexCoordComponents), GL.GL_FLOAT, false, vertexStride, texCoordPointer );
        	}
            gl3.glUniform1i(texCoordCountLoc, numTexCoordComponents);
            
            int samplerLoc = gl3.glGetUniformLocation(shader, "sampler0");
            gl3.glUniform1i(samplerLoc, 0);
            
			int end = 0;
			for(int i=0; i<faceModeIndices.size(); i++)
			{
				int currentFaceMode = faceModes.get(i);
				int start=end;
				end = faceModeIndices.get(i);
				gl3.glDrawElements(currentFaceMode, end-start, GL2.GL_UNSIGNED_INT, start*Calc.bytesPerInt);
			}
			
			gl3.glDisableVertexAttribArray( 0 );
            if(useNormal) {
	            gl3.glDisableVertexAttribArray( 1 );
            } 
            if(useColor) {
	            gl3.glDisableVertexAttribArray( 2 );
			}
            if(useTexCoord) {
	            gl3.glDisableVertexAttribArray( 3 );
			}
			
			gl3.glUseProgram(0);
		}
		else if(gl.isGL2())
			{
			GL2 gl2 = (GL2) gl;
			gl2.glEnableClientState(GL2.GL_VERTEX_ARRAY);
			
			gl2.glVertexPointer(3, GL.GL_FLOAT, vertexStride, vertexPointer);
			if(useNormal)
				gl2.glNormalPointer(GL.GL_FLOAT, vertexStride, normalPointer);
			if(useColor)
				gl2.glColorPointer(numColorComponents, GL.GL_FLOAT, vertexStride, colorPointer);
			if(useTexCoord)
				gl2.glTexCoordPointer(numTexCoordComponents, GL.GL_FLOAT, vertexStride, texCoordPointer);
			
			gl2.glEnableClientState(GL2.GL_VERTEX_ARRAY);
			if(useNormal)
				gl2.glEnableClientState(GL2.GL_NORMAL_ARRAY);
			if(useColor)
				gl2.glEnableClientState(GL2.GL_COLOR_ARRAY);
	
			if(useTexCoord)
				gl2.glEnableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
			
			int end = 0;
			for(int i=0; i<faceModeIndices.size(); i++)
			{
				int currentFaceMode = faceModes.get(i);
				int start=end;
				end = faceModeIndices.get(i); 
				gl2.glDrawElements(currentFaceMode, end-start, GL2.GL_UNSIGNED_INT, start*Calc.bytesPerInt);
			}
	
			gl2.glDisableClientState(GL2.GL_VERTEX_ARRAY);
			if(useNormal)
				gl2.glDisableClientState(GL2.GL_NORMAL_ARRAY);
			if(useColor)
				gl2.glDisableClientState(GL2.GL_COLOR_ARRAY);
			if(useTexCoord)
				gl2.glDisableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
		}
		else {
			throw new RuntimeException("No VBO rendering implemented for: "+gl);
		}

		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);
		gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, 0);
	}

	/* (non-Javadoc)
	 * @see graphics.HPR#dispose(javax.media.opengl.GL)
	 */
	@Override
	public void dispose() 
	{
		if(vbo != -1 && vboBound)
		{
			glUtil.getGL().glDeleteBuffers(1, new int[] { vbo }, 0);
			vbo = -1;
			vboBound = false;
		}
		if(ibo != -1 && iboBound)
		{
			glUtil.getGL().glDeleteBuffers(1, new int[] { ibo }, 0);
			ibo = -1;
			iboBound = false;
		}
	}
	/* (non-Javadoc)
	 * @see graphics.HPR#render(calc.V3D, double)
	 */
	@Override
	public void render(V3F center, float scale)
	{
		MatrixStack s = glUtil.modelviewMatrix();
		s.push();
		s.translatef(center.x(),center.y(),center.z());
		s.scalef(scale,scale,scale);
		render();
		s.pop();
	}
	/* (non-Javadoc)
	 * @see graphics.HPR#getVStride()
	 */
	@Override
	public int getVStride()
	{
		return vertexStride/Calc.bytesPerFloat;
	}
	/* (non-Javadoc)
	 * @see graphics.HPR#getVertexPointer()
	 */
	@Override
	public int getVertexPointer()
	{
		return vertexPointer/Calc.bytesPerFloat;
	}
	/* (non-Javadoc)
	 * @see graphics.HPR#getNormalPointer()
	 */
	@Override
	public int getNormalPointer()
	{
		return normalPointer/Calc.bytesPerFloat;
	}
	/* (non-Javadoc)
	 * @see graphics.HPR#getColorPointer()
	 */
	@Override
	public int getColorPointer()
	{
		return colorPointer/Calc.bytesPerFloat;
	}
	/* (non-Javadoc)
	 * @see graphics.HPR#getTexCoordPointer()
	 */
	@Override
	public int getTexCoordPointer()
	{
		return texCoordPointer/Calc.bytesPerFloat;
	}
	@Override
	public int[] getIData()
	{
		return iData;
	}
	@Override
	public int[] getFaceModeIndices()
	{
		Integer[] src = faceModeIndices.toArray(new Integer[faceModeIndices.size()]);

		int[] dest = new int[src.length];
		for(int i=0; i<src.length; i++)
			dest[i] = src[i];
		return dest;
	}
	@Override
	public int[] getFaceModes()
	{
		Integer[] src = faceModes.toArray(new Integer[faceModes.size()]);
		int[] dest = new int[src.length];
		for(int i=0; i<src.length; i++)
			dest[i] = src[i];
		return dest;
	}

	@Override
	public void setPrepackedVData(float[] data)
	{
		if(vData!=null)
			throw new RuntimeException("Cannot set vData because it is already defined for this VBO!");
		vData = data;
	}
	@Override
	public void setPrepackedIData(int[] data)
	{
		if(iData!=null)
			throw new RuntimeException("Cannot set iData because it is already defined for this VBO!");
		iData = data;
	}

	@Override
	public void finalize()
	{
		//GC runs in a different thread, so accessing the GL here is unsafe.
		glUtil.invokeLater(new Runnable(){
			@Override
			public void run() {
				glUtil.checkError("Purge: finalize: "+toString());
				if(vbo != -1 && vboBound) {
					glUtil.getGL().glDeleteBuffers(1, new int[]{vbo}, 0);
					glUtil.checkError("Finalize v "+toString()+" failed!");
				}
				if(ibo != -1 && iboBound) {
					glUtil.getGL().glDeleteBuffers(1, new int[]{ibo}, 0);
					glUtil.checkError("Finalize i "+toString()+" failed!");
				}
			}
		});
	}
	@Override
	public String toString() {
		return name;
	}
}