package net.alevel.asteroids.game.objects;

import java.util.ArrayList;
import java.util.List;

import org.joml.Matrix3f;
import org.joml.Vector3f;

import net.alevel.asteroids.engine.GameObject;
import net.alevel.asteroids.game.GameLogic;

public abstract class ObjectAssembly {
	private final List<GameObject> objects;
	private final List<Vector3f> relativePositions; //The vector linking the centre of a single component to the centre of the whole 'object'
	private final Vector3f position;
	private final Vector3f rotation;
	
	public ObjectAssembly() {
		this(new Vector3f(), new Vector3f());
	}
	
	public ObjectAssembly(Vector3f position, Vector3f rotation) {
		this.objects = new ArrayList<GameObject>();
		this.relativePositions = new ArrayList<Vector3f>();
		this.position = position;
		this.rotation = rotation;
	}
	
	public void rotate(float dx, float dy, float dz) {
		this.rotation.add(dx, dy, dz);
		this.rotateObjects();
	}
	
	public void setRotation(float x, float y, float z) {
		this.rotation.set(x, y, z);
		this.rotateObjects();
	}
	
	private void rotateObjects() {
		Matrix3f rotation = new Matrix3f();
		rotation.rotateX((float) Math.toRadians(this.rotation.x));
		rotation.rotateY((float) Math.toRadians(this.rotation.y));
		rotation.rotateZ((float) Math.toRadians(this.rotation.z));
		for(int i = 0; i < this.objects.size(); i++) {
			Vector3f newPos = new Vector3f(this.relativePositions.get(i));
			newPos.mul(rotation);
			newPos.add(this.position);
			this.objects.get(i).setPosition(newPos);
			this.objects.get(i).setRotation(-this.rotation.x, -this.rotation.y, -this.rotation.z);
		}
	}
	
	public void translate(float dx, float dy, float dz) {
		this.position.add(dx, dy, dz);
		this.moveObjects();
	}
	
	public void setPosision(float x, float y, float z) {
		this.position.set(x, y, z);
		this.moveObjects();
	}
	
	private void moveObjects() {
		for(int i = 0; i < this.objects.size(); i++)
			this.objects.get(i).setPosition(this.objects.get(i).getPosition().add(this.position));
	}
	
	public void addObject(GameObject o, Vector3f relativePos) {
		this.objects.add(o);
		o.setPosition(relativePos.add(this.position));
		this.relativePositions.add(relativePos);
	}
	
	/**Add object to array of objects that are in the world
	 */
	public void spawn() {
		for(int i = 0; i < this.objects.size(); i++)
			GameLogic.getInstance().addObject(this.objects.get(i));
	}
	
	/**Opposite of {@link ObjectAssembly#spawn()}
	 */
	public void despawn() {
		for(int i = 0; i < this.objects.size(); i++)
			GameLogic.getInstance().removeObject(this.objects.get(i));
	}
}
