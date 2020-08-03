package net.alevel.asteroids.engine.input;

import java.util.BitSet;

import org.joml.Vector2d;
import org.joml.Vector2f;

import net.alevel.asteroids.engine.Window;
import net.alevel.asteroids.engine.input.enums.MouseBtns;
import net.alevel.asteroids.engine.input.enums.NonPrintableChars;
import net.alevel.asteroids.engine.input.enums.SpecialChars;

public class Input {
	private final MouseInput mouse;
	private final Vector2f deltaMouseMove; //create copies of mouse pos to store values at the instant they were recorded
	private final Vector2d currentMousePos;
	private boolean isMouseInWindow;
	private final BitSet mouseBtnsPressed;
	
	private final KeyBoardInput keyBoard; //keyboard does not update its variables until the update method is run so dont need to create copies
	
	public Input() {
		this.mouse = new MouseInput();
		this.deltaMouseMove = new Vector2f();
		this.currentMousePos = new Vector2d();
		this.mouseBtnsPressed = new BitSet(this.mouse.getMouseButtonsPressed().size());
		this.keyBoard = new KeyBoardInput();
	}
	
	public void init(Window window) {
		this.mouse.init(window);
	}
	
	public void input(Window window) {
		this.mouse.input(window);
		this.deltaMouseMove.set(this.mouse.getDisplayVec());
		this.currentMousePos.set(this.mouse.getCurrentVec());
		this.isMouseInWindow = this.mouse.isMouseInWindow();
		this.mouseBtnsPressed.clear();
		this.mouseBtnsPressed.or(this.mouse.getMouseButtonsPressed()); //copy values not object reference, should be independant of each other
		this.keyBoard.input(window.getWindowHandle());
	}
	
	public boolean isKeyPressed(char character) {
		if(character >= 65 && character <= 90) //if true, it is an alphabetical letter
			return this.keyBoard.getAlphabetKeysPressed().get(character - 65);
		else if(character >= 48 && character <= 57) //if true, it is a number
			return this.keyBoard.getNumberKeysPressed().get(character - 48);
		else
			throw new IndexOutOfBoundsException("This method only accepts characters A-Z (capitals) and 0-9");
	}
	
	public boolean isKeyPressed(SpecialChars character) {
		return this.keyBoard.getSpecialCharacters().get(character.ordinal());
	}
	
	public boolean isKeyPressed(NonPrintableChars character) {
		return this.keyBoard.getNonPrintableCharacters().get(character.ordinal());
	}
	
	public boolean isMouseBtnPressed(MouseBtns btn) {
		return this.mouseBtnsPressed.get(btn.ordinal());
	}
	
	public Vector2f getDeltaMousePos() {
		return this.deltaMouseMove;
	}
	
	public Vector2d getCurrentMousePos() {
		return this.currentMousePos;
	}
	
	public boolean isMouseInWindow() {
		return this.isMouseInWindow;
	}
}