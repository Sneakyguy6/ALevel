package net.alevel.asteroids.engine;

import java.util.List;

import net.alevel.asteroids.engine.graphics.Camera;
import net.alevel.asteroids.engine.graphics.Renderer;
import net.alevel.asteroids.engine.input.Input;
import net.alevel.asteroids.engine.objects.GameObject;
import net.alevel.asteroids.engine.utils.Pair;

public class GameEngine implements Runnable {
	/**The target number of 'Frames per second' to be rendered.
	 */
	public static final int TARGET_FPS = 60;
	/**The target number of 'Updates per second'. It is the speed of the in game clock.
	 */
	public static final int TARGET_UPS = 100; //updates per second
	
	private final Window window;
	private final Renderer renderer;
	private final ILogic gameLogic;
	private final Input humanInput;
	
	public GameEngine(ILogic gameLogic) {
		this.window = new Window();
		this.renderer = new Renderer();
		this.gameLogic = gameLogic;
		this.humanInput = new Input();
	}
	
	/**Starts the simulation
	 */
	@Override
	public void run() { 
		try {
			this.init();
			this.gameLoop();
		} catch (Exception e) {
			System.out.println("A fatal error has occured and the program has stopped!");
			e.printStackTrace(); //print any error to console
		}
	}
	
	/**Initialises the core components (like the renderer and the input sampler) and runs {@link ILogic#init(Window)} to setup the initial state of the simulation
	 * @throws Exception
	 */
	protected void init() throws Exception { //any errors will passed to the method that called this method
		//CLManager.init();
		this.window.init();
		this.renderer.initShaderProgram();
		this.humanInput.init(this.window);
		this.gameLogic.init(this.window);
	}
	
	/**This is the main game loop. Methods are protected for convenience. It may come in useful if I need to alter what happens in the loop.<br>
	 * It is designed to hit {@link GameEngine#TARGET_FPS} and {@link GameEngine#TARGET_UPS} values. If a render takes more time than the UPS time interval, multiple updates will
	 * happen before the next render to catch up. This ensures that the actual speed of the in game clock does not change when the FPS changes
	 * @throws Exception 
	 */
	protected void gameLoop() throws Exception { //the main loop
		float lastLoop = System.nanoTime() / 1000_000_000f; //stores time that last loop started
		float accumulator = 0f; //stores the amount of time that the game needs to catch up with
		float interval = 1f / TARGET_UPS; //the time interval between each update (the speed of the in game clock)
		float loopSlot = 1f / TARGET_FPS; //The loop runs every frame per second, not every update per second
		float totalTime = 0; //the actual in game time (time the simulation has been running for)
		
		while(!this.window.windowShouldClose()) { //game loop will stop if the window is about to close (i.e. if the user closes the window). This will cause the whole app to terminate
			float time = System.nanoTime() / 1000_000_000f;
			accumulator += time - lastLoop; //get time (in seconds) to complete last loop and add it to the time accumulated (time behind).
			lastLoop = time; //last loop is now equal to the time that this run started
			
			this.input();
			
			for(; accumulator >= interval; accumulator -= interval) { //keep updating until caught up with the time lost. This should mean the UPS should not change when the FPS changes
				this.update(totalTime + accumulator, 0.0001f); //the value passed here is 1 in game time second. You can change the speed of the physics with this value
				totalTime += interval; //test with this and 0.0001f
			}
			
			this.render(); //render
			double endTime = time + loopSlot; //endTime is the start time + the minimum amount of time a loop is allowed to complete
			try { //if the loop completed too quickly, the thread pauses to keep the FPS going beyond the target FPS
				Thread.sleep((long) (endTime * 1000) - (System.nanoTime() / 1000_000)); //convert endTime to milliseconds (its in seconds)
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) { //A lazy way to handle the fact that if the time elapsed is greater than the minimum time, the thread doesn't have to pause
			}
		}
		
		this.cleanUp();
	}
	
	/**Samples any keys pressed
	 */
	protected void input() {
		this.humanInput.input(this.window);
	}
	
	/**Update objects (simulate physics for that instant of time)
	 * @throws Exception 
	 */
	protected void update(float accumulatedTime, float interval) throws Exception {
		this.gameLogic.update(accumulatedTime, interval, this.humanInput);
	}
	
	/**Draw the updated objects onto the screen. Then the window will be called to swap frame buffers
	 */
	protected void render() {
		Pair<Camera, List<GameObject>> p = this.gameLogic.toRender();
		this.renderer.render(this.window, p.getO1(), p.getO2());
		this.window.update(); //the method will tell OpenGL to swap the old frame buffer with the new frame buffer (i.e update what is being displayed)
	}
	
	/**To be run when the simulation is about to end.
	 */
	protected void cleanUp() {
		this.renderer.cleanUp();
		this.gameLogic.cleanUp();
		//CLManager.cleanUp();
	}
}
