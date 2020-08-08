package net.alevel.asteroids.game.physics;

import org.joml.AABBf;
import org.joml.Matrix4f;
import org.joml.Vector4f;

import net.alevel.asteroids.engine.GameObject;
import net.alevel.asteroids.engine.graphics.Mesh;

public class PhysicalObject extends GameObject {
	private final AABBf boudingBox;
	private final Matrix4f worldMatrix;
	
	public PhysicalObject(Mesh mesh) {
		super(mesh);
		this.worldMatrix = new Matrix4f();
		this.boudingBox = new AABBf();
	}
	
	@Override
	public final void update(float time) {
		//Vector3f posBeforeUpdate = new Vector3f().set(super.position);
		this.simulatePhysics(time);
		this.worldMatrix.identity()
						.translate(super.position)
						.rotateX((float) Math.toRadians(super.rotation.x))
						.rotateY((float) Math.toRadians(super.rotation.y))
						.rotateZ((float) Math.toRadians(super.rotation.z))
						.scale(super.scale);
		float minX = 0, maxX = 0, minY = 0, maxY = 0, minZ = 0, maxZ = 0;
		for(int i = 0; i < super.mesh.getVertexPositionFloats().length; i += 3) {
			Vector4f temp = new Vector4f(super.mesh.getVertexPositionFloats()[i],
									  	 super.mesh.getVertexPositionFloats()[i + 1],
									  	 super.mesh.getVertexPositionFloats()[i + 2],
									  	 1);
			temp.mul(this.worldMatrix);
			if(temp.x > maxX)
				maxX = temp.x;
			else if(temp.x < minX)
				minX = temp.x;
			if(temp.y > maxY)
				maxY = temp.y;
			else if(temp.y < minY)
				minY = temp.y;
			if(temp.z > maxZ)
				maxZ = temp.z;
			else if(temp.z < minZ)
				minZ = temp.z;
			//System.out.println(temp);
		}
		
		this.boudingBox.setMax(maxX, maxY, maxZ).setMin(minX, minY, minZ);
		//System.out.println(this.boudingBox);
	}
	
	protected void simulatePhysics(float time) {
		//nothing
	}

	public final Matrix4f getWorldMatrix() {
		return this.worldMatrix;
	}
	
	public final AABBf getBoundingBox() {
		return this.boudingBox;
	}
}
