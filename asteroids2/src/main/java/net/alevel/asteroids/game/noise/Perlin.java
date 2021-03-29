package net.alevel.asteroids.game.noise;

import java.util.Random;

import org.joml.Vector2d;

/**Use Perlin2
 *
 */
@Deprecated
public class Perlin {
	private static final Vector2d[] gVectorList = {
			new Vector2d(1, 0),
			new Vector2d(1, 1),
			new Vector2d(0, 1),
			new Vector2d(-1, 1),
			new Vector2d(-1, 0),
			new Vector2d(-1, -1),
			new Vector2d(0, -1),
			new Vector2d(1, -1)
	};
	
	private final int[][] gVectors;
	//private final int[][] influenceValues;
	
	public Perlin(int width, int height, long seed) {
		this.gVectors = new int[width][height];
		//this.influenceValues = new int[width][height];
		Random rng = new Random(seed);
		
		//System.out.println(width + " " + height);
		for(int i = 0; i < width; i++) {
			for(int j = 0; j < height; j++) {
				this.gVectors[i][j] = rng.nextInt(8);
				//this.influenceValues[i][j] = rng.nextInt(1);
				//System.out.println(gVectors[i][j]);
			}
		}
	}
	
	public double get(double x, double y) {
		//get reference to square that point is in. Where (x0, y0) represent bottom left corner
		int x0 = (int) x,
			x1 = x0 + 1,
			y0 = (int) y,
			y1 = y0 + 1;
		//System.out.println(x + " " + y);
		
		//calculate distance vectors
		Vector2d[] distVectors = {
				new Vector2d(x - x0, y - y0),
				new Vector2d(x - x0, y - y1),
				new Vector2d(x - x1, y - y0),
				new Vector2d(x - x1, y - y1)
		};
		//System.out.println(Arrays.toString(distVectors));
		
		double[] dotProducts = {
				gVectorList[this.gVectors[x0][y0]].dot(distVectors[0]),
				gVectorList[this.gVectors[x0][y1]].dot(distVectors[1]),
				gVectorList[this.gVectors[x1][y0]].dot(distVectors[2]),
				gVectorList[this.gVectors[x1][y1]].dot(distVectors[3]),
		};
		
		double u = fade(x - x0),
			   v = fade(y - y0);
		
		double lerp1 = lerp(dotProducts[0], dotProducts[1], v);
		double lerp2 = lerp(dotProducts[2], dotProducts[3], v);
		
		double avg = lerp(lerp1, lerp2, u);
		//System.out.println(avg);
		
		return avg;
	}
	
	private double lerp(double a, double b, double x) {
		return a + x * (b - a);
	}
	
	private double fade(double x) {
		return (6 * Math.pow(x, 5)) - (15 * Math.pow(x, 4)) + (10 * Math.pow(x, 3));
	}
}
