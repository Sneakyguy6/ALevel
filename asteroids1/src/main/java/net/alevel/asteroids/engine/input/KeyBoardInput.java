package net.alevel.asteroids.engine.input;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.glfwGetKey;

import java.util.BitSet;

import net.alevel.asteroids.engine.input.enums.NonPrintableChars;
import net.alevel.asteroids.engine.input.enums.SpecialChars;

class KeyBoardInput {
	private final BitSet alphabetKeysPressed; //best way to store a set of flags
	private final BitSet numberKeysPressed;
	private final BitSet specialCharacters;
	private final BitSet nonPrintableCharacters;
	
	public KeyBoardInput() {
		this.alphabetKeysPressed = new BitSet(26);
		this.numberKeysPressed = new BitSet(10);
		this.specialCharacters = new BitSet(SpecialChars.values().length);
		this.nonPrintableCharacters = new BitSet(NonPrintableChars.values().length);
	}
	
	public void input(long windowId) {
		for(int i = 65; i <= 90; i++)
			this.alphabetKeysPressed.set(i - 65, glfwGetKey(windowId, i) == GLFW_PRESS);
		for(int i = 0; i <= 9; i++)
			this.numberKeysPressed.set(i, glfwGetKey(windowId, i + 48) == GLFW_PRESS);
		for(int i = 0; i < SpecialChars.values().length; i++)
			this.specialCharacters.set(i, glfwGetKey(windowId, SpecialChars.values()[i].getGlId()) == GLFW_PRESS);
		for(int i = 0; i < NonPrintableChars.values().length; i++)
			this.nonPrintableCharacters.set(i, glfwGetKey(windowId, NonPrintableChars.values()[i].getGlId()) == GLFW_PRESS);
	}

	public BitSet getAlphabetKeysPressed() {
		return alphabetKeysPressed;
	}

	public BitSet getNumberKeysPressed() {
		return numberKeysPressed;
	}

	public BitSet getSpecialCharacters() {
		return specialCharacters;
	}

	public BitSet getNonPrintableCharacters() {
		return nonPrintableCharacters;
	}
}
