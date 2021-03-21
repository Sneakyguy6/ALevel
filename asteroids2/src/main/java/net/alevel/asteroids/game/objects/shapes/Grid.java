package net.alevel.asteroids.game.objects.shapes;

import net.alevel.asteroids.engine.graphics.Mesh;
import net.alevel.asteroids.game.objects.GameObjects;
import net.alevel.asteroids.game.objects.ModifiableMesh;
import net.alevel.asteroids.game.objects.StaticGameObject;

public class Grid {
	public static ModifiableMesh create(int width, int length, int div) {
		float[] points = new float[width * length * (1/div) * 3];
		//System.out.println(points.length);
		//System.out.println(width + " " + length);
		int[] indices = new int[(width - 1) * (length - 1) * 12];
		
		int c = 0;
		for(float i = 0; i < length; i += div) {
			for(float j = 0; j < width; j += div) {
				points[c] = i;
				points[c + 1] = 0;
				points[c + 2] = j;
				c += 3;
				//System.out.println(i + "f, 0f, " + j + "f, ");
			}
		}
		//System.out.println(Arrays.toString(points));
		
		c = 0;
		int c2 = 0;
		for(int i = 0; i < length; i++) {
			if(i + 1 != length)
			for(int j = 0; j < width; j++) {
				if(j + 1 != width) {
					indices[c2] = c;
					indices[c2 + 1] = c + 1;
					indices[c2 + 2] = c + width + 1;
					indices[c2 + 3] = c;
					indices[c2 + 4] = c + width;
					indices[c2 + 5] = c + width + 1;
					
					//System.out.println(c + ", " + (c + 1) + ", " + (c + width + 1) + ",");
					//System.out.println(c + ", " + (c + width) + ", " + (c + width + 1) + ",");
				}
				
				c++;
				c2 += 6;
			}
		}
		//System.out.println(Arrays.toString(indices));
		
		return new ModifiableMesh(points, indices);
	}
	
	public static void debug(GameObjects objects) {
		float[] points = {
				
				/*0.0f, 0, 0.0f, 
				0.0f, 0, 1.0f, 
				0.0f, 0, 2.0f, 
				0.0f, 0, 3.0f, 
				1.0f, 0, 0.0f, 
				1.0f, 0, 1.0f, 
				1.0f, 0, 2.0f, 
				1.0f, 0, 3.0f, 
				2.0f, 0, 0.0f, 
				2.0f, 0, 1.0f, 
				2.0f, 0, 2.0f, 
				2.0f, 0, 3.0f, 
				3.0f, 0, 0.0f, 
				3.0f, 0, 1.0f, 
				3.0f, 0, 2.0f, 
				3.0f, 0, 3.0f, */
				0.0f, 0f, 0.0f, 
				0.0f, 0f, 1.0f, 
				0.0f, 0f, 2.0f, 
				0.0f, 0f, 3.0f, 
				1.0f, 0f, 0.0f, 
				1.0f, 0f, 1.0f, 
				1.0f, 0f, 2.0f, 
				1.0f, 0f, 3.0f, 
				2.0f, 0f, 0.0f, 
				2.0f, 0f, 1.0f, 
				2.0f, 0f, 2.0f, 
				2.0f, 0f, 3.0f, 
				3.0f, 0f, 0.0f, 
				3.0f, 0f, 1.0f, 
				3.0f, 0f, 2.0f, 
				3.0f, 0f, 3.0f,
		};
		int[] indices = {
				0, 1, 5,
				0, 4, 5,
				1, 2, 6,
				1, 5, 6,
				2, 3, 7,
				2, 6, 7,
				4, 5, 9,
				4, 8, 9,
				5, 6, 10,
				5, 9, 10,
				6, 7, 11,
				6, 10, 11,
				8, 9, 13,
				8, 12, 13,
				9, 10, 14,
				9, 13, 14,
				10, 11, 15,
				10, 14, 15,
				/*0, 1, 5,
				0, 4, 5,
				1, 2, 6,
				1, 5, 6,
				2, 3, 7,
				2, 6, 7,
				4, 5, 9,
				4, 8, 9,
				5, 6, 10,
				5, 9, 10,
				6, 7, 11,
				6, 10, 11,
				8, 9, 13,
				8, 12, 13,
				9, 10, 14,
				9, 13, 14,
				10, 11, 15,
				10, 14, 15,*/
				//11, 12, 16,
				//11, 15, 16,
				/*0, 4, 5,
				0, 1, 5,
				1, 5, 6,
				2, 6, 7,
				3, 7, 8,
				4, 8, 9,*/
		};
		objects.spawnObject(new StaticGameObject(new Mesh(points, points, points, indices)));
	}
}
