package net.alevel.asteroids.game;

import net.alevel.asteroids.engine.GameObject;
import net.alevel.asteroids.engine.graphics.Mesh;

public class Projectile extends GameObject {
	public static float ACC_GRAV = -9.8f;
	private float C; //angle between where it is pointing and the x axis (between 0 and 90 degrees)
	private float D; //angle between where it is pointing at and the direction towards x=+infinity. Therefore change in X is cosC and change in Z will be sinC (between 0 and 359 degrees)
	private float U; //initial projection speed
	
	public Projectile(Mesh mesh) {
		super(mesh);
		this.U = 1f;
		this.C = (float) Math.toRadians(60);
		this.D = (float) Math.toRadians(120);
		super.position.set(0, 0, 0);
	}

	@Override
	public void update(float t) {
		super.position.x += (float) ((this.U * Math.cos(this.C)) * t * Math.cos(this.D));
		super.position.y += (float) ((t * this.U * Math.sin(this.C)) + ((ACC_GRAV / 2) * Math.pow(t, 2)));
		super.position.z += (float) ((this.U * Math.cos(this.C)) * t * Math.sin(this.D));
		System.out.println(this);
	}
	
	@Override
	public String toString() {
		return super.hashCode() + ": " + super.position;
	}
}
