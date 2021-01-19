package net.alevel.asteroids.engine.objects;

import org.joml.Vector3f;

import net.alevel.asteroids.engine.graphics.Mesh;

/**A generic in game object.
 */
public abstract class GameObject extends NonRenderableObject {
	protected final Mesh mesh;
	//protected final Vector3f position;
	//protected float scale;
	//protected final Vector3f rotation;
	
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
