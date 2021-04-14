package net.alevel.asteroids.game.physics.SATJava;

import static java.lang.Math.pow;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.joml.Vector3f;

import net.alevel.asteroids.game.physics.RigidObject;
import net.alevel.asteroids.game.physics.pipeline.FunctionPipeline;
import net.alevel.asteroids.game.physics.pipeline.PipelineBuffer;
import net.alevel.asteroids.game.physics.pipeline.PipelineableFunction;

public class SATJava extends FunctionPipeline implements PipelineableFunction {

	public SATJava(PipelineBuffer globalPipeline) {
		super(globalPipeline);
		super.setFunctions(this);
	}

	@Override
	public void pipeFunction(PipelineBuffer pipelineBuffer, PipelineBuffer globalPipelineBuffer, List<RigidObject> rigidObjects) {
		//calculate surface normals (these will be the axis used)
		System.out.println(rigidObjects);
		Set<Vector3f> surfaceNormals = new HashSet<Vector3f>();
		for(RigidObject rigidObject : rigidObjects) {
			float[] vertices = rigidObject.getWorldVerticesArr();
			System.out.println(Arrays.toString(vertices));
			int[] indices = rigidObject.getMesh().getIndices();
			for(int i = 0; i < indices.length; i += 3) {
				Vector3f a = new Vector3f(
				        vertices[indices[i + 1] * 3] - vertices[indices[i] * 3],
				        vertices[indices[i + 1] * 3 + 1] - vertices[indices[i] * 3 + 1],
				        vertices[indices[i + 1] * 3 + 2] - vertices[indices[i] * 3 + 2]
				);
				Vector3f b = new Vector3f(
				        vertices[indices[i + 2] * 3] - vertices[indices[i] * 3],
				        vertices[indices[i + 2] * 3 + 1] - vertices[indices[i] * 3 + 1],
				        vertices[indices[i + 2] * 3 + 2] - vertices[indices[i] * 3 + 2]
				);
				
				Vector3f c = a.cross(b).normalize();
				//Vector3f c2 = new Vector3f(c).mul(-1);
				//System.out.println(c + " " + c2 + " " + surfaceNormals.contains(c) + " " + surfaceNormals.contains(c2));
				if(!this.alreadyHasOppositeDirection(c, surfaceNormals))
				//if(!surfaceNormals.contains(new Vector3f(c).mul(-1)))
					surfaceNormals.add(c);
			}
		}
		//System.out.println();
		//System.out.println(surfaceNormals);
		
		//calculate the projected boundaries for each normal (axis)
		ObjectProjection[] projectedVertices = new ObjectProjection[rigidObjects.size()];
		for(int a = 0; a < rigidObjects.size(); a++) {
			float[] allRanges = new float[surfaceNormals.size() * 2];
			int counter = 0;
			for(Vector3f normal : surfaceNormals) {
				float[] vertices = rigidObjects.get(a).getWorldVerticesArr();
				float modNSquared = (float) (pow(normal.x, 2) + pow(normal.y, 2) + pow(normal.z, 2));
				float lambda = ((vertices[0] * normal.x) + (vertices[1] * normal.y) + (vertices[2] * normal.z)) / modNSquared;
				float min = lambda,
					  max = lambda;
				for(int i = 3; i < vertices.length; i += 3) {
					lambda = ((vertices[i] * normal.x) + (vertices[i + 1] * normal.y) + (vertices[i + 2] * normal.z)) / modNSquared;
					if(lambda < min)
						min = lambda;
					else if(lambda > max)
						max = lambda;
				}
				allRanges[counter] = min;
				allRanges[counter + 1] = max;
				counter += 2;
			}
			projectedVertices[a] = new ObjectProjection(rigidObjects.get(a), allRanges);
			System.out.println(surfaceNormals);
			System.out.println(Arrays.toString(allRanges) + "\n");
		}
		
		//test collisions
		for(int i = 0; i < projectedVertices.length; i++) 
			for(int j = i + 1; j < projectedVertices.length; j++) 
				testCollision(projectedVertices[i], projectedVertices[j]);
	}
	
	private boolean alreadyHasOppositeDirection(Vector3f v, Set<Vector3f> set) {
		for(Vector3f i : set) {
			//System.out.println(v + " " + i + " " + (i.x + v.x == 0f && i.y + v.y == 0f && i.z + v.z == 0f));
			if(i.x + v.x == 0f && i.y + v.y == 0f && i.z + v.z == 0f) {
				//System.out.println(0);
				return true;
			}
		}
		return false;
	}
	
	private void testCollision(ObjectProjection o1, ObjectProjection o2) {
		for(int i = 0; i < o1.projections.length; i += 2) {
			System.out.println(o1.projections[i + 1] < o2.projections[i] || o2.projections[i + 1] < o1.projections[i]);
			if(o1.projections[i + 1] < o2.projections[i] || o2.projections[i + 1] < o1.projections[i])
				return;
		}
		System.out.println("Collision!");
		o1.object.onCollision(o2.object);
		o2.object.onCollision(o1.object);
	}
	
	private class ObjectProjection {
		private RigidObject object;
		private float[] projections;
		public ObjectProjection(RigidObject o, float[] p) {
			this.object = o;
			this.projections = p;
		}
	}
}
