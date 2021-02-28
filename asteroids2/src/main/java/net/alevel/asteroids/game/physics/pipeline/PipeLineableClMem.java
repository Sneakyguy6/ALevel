package net.alevel.asteroids.game.physics.pipeline;

import static org.jocl.CL.clReleaseMemObject;

import org.jocl.cl_mem;

public class PipeLineableClMem implements Releasable {
	private cl_mem buffer;
	
	public PipeLineableClMem(cl_mem mem) {
		this.buffer = mem;
	}
	
	@Override
	public void release() {
		clReleaseMemObject(this.buffer);
	}
	
	public cl_mem getBuffer() {
		return this.buffer;
	}
}
