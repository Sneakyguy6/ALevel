package net.alevel.asteroids.game;

import net.alevel.asteroids.engine.GameEngine;

public class Main {
	public static void main(String... args) {
		GameLogic.init();
		new GameEngine(GameLogic.getInstance()).run();
	}
}
