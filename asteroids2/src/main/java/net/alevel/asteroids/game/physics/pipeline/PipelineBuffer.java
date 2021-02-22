package net.alevel.asteroids.game.physics.pipeline;

import java.util.HashMap;
import java.util.Map;

public class PipelineBuffer {
	private Map<Integer, PipelineBufferable> pipeProps;
	
	public PipelineBuffer() {
		this.pipeProps = new HashMap<Integer, PipelineBufferable>();
	}
	
	public PipelineBufferable get(int i) {
		return this.pipeProps.get(i);
	}
	
	public void add(int i, PipelineBufferable buf) {
		if(this.pipeProps.containsKey(i))
			throw new RuntimeException("The index " + i + " is already in use");
		this.pipeProps.put(i, buf);
	}
	
	public void release(int i) {
		this.pipeProps.get(i).release();
		this.pipeProps.remove(i);
	}
	
	public void releaseAll() {
		for(PipelineBufferable i : this.pipeProps.values())
			i.release();
		this.pipeProps.clear();
	}
}
