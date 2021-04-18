package net.alevel.asteroids.engine.objects;

import org.joml.Vector3f;

import net.alevel.asteroids.engine.graphics.Mesh;

/**Represents a generic in game object that can be rendered.
 */
public abstract class GameObject extends NonRenderableObject {
	protected final Mesh mesh;
	
	public GameObject(Mesh mesh) {
		this.mesh = mesh;
		this.position = new Vector3f();
		this.scale = 1;
		this.rotation = new Vector3f();
	}
	
	public Mesh getMesh() {
		return this.mesh;
	}
	
	@Override
	public void cleanUp() {
		this.mesh.cleanUp();
	}
}
