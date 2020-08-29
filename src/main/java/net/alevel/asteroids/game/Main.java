package net.alevel.asteroids.game;

import net.alevel.asteroids.engine.GameEngine;

public class Main {
	public static void main(String... args) {
		new GameEngine(GameLogic.init()).run();
	}
}
