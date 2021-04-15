package net.alevel.asteroids.game.objects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.joml.Matrix3f;
import org.joml.Vector3f;

import net.alevel.asteroids.engine.objects.GameObject;
import net.alevel.asteroids.engine.objects.NonRenderableObject;

/**Represents an Object made of multiple objects. These would be used to create more complex shapes (like ships)
 */
public class ObjectAssembly extends NonRenderableObject {
	private final List<GameObject> objects;
	private final List<Vector3f> relativePositions; //The vector linking the centre of a single component to the centre of the whole 'object'
	
	public ObjectAssembly() {
		this(new Vector3f(), new Vector3f());
	}
	
	public ObjectAssembly(Vector3f position, Vector3f rotation) {
		super(position, rotation);
		this.objects = new ArrayList<GameObject>();
		this.relativePositions = new ArrayList<Vector3f>();
	}
	
	@Override
	protected void onUpdate(float time) {
		this.rotateObjects();
		this.moveObjects();
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
	
	private void moveObjects() {
		for(int i = 0; i < this.objects.size(); i++)
			this.objects.get(i).setPosition(this.objects.get(i).getPosition().add(this.position));
	}
	
	/**Add new object to assembly
	 * @param o the object
	 * @param relativePos the position of this object relative to the centre of the whole assembly
	 */
	public void addObject(GameObject o, Vector3f relativePos) {
		this.objects.add(o);
		o.setPosition(relativePos.add(this.position));
		this.relativePositions.add(relativePos);
	}
	
	public List<GameObject> getObjects() {
		return Collections.unmodifiableList(this.objects);
	}

	@Override
	public void onUpdateFinish(float time) {
	}

	@Override
	public void onSpawn(GameObjects objectsManager) {
		for(GameObject i : this.objects)
			objectsManager.spawnObject(i);
	}

	@Override
	public void onDespawn(GameObjects objectsManager) {
		for(GameObject i : this.objects)
			objectsManager.despawnObject(i);
	}

	@Override
	public void cleanUp() {
		for(GameObject i : this.objects)
			i.cleanUp();
	}
}

/*@Override
public NonRenderableObject translate(float dx, float dy, float dz) {
	this.position.add(dx, dy, dz);
	this.moveObjects();
	return this;
}

@Override
public NonRenderableObject translate(Vector3f v) {
	return this.translate(v.x, v.y, v.z);
}

@Override
public NonRenderableObject setPosition(float x, float y, float z) {
	this.position.set(x, y, z);
	this.moveObjects();
	return this;
}

@Override
public NonRenderableObject setPosition(Vector3f v) {
	return this.setPosition(v.x, v.y, v.z);
}

@Override
public NonRenderableObject setScale(float scale) {
	this.scale = scale;
	return this;
}

@Override
public NonRenderableObject enlarge(float dx) {
	this.scale += dx;
	return this;
}

@Override
public NonRenderableObject shrink(float dx) {
	return this.enlarge(-dx);
}*/

/*@Override
public NonRenderableObject rotate(float dx, float dy, float dz) {
	super.rotation.add(dx, dy, dz);
	this.rotateObjects();
	return this;
}

@Override
public NonRenderableObject rotate(Vector3f v) {
	return this.rotate(v.x, v.y, v.z);
}

@Override
public NonRenderableObject setRotation(float x, float y, float z) {
	super.rotation.set(x, y, z);
	this.rotateObjects();
	return this;
}

@Override
public NonRenderableObject setRotation(Vector3f v) {
	return this.setRotation(v.x, v.y, v.z);
}*/

/**Add object to array of objects that are in the world
 */
/*public void spawn() {
	for(int i = 0; i < this.objects.size(); i++)
		GameLogic.getInstance().addObject(this.objects.get(i));
}

/**Opposite of {@link ObjectAssembly#spawn()}
 */
/*public void despawn() {
	for(int i = 0; i < this.objects.size(); i++)
		GameLogic.getInstance().removeObject(this.objects.get(i));
}*/
