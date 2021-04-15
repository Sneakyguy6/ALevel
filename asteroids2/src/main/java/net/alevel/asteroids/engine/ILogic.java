package net.alevel.asteroids.engine;

import java.util.List;

import net.alevel.asteroids.engine.graphics.Camera;
import net.alevel.asteroids.engine.input.Input;
import net.alevel.asteroids.engine.objects.GameObject;
import net.alevel.asteroids.engine.utils.Pair;

/**Implement this interface for classes that contribute to the main logic of the game. the functions {@link ILogic#update(float, float, Input)} and {@link ILogic#toRender()} run periodically
 * as it is constantly called by the game loop. {@link ILogic#init(Window)} only runs once (at the start) and {@link ILogic#cleanUp()} also only runs once (at the end)
 */
public interface ILogic {
	/**Runs when the object is instantiated. Setup any initial states here
	 */
	public void init(Window window) throws Exception;
	
	/**Defines what should happen each update (i.e what the simulation should do at that time) (e.g. change object states and/or positions)
	 * @param interval the time between this update and the previous update (in terms of in game clock)
	 * @param input the sample of the mouse and keyboard inputs at a certain instant
	 */
	public void update(float accumulatedTime, float interval, Input input) throws Exception;
	
	/**Tells the renderer what to render and also passes the camera to use
	 * @param camera camera to use
	 * @param objectsToRender game objects that will be rendered. If you do not want an object to be rendered, exclude it from this list
	 */
	public Pair<Camera, List<GameObject>> toRender();
	
	/**Runs when the object is about to be destroyed (either on shutdown or not needed anymore)
	 */
	public void cleanUp();
}
