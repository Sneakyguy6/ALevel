package net.alevel.asteroids.game;

import java.io.IOException;

import net.alevel.asteroids.engine.GameEngine;

public class Main {
	public static void main(String... args) throws IOException {
		new GameEngine(GameLogic.init()).run();
	}
}
