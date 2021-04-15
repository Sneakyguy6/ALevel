package net.alevel.asteroids.game.physics.pipeline;

/**Any class that implements this can be added to a {@link PipelineBuffer}. It makes sure that at the end of the pipeline, the buffer can be reset safely
 */
public interface Releasable {
	public void release();
}
