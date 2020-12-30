package net.alevel.asteroids.game.physics;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joml.Vector3f;

import net.alevel.asteroids.engine.GameObject;

/**Checks for collisions using the 'Separating axis' theorem
 */
public class SATCollision {
	public static void checkCollisions(List<GameObject> objects) {
		for(int i = 0; i < objects.size(); i++) {
			for(int j = i + 1; j < objects.size(); j++) {
				GameObject o1 = objects.get(i),
						   o2 = objects.get(j);
				if(!(o1 instanceof RigidObject) || !(o2 instanceof RigidObject)) //check that they have the ability to collide
					continue;
				RigidObject rigid1 = (RigidObject) o1,
							rigid2 = (RigidObject) o2;
				if(checkIfCollide(rigid1, rigid2)) {
					rigid1.onCollision(rigid2);
					rigid2.onCollision(rigid1);
				}
			}
		}
	}
	
	private static boolean checkIfCollide(RigidObject o1, RigidObject o2) {
		Map<Vector3f, float[]> o1Points = o1.getMinMaxPoints();
		Map<Vector3f, float[]> o2Points = o2.getMinMaxPoints();
		System.out.println(o1Points.size());
		for(Vector3f axis : o1Points.keySet()) {
			if(!o2Points.containsKey(axis)) //this assumes that util.Map locates a value depending on the components of the vector and not the vector object itself
				continue; //(depends on how equals and hashcode are defined in Vector3f) need to check
			
			float o1ProjectedPos = o1.getPosition().dot(axis);
			System.out.print(o1ProjectedPos + "\t");
			float o2ProjectedPos = o2.getPosition().dot(axis);
			System.out.print(o2ProjectedPos);
			System.out.println("____________________");
			float[] o1MinMax = o1Points.get(axis);
			float[] o2MinMax = o2Points.get(axis);
			if(o2ProjectedPos + o2MinMax[0] > o1MinMax[1] + o1ProjectedPos)
				return false;
			if(o1ProjectedPos + o1MinMax[0] > o2MinMax[1] + o2ProjectedPos)
				return false;
		}
		System.out.println();
		return true;
	}
	
	public static Map<Vector3f, float[]> getMinMaxPoints(float[] vertices, int[] indices) {
		//calculate surfaceNormals (these will be the list of axis to be used in SAT)
		Map<Vector3f, float[]> minMaxPoints = new HashMap<Vector3f, float[]>();
		Set<Vector3f> surfaceNormals = new HashSet<Vector3f>();
		//System.out.println(vertices.length + " " + indices.length);
		for(int i = 0; i < indices.length - 2; i += 3) { //from https://stackoverflow.com/questions/19350792/calculate-normal-of-a-single-triangle-in-3d-space
			Vector3f a = new Vector3f(
					vertices[indices[i + 1] * 3] - vertices[indices[i] * 3],
					vertices[(indices[i + 1] * 3) + 1] - vertices[(indices[i] * 3) + 1],
					vertices[(indices[i + 1] * 3) + 2] - vertices[(indices[i] * 3) + 2]);
			Vector3f b = new Vector3f(
					vertices[indices[i + 2] * 3] - vertices[indices[i] * 3],
					vertices[(indices[i + 2] * 3) + 1] - vertices[(indices[i] * 3) + 1],
					vertices[(indices[i + 2] * 3) + 2] - vertices[(indices[i] * 3) + 2]);
			Vector3f n = new Vector3f(
					(a.y * b.z) - (a.z * b.y),
					(a.z * b.x) - (a.x * b.z),
					(a.x * b.y) - (a.y * b.x));
			n.x = Math.abs(n.x);
			n.y = Math.abs(n.y);
			n.z = Math.abs(n.z);
			surfaceNormals.add(n);
		}
		System.out.println("Surface normals: " + surfaceNormals + " -> " + surfaceNormals.size() + " elements");
		
		//calculate max and min coordinate on each axis
		for(Vector3f normal : surfaceNormals) { //https://gamedevelopment.tutsplus.com/tutorials/collision-detection-using-the-separating-axis-theorem--gamedev-169
			float min = 0,
				  max = 0;
			for(int i = 0; i < vertices.length; i += 3) {
				float value = new Vector3f(vertices[i], vertices[i + 1], vertices[i + 2]).dot(normal.normalize());
				if(value > max)
					max = value;
				else if(value < min)
					min = value;
			}
			minMaxPoints.put(normal, new float[] {min, max});
		}
		minMaxPoints.forEach((a, b) -> System.out.println(a + " -> [" + b[0] + " " + b[1] + "]"));
		System.out.println(minMaxPoints.size());
		return minMaxPoints;
	}
}


/*Matrix3f rotation = new Matrix3f()
.rotate((float) Math.acos(normal.x / (Math.sqrt(Math.pow(normal.x, 2) + Math.pow(normal.z, 2)))), new Vector3f(0, 1, 0))
.rotate((float) Math.acos(normal.x / (Math.sqrt(Math.pow(normal.x, 2) + Math.pow(normal.y, 2)))), new Vector3f(0, 1, 0));
System.out.println(rotation);
for(int i = 0; i < vertices.length; i += 3) {
Vector3f vertex = new Vector3f(vertices[i], vertices[i + 1], vertices[i + 2]);
vertex.mul(rotation);
if(vertex.x < min)
min = vertex.x;
else if(vertex.x > max)
max = vertex.x;
}
minMaxPoints.put(normal, new float[] {min, max});*/