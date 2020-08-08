package net.alevel.asteroids.game.physics;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.alevel.asteroids.engine.GameObject;
import net.alevel.asteroids.game.GameLogic;

public class Collision {
	private final Set<PhysicalObject> objectsToCheck;
	
	private Collision() {
		this.objectsToCheck = new HashSet<PhysicalObject>();
	}
	
	public void checkForCollisions() {
		for(Iterator<PhysicalObject> i = this.objectsToCheck.iterator(); i.hasNext();) {
			PhysicalObject iObject = i.next();
			for(Iterator<PhysicalObject> j = this.objectsToCheck.iterator(); i.hasNext();) {
				PhysicalObject jObject = j.next();
				if(iObject.equals(jObject))
					continue;
				if(iObject.getBoundingBox().testAABB(jObject.getBoundingBox()))
					this.onCollision(iObject, jObject);
			}
		}
	}
	
	private void onCollision(GameObject o1, GameObject o2) {
		System.out.println("intersection found");
		this.objectsToCheck.remove(o1);
		this.objectsToCheck.remove(o2);
		GameLogic.getInstance().removeObject(o1);
		GameLogic.getInstance().removeObject(o2);
	}
	
	private static Collision instance;
	
	public static void init() {
		if(instance == null)
			instance = new Collision();
	}
	
	public static Collision getInstance() {
		return instance;
	}
}
