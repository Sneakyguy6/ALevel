package net.alevel.asteroids.game.objects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.alevel.asteroids.engine.objects.GameObject;
import net.alevel.asteroids.engine.objects.NonRenderableObject;
import net.alevel.asteroids.game.physics.RigidObject;

/**Manages objects in the world
 *
 */
public class GameObjects {
	private final List<NonRenderableObject> allObjects;
	private final List<GameObject> renderableObjects;
	private final List<RigidObject> rigidObjects;
	
	public GameObjects() {
		this.allObjects = new ArrayList<NonRenderableObject>();
		this.renderableObjects = new ArrayList<GameObject>();
		this.rigidObjects = new ArrayList<RigidObject>();
	}
	
	public void spawnObject(NonRenderableObject o) {
		o.onSpawn(this);
		this.allObjects.add(o);
		if(o instanceof GameObject) {
			this.renderableObjects.add((GameObject) o);
			if(o instanceof RigidObject)
				this.rigidObjects.add((RigidObject) o);
		}
	}
	
	public void despawnObject(NonRenderableObject o) {
		o.onDespawn(this);
		this.allObjects.remove(o);
		if(o instanceof GameObject) {
			this.renderableObjects.remove((GameObject) o);
			if(o instanceof RigidObject)
				this.rigidObjects.remove((RigidObject) o);
		}
	}
	
	public void spawnAll(List<NonRenderableObject> os) {
		for(int i = 0; i < os.size(); i++)
			this.spawnObject(os.get(i));
	}
	
	public void spawnAll(NonRenderableObject[] os) {
		for(int i = 0; i < os.length; i++)
			this.spawnObject(os[i]);
	}
	
	public List<RigidObject> getRigidObjects(){
		return Collections.unmodifiableList(this.rigidObjects);
	}
	
	public List<NonRenderableObject> getAllObjects(){
		return Collections.unmodifiableList(this.allObjects);
	}
	
	public List<GameObject> getRenderableObjects(){
		return Collections.unmodifiableList(this.renderableObjects);
	}
	
	public int totalObjectCount() {
		return this.allObjects.size();
	}
}
