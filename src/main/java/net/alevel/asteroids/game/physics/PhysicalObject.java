package net.alevel.asteroids.game.physics;

import org.joml.AABBf;
import org.joml.Matrix3f;
import org.joml.Vector3f;
import net.alevel.asteroids.engine.GameObject;
import net.alevel.asteroids.engine.graphics.Mesh;

public class PhysicalObject extends GameObject {
	private final AABBf modelBoundingBox;
	private final AABBf boundingBox;
	
	public PhysicalObject(Mesh mesh) {
		super(mesh);
		this.modelBoundingBox = new AABBf();
		this.boundingBox = new AABBf();
		this.createBoundingBox(mesh.getPositionsTemp());
		Collision.getInstance().addObjectToCheck(this);
	}
	
	@Override
	public final void update(float time) {
		this.simulatePhysics(time);
		
		Matrix3f rotateAndScale = new Matrix3f();
		rotateAndScale.rotateX((float) Math.toRadians(-super.rotation.x));
		rotateAndScale.rotateY((float) Math.toRadians(-super.rotation.y));
		rotateAndScale.rotateZ((float) Math.toRadians(-super.rotation.z));
		rotateAndScale.scale(super.scale);
		
		//System.out.println("PhysUpdate: " + new Vector3f(this.modelBoundingBox.maxX, this.modelBoundingBox.maxY, this.modelBoundingBox.maxZ).mul(rotateAndScale).add(super.position));
		//System.out.println("PhysUpdate: " + new Vector3f(this.modelBoundingBox.minX, this.modelBoundingBox.minY, this.modelBoundingBox.minZ).mul(rotateAndScale).add(super.position));
		//System.out.println();
		
		this.boundingBox.setMax(new Vector3f(this.modelBoundingBox.maxX, this.modelBoundingBox.maxY, this.modelBoundingBox.maxZ).mul(rotateAndScale).add(super.position))
						.setMin(new Vector3f(this.modelBoundingBox.minX, this.modelBoundingBox.minY, this.modelBoundingBox.minZ).mul(rotateAndScale).add(super.position));
		System.out.println();
		//System.out.println(this.modelBoundingBox);
		//System.out.println(this.boundingBox);
	}
	
	protected void simulatePhysics(float time) {
		//nothing
	}
	
	private final void createBoundingBox(float[] positions) {
		/*this.worldMatrix.identity()
		.translation(super.position)
		.rotateX((float) Math.toRadians(super.rotation.x))
		.rotateY((float) Math.toRadians(super.rotation.y))
		.rotateZ((float) Math.toRadians(super.rotation.z))
		.scale(super.scale);*/
	//float minX = Float.MIN_VALUE, maxX = Float.MAX_VALUE, minY = Float.MIN_VALUE, maxY = Float.MAX_VALUE, minZ = Float.MIN_VALUE, maxZ = Float.MAX_VALUE;
	float minX = 0, maxX = 0, minY = 0, maxY = 0, minZ = 0, maxZ = 0;
		for(int i = 0; i < positions.length; i += 3) {
			/*Vector4f temp = new Vector4f(super.mesh.getVertexPositionFloats()[i],
								  	 	super.mesh.getVertexPositionFloats()[i + 1],
								  	 	super.mesh.getVertexPositionFloats()[i + 2],
								  	 	1);*/
			Vector3f temp = new Vector3f(positions[i],
									 	positions[i + 1],
									 	positions[i + 2]);
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
			if(temp.x > maxX)
				maxX = temp.x;
			if(temp.x < minX)
				minX = temp.x;
			if(temp.y > maxY)
				maxY = temp.y;
			if(temp.y < minY)
				minY = temp.y;
			if(temp.z > maxZ)
				maxZ = temp.z;
			if(temp.z < minZ)
				minZ = temp.z;
			//System.out.println(temp);
		}
		//System.out.println(new AABBf().setMax(maxX, maxY, maxZ).setMin(minX, minY, minZ));
		this.modelBoundingBox.setMax(maxX, maxY, maxZ)
					   		 .setMin(minX, minY, minZ);
	}
	
	public final AABBf getBoundingBox() {
		return this.boundingBox;
	}
	
	public final AABBf getModelBoundingBox() {
		return this.modelBoundingBox;
	}
}
