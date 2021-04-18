package net.alevel.asteroids.engine.graphics;

import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniform1i;
import static org.lwjgl.opengl.GL20.glUniform3f;
import static org.lwjgl.opengl.GL20.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;

/**This class manages the shader files and all the uniforms. This is the interface between java and GLSL.
 * A shader is a function that processes vertices and calculated the colour and position of each vertex on the screen.
 * These functions are executed on the GPU
 */
public class ShaderProgram {
	private final int programId;
	private int vertexShaderId;
	private int fragmentShaderId;
	private final Map<String, Integer> uniforms; //this map will hold all the uniforms
	
	public ShaderProgram() throws IllegalStateException {
		if((this.programId = glCreateProgram()) == 0)
			throw new IllegalStateException("Could not create shader program");
		this.uniforms = new HashMap<String, Integer>();
	}
	
	/**This adds a uniform (defined in GLSL) to the hash map so java can
	 */
	public void createUniform(String uniformName) {
		int uniformLocation = glGetUniformLocation(this.programId, uniformName);
		if(uniformLocation < 0)
			throw new IllegalStateException("Could not find uniform: " + uniformName);
		this.uniforms.put(uniformName, uniformLocation);
	}
	
	/**Use this to set a uniform to a 4x4 floating point matrix
	 */
	public void setUniform(String uniformName, Matrix4f value) {
		try(MemoryStack stack = MemoryStack.stackPush()) { //MemoryStack is MemoryUtil but it auto closes
			glUniformMatrix4fv(this.uniforms.get(uniformName), false, value.get(stack.mallocFloat(16)));
		}
	}
	
	/**Use this to set a uniform to a 3 dimensional float vector
	 */
	public void setUniform(String uniformName, Vector3f value) {
		glUniform3f(this.uniforms.get(uniformName), value.x, value.y, value.z);
	}
	
	/**Use this to set a uniform to an integer
	 */
	public void setUniform(String uniformName, int value) {
		glUniform1i(this.uniforms.get(uniformName), value);
	}
	
	/**Load and compile GLSL code
	 */
	public void initShaders() {
		try {
			this.vertexShaderId = this.initShaderObject("vertex.vs", GL_VERTEX_SHADER);
			this.fragmentShaderId = this.initShaderObject("fragment.fs", GL_FRAGMENT_SHADER);
		} catch (ClassNotFoundException e) { //this should never be thrown as the class its looking for is this one
			e.printStackTrace();
		}
	}
	
	private int initShaderObject(String fileName, int shaderType) throws ClassNotFoundException {
		int id;
		try (Scanner s = new Scanner(Class.forName(ShaderProgram.class.getName()).getResourceAsStream("/" + fileName), "UTF-8")) { //this scanner reads the file as a stream as it is within the jar
			if((id = glCreateShader(shaderType)) == 0)
				throw new IllegalStateException("Error creating shader object of type: " + shaderType);
			glShaderSource(id, s.useDelimiter("\\A").next()); //links the GLSL code to the shader object
			glCompileShader(id); //compiles the GLSL code
			if(glGetShaderi(id, GL_COMPILE_STATUS) == 0) //gets the returned code from the GLSL compiler and checks if it compiled correctly
				throw new IllegalStateException("Compilation error when compiling shader code: " + glGetShaderInfoLog(id) + glGetProgramInfoLog(this.programId, 1024));
			System.out.println(glGetShaderi(id, GL_COMPILE_STATUS));
			glAttachShader(this.programId, id);
		}
		return id;
	}
	
	/**Link this shader program to the shader pipeline. Essentially applies the program to the OpenGL engine
	 */
	public void link() {
		glLinkProgram(this.programId);
		if(glGetProgrami(this.programId, GL_LINK_STATUS) == 0)
			throw new IllegalStateException("Error linking shader code: " + glGetProgramInfoLog(this.programId, 1024));
		
		if(this.vertexShaderId != 0) //the shader objects are no longer needed after linking
			glDetachShader(this.programId, this.vertexShaderId);
		if(this.fragmentShaderId != 0)
			glDetachShader(this.programId, this.fragmentShaderId);
		
		glValidateProgram(this.programId);
		if(glGetProgrami(this.programId, GL_VALIDATE_STATUS) == 0)
			System.err.println("A warning appeared when validating shader program: " + glGetProgramInfoLog(this.programId, 1024));
		System.out.println(glGetProgrami(this.programId, GL_LINK_STATUS));
	}
	
	/**Use if need to run methods that should refer to this object instance.
	 */
	public void bind() {
		glUseProgram(this.programId);
	}
	
	/**Once finished with this instance, unbind from it so accidental changes are not made to this
	 */
	public void unbind() {
		glUseProgram(0);
	}
	
	/**Destroys the program completely. Use on shutdown
	 */
	public void cleanUp() {
		this.unbind();
		if(this.programId != 0)
			glDeleteProgram(this.programId);
	}
}
