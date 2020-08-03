package net.alevel.asteroids.game;

import net.alevel.asteroids.engine.GameObject;
import net.alevel.asteroids.engine.graphics.Mesh;

public class StaticGameObject extends GameObject {

	public StaticGameObject(Mesh mesh) {
		super(mesh);
	}

	@Override
	public void update(float time) { //static objects do nothing
	}
}
