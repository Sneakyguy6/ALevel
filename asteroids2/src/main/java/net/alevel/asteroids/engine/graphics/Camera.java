package net.alevel.asteroids.engine.graphics;

import org.joml.Vector3f;

/**The camera object
 */
public class Camera {
	private final Vector3f position;
	private final Vector3f rotation;
	
	public Camera() {
		this.position = new Vector3f();
		this.rotation = new Vector3f();
	}
	
	/**Note: this copies the values in the vector.
	 * It does not reference the vector being passed (i.e. any changes made to the vector being passed will not affect the vector stored in this object)
	 */
	public Camera(Vector3f position, Vector3f rotation) {
		this.position = new Vector3f().set(position);
		this.rotation = new Vector3f().set(rotation);
	}
	
	public Vector3f getPosition() {
		return this.position;
	}
	
	public Vector3f getRotation() {
		return this.rotation;
	}
	
	/**Note: this copies the values in the vector.
	 * It does not reference the vector being passed (i.e. any changes made to the vector being passed will not affect the vector stored in this object)
	 */
	public void setPosition(Vector3f position) {
		this.position.set(position);
	}
	
	public void setPosition(float x, float y, float z) {
		this.position.x = x;
		this.position.y = y;
		this.position.z = z;
	}
	
	/**Note: this copies the values in the vector.
	 * It does not reference the vector being passed (i.e. any changes made to the vector being passed will not affect the vector stored in this object)
	 */
	public void setRotation(Vector3f rotation) {
		this.rotation.set(rotation);
	}
	
	public void setRotation(float x, float y, float z) {
		this.rotation.x = x;
		this.rotation.y = y;
		this.rotation.z = z;
	}
	
	public void movePosition(float offsetX, float offsetY, float offsetZ) {
		if(offsetZ != 0) {
			this.position.x += (float) Math.sin(Math.toRadians(rotation.y)) * -1f * offsetZ;
			this.position.z += (float) Math.cos(Math.toRadians(rotation.y)) * offsetZ;
		}
		if(offsetX != 0) {
			this.position.x += (float) Math.sin(Math.toRadians(rotation.y - 90)) * -1f * offsetX;
			this.position.z += (float) Math.cos(Math.toRadians(rotation.y - 90)) * offsetX;
		}
		this.position.y += offsetY;
	}
	
	public void moveRotation(float offsetX, float offsetY, float offsetZ) {
		this.rotation.x += offsetX;
		this.rotation.y += offsetY;
		this.rotation.z += offsetZ;
	}
}
