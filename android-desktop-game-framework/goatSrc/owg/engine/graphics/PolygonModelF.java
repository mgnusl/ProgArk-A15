package owg.engine.graphics;

import owg.engine.util.V3F;

/**
 * A polygonal renderable. Could either be a Vertex Buffer Object, a Vertex Array, or a Display List.
 * The construction functions do not perform any OpenGL calls until end() is called.
 * Subclasses of this interface will use 32 bit float precision for all vertex data.
 * */
public interface PolygonModelF<GLT> {
	/**Can be used to set the index data instead of adding indices/faces, when building the HPR.*/
	public abstract void setPrepackedIData(int[] data);

	/**Can be used to specify explicitly when the current sequence should end, and what the next sequence's mode should be.
	 * Note that the last sequence is automatically ended after the last index when end() is called.
	 * Also note that the first sequence is automatically started when the object is created.*/
	public abstract void addSequence(int terminatingIndex, int newFaceMode);

	/**Makes a triangle from the last three vertices.*/
	public abstract void triangle(boolean reverse);

	/**Add 3 indices. Use to make faces from vertices.*/
	public abstract void triangle(int i1, int i2, int i3);

	/**Add two triangles from the last 4 vertices. If faceMode is GL_TRIANGLES, 6 indices are added.*/
	public abstract void quad(boolean reverse);

	/**Add 6 indices from 4 inputs as if it was a trianglestrip, or 4 indices if the mode is currently triangle_strip.*/
	public abstract void quad(int i1, int i2, int i3, int i4);

	/**Add an index. Use to make faces from vertices.*/
	public abstract void addIndex(int index);

	/**Get the current size of the vertex list*/
	public abstract int getIndex();

	/**Call after changing the vertex array. Update mode should be dynamic if this is called often.
	 * The HPR or directbuffers get updated on the next render call.*/
	public abstract void requestUpdate();

	/**Terminate the current sequence and begin a new one with the indicated face mode*/
	public abstract void addSequence(int newFaceMode);

	/**Complete the HPR from the data input.*/
	public abstract void end(GLUtil<GLT> gl);

	/**Changes to this array are not supported, but it is useful for saving model data, etc.*/
	public abstract int[] getIData();

	/**Changes to this array are not supported, but it is useful for saving model data, etc.*/
	public abstract int[] getFaceModeIndices();

	/**Changes to this array are not supported, but it is useful for saving model data, etc.*/
	public abstract int[] getFaceModes();

	/**Render*/
	public abstract void render();

	/**Free memory*/
	public abstract void dispose();

	/**Render at the given location with the indicated scale.*/
	public abstract void render(V3F center, float scale);

	/**Get the number of indices occupied by a full vertex in the float array*/
	public abstract int getVStride();

	/**Get the index of the vertex position data relative to the start of the nearest full vertex in the float array*/
	public abstract int getVertexPointer();

	/**Get the index of the normal data relative to the start of the nearest full vertex in the float array*/
	public abstract int getNormalPointer();

	/**Get the index of the color data relative to the start of the nearest full vertex in the float array*/
	public abstract int getColorPointer();

	/**Get the index of the texture coordinate data relative to the start of the nearest full vertex in the float array*/
	public abstract int getTexCoordPointer();

	/**Add a vertex
	 * The vertex should contain the data that was specified when constructing the empty HPR.
	 * Note: It is safe to specify a 4-component color even if alpha is not used, and vica versa.
	 * If the exact layout is known, a Vertex with raw data may be used, but the data should only be for one vertex.*/
	public abstract void addVertex(VertexFloat v);

	/**Can be used to set the vertex data instead of adding Vertex objects, when building the HPR.*/
	public abstract void setPrepackedVData(float[] data);

	/**Use the returned array to change the vertex data. Call requestUpdate() when done.*/
	public abstract float[] getVData();
}