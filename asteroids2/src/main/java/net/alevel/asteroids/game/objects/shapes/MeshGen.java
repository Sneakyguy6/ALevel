package net.alevel.asteroids.game.objects.shapes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.joml.Matrix2f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import net.alevel.asteroids.engine.graphics.Mesh;
import net.alevel.asteroids.engine.objects.GameObject;
import net.alevel.asteroids.game.objects.ModifiableMesh;

/**Creates different shapes.<br>
 * NOTE: when used in a {@link GameObject}, the position points to the centre of the shape
 */
public class MeshGen {
	/**Creates a mesh of a cube
	 * @param length the cube length
	 * @param height the cube height
	 * @param width the cube width
	 * @return a mesh of the cube
	 */
	public static Mesh cube(float length, float height, float width) { //x, y, z
		float[] positions = {
				length / 2, height / 2, width / 2,   //0
				-length / 2, height / 2, width / 2,  //1
				length / 2, -height / 2, width / 2,  //2
				length / 2, height / 2, -width / 2,  //3
				-length / 2, -height / 2, width / 2, //4
				length / 2, -height / 2, -width / 2, //5
				-length / 2, height / 2, -width / 2, //6
				-length / 2, -height / 2, -width / 2 //7
		};
		int[] indices = {
			 	0, 1, 2,
			 	1, 2, 4,
			 	1, 6, 4,
			 	7, 6, 4,
			 	6, 7, 5,
			 	7, 3, 5,
			 	0, 2, 3,
			 	3, 2, 5,
			 	0, 1, 6,
			 	0, 6, 3,
			 	2, 4, 7,
			 	2, 5, 7
		};
		return new Mesh(positions, positions, positions, indices);
	}
	
	/**Constructs a triangle from the 3 vertices and extrudes it a certain length
	 * @param v1 Vertex 1 of the triangle
	 * @param v2 Vertex 2 of the triangle
	 * @param v3 Vertex 3 of the triangle
	 * @param length the length of the prism
	 * @return a mesh of a triangular prism
	 */
	public static Mesh triangularPrism(Vector2f v1, Vector2f v2, Vector2f v3, float length) {
		Vector2f midpoint = new Vector2f(
				(v1.x + v2.x + v3.x) / 3,
				(v1.y + v2.y + v3.y) / 3);
		Vector2f newV1 = v1.sub(midpoint);
		Vector2f newV2 = v2.sub(midpoint);
		Vector2f newV3 = v3.sub(midpoint);
		float[] positions = {
				newV1.x, newV1.y, length / 2,  //0
				newV2.x, newV2.y, length / 2,  //1
				newV3.x, newV3.y, length / 2,  //2
				newV1.x, newV1.y, -length / 2, //3
				newV2.x, newV2.y, -length / 2, //4
				newV3.x, newV3.y, -length / 2, //5
		};
		int[] indices = {
			0, 1, 2,
			3, 4, 5,
			0, 3, 4,
			0, 1, 4,
			0, 5, 2,
			0, 5, 3,
			1, 4, 2,
			2, 4, 5
		};
		return new Mesh(positions, positions, positions, indices);
	}
	
	public static Mesh sphere(float f) {
		return sphere(f, 5);
	}
	
	/**Creates a mesh of a sphere
	 * @param radius
	 * @param resolution
	 * @return a mesh of the sphere
	 */
	public static Mesh sphere(float radius, int resolution) {
		/*GenOctahedron genOctahedron = new GenOctahedron(resolution);
		List<Vector3f> positions = genOctahedron.getPositions();
		float[] floats = new float[positions.size() * 3];
		for(int i = 0; i < positions.size(); i++) {
			Vector3f vertex = positions.get(i);
			float scale = radius - vertex.length();
			float dX = vertex.x * (scale / vertex.length());
			float dY = vertex.y * (scale / vertex.length());
			float dZ = vertex.z * (scale / vertex.length());
			floats[i * 3] = vertex.x + dX;
			floats[i * 3 + 1] = vertex.y + dY;
			floats[i * 3 + 2] = vertex.z + dZ;
		}
		return new Mesh(floats, floats, floats, genOctahedron.getIndices());*/
		ModifiableMesh m = modifiableSphere(radius, resolution);
		return new Mesh(m.getPositions(), null, null, m.getIndices());
	}
	
	public static ModifiableMesh modifiableSphere(float radius, int resolution) {
		GenOctahedron genOctahedron = new GenOctahedron(resolution);
		List<Vector3f> positions = genOctahedron.getPositions();
		float[] floats = new float[positions.size() * 3];
		for(int i = 0; i < positions.size(); i++) {
			Vector3f vertex = positions.get(i);
			float scale = radius - vertex.length();
			float dX = vertex.x * (scale / vertex.length());
			float dY = vertex.y * (scale / vertex.length());
			float dZ = vertex.z * (scale / vertex.length());
			floats[i * 3] = vertex.x + dX;
			floats[i * 3 + 1] = vertex.y + dY;
			floats[i * 3 + 2] = vertex.z + dZ;
		}
		return new ModifiableMesh(floats, genOctahedron.getIndices());
	}
	
	public static Mesh cylinder(float radius, float length) {
		return cylinder(radius, length, 4);
	}
	
	public static Mesh cylinder(float radius, float length, int circleResolution) {
		Matrix2f rotationMatrix = new Matrix2f().rotate((float) (Math.PI / 2) / circleResolution); //Where the angle is 90 degrees / circleResolution
		List<Vector2f> pointsOnCircle = new ArrayList<Vector2f>(); //holds all points on a circle
		Vector2f temp = new Vector2f(1, 0);
		pointsOnCircle.add(temp);
		for(int i = 0; i < 4 * circleResolution; i++) { //to rotate around 360 degrees, do number of points per 90 degrees (circleResolution) 4 times
			temp = new Vector2f().set(temp).mul(rotationMatrix);
			pointsOnCircle.add(temp);
		}
		float[] positions = new float[pointsOnCircle.size() * 6 + 6]; //* 2 (because 2 circles) * 3 (because each vertex has 3 components) therefore * 6 + 6 for 2 centres
		for(int i = 0; i < pointsOnCircle.size(); i++) {
			positions[i * 6] = pointsOnCircle.get(i).x;
			positions[(i * 6) + 1] = pointsOnCircle.get(i).y;
			positions[(i * 6) + 2] = -length / 2;
			positions[(i * 6) + 3] = pointsOnCircle.get(i).x;
			positions[(i * 6) + 4] = pointsOnCircle.get(i).y;
			positions[(i * 6) + 5] = length / 2;
		}
		//add centres
		positions[positions.length - 6] = 0;
		positions[positions.length - 5] = 0;
		positions[positions.length - 4] = -length / 2;
		positions[positions.length - 3] = 0;
		positions[positions.length - 2] = 0;
		positions[positions.length - 1] = length / 2;
		
		int[] indices = new int[positions.length + (pointsOnCircle.size() * 6)];
		int i = 0;
		for(int j = 0; i < pointsOnCircle.size() * 6; i += 3, j++) {
			indices[i] = j;
			indices[i + 1] = (j + 1) % (pointsOnCircle.size() * 2);
			indices[i + 2] = (j + 2) % (pointsOnCircle.size() * 2);
		}
		for(int j = 0, c = 0; c < pointsOnCircle.size(); i += 3, j += 2, c++) {
			indices[i] = j;
			indices[i + 1] = (positions.length / 3) - 2;
			indices[i + 2] = j + 2;
		}
		for(int j = 1; i < indices.length - 2; i += 3, j += 2) {
			indices[i] = j;
			indices[i + 1] = (positions.length / 3) - 1;
			indices[i + 2] = j + 2;
		}
		System.out.println(Arrays.toString(indices));
		System.out.println(Arrays.toString(positions) + pointsOnCircle.size());
		return new Mesh(positions, positions, positions, indices);
	}
}
