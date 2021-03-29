package net.alevel.asteroids.game.physics.SAT;

import static net.alevel.asteroids.game.cl.CLUtil.loadProgram;

import java.io.IOException;

import org.jocl.cl_command_queue;
import org.jocl.cl_context;
import org.jocl.cl_program;

import net.alevel.asteroids.game.cl.CLManager;
import net.alevel.asteroids.game.physics.pipeline.FunctionPipeline;
import net.alevel.asteroids.game.physics.pipeline.PipelineBuffer;

/**Note on local pipeline buffer<br>
 * 0 -> surface normals<br>
 * 1 -> projectedBoundaries<br>
 * <br>
 * World coordinates are in global pipeline (0) because they may be used in other pipelines
 */
public class SAT extends FunctionPipeline {
	private final cl_context context;
	private final cl_command_queue commandQueue;
	private final cl_program program;
	
	public SAT(PipelineBuffer globalBufferPipeline) throws IOException {
		super(globalBufferPipeline);
		
		this.context = CLManager.getContext();
		this.commandQueue = CLManager.getCommandQueue();
		this.program = loadProgram("SAT.cl", SAT.class, this.context);
		
		super.setFunctions(
				new SurfaceNormals(this),
				new ProjectedBoundaries(this)
		);
	}
	
	/*@Override
	public void runPipeline(List<RigidObject> rigidObjects) {
		//if(rigidObjects.size() < 2)
		//	return;
		super.runPipeline(rigidObjects);
	}*/

	public cl_context getContext() {
		return this.context;
	}

	public cl_command_queue getCommandQueue() {
		return this.commandQueue;
	}

	public cl_program getProgram() {
		return this.program;
	}
}
