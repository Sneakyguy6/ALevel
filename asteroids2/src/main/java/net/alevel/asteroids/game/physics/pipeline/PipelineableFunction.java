package net.alevel.asteroids.game.physics.pipeline;

import java.util.List;

import net.alevel.asteroids.game.physics.RigidObject;

public interface PipelineableFunction {
	public void pipeFunction(PipelineBuffer pipelineBuffer, PipelineBuffer globalPipelineBuffer, List<RigidObject> rigidObjects);
}
