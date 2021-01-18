package net.alevel.asteroids.game.physics;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.alevel.asteroids.game.GameLogic;

/**Being replaced with a better one
 */
@Deprecated
public class Collision {
	private final Set<PhysicalObject> objectsToCheck;
	
	private Collision() {
		this.objectsToCheck = new HashSet<PhysicalObject>();
	}
	
	public void checkForCollisions() {
		//System.out.println(this.objectsToCheck);
		final Map<PhysicalObject, PhysicalObject> eventsToFire = new HashMap<PhysicalObject, PhysicalObject>();
		for(Iterator<PhysicalObject> i = this.objectsToCheck.iterator(); i.hasNext();) {
			PhysicalObject iObject = i.next();
			for(Iterator<PhysicalObject> j = this.objectsToCheck.iterator(); j.hasNext();) {
				PhysicalObject jObject = j.next();
				if(iObject.equals(jObject))
					continue;
				//System.out.println("Testing for intersection");
				if(iObject.getBoundingBox().testAABB(jObject.getBoundingBox()))
					eventsToFire.put(iObject, jObject);
			}
		}
		this.fireCollisionEvents(eventsToFire);
	}
	
	private void fireCollisionEvents(Map<PhysicalObject, PhysicalObject> eventsToFire) {
		for(Iterator<Entry<PhysicalObject, PhysicalObject>> iterator = eventsToFire.entrySet().iterator(); iterator.hasNext();) {
			Entry<PhysicalObject, PhysicalObject> objects = iterator.next();
			//System.out.println("intersection found");
			this.objectsToCheck.remove(objects.getKey());
			this.objectsToCheck.remove(objects.getValue());
			GameLogic.getInstance().removeObject(objects.getKey());
			GameLogic.getInstance().removeObject(objects.getValue());
		}
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
