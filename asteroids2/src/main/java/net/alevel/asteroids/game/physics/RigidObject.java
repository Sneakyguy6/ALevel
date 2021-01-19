package net.alevel.asteroids.game.physics;

import static org.jocl.CL.clReleaseMemObject;

import org.jocl.CL;
import org.jocl.cl_mem;
import org.joml.Vector3f;

import net.alevel.asteroids.engine.graphics.Mesh;
import net.alevel.asteroids.engine.objects.GameObject;
import net.alevel.asteroids.game.objects.GameObjects;

public class RigidObject extends GameObject {
	private cl_mem worldVertices; //sub buffer of the worldVertices buffer
	//private float[] worldVerticesArr;
	
	public RigidObject(Mesh mesh) {
		super(mesh);
		//this.maxMinPoints = SATCollision.getMinMaxPoints(mesh.getVertices(), mesh.getIndices());
	}
	
	/**Runs every time this object collides with another.<br>
	 * Currently this method just turns the object red. For more advanced functionality, this method should be overridden.
	 * @param otherObject
	 */
	public void onCollision(RigidObject otherObject) {
		super.getMesh().setColour(new Vector3f(1, 0, 0));
		//GameLogic.getInstance().removeObject(this);
	}

	@Override
	public void onUpdate(float time) {
		//System.out.println(Arrays.toString(this.getWorldVerticesArr()));
	}
	
	@Override
	public void onUpdateFinish(float time) {
		clReleaseMemObject(this.worldVertices);
		this.worldVertices = null;
	}
	
	@Override
	public void onSpawn(GameObjects objectsManager) {
	}
	
	@Override
	public void onDespawn(GameObjects objectsManager) {
		CL.clReleaseMemObject(this.worldVertices);
	}
	
	public void setWorldVerticesMem(cl_mem in) {
		this.worldVertices = in;
	}
	
	public cl_mem getWorldVertices() {
		return this.worldVertices;
	}
	
	/**Returns the world vertices float array.<br>
	 * NOTE: this is fetching the floats from the GPU
	 * @return
	 */
	public float[] getWorldVerticesArr() {
		float[] out = new float[super.mesh.getVertices().length];
		/*clEnqueueReadBuffer(
				CLManager.getCommandQueue(),
				this.worldVertices,
				CL_TRUE,
				0,
				Sizeof.cl_float * out.length,
				Pointer.to(out),
				0,
				null,
				null);*/
		return out;
	}
	
	/*public Map<Vector3f, float[]> getMinMaxPoints() {
		return this.maxMinPoints;
	}*/
}
