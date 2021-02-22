package net.alevel.asteroids.game.physics.pipeline;

import java.util.List;

import net.alevel.asteroids.game.physics.RigidObject;

public class FunctionPipeline {
	private final PipelineableFunction[] functionPipeline;
	private final PipelineBuffer bufferPipeline;
	private final PipelineBuffer globalBufferPipeline;
	
	public FunctionPipeline(PipelineBuffer globalPipeline, PipelineableFunction... functions) {
		this.functionPipeline = functions;
		this.globalBufferPipeline = globalPipeline;
		this.bufferPipeline = new PipelineBuffer();
	}
	
	public void runPipeline(List<RigidObject> rigidObjects) {
		this.bufferPipeline.releaseAll();
		for(PipelineableFunction i : this.functionPipeline)
			i.pipeFunction(this.bufferPipeline, this.globalBufferPipeline, rigidObjects);
	}
	
	public PipelineBuffer getBufferPipeline() {
		return this.bufferPipeline;
	}
}
