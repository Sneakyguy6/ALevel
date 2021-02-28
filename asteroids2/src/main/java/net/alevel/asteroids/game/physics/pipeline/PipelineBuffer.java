package net.alevel.asteroids.game.physics.pipeline;

import java.util.HashMap;
import java.util.Map;

public class PipelineBuffer {
	private Map<Integer, PipelineBufferable> buffers;
	
	public PipelineBuffer() {
		this.buffers = new HashMap<Integer, PipelineBufferable>();
	}
	
	public PipelineBufferable get(int i) {
		return this.buffers.get(i);
	}
	
	public void add(int i, PipelineBufferable buf) {
		if(this.buffers.containsKey(i))
			throw new RuntimeException("The index " + i + " is already in use");
		this.buffers.put(i, buf);
	}
	
	public void release(int i) {
		this.buffers.get(i).release();
		this.buffers.remove(i);
	}
	
	public void releaseAll() {
		for(PipelineBufferable i : this.buffers.values())
			i.release();
		this.buffers.clear();
	}
}
