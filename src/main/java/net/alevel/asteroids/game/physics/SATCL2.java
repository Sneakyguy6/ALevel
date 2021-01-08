package net.alevel.asteroids.game.physics;

import java.util.ArrayList;
import java.util.List;

import net.alevel.asteroids.engine.objects.GameObject;

public class SATCL2 {
	public static void checkCollisionsGeneric(List<GameObject> objects) {
		List<RigidObject> rigidObjects = new ArrayList<RigidObject>();
		for(int i = 0; i < objects.size(); i++) {
			/*for(int j = i + 1; j < objects.size(); j++) {
				GameObject o1 = objects.get(i),
						   o2 = objects.get(j);
				if((objects.get(i) instanceof RigidObject) && (objects.get(j) instanceof RigidObject)) {
					RigidObject ro1 = (RigidObject) objects.get(i);
					RigidObject ro2 = (RigidObject) objects.get(i);
					if()
				}
			}*/
			GameObject o = objects.get(i);
			if(o instanceof RigidObject)
				rigidObjects.add((RigidObject) o);
		}
	}
	
	public static void checkCollisions(List<RigidObject> objects) {
		
	}
	
	private boolean checkCollision() {
		
		return false;
	}
}
