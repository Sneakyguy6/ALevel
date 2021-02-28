package net.alevel.asteroids.game.physics.pipeline;

import java.util.Iterator;
import java.util.List;

import net.alevel.asteroids.game.physics.RigidObject;

public class FunctionPipeline implements Iterable<PipelineableFunction> {
	private PipelineableFunction[] functionPipeline;
	private final PipelineBuffer bufferPipeline;
	private final PipelineBuffer globalBufferPipeline;
	
	public FunctionPipeline(PipelineBuffer globalPipeline, PipelineableFunction... functions) {
		this.functionPipeline = functions;
		this.globalBufferPipeline = globalPipeline;
		this.bufferPipeline = new PipelineBuffer();
	}
	
	protected void setFunctions(PipelineableFunction... functions) {
		this.functionPipeline = functions;
	}
	
	public void runPipeline(List<RigidObject> rigidObjects) {
		this.bufferPipeline.releaseAll();
		for(PipelineableFunction i : this.functionPipeline)
			i.pipeFunction(this.bufferPipeline, this.globalBufferPipeline, rigidObjects);
	}
	
	@Override
	public Iterator<PipelineableFunction> iterator() {
		return new Iterator<PipelineableFunction>() {
			private int pointer = 0;
			@Override
			public boolean hasNext() {
				return pointer < functionPipeline.length;
			}

			@Override
			public PipelineableFunction next() {
				return functionPipeline[pointer++];
			}
			
			@Override
			public void remove() {
				throw new UnsupportedOperationException("Cannot remove functions from pipeline!");
			}
		};
	}
	
	public PipelineBuffer getBufferPipeline() {
		return this.bufferPipeline;
	}
}
