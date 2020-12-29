package net.alevel.asteroids.engine.graphics;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.joml.Vector3f;
import org.lwjgl.system.MemoryUtil;

/**Stores data about the object's shape and texture.
 */
public class Mesh {
	private static final Vector3f DEFAULT_COLOUR = new Vector3f(1f, 1f, 1f);
	
	private final int vaoId;
	private final List<Integer> vboIdList;
	private final int vertexCount;
	private Texture texture;
	private Vector3f colour;
	
	private float[] positions; //for physics
	
	public Mesh(float[] positions, float[] textCoords, float[] normals, int[] indices) { //normals are only needed for lighting
		this.positions = positions;
		
		FloatBuffer posBuffer = null;
		FloatBuffer textBuffer = null;
		FloatBuffer normBuffer = null;
		IntBuffer indicesBuffer = null;
		try {
			this.colour = DEFAULT_COLOUR;
			this.vertexCount = indices.length; //openGL needs to know how many points to draw and its harder to get this value from the binded buffer
			this.vboIdList = new ArrayList<Integer>();
			
			this.vaoId = glGenVertexArrays(); //VAO (Vertex Array Object) stores all the properties for the mesh (i.e. all the buffers) in 1 object
			glBindVertexArray(this.vaoId); //tell OpenGL to focus on this object (any methods related to VAOs will affect this object now)
			
			//Vertex buffer objects are buffers that store certain data about a mesh. It is found in the graphics card memory.
			//Create position VBO
			int vboId = glGenBuffers();
			this.vboIdList.add(vboId);
			posBuffer = MemoryUtil.memAllocFloat(positions.length);
			posBuffer.put(positions).flip(); //Switch the buffer to read mode (sets the position to 0)
			glBindBuffer(GL_ARRAY_BUFFER, vboId); //any methods to do with VBOs will affect 'this' buffer
			glBufferData(GL_ARRAY_BUFFER, posBuffer, GL_STATIC_DRAW); //adds the buffered data to the buffer that is currently binded
			glEnableVertexAttribArray(0); //add it to position 0 in the currently binded vertex array object
			glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
			/*index - is the index of the VBO to modify in the VAO.
			 *size - number of values that contribute to the same vertex (3 as this is 3D so coordinates have 3 values)
			 *type - the type of values in the buffer
			 *normalise - whether the vectors should be converted into unit vectors
			 *stride - the number of bytes between each vertex (allows VBOs to contain more properties about a vertex but not use them all immediately)
			 *pointer - the index the reader should start reading from
			 *
			 *the next lines in the try block do the same process but for the other attributes
			 */
			
			//Create texture VBO
			vboId = glGenBuffers();
			this.vboIdList.add(vboId);
			textBuffer = MemoryUtil.memAllocFloat(textCoords.length);
			textBuffer.put(textCoords).flip();
			glBindBuffer(GL_ARRAY_BUFFER, vboId);
			glBufferData(GL_ARRAY_BUFFER, textBuffer, GL_STATIC_DRAW);
			glEnableVertexAttribArray(1);
			glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
			
			//Create normals VBO
			vboId = glGenBuffers();
			this.vboIdList.add(vboId);
			normBuffer = MemoryUtil.memAllocFloat(normals.length);
			normBuffer.put(normals).flip();
			glBindBuffer(GL_ARRAY_BUFFER, vboId);
			glBufferData(GL_ARRAY_BUFFER, normBuffer, GL_STATIC_DRAW);
			glEnableVertexAttribArray(2);
			glVertexAttribPointer(2, 3, GL_FLOAT, false, 0, 0);
			
			//Create indices VBO
			vboId = glGenBuffers();
			this.vboIdList.add(vboId);
			indicesBuffer = MemoryUtil.memAllocInt(indices.length);
			indicesBuffer.put(indices).flip();
			glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboId);
			glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);
			//This is just an array of integers. They do not represent vectors so do not need the last 2 lines that are executed for the other buffers
			
			//Unbind (by passing 0) as no longer running any extra methods that require these VBOs and VAO
			glBindBuffer(GL_ARRAY_BUFFER, 0);
			glBindVertexArray(0);
		} finally { //clear all the buffers as the data is now stored in the graphics card rather than main memory
			if(posBuffer != null)
				MemoryUtil.memFree(posBuffer);
			if(textBuffer != null)
				MemoryUtil.memFree(textBuffer);
			if(normBuffer != null)
				MemoryUtil.memFree(normBuffer);
			if(indicesBuffer != null)
				MemoryUtil.memFree(indicesBuffer);
		}
	}
	
	/*private AABBf createModelAABB(float[] positions) {
		
	}*/
	
	/**Called by the renderer to tell the engine to draw this element onto the screen
	 */
	public void render() {
		if(this.texture != null) {
			glActiveTexture(GL_TEXTURE0); //activate the texture unit (the place where the texture will be stored in graphics card memory)
			glBindTexture(GL_TEXTURE_2D, texture.getId()); //Bind the texture to this unit
		}
		
		glBindVertexArray(this.vaoId); //Get the VAO as the next call is going to draw what is in the VAO that was binded
		glDrawElements(GL_TRIANGLES, this.vertexCount, GL_UNSIGNED_INT, 0);
		
		//Unbind and restore state
		glBindVertexArray(0);
		glBindTexture(GL_TEXTURE_2D, 0);
	}
	
	/**Destroys all VBOs and the VAO. Use this when the game object is about to be destroyed
	 */
	public void cleanUp() {
		//if this method does not work, try binding the VAO and then disabling it
		glDisableVertexAttribArray(0);
		
		//destroy VBOs
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		for(int vboId : this.vboIdList)
			glDeleteBuffers(vboId);
		
		//destroy texture
		if(this.texture != null)
			this.texture.cleanUp();
		
		//Delete the VAO
		glBindVertexArray(0);
		glDeleteVertexArrays(this.vaoId);
	}
	
	/**Checks if the mesh has a texture associated with it
	 * @return <strong>true</strong> if it does
	 */
	public boolean isTextured() {
		return this.texture != null;
	}
	
	public Texture getTexture() {
		return this.texture;
	}
	
	public void setTexture(Texture t) {
		this.texture = t;
	}
	
	public void setColour(Vector3f colour) {
		this.colour = colour;
	}
	
	public Vector3f getColour() {
		return this.colour;
	}
	
	public int getVaoId() {
		return this.vaoId;
	}
	
	public int getVertexCount() {
		return this.vertexCount;
	}
	
	public float[] getVertices() {
		//float[] temp = this.positionsTemp;
		//this.positionsTemp = null;
		return this.positions;
	}
	
	/*public AABBf getModelAABB() {
		return this.boundingBox;
	}*/
}
