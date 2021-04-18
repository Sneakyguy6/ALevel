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

/**A simple implementation of the 'Separating Axis Theorem (SAT)' algorithm. This is a common method of collision detection in games.
 * Firstly, it calculates a normal vector for each triangle. These will be used as the axis that each object.<br>
 * <br>The projection is done like this:
 * <br>let 'A' be The world coordinate of the vertex to project
 * <br>let 'n' the axis to project onto. The axis is treated as a 3D line going through the origin. n is the direction of this line
 * <br>let 'B' the point on the line where the line AB is perpendicular to the line 'n'
 * <br>then B = lambda * n
 * <br>AB = B - A
 * <br>dot(AB, n) = 0
 * <br>n.x * (n.x * lambda - A.x) + n.y * (n.y * lambda - A.y) + n.z * (n.z * lambda - A.z) = 0
 * <br>pow(n.x, 2) * lambda - A.x * n.x + pow(n.y, 2) * lambda - A.y * n.y + pow(n.z, 2) * lambda - A.z * n.z = 0
 * <br>lambda * (pow(n.x, 2) + pow(n.y, 2) + pow(n.z, 2)) = A.x * n.x + A.y * n.y + A.z * n.z
 * <br>lambda = dot(A, n) / pow(length(n), 2)
 */
public class SATJava extends FunctionPipeline implements PipelineableFunction {
	private boolean calc;
	
	public SATJava(PipelineBuffer globalPipeline) {
		super(globalPipeline);
		super.setFunctions(this);
		this.calc = false;
	}

	@Override
	public void pipeFunction(PipelineBuffer pipelineBuffer, PipelineBuffer globalPipelineBuffer, List<RigidObject> rigidObjects) {
		//calculate surface normals (these will be the axis used)
		if(!this.calc) {
			this.calc = true;
			return;
		}
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
