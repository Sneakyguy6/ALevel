package net.alevel.asteroids.game.objects;

import net.alevel.asteroids.engine.graphics.Mesh;

/**Similar to {@link Mesh}. It stores positions, textures, normals and indices but does not load them into GL buffers as they could still change.<br>
 * {@link Mesh} contains non modifiable attributes due to the fact that it represents data already sent to the GPU
 */
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
	
	/**Generates a non modifiable mesh using the attributes of 'this'.
	 * @return
	 */
	public Mesh generateMesh() {
		return new Mesh(positions, textures, normals, indices);
	}
}
