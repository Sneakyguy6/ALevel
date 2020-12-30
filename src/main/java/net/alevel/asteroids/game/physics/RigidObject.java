package net.alevel.asteroids.game.physics;

import java.util.Map;

import org.joml.Vector3f;

import net.alevel.asteroids.engine.GameObject;
import net.alevel.asteroids.engine.graphics.Mesh;

public class RigidObject extends GameObject {
	private final Map<Vector3f, float[]> maxMinPoints;
	
	public RigidObject(Mesh mesh) {
		super(mesh);
		this.maxMinPoints = SATCollision.getMinMaxPoints(mesh.getVertices(), mesh.getIndices());
	}
	
	/**Runs every time this object collides with another.<br>
	 * Currently this method just turns the object red. For more advanced functionality, this method should be overridden.
	 * @param otherObject
	 */
	public void onCollision(RigidObject otherObject) {
		super.getMesh().setColour(new Vector3f(1, 0, 0));
		//GameLogic.getInstance().removeObject(this);
	}

	@Override
	public void update(float time) {
		
	}
	
	public Map<Vector3f, float[]> getMinMaxPoints() {
		return this.maxMinPoints;
	}
}
