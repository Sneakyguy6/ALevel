package net.alevel.asteroids.game.physics;

import org.joml.AABBf;
import org.joml.Vector3f;

import net.alevel.asteroids.engine.GameObject;
import net.alevel.asteroids.engine.graphics.Mesh;

public class PhysicalObject extends GameObject {
	private final AABBf boudingBox;
	//private final Matrix4f worldMatrix;
	//private final Matrix3f worldMatrix2;
	
	public PhysicalObject(Mesh mesh) {
		super(mesh);
		//this.worldMatrix = new Matrix4f();
		//this.worldMatrix2 = new Matrix3f();
		this.boudingBox = new AABBf();
		Collision.getInstance().addObjectToCheck(this);
	}
	
	@Override
	public final void update(float time) {
		//Vector3f posBeforeUpdate = new Vector3f().set(super.position);
		this.simulatePhysics(time);
		/*this.worldMatrix.identity()
						.translation(super.position)
						.rotateX((float) Math.toRadians(super.rotation.x))
						.rotateY((float) Math.toRadians(super.rotation.y))
						.rotateZ((float) Math.toRadians(super.rotation.z))
						.scale(super.scale);*/
		
		//this.worldMatrix2.identity()
		//				 .mul
		
		float minX = Float.MIN_VALUE, maxX = Float.MAX_VALUE, minY = Float.MIN_VALUE, maxY = Float.MAX_VALUE, minZ = Float.MIN_VALUE, maxZ = Float.MAX_VALUE;
		for(int i = 0; i < super.mesh.getVertexPositionFloats().length; i += 3) {
			/*Vector4f temp = new Vector4f(super.mesh.getVertexPositionFloats()[i],
									  	 super.mesh.getVertexPositionFloats()[i + 1],
									  	 super.mesh.getVertexPositionFloats()[i + 2],
									  	 1);*/
			Vector3f temp = new Vector3f(super.mesh.getVertexPositionFloats()[i],
									  	 super.mesh.getVertexPositionFloats()[i + 1],
									  	 super.mesh.getVertexPositionFloats()[i + 2]);
			temp.add(super.position);
			/*temp.mul(new Matrix3f(1, 0, 0,
								  0, (float) cos(super.rotation.x), (float) sin(super.rotation.x),
								  0, (float) -sin(super.rotation.x), (float) cos(super.rotation.x)));
			temp.mul(new Matrix3f((float) cos(super.rotation.y), 0, (float) -sin(super.rotation.y),
								  0, 1, 0,
								  (float) -sin(super.rotation.y), 0, (float) cos(super.rotation.y)));
			temp.mul(new Matrix3f((float) cos(super.rotation.z), (float) sin(super.rotation.z), 0,
								  (float) -sin(super.rotation.z), (float) cos(super.rotation.z), 0,
								  0, 0, 1));*/
			//temp.mul(super.scale);
			
			//temp.mul(this.worldMatrix);
			if(temp.x < maxX)
				maxX = temp.x;
			if(temp.x > minX)
				minX = temp.x;
			if(temp.y < maxY)
				maxY = temp.y;
			if(temp.y > minY)
				minY = temp.y;
			if(temp.z < maxZ)
				maxZ = temp.z;
			if(temp.z > minZ) {
				minZ = temp.z;
				System.out.println(temp);
			}
			//System.out.println(temp);
		}
		
		this.boudingBox.setMax(maxX, maxY, maxZ).setMin(minX, minY, minZ);
		//System.out.println(this.boudingBox);
	}
	
	protected void simulatePhysics(float time) {
		//nothing
	}

	/*public final Matrix4f getWorldMatrix() {
		return this.worldMatrix;
	}*/
	
	public final AABBf getBoundingBox() {
		return this.boudingBox;
	}
}
