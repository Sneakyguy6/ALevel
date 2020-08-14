package net.alevel.asteroids.engine.input;

import org.joml.Vector2d;
import org.joml.Vector2f;

import net.alevel.asteroids.engine.Window;

import static org.lwjgl.glfw.GLFW.*;

import java.util.BitSet;

/**Can get data on mouse activity with an instance of this class (Should be a singleton)
 */
class MouseInput {
	private final Vector2d previousPos;
	private final Vector2d currentPos;
	private final Vector2f displVec; //the variables in these instances can change but the instances should not change.
	private boolean inWindow; //true is cursor is hovering in the window, false if outside window
	private final BitSet mouseButtonsPressed;
	
	public MouseInput() {
		this.previousPos = new Vector2d(-1, -1);
		this.currentPos = new Vector2d(0, 0);
		this.displVec = new Vector2f();
		this.mouseButtonsPressed = new BitSet(3);
	}
	
	/**Setup mouse event listeners
	 */
	public void init(Window window) {
		//glfwSetCursorPos(window.getWindowHandle(), window.getWidth() / 2, window.getHeight() / 2); //stops camera constantly moving glitch (it makes it worse) TODO
		glfwSetInputMode(window.getWindowHandle(), GLFW_CURSOR, GLFW_CURSOR_HIDDEN); //hides the cursor
		glfwSetCursorPosCallback(window.getWindowHandle(), (windowHande, xpos, ypos) -> { //updates the currentPos values with the current cursor location
			this.currentPos.x = xpos;
			this.currentPos.y = ypos;
		});
		glfwSetCursorEnterCallback(window.getWindowHandle(), (windowHandle, enteredWindow) -> { //updates the inWindow boolean
			this.inWindow = enteredWindow;
		});
		glfwSetMouseButtonCallback(window.getWindowHandle(), (windowHandle, button, action, mode) -> { //updates the Btn booleans so the logic can know if buttons were pressed on the mouse
			this.mouseButtonsPressed.set(0, button == GLFW_MOUSE_BUTTON_1 && action == GLFW_PRESS);
			this.mouseButtonsPressed.set(1, button == GLFW_MOUSE_BUTTON_2 && action == GLFW_PRESS);
			this.mouseButtonsPressed.set(2, button == GLFW_MOUSE_BUTTON_3 && action == GLFW_PRESS);
		});
	}
	
	public Vector2f getDisplayVec() {
		return this.displVec;
	}
	
	public Vector2d getCurrentVec() {
		return this.currentPos;
	}
	
	public void input(Window window) {
		//this.displVec.x = 0;
		//this.displVec.y = 0;
		if(this.previousPos.x > 0 && this.previousPos.y > 0 && this.inWindow) {
			double deltax = this.currentPos.x - this.previousPos.x;
			double deltay = this.currentPos.y - this.previousPos.y;
			if(deltax != 0)
				this.displVec.y += (float) deltax; //by switching y and x, the look inverts
			if(deltay != 0)
				this.displVec.x += (float) deltay;
		}
		this.previousPos.x = this.currentPos.x;
		this.previousPos.y = this.currentPos.y;
		glfwSetCursorPos(window.getWindowHandle(), window.getWidth() / 2, window.getHeight() / 2); //sets the cursor to the centre of the screen so when doing large turns, cursor has enough space to move
	}
	
	public BitSet getMouseButtonsPressed() {
		return this.mouseButtonsPressed;
	}
	
	public boolean isMouseInWindow() {
		return this.inWindow;
	}
}
