package net.alevel.asteroids.game.physics.pipeline;

import java.util.HashMap;
import java.util.Map;

public class PipelineBuffer {
	private final Map<Integer, Releasable> releasableBuffers;
	private final Map<Integer, Object> nonReleasableBuffers;
	
	public PipelineBuffer() {
		this.releasableBuffers = new HashMap<Integer, Releasable>();
		this.nonReleasableBuffers = new HashMap<Integer, Object>();
	}
	
	public Object get(int i) {
		return this.releasableBuffers.containsKey(i) ? this.releasableBuffers.get(i) : this.nonReleasableBuffers.get(i);
	}
	
	public void add(int i, Releasable buf) {
		if(this.releasableBuffers.containsKey(i) || this.nonReleasableBuffers.containsKey(i))
			throw new RuntimeException("The index " + i + " is already in use");
		this.releasableBuffers.put(i, buf);
	}
	
	public void add(int i, Object buf) {
		if(this.releasableBuffers.containsKey(i) || this.nonReleasableBuffers.containsKey(i))
			throw new RuntimeException("The index " + i + " is already in use");
		this.nonReleasableBuffers.put(i, buf);
	}
	
	public void release(int i) {
		this.releasableBuffers.get(i).release();
		this.releasableBuffers.remove(i);
	}
	
	public void releaseAll() {
		for(Releasable i : this.releasableBuffers.values())
			i.release();
		this.releasableBuffers.clear();
	}
}
