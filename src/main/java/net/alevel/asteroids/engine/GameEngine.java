package net.alevel.asteroids.engine;

public class GameEngine implements Runnable {
	public static final int TARGET_FPS = 60; //frames per second
	public static final int TARGET_UPS = 50; //updates per second
	
	private final Window window;
	private final ILogic gameLogic;
	private final MouseInput mouseInput;
	
	public GameEngine(ILogic gameLogic) {
		this.window = new Window();
		this.gameLogic = gameLogic;
		this.mouseInput = new MouseInput();
	}
	
	@Override
	public void run() { 
		try {
			this.init();
			this.gameLoop();
		} catch (Exception e) {
			e.printStackTrace(); //print any error to console
		}
	}
	
	protected void init() throws Exception { //any errors will passed to the method that called this method
		this.window.init();
		this.mouseInput.init(this.window);
		this.gameLogic.init(this.window);
	}
	
	/** This is the main game loop. Methods are protected for convenience. It may come in useful if I need to alter what happens in the loop.
	 */
	protected void gameLoop() { //the main loop
		float lastLoop = System.nanoTime() / 1000_000_000f; //stores time that last loop started
		float accumulator = 0f; //stores the amount of time that the game needs to catch up with
		float interval = 1f / TARGET_UPS; //the time interval between each update (the speed of the in game clock)
		float loopSlot = 1f / TARGET_FPS; //The loop runs every frame per second, not every update per second
		
		this.input();
		
		while(!this.window.windowShouldClose()) { //game loop will stop if the window is about to close (i.e. if the user closes the window). This will cause the whole app to terminate
			float time = System.nanoTime() / 1000_000_000f;
			accumulator += time - lastLoop; //get time (in seconds) to complete last loop and add it to the time accumulated (time behind).
			lastLoop = time; //last loop is now equal to the time that this run started
			
			while(accumulator >= interval) { //keep updating until caught up with the time lost. This should mean the UPS should not change when the FPS changes
				this.update();
				accumulator -= interval;
			}
			
			this.render(); //render
			double endTime = time + loopSlot; //endTime is the start time + the minimum amount of time a loop is allowed to complete
			try { //if the loop completed too quickly, the thread pauses to keep the FPS going beyond the target FPS
				Thread.sleep((long) (endTime * 1000_000_000) - System.nanoTime()); //convert endTime to nanoseconds (its in seconds)
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) { //A lazy way of handling the fact that if the time elapsed is greater than the minimum time, the thread doesn't have to pause
			}
		}
	}
	
	/** Record any keys pressed
	 */
	protected void input() {
		
	}
	
	/** Update objects (simulate physics for that instant of time)
	 */
	protected void update() {
		
	}
	
	/** Draw the updated objects onto the screen. Then the window will be called to swap frame buffers
	 */
	protected void render() {
		
		this.window.update(); //the method will tell OpenGL to swap the old frame buffer with the new frame buffer (i.e update what is being displayed)
	}
}
