package net.alevel.asteroids.engine.input.enums;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_APOSTROPHE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_BACKSLASH;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_COMMA;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_EQUAL;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_GRAVE_ACCENT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_BRACKET;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_MINUS;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_PERIOD;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT_BRACKET;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SEMICOLON;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SLASH;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;

public enum SpecialChars {
	MINUS(GLFW_KEY_MINUS),
	EQUALS(GLFW_KEY_EQUAL),
	OPEN_SQUARE_BRACKET(GLFW_KEY_LEFT_BRACKET),
	CLOSE_SQUARE_BRACKET(GLFW_KEY_RIGHT_BRACKET),
	SEMICOLON(GLFW_KEY_SEMICOLON),
	APOSTROPHE(GLFW_KEY_APOSTROPHE),
	COMMA(GLFW_KEY_COMMA),
	FULL_STOP(GLFW_KEY_PERIOD),
	FORWARD_SLASH(GLFW_KEY_SLASH),
	BACK_SLASH(GLFW_KEY_BACKSLASH),
	GRAVE(GLFW_KEY_GRAVE_ACCENT),
	SPACE(GLFW_KEY_SPACE);
	
	private final int glId;
	private SpecialChars(int glId) {
		this.glId = glId;
	}
	
	public int getGlId() {
		return this.glId;
	}
}
