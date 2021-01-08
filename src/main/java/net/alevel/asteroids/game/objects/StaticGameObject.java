package net.alevel.asteroids.game.objects;

import net.alevel.asteroids.engine.graphics.Mesh;
import net.alevel.asteroids.engine.objects.GameObject;

public class StaticGameObject extends GameObject {

	public StaticGameObject(Mesh mesh) {
		super(mesh);
	}

	@Override
	public void onUpdate(float time) { //static objects do nothing
	}

	@Override
	public void onUpdateFinish(float time) {
	}

	@Override
	public void onSpawn(GameObjects objectsManager) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDespawn(GameObjects objectsManager) {
		// TODO Auto-generated method stub
		
	}
}
