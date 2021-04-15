package net.alevel.asteroids.game.physics;

import net.alevel.asteroids.engine.graphics.Mesh;

/**Represents a projectile. It uses projectile motion equations to calculate its position each update.
 */
public class Projectile extends RigidObject {
	public static float ACC_GRAV = -9.8f;
	private float C; //angle between where it is pointing and the x axis (between 0 and 90 degrees)
	private float D; //angle between where it is pointing at and the direction towards x=+infinity. Therefore change in X is cosC and change in Z will be sinC (between 0 and 359 degrees)
	private float U; //initial projection speed
	//private float t;
	
	public Projectile(Mesh mesh) {
		super(mesh);
		this.U = 0.5f;
		this.C = 0;
		this.D = 0;
		super.position.set(0, 0, 0);
		//this.t = 0;
	}

	@Override
	public void onUpdate(float t) {
		if(t < 0.05)
			return;
		t -= 0.05;
		super.position.x += (float) ((this.U * Math.cos(this.C)) * t * Math.cos(this.D));
		super.position.y += (float) ((t * this.U * Math.sin(this.C)) + ((ACC_GRAV / 2) * Math.pow(t, 2)));
		super.position.z += (float) ((this.U * Math.cos(this.C)) * t * Math.sin(this.D));
		//System.out.println(this + " --- " + t);
	}
	
	@Override
	public String toString() {
		return super.hashCode() + ": " + super.position;
	}
	
	public Projectile setVerticalAngleProjected(float C) {
		this.C = (float) Math.toRadians(C);
		return this;
	}
	
	public Projectile setHorizontalAngleProjected(float D) {
		this.D = (float) Math.toRadians(D);
		return this;
	}
	
	public Projectile setProjectionSpeed(float U) {
		this.U = U;
		return this;
	}
}
