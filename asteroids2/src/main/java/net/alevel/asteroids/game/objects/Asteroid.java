package net.alevel.asteroids.game.objects;

import net.alevel.asteroids.engine.graphics.Mesh;
import net.alevel.asteroids.game.noise.Perlin2;
import net.alevel.asteroids.game.objects.shapes.MeshGen;

public class Asteroid extends StaticGameObject {

	public Asteroid() {
		super(generate());
		super.scale = 10;
	}
	
	private static Mesh generate() {
		ModifiableMesh asteroid = MeshGen.modifiableSphere(5, 4);
		Perlin2 noise2 = new Perlin2();
		for(int i = 0; i < asteroid.getPositions().length / 100; i++) {
			for(int j = 0; j < 100; j++) {
				float height = (float) noise2.get((double) i / 500d, (double) j / 500d);
				//System.out.println(height);
				asteroid.changePosition((i * 100) + j + 1, (float) (asteroid.getPositions()[(i * 100) + j + 1] * (height + 0.5)));
			}
		}
		
		return new Mesh(asteroid.getPositions(), asteroid.getPositions(), asteroid.getPositions(), asteroid.getIndices());
	}
}
