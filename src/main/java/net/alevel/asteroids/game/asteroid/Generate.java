package net.alevel.asteroids.game.asteroid;

import java.util.Arrays;

import org.joml.Vector3f;

import net.alevel.asteroids.engine.graphics.Mesh;

public class Generate {
	public static Mesh createNewModel() {
		Vector3f[] vertices = {
				new Vector3f(0, 0, 1),
				new Vector3f(0, 0, -1),
				new Vector3f(0, 1, 0),
				new Vector3f(0, -1, 0),
				new Vector3f(1, 0, 0),
				new Vector3f(-1, 0, 0), //indices 0 -> 5
		};
		
		
		
		Vector3f[] midPoints = {
				getMidpoint(vertices[0], vertices[2]),
				getMidpoint(vertices[2], vertices[4]),
				getMidpoint(vertices[4], vertices[0]),
				
				getMidpoint(vertices[0], vertices[3]),
				getMidpoint(vertices[3], vertices[4]),
				getMidpoint(vertices[4], vertices[0]),
		};
		
		float[] floats = new float[(vertices.length + midPoints.length) * 3];
		for(int i = 0; i < vertices.length; i++) {
			floats[i * 3] = vertices[i].x;
			floats[i * 3 + 1] = vertices[i].y;
			floats[i * 3 + 2] = vertices[i].z;
		}
		for(int i = 0; i < midPoints.length; i++) {
			floats[(i * 3) + (vertices.length * 3)] = midPoints[i].x;
			floats[(i * 3) + (vertices.length * 3) + 1] = midPoints[i].y;
			floats[(i * 3) + (vertices.length * 3) + 2] = midPoints[i].z;
		}
		System.out.println(Arrays.toString(floats));
		System.out.println(Arrays.toString(midPoints));
		
		Mesh mesh = new Mesh(floats, floats, floats, new int[] {
			0, 2, 4,
			0, 3, 4,
			1, 3, 5,
			1, 5, 2,
			2, 1, 4,
			3, 1, 4,
			5, 0, 2,
			5, 0, 3,
			
			6, 7, 8,
			9, 10, 11
		});
		
		return mesh;
	}
	
	private static Vector3f getMidpoint(Vector3f a, Vector3f b) {
		return new Vector3f(a).add(b).div(2);
	}
}

//new Vector3f((float) Math.sqrt(1) / 2, 0, (float) Math.sqrt(1) / 2),
//new Vector3f(0, (float) Math.sqrt(1) / 2, (float) Math.sqrt(1) / 2),
//new Vector3f((float) Math.sqrt(1) / 2, (float) Math.sqrt(1) / 2, 0),
