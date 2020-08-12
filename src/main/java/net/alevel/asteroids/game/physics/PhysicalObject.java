package net.alevel.asteroids.game.physics;

import org.joml.AABBf;
import org.joml.Matrix3f;
import org.joml.Vector3f;
import net.alevel.asteroids.engine.GameObject;
import net.alevel.asteroids.engine.graphics.Mesh;

public class PhysicalObject extends GameObject {
	private final AABBf boudingBox;
	
	public PhysicalObject(Mesh mesh) {
		super(mesh);
		this.boudingBox = new AABBf();
		Collision.getInstance().addObjectToCheck(this);
	}
	
	@Override
	public final void update(float time) {
		this.simulatePhysics(time);
		
		
		Matrix3f rotateAndScale = new Matrix3f();
		rotateAndScale.rotateX(super.rotation.x);
		rotateAndScale.rotateY(super.rotation.y);
		rotateAndScale.rotateZ(super.rotation.z);
		rotateAndScale.scale(super.scale);
		
		this.boudingBox
			.setMax(new Vector3f(super.getMesh().getModelAABB().maxX, super.getMesh().getModelAABB().maxY, super.getMesh().getModelAABB().maxZ).mul(rotateAndScale).add(super.position))
			.setMin(new Vector3f(super.getMesh().getModelAABB().minX, super.getMesh().getModelAABB().minY, super.getMesh().getModelAABB().minZ).mul(rotateAndScale).add(super.position));
		//System.out.println(this.boudingBox);
	}
	
	protected void simulatePhysics(float time) {
		//nothing
	}
	
	public final AABBf getBoundingBox() {
		return this.boudingBox;
	}
}
