package net.alevel.asteroids.engine.graphics;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import net.alevel.asteroids.engine.objects.GameObject;

/**Utility class. It has functions for the 3 matrices (view/camera, modelView, projection) used to calculate the positions of mesh vertices that OpenGL should draw.<br>
 */
public class Transformations {
	private final Matrix4f projectionMatrix; //The matrix that converts the view coords to on screen coords. Creates depth perception
	private final Matrix4f viewMatrix; //calculates the position of the object relative to the camera
	private final Matrix4f modelViewMatrix; //Converts the model coods to in world coords by getting the rotation and scale properties as well as the actual loction of the object
	
	public Transformations() {
		this.modelViewMatrix = new Matrix4f();
		this.projectionMatrix = new Matrix4f();
		this.viewMatrix = new Matrix4f();
	}
	
	/**Sets the projection matrix and the returns it
	 * @param fov field of view. The biggest angle from the centre that should be displayed on the screen
	 * @param width the window width
	 * @param height the window height
	 * @param zNear closest distance to the camera before the object should no longer be rendered
	 * @param zFar farthest distance to the camera before the object should no longer be rendered
	 * @return the projection matrix. Converts view coordinates to 2D screen coordinates
	 */
	public final Matrix4f getProjectionMatrix(float fov, float width, float height, float zNear, float zFar) {
		return this.projectionMatrix.setPerspective(fov, width / height, zNear, zFar);
	}
	
	/**Uses the view matrix (camera) as well as the scale (size), rotation and position to create the matrix
	 * to be used to calculate the position of each vertex in a mesh in relation to the camera 
	 * @param gameObject
	 * @param viewMatrix
	 * @return The model view matrix
	 */
	public Matrix4f getModelViewMatrix(GameObject gameObject, Matrix4f viewMatrix) {
		Vector3f rotation = gameObject.getRotation();
		this.modelViewMatrix.set(viewMatrix)
							.translate(gameObject.getPosition())
							.rotateX((float) Math.toRadians(-rotation.x))
							.rotateY((float) Math.toRadians(-rotation.y))
							.rotateZ((float) Math.toRadians(-rotation.z))
							.scale(gameObject.getScale());
		return this.modelViewMatrix;
	}
	
	/**This gets the camera's rotation and position vectors as a matrix that can applied to mesh vertices.<br>
	 * The camera in a simulation is always at position (0,0,0) and rotation (0,0,0).<br>
	 * E.g. every time you move forwards, to the renderer, the objects are moving backwards
	 * @see Transformations#getModelViewMatrix(GameObject, Matrix4f)
	 * @return the view matrix
	 */
	public Matrix4f getViewMatrix(Camera camera) {
		Vector3f position = camera.getPosition();
		Vector3f rotation = camera.getRotation();
		this.viewMatrix.identity();
		this.viewMatrix.rotate((float) Math.toRadians(rotation.x), new Vector3f(1, 0, 0))
					   .rotate((float) Math.toRadians(rotation.y), new Vector3f(0, 1, 0)); //rotate first so it rotates over current pos
		this.viewMatrix.translate(-position.x, -position.y, -position.z); //then translate
		return this.viewMatrix;
	}
}
