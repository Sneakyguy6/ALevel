package net.alevel.asteroids.game.objects.shapes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.joml.Vector3f;

class GenOctahedron {
	private List<Vector3f> positions;
	private int[] indices;
	
	public GenOctahedron(int resolution) {
		this.generate(resolution);
	}
	
	public void generate(int resolution) {
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
		this.positions = vertices;
		
		this.indices = new int[indices.size() * 3];
		for(int i = 0; i < indices.size(); i++) {
			int[] triangle = indices.get(i);
			this.indices[i * 3] = triangle[0];
			this.indices[i * 3 + 1] = triangle[1];
			this.indices[i * 3 + 2] = triangle[2];
		}
		
		//System.out.println(Arrays.toString(floats));
		//System.out.println(Arrays.toString(ints));
		//System.out.println(floats.length + " <- number of floats in sphere");
		//return new Mesh(floats, floats, floats, ints);
	}
	
	private static void getMidpointTriangle(int[] selectedVertices, List<Vector3f> vertices, List<int[]> indices) {
		Vector3f[] midpoints = new Vector3f[3];
		midpoints[0] = getMidpoint(vertices.get(selectedVertices[0]), vertices.get(selectedVertices[1]));
		midpoints[1] = getMidpoint(vertices.get(selectedVertices[1]), vertices.get(selectedVertices[2]));
		midpoints[2] = getMidpoint(vertices.get(selectedVertices[2]), vertices.get(selectedVertices[0]));
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
	
	private static Vector3f getMidpoint(Vector3f a, Vector3f b) {
		return new Vector3f(a).add(b).div(2);
	}
	
	public List<Vector3f> getPositions() {
		return this.positions;
	}
	
	public float[] getPositionsAsFloats() {
		float[] floats = new float[this.positions.size() * 3];
		for(int i = 0; i < this.positions.size(); i++) {
			floats[i * 3] = this.positions.get(i).x;
			floats[i * 3 + 1] = this.positions.get(i).y;
			floats[i * 3 + 2] = this.positions.get(i).z;
		}
		return floats;
	}
	
	public int[] getIndices() {
		return this.indices;
	}
}
