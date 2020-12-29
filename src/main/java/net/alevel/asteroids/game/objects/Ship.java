package net.alevel.asteroids.game.objects;

import org.joml.Vector2f;
import org.joml.Vector3f;

import net.alevel.asteroids.engine.GameObject;
import net.alevel.asteroids.game.objects.shapes.MeshGen;

/**A standard ship
 */
public class Ship extends ObjectAssembly {
	
	public Ship() {
		GameObject cockpit = new StaticGameObject(MeshGen.triangularPrism(new Vector2f(0, 0), new Vector2f(0, 1), new Vector2f(1, 0), 2));
		GameObject leftArm = new StaticGameObject(MeshGen.cube(2, .5f, .5f));
		GameObject rightArm = new StaticGameObject(MeshGen.cube(2, .5f, .5f));
		super.addObject(cockpit, new Vector3f(-.5f, 0, 0));
		super.addObject(rightArm, new Vector3f(0, 0, 1));
		super.addObject(leftArm, new Vector3f(0, 0, -1));
	}
}
