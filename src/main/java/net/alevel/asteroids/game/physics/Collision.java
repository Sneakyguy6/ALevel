package net.alevel.asteroids.game.physics;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.alevel.asteroids.game.GameLogic;

public class Collision {
	private final Set<PhysicalObject> objectsToCheck;
	
	private Collision() {
		this.objectsToCheck = new HashSet<PhysicalObject>();
	}
	
	public void checkForCollisions() {
		//System.out.println(this.objectsToCheck);
		for(Iterator<PhysicalObject> i = this.objectsToCheck.iterator(); i.hasNext();) {
			PhysicalObject iObject = i.next();
			for(Iterator<PhysicalObject> j = this.objectsToCheck.iterator(); j.hasNext();) {
				PhysicalObject jObject = j.next();
				if(iObject.equals(jObject))
					continue;
				if(iObject.getBoundingBox().testAABB(jObject.getBoundingBox()))
					this.onCollision(iObject, jObject);
			}
		}
	}
	
	private void onCollision(PhysicalObject o1, PhysicalObject o2) {
		System.out.println("intersection found");
		this.objectsToCheck.remove(o1);
		this.objectsToCheck.remove(o2);
		GameLogic.getInstance().removeObject(o1);
		GameLogic.getInstance().removeObject(o2);
	}
	
	public boolean addObjectToCheck(PhysicalObject o) {
		return this.objectsToCheck.add(o);
	}
	
	public boolean removeObjectToCheck(PhysicalObject o) {
		return this.objectsToCheck.remove(o);
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
