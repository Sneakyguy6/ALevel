package net.alevel.asteroids.game.physics.collisions;

import static org.jocl.CL.CL_MEM_COPY_HOST_PTR;
import static org.jocl.CL.CL_MEM_READ_ONLY;
import static org.jocl.CL.CL_MEM_READ_WRITE;
import static org.jocl.CL.CL_TRUE;
import static org.jocl.CL.clBuildProgram;
import static org.jocl.CL.clCreateBuffer;
import static org.jocl.CL.clCreateKernel;
import static org.jocl.CL.clCreateProgramWithSource;
import static org.jocl.CL.clEnqueueNDRangeKernel;
import static org.jocl.CL.clEnqueueReadBuffer;
import static org.jocl.CL.clFinish;
import static org.jocl.CL.clSetKernelArg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

import org.jocl.Pointer;
import org.jocl.Sizeof;
import org.jocl.cl_command_queue;
import org.jocl.cl_context;
import org.jocl.cl_kernel;
import org.jocl.cl_mem;
import org.jocl.cl_program;

import net.alevel.asteroids.game.cl.CLManager;
import net.alevel.asteroids.game.physics.RigidObject;
import net.alevel.asteroids.game.physics.SAT.SAT;
import net.alevel.asteroids.game.physics.pipeline.PipelineBuffer;
import net.alevel.asteroids.game.physics.worldCoords.WorldCoordinates;

/**
 * @deprecated use {@link SAT} which uses the physics pipeline
 */
@Deprecated
public class SATCL {
	private final cl_program program;
	private final cl_kernel surfaceNormalKernel;
	
	private final cl_context context;
	private final cl_command_queue commandQueue;
	
	public SATCL() throws IOException {
		this.context = CLManager.getContext();
		this.commandQueue = CLManager.getCommandQueue();
		
		String programString = "";
		try(BufferedReader br = new BufferedReader(new InputStreamReader(SATCL.class.getResourceAsStream("SATCL.cl")))) {
			String line;
			while((line = br.readLine()) != null)
				programString += line + "\n";
		}
		
		this.program = clCreateProgramWithSource(this.context,
				1,
				new String[] {programString},
				null,
				null);
		clBuildProgram(program, 0, null, null, null, null);
		
		this.surfaceNormalKernel = clCreateKernel(this.program, "getSurfaceNormals", null);
	}
	
	public void testCollisions(PipelineBuffer pipeline, List<RigidObject> rigidObjects) {
		if(rigidObjects.size() < 2)
			return;
		this.getSurfaceNormals((WorldCoordinates) pipeline.get(0), rigidObjects);
		
	}
	
	private cl_mem getSurfaceNormals(WorldCoordinates worldCoordsObject, List<RigidObject> rigidObjects) {
		int indicesSize = 0;
		for(RigidObject i : rigidObjects)
			indicesSize += i.getMesh().getIndices().length;
		int[] indicesArray = new int[indicesSize];
		
		for(int objectIndex = 0, i = 0; objectIndex < rigidObjects.size(); objectIndex++) {
			int[] oIndices = rigidObjects.get(objectIndex).getMesh().getIndices();
			for(int j = 0; j < oIndices.length; j++, i++)
				indicesArray[i] = oIndices[j];
		}
		
		cl_mem indicesMem = clCreateBuffer(this.context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, Sizeof.cl_float * indicesSize, Pointer.to(indicesArray), null);
		cl_mem surfaceNormals = clCreateBuffer(this.context, CL_MEM_READ_WRITE, Sizeof.cl_float * indicesSize, null, null);
		
		clSetKernelArg(this.surfaceNormalKernel, 0, Sizeof.cl_mem, Pointer.to(worldCoordsObject.getWorldCoords()));
		clSetKernelArg(this.surfaceNormalKernel, 1, Sizeof.cl_mem, Pointer.to(indicesMem));
		clSetKernelArg(this.surfaceNormalKernel, 2, Sizeof.cl_mem, Pointer.to(surfaceNormals));
		
		long[] global_work_size = {indicesSize / 3};
		clEnqueueNDRangeKernel(
				this.commandQueue,
				this.surfaceNormalKernel,
				global_work_size.length,
				null,
				global_work_size,
				null,
				0,
				null,
				null);
		clFinish(this.commandQueue);
		float[] surfaceNormalsArray = new float[indicesSize];
		clEnqueueReadBuffer(
				CLManager.getCommandQueue(),
				surfaceNormals,
				CL_TRUE,
				0,
				Sizeof.cl_float * surfaceNormalsArray.length,
				Pointer.to(surfaceNormalsArray),
				0,
				null,
				null);
		System.out.println(Arrays.toString(surfaceNormalsArray));
		return surfaceNormals;
	}
}
