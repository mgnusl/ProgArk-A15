
package owg.engine.android.gles1;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

import owg.engine.graphics.GLUtil;
import owg.engine.graphics.MatrixStack;
import owg.engine.graphics.PolygonModelF;
import owg.engine.graphics.VertexFloat;
import owg.engine.util.Calc;
import owg.engine.util.V3F;



/**
 * This object can be used to build and store Vertex Arrays in a conceptually simple and general way.
 * Is an implementation of High Performance Renderable.
 */
public class PolygonModelAndroidVAF implements PolygonModelF<GL10>
{
	private GLUtil<GL10> glUtil;

	private ArrayList<VertexFloat> vertices;
	private ArrayList<Short> indices;
	private int index;
	/**Face type*/
	ArrayList<Integer> faceModes;
	ArrayList<Integer> faceModeIndices;
	/**Number of vertices*/
	int totalNumVerts;
	/**Number of indices*/
	int totalNumInds;
	float[] vData;
	FloatBuffer vBuf,cBuf,nBuf,tcBuf;

	short[] iData;
	ShortBuffer iBuf;
	/**The total space of a single vertex with all its data, in number of bytes*/
	int vertexStride;
	/**Whether we're using another VA for vertex data*/
	private boolean remoteData;

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
	private boolean arrayOutdated;
	/**Construct a new object which uses the vertex data from dataSrc
	 * Index data must be specified.*/
	public PolygonModelAndroidVAF(PolygonModelAndroidVAF dataSrc,int faceMode)
	{
		remoteData = true;
		faceModes = new ArrayList<Integer>();
		faceModeIndices = new ArrayList<Integer>();
		faceModes.add(faceMode);
		this.useNormal  = dataSrc.useNormal;
		this.useColor   = dataSrc.useColor;
		this.useAlpha   = dataSrc.useAlpha;
		this.useTexCoord= dataSrc.useTexCoord;
		this.use3DTexCoord = dataSrc.use3DTexCoord;

		vertices = null;
		indices  = new ArrayList<Short>();


		vertexPointer  = dataSrc.vertexPointer;
		normalPointer  = dataSrc.normalPointer;
		colorPointer   = dataSrc.colorPointer;
		numColorComponents = dataSrc.numColorComponents;
		texCoordPointer= dataSrc.texCoordPointer;
		numTexCoordComponents=dataSrc.numTexCoordComponents;

		totalNumVerts = dataSrc.totalNumVerts;
		vData = dataSrc.vData;
		vBuf = dataSrc.vBuf;

		//Duplicates of vBuf with different offsets
		nBuf = dataSrc.nBuf;
		cBuf = dataSrc.cBuf;
		tcBuf = dataSrc.tcBuf;


		vertexStride=dataSrc.vertexStride;
		arrayOutdated = false;
	}
	/**Construct an empty object with the given settings
	 * Note: updateMode currently applies only to vertex data, not index data.*/
	public PolygonModelAndroidVAF(int faceMode, boolean useNormal, boolean useColor, boolean useAlpha, boolean useTexCoord,boolean use3DTexCoord) 
	{
		remoteData = false;
		faceModes = new ArrayList<Integer>();
		faceModeIndices = new ArrayList<Integer>();
		faceModes.add(faceMode);
		this.useNormal  = useNormal;
		this.useColor   = useColor;
		this.useAlpha   = useAlpha;
		this.useTexCoord= useTexCoord;
		this.use3DTexCoord = use3DTexCoord;

		vertices = new ArrayList<VertexFloat>();
		indices  = new ArrayList<Short>();
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
			indices.add((short)(index-1));
			indices.add((short)(index-2));
			indices.add((short)(index-3));
		}
		else
		{
			indices.add((short)(index-3));
			indices.add((short)(index-2));
			indices.add((short)(index-1));
		}
	}
	/* (non-Javadoc)
	 * @see graphics.HPR#triangle(int, int, int)
	 */
	@Override
	public void triangle(int i1, int i2, int i3)
	{
		indices.add((short)i1);
		indices.add((short)i2);
		indices.add((short)i3);
	}
	/* (non-Javadoc)
	 * @see graphics.HPR#quad(boolean)
	 */
	@Override
	public void quad(boolean reverse)
	{
		if(reverse)
		{
			if(faceModes.get(faceModeIndices.size())==GL10.GL_TRIANGLES)
			{
				indices.add((short)(index-2));
				indices.add((short)(index-3));
				indices.add((short)(index-4));
				indices.add((short)(index-3));
				indices.add((short)(index-2));
				indices.add((short)(index-1));
			}
			else
			{
				indices.add((short)(index-1));
				indices.add((short)(index-2));
				indices.add((short)(index-3));
				indices.add((short)(index-4));
			}
		}
		else
		{
			if(faceModes.get(faceModeIndices.size())==GL10.GL_TRIANGLES)
			{
				indices.add((short)(index-4));
				indices.add((short)(index-3));
				indices.add((short)(index-2));
				indices.add((short)(index-1));
				indices.add((short)(index-2));
				indices.add((short)(index-3));
			}
			else
			{
				indices.add((short)(index-4));
				indices.add((short)(index-3));
				indices.add((short)(index-2));
				indices.add((short)(index-1));
			}
		}
	}
	/* (non-Javadoc)
	 * @see graphics.HPR#quad(int, int, int, int)
	 */
	@Override
	public void quad(int i1, int i2, int i3, int i4)
	{
		if(faceModes.get(faceModeIndices.size())==GL10.GL_TRIANGLES)
		{
			indices.add((short)i2);
			indices.add((short)i3);
			indices.add((short)i4);
			indices.add((short)i3);
			indices.add((short)i2);
			indices.add((short)i1);
		}
		else
		{
			indices.add((short)i1);
			indices.add((short)i2);
			indices.add((short)i3);
			indices.add((short)i4);
		}
	}
	/* (non-Javadoc)
	 * @see graphics.HPR#addIndex(int)
	 */
	@Override
	public void addIndex(int index)
	{
		indices.add((short)index);
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
		arrayOutdated = true;
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
	public void end(GLUtil<GL10> glUtil)
	{
		this.glUtil = glUtil;
		glUtil.checkError("Error occured before generating: "+this);
		faceModeIndices.add(iData==null?indices.size():iData.length);
		//Calculate component offsets and stuff
		if(!remoteData)
		{
			vertexPointer  = 0;
			vertexStride   = 3;
			normalPointer  = vertexStride*Calc.bytesPerFloat;
			if(useNormal)
				vertexStride+=3;
			colorPointer   = vertexStride*Calc.bytesPerFloat;
			if(useColor)
				vertexStride+=3;
			numColorComponents = 3;
			if(useAlpha)
			{vertexStride+=1;
			numColorComponents++;}
			texCoordPointer= vertexStride*Calc.bytesPerFloat;
			if(useTexCoord)
				vertexStride+=2;
			numTexCoordComponents=2;
			if(use3DTexCoord)
			{vertexStride+=1;
			numTexCoordComponents++;}

			if(vData==null)
			{
				//Create buffers and interleave data
				totalNumVerts = vertices.size();
				vData = new float[totalNumVerts*vertexStride];
				int vPos = 0;

				for(int i=0; i<totalNumVerts; i++)
				{
					VertexFloat v = vertices.get(i);
					{
						System.arraycopy(v.vertex.data, v.vertex.index, vData, vPos, 3);
						vPos += 3;
						if (useNormal)
						{
							System.arraycopy(v.normal.data, v.normal.index, vData, vPos, 3);
							vPos += 3;
						}
						if (useColor)
						{
							System.arraycopy(v.color.data, v.color.index, vData, vPos, 3);
							vPos += 3;
							if(useAlpha)
							{
								//Accomodate for missing alpha
								if(v.color.length==3)
									vData[vPos] = (float)1;
								else
									vData[vPos] = v.color.data[3];
								vPos++;
							}
						}
						if (useTexCoord)
						{
							System.arraycopy(v.texCoord.data, v.texCoord.index, vData, vPos, v.texCoord.length);
							vPos += v.texCoord.length;
						}
					}
				}
				vBuf = glUtil.asBuffer(vData);
			}
			else
			{
				//Data is prepacked
				totalNumVerts = vData.length/vertexStride;
				vBuf = glUtil.asBuffer(vData);
			}

			vertices=null;//don't need it anymore
			vertexStride*=Calc.bytesPerFloat;

			nBuf = vBuf.duplicate();
			nBuf.position(normalPointer/Calc.bytesPerFloat);
			cBuf = vBuf.duplicate();
			cBuf.position(colorPointer/Calc.bytesPerFloat);
			tcBuf = vBuf.duplicate();
			tcBuf.position(texCoordPointer/Calc.bytesPerFloat);
		}
		if(iData==null)
		{
			totalNumInds = indices.size();
			iData = new short[totalNumInds];
			for(int i=0; i<totalNumInds; i++)
			{
				iData[i] = indices.get(i);
			}
			iBuf = glUtil.asBuffer(iData);
		}
		else
		{
			totalNumInds = iData.length;
			iBuf = glUtil.asBuffer(iData);
		}

		indices=null;//don't need it anymore

		arrayOutdated = false;
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


	/* (non-Javadoc)
	 * @see graphics.HPR#render()
	 */
	@Override
	public void render() 
	{
		GL10 gl = glUtil.getGL();

		if(arrayOutdated)
		{
			vBuf.put(vData);
			vBuf.rewind();
			arrayOutdated = false;
		}

		gl.glVertexPointer(3				, GL10.GL_FLOAT, vertexStride, vBuf);
		if(useNormal)
			gl.glNormalPointer(					  GL10.GL_FLOAT, vertexStride, nBuf);
		if(useColor)
			gl.glColorPointer(numColorComponents, GL10.GL_FLOAT, vertexStride, cBuf);
		if(useTexCoord)
			gl.glTexCoordPointer(numTexCoordComponents	, GL10.GL_FLOAT, vertexStride, tcBuf);

		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		if(useNormal)
			gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
		if(useColor)
			gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
		if(useTexCoord)
			gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

		int end = 0;
		for(int i=0; i<faceModeIndices.size(); i++)
		{
			int currentFaceMode = faceModes.get(i);
			int start = end;
			end = faceModeIndices.get(i);
			int length = end-start;
			//Select position in index buffer
			iBuf.position(start);

			gl.glDrawElements(currentFaceMode,length, GL10.GL_UNSIGNED_SHORT, iBuf);
		}
		iBuf.rewind();

		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		if(useNormal)
			gl.glDisableClientState(GL10.GL_NORMAL_ARRAY);
		if(useColor)
			gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
		if(useTexCoord)
			gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

		//Not necessary:
		vBuf.rewind();
		nBuf.position(normalPointer/Calc.bytesPerFloat);
		cBuf.position(colorPointer/Calc.bytesPerFloat);
		tcBuf.position(texCoordPointer/Calc.bytesPerFloat);
	}

	/* (non-Javadoc)
	 * @see graphics.HPR#dispose(javax.media.opengl.GL)
	 */
	@Override
	public void dispose() 
	{
		this.vData = null;
		this.iData = null;
		this.vBuf = null;
		this.iBuf = null;
		this.nBuf = null;
		this.cBuf = null;
		this.tcBuf = null;
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
		int[] data = new int[iData.length];
		for(int i = 0; i<iData.length; i++)
			data[i] = iData[i];
		return data;
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
			throw new RuntimeException("Cannot set vData because it is already defined for this VA!");
		vData = data;
	}
	@Override
	public void setPrepackedIData(int[] data)
	{
		if(iData!=null)
			throw new RuntimeException("Cannot set iData because it is already defined for this VA!");
		iData = new short[data.length];
		for(int i = 0; i<iData.length; i++)
			iData[i] = (short)data[i];
	}
}
