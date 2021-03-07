package net.alevel.asteroids.game.objects;

public class ModifiableMesh {
	private float[] positions;
	private float[] textures;
	private float[] normals;
	private int[] indices;

	public ModifiableMesh(float[] positions, int[] indices) {
		this.positions = positions;
		this.indices = indices;
	}

	public float[] getPositions() {
		return this.positions;
	}

	public void setPositions(float[] positions) {
		this.positions = positions;
	}
	
	public void changePosition(int index, float value) {
		this.positions[index] = value;
	}

	public float[] getTextures() {
		return this.textures;
	}

	public void setTextures(float[] textures) {
		this.textures = textures;
	}

	public float[] getNormals() {
		return this.normals;
	}

	public void setNormals(float[] normals) {
		this.normals = normals;
	}

	public int[] getIndices() {
		return this.indices;
	}

	public void setIndices(int[] indices) {
		this.indices = indices;
	}
}
