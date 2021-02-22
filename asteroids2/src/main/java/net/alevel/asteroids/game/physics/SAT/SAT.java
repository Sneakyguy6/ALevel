package net.alevel.asteroids.game.physics.SAT;

import static net.alevel.asteroids.game.cl.CLUtil.loadProgram;

import java.io.IOException;
import java.util.List;

import org.jocl.cl_command_queue;
import org.jocl.cl_context;
import org.jocl.cl_program;

import net.alevel.asteroids.game.cl.CLManager;
import net.alevel.asteroids.game.physics.RigidObject;
import net.alevel.asteroids.game.physics.pipeline.FunctionPipeline;
import net.alevel.asteroids.game.physics.pipeline.PipelineBuffer;

public class SAT extends FunctionPipeline {
	private final cl_context context;
	private final cl_command_queue commandQueue;
	private final cl_program program;
	
	public SAT(PipelineBuffer globalBufferPipeline) throws IOException {
		super(globalBufferPipeline,
				new SurfaceNormals()
		);
		
		this.context = CLManager.getContext();
		this.commandQueue = CLManager.getCommandQueue();
		this.program = loadProgram("SAT.cl", SAT.class, this.context);
	}
	
	@Override
	public void runPipeline(List<RigidObject> rigidObjects) {
		if(rigidObjects.size() < 2)
			return;
		super.runPipeline(rigidObjects);
	}
}
