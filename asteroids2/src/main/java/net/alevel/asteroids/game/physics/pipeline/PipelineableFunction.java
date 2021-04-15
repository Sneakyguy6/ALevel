package net.alevel.asteroids.game.physics.pipeline;

import java.util.List;

import net.alevel.asteroids.game.physics.RigidObject;

/**A class that implements this can be added to a {@link FunctionPipeline}
 */
public interface PipelineableFunction {
	/**The function to be run in the pipeline
	 * @param pipelineBuffer the buffer specific to this pipeline
	 * @param globalPipelineBuffer the buffer available to all pipelines
	 * @param rigidObjects the in game objects
	 */
	public void pipeFunction(PipelineBuffer pipelineBuffer, PipelineBuffer globalPipelineBuffer, List<RigidObject> rigidObjects);
}
