package net.alevel.asteroids.engine.graphics;

import static org.lwjgl.stb.STBImage.*;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.system.MemoryStack;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;

/**Stores a texture
 */
public class Texture {
	private final int id;
	
	public Texture(String fileName) {
		this(loadTexture(fileName));
	}
	
	public Texture(int id) {
		this.id = id;
	}
	
	public void bind() {
		glBindTexture(GL_TEXTURE_2D, this.id);
	}
	
	public int getId() {
		return this.id;
	}
	
	private static int loadTexture(String fileName) {
		int width;
		int height;
		ByteBuffer buf;
		try (MemoryStack stack = MemoryStack.stackPush()) {
			IntBuffer w = stack.mallocInt(1);
			IntBuffer h = stack.mallocInt(1);
			IntBuffer channels = stack.mallocInt(1);
			
			//load the raw bytes from the specified file and stores it in the byte buffer
			if((buf = stbi_load(fileName, w, h, channels, 4)) == null)
				throw new IllegalStateException("Error loading image '" + fileName + "': " + stbi_failure_reason());
			
			width = w.get();
			height = h.get();
		}
		
		//create texture object and bind to it as next methods will be referring to it
		int textureId = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, textureId);
		
		glPixelStorei(GL_UNPACK_ALIGNMENT, 1); //Tells the engine how to unpack the RGBA bytes. Each component is 1 byte (second parameter)
		
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buf);
		/*Target - the type of texture
		 *Level - the level of detail. Level 0 is the base image level. Level n is the nth mipmap reduction image
		 *Internal format - the number of colour components in the texture
		 *Width - the width (in pixels)
		 *Height - the height (in pixels)
		 *Border - the distance between the edge of the texture and the edge of the shape. 0 means it goes all the way to the vertex
		 *Format - the format of the pixel data
		 *Type - the data type of the raw data
		 *Data - the byte buffer containing the actual texture bytes
		 */
		
		glGenerateMipmap(GL_TEXTURE_2D); //A mipmap is a decreasing resolution set of images generated from a high detailed texture. These are automatically applied when the object is scaled
		
		stbi_image_free(buf); //Texture is now in graphics card memory so can be removed from main memory
		
		return textureId;
	}
	
	public void cleanUp() {
		glDeleteTextures(this.id); //only need one command to destroy a texture
	}
}
