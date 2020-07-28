package net.alevel.asteroids.engine;

/** Implement this interface for classes that contribute to the logic of the game
 */
public interface ILogic {
	/**Runs when the object is instantiated. Setup any initial states here
	 */
	public void init(Window window) throws Exception;
	
	/**Take keyboard and mouse input
	 */
	public void input(Window window, MouseInput mouseInput);
	
	/**Simulate the game (change object states and/or positions)
	 */
	public void update(float interval, MouseInput mouseInput);
	
	/**Tells the engine what to render. The game logic can control what is rendered with this method
	 */
	public void render(Window window);
	
	/**Runs when the object is about to be destroyed (either on shutdown or not needed anymore)
	 */
	public void cleanUp();
}
