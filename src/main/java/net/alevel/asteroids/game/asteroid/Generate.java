package net.alevel.asteroids.game.asteroid;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.joml.Vector3f;

import net.alevel.asteroids.engine.graphics.Mesh;

public class Generate {
	public static Mesh createNewModel(int resolution) {
		List<Vector3f> vertices = new ArrayList<Vector3f>();
		vertices.add(new Vector3f(0, 0, 1));
		vertices.add(new Vector3f(0, 0, -1));
		vertices.add(new Vector3f(0, 1, 0));
		vertices.add(new Vector3f(0, -1, 0));
		vertices.add(new Vector3f(1, 0, 0));
		vertices.add(new Vector3f(-1, 0, 0)); //indices 0 -> 5
		
		List<int[]> indices = new ArrayList<int[]>();
		indices.add(new int[] {0, 2, 4});
		indices.add(new int[] {0, 3, 4});
		indices.add(new int[] {1, 3, 5});
		indices.add(new int[] {1, 5, 2});
		indices.add(new int[] {2, 1, 4});
		indices.add(new int[] {3, 1, 4});
		indices.add(new int[] {5, 0, 2});
		indices.add(new int[] {5, 0, 3});
		
		for(int i = 0; i < resolution; i++)
			splitTriangles(vertices, indices);
		for(int i = 0; i < vertices.size(); i++)
			vertices.set(i, adjustVertexToRadius(vertices.get(i)));
		
		float[] floats = new float[vertices.size() * 3];
		int[] ints = new int[indices.size() * 3];
		for(int i = 0; i < vertices.size(); i++) {
			floats[i * 3] = vertices.get(i).x;
			floats[i * 3 + 1] = vertices.get(i).y;
			floats[i * 3 + 2] = vertices.get(i).z;
		}
		for(int i = 0; i < indices.size(); i++) {
			int[] triangle = indices.get(i);
			ints[i * 3] = triangle[0];
			ints[i * 3 + 1] = triangle[1];
			ints[i * 3 + 2] = triangle[2];
		}
		
		//System.out.println(Arrays.toString(floats));
		//System.out.println(Arrays.toString(ints));
		return new Mesh(floats, floats, floats, ints);
	}
	
	private static void getMidpointTriangle(int[] selectedVertices, List<Vector3f> vertices, List<int[]> indices) {
		Vector3f[] midpoints = new Vector3f[3];
		midpoints[0] = getMidpoint(vertices.get(selectedVertices[0]), vertices.get(selectedVertices[1]));//.mul(1.5f);
		midpoints[1] = getMidpoint(vertices.get(selectedVertices[1]), vertices.get(selectedVertices[2]));//.mul(1.5f);
		midpoints[2] = getMidpoint(vertices.get(selectedVertices[2]), vertices.get(selectedVertices[0]));//.mul(1.5f);
		int firstIndex = vertices.size();
		vertices.addAll(Arrays.asList(midpoints));
		List<int[]> newTriangles = new ArrayList<int[]>();
		newTriangles.add(new int[] {firstIndex, firstIndex + 1, firstIndex + 2});
		newTriangles.add(new int[] {firstIndex, selectedVertices[0], firstIndex + 2});
		newTriangles.add(new int[] {firstIndex, selectedVertices[1], firstIndex + 1});
		newTriangles.add(new int[] {firstIndex + 1, selectedVertices[2], firstIndex + 2});
		indices.remove(selectedVertices);
		indices.addAll(newTriangles);
	}
	
	private static void splitTriangles(List<Vector3f> vertices, List<int[]> indices) {
		@SuppressWarnings("unchecked")
		ArrayList<int[]> indicesCopy = (ArrayList<int[]>) ((ArrayList<int[]>) indices).clone();
		//System.out.println(indicesCopy);
		for(int[] i : indicesCopy)
			getMidpointTriangle(i, vertices, indices);
	}
	
	private static Vector3f adjustVertexToRadius(Vector3f vertex) {
		//Vector3f translateBy = new Vector3f();
		//float angleToX = vertex.x == 0 ? vertex.z : vertex.z / vertex.x;
		//float angleToY = vertex.x == 0 ? vertex.y : vertex.y / vertex.x;
		//float angleToZ = vertex.z == 0 ? vertex.x : vertex.x / vertex.z;
		float scale = 1 - (vertex.length() / 1);
		//return vertex.mul(scale);
		float dX = vertex.x * (scale / vertex.length());
		float dY = vertex.y * (scale / vertex.length());
		float dZ = vertex.z * (scale / vertex.length());
		return vertex.add(dX, dY, dZ);
		
		//float angleToX = getAngle(vertex.z, vertex.x);
		//float angleToY = getAngle(vertex.y, vertex.x);
		//return new Vector3f(radius, 0, 0).rotateY(angleToX).rotateX(angleToY);
		//float scale = vertex.length() / radius;
		//Vector3f out = new Vector3f(radius * angleToX, radius * angleToY, radius * angleToZ);
		
		//return vertex.mul(scale);
		//float angleToZ = getAngle(vertex.x, vertex.z);
		//System.out.println(angleToX + " - " + angleToY + " - " + angleToZ);
		//float angle = (float) Math.acos(vertex.dot(new Vector3f(0, 0, 0)) / vertex.distance(0, 0, 0));
		//return new Vector3f((float) Math.cos(angleToX), (float) Math.cos(angleToY), (float) Math.cos(angleToZ)).mul(radius).mul(angle);
	}
	
	/*private static float getAngle(float opp, float adj) {
		float angleToAdd;
		if(adj < 0 && opp < 0)
			angleToAdd = (float) Math.PI;
		else if(adj < 0 && opp >= 0)
			angleToAdd = (float) Math.PI;
		else if(adj >= 0 && opp < 0)
			angleToAdd = (float) Math.PI * 2;
		else
			angleToAdd = 0;
		/*double angle = Math.atan(opp / adj);
		if(Double.isNaN(angle))
			return angleToAdd;
		else
			return angleToAdd + (float) angle;
		//return angleToAdd + ((float) Math.atan(opp / adj));
	}*/
	
	private static Vector3f getMidpoint(Vector3f a, Vector3f b) {
		return new Vector3f(a).add(b).div(2);
	}
}
