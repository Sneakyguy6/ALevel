package net.alevel.asteroids.game.physics.SAT;

import java.util.List;

import org.jocl.cl_kernel;
import static org.jocl.CL.*;

import net.alevel.asteroids.game.physics.RigidObject;
import net.alevel.asteroids.game.physics.pipeline.PipelineBuffer;
import net.alevel.asteroids.game.physics.pipeline.PipelineableCLFunction;

public class ProjectedBoundaries extends PipelineableCLFunction {
	private final cl_kernel kernel;
	
	public ProjectedBoundaries(SAT sat) {
		super(sat);
		this.kernel = clCreateKernel(super.program, "getProjectedBoundaries", null);
	}
	
	@Override
	public void pipeFunction(PipelineBuffer pipelineBuffer, PipelineBuffer globalPipelineBuffer, List<RigidObject> rigidObjects) {
		
	}
}
