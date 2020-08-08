package net.alevel.asteroids.engine.graphics;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glViewport;

import java.util.Set;

import org.joml.Matrix4f;

import net.alevel.asteroids.engine.GameObject;
import net.alevel.asteroids.engine.Window;

/**This manages which objects are to be rendered and also manages the uniforms within the shader program
 */
public class Renderer {//Might be worth making this a singleton as I don't see why you would want more than 1 instance of the renderer. It would just cause problems
	private static final float FOV = (float) Math.toRadians(60f); //FOV value is the largest angle from the centre that you can see on the screen
	private static final float Z_NEAR = 0.01f; //closest distance before object is no longer rendered
	private static final float Z_FAR = 1000f; //farthest distance before object is no longer rendered.
	private final Transformations transformations;
	private ShaderProgram shaderProgram;
	
	public Renderer() throws IllegalStateException {
		this.transformations = new Transformations();
		//this.initShaderProgram(window);
	}
	
	public void initShaderProgram(Window window) throws IllegalStateException {
		this.shaderProgram = new ShaderProgram(); //create the shader program instance
		this.shaderProgram.initShaders(); //initialise shaders (compile GLSL and create shader objects)
		this.shaderProgram.link(); //apply to shader pipeline
		
		//Create uniforms (GLSL variables that can be accessed through the OpenGL API)
		this.shaderProgram.createUniform("projectionMatrix");
		this.shaderProgram.createUniform("modelViewMatrix");
		this.shaderProgram.createUniform("texture_sampler");
		this.shaderProgram.createUniform("colour");
		this.shaderProgram.createUniform("useColour");
		
		//this.shaderProgram.createUniform("lightCoord"); //light test
	}
	
	public void clear() {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); //| is bitwise OR. Clears the 2 buffers specified (sets them to their clear values)
	}
	
	public void render(Window window, Camera camera, Set<GameObject> gameObjects) {
		this.clear();
		if(window.isResized()) {//If window is resized, update how OpenGL transforms the final projection coordinates to pixel coordinates
			glViewport(0, 0, window.getWidth(), window.getHeight());
			window.setResized(false);
		}
		
		this.shaderProgram.bind(); //Methods that use the shader program will use this instance
		
		//this.shaderProgram.setUniform("lightCoord", new Vector3f(0, 0, 0)); //light test
		
		//Get updated projection matrix which depends on the new window size
		Matrix4f projectionMatrix = this.transformations.getProjectionMatrix(FOV, window.getWidth(), window.getHeight(), Z_NEAR, Z_FAR);
		this.shaderProgram.setUniform("projectionMatrix", projectionMatrix); //update projection matrix in shaders
		//Get updated view matrix which depends on the camera position
		Matrix4f viewMatrix = this.transformations.getViewMatrix(camera);
		
		this.shaderProgram.setUniform("texture_sampler", 0); //Only using 1 texture so set it as a constant that points to texture unit 0
		
		//Render each game object
		for(GameObject o : gameObjects) {
			//Get updated model view matrix
			Matrix4f modelViewMatrix = this.transformations.getModelViewMatrix(o, viewMatrix);
			this.shaderProgram.setUniform("modelViewMatrix", modelViewMatrix); //update model view matrix in the shaders
			this.shaderProgram.setUniform("colour", o.getMesh().getColour());
			this.shaderProgram.setUniform("useColour", o.getMesh().isTextured() ? 0 : 1);
			
			//Once matrices are updated, we can draw the object
			o.getMesh().render();
		}
		
		//Once rendering is done, unbind so accidental modifications do not happen
		this.shaderProgram.unbind();
	}
	
	/**Run this on shutdown
	 */
	public void cleanUp() {
		this.shaderProgram.cleanUp();
	}
}
