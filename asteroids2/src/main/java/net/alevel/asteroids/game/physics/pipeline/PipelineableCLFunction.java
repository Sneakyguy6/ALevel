package net.alevel.asteroids.game.physics.pipeline;

import org.jocl.cl_command_queue;
import org.jocl.cl_context;
import org.jocl.cl_program;

import net.alevel.asteroids.game.physics.SAT.SAT;

public abstract class PipelineableCLFunction implements PipelineableFunction {
	protected final cl_program program;
	protected final cl_context context;
	protected final cl_command_queue commandQueue;
	
	public PipelineableCLFunction(SAT pipelineController) {
		this.program = pipelineController.getProgram();
		this.context = pipelineController.getContext();
		this.commandQueue = pipelineController.getCommandQueue();
	}
}
