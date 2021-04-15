package net.alevel.asteroids.game;

import java.io.IOException;

import net.alevel.asteroids.engine.GameEngine;

/**The entry point of the program. It doesn't do much since {@link GameLogic} handles all the main logic
 */
public class Main {
	public static void main(String... args) throws IOException {
		new GameEngine(GameLogic.init()).run();
	}
}
