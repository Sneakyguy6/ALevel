package net.alevel.asteroids.engine.graphics;

import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.*;

import java.util.HashMap;
import java.util.Map;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;

/**This class manages the shader files and all the uniforms. This is the interface between java and GLSL
 */
public class ShaderProgram {
	private final int programId;
	private int vertexShader;
	private int fragmentShaderId;
	private final Map<String, Integer> uniforms; //this map will hold all the uniforms
	
	public ShaderProgram() throws Exception {
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
	
	
}
