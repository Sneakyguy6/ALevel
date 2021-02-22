package net.alevel.asteroids.game.physics.SAT;

import static net.alevel.asteroids.game.cl.CLUtil.loadProgram;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.jocl.Pointer;
import org.jocl.Sizeof;
import org.jocl.cl_command_queue;
import org.jocl.cl_context;
import org.jocl.cl_kernel;
import org.jocl.cl_mem;
import org.jocl.cl_program;
import static org.jocl.CL.*;

import net.alevel.asteroids.game.cl.CLManager;
import net.alevel.asteroids.game.physics.RigidObject;
import net.alevel.asteroids.game.physics.pipeline.PipelineBuffer;
import net.alevel.asteroids.game.physics.pipeline.PipelineableFunction;
import net.alevel.asteroids.game.physics.worldCoords.WorldCoordinates;

public class SurfaceNormals implements PipelineableFunction {
	private final cl_program program;
	private final cl_kernel surfaceNormalKernel;
	
	private final cl_context context;
	private final cl_command_queue commandQueue;
	
	public SurfaceNormals() throws IOException {
		this.context = CLManager.getContext();
		this.commandQueue = CLManager.getCommandQueue();
		
		this.program = loadProgram("SurfaceNormals.cl", SurfaceNormals.class, this.context);
		this.surfaceNormalKernel = clCreateKernel(this.program, "getSurfaceNormals", null);
	}

	@Override
	public void pipeFunction(PipelineBuffer pipelineBuffer, PipelineBuffer globalPipelineBuffer, List<RigidObject> rigidObjects) {
		setExceptionsEnabled(true);
		WorldCoordinates worldCoordinates = (WorldCoordinates) globalPipelineBuffer.get(0);
		int noOfIndices = 0;
		for(RigidObject i : rigidObjects)
			noOfIndices += i.getMesh().getIndices().length;
		int[] indicesArray = new int[noOfIndices];
		
		int[] worldCoordSubBufferPointers = worldCoordinates.getSubBufferPointers();
		for(int i = 0; i < rigidObjects.size(); i++) {
			int[] rigidObjectIndicesTemp = rigidObjects.get(i).getMesh().getIndices();
			for(int j = 0; j < rigidObjectIndicesTemp.length; j++)
				indicesArray[worldCoordSubBufferPointers[i] + j] = rigidObjectIndicesTemp[j] + worldCoordSubBufferPointers[i];
		}
		
		cl_mem indicesBuffer = clCreateBuffer(this.context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, Sizeof.cl_int * noOfIndices, Pointer.to(indicesArray), null);
		cl_mem surfaceNormalsBuffer = clCreateBuffer(this.context, CL_MEM_READ_WRITE, Sizeof.cl_float * noOfIndices, null, null);
		
		clSetKernelArg(this.surfaceNormalKernel, 0, Sizeof.cl_mem, Pointer.to(worldCoordinates.getWorldCoords()));
		clSetKernelArg(this.surfaceNormalKernel, 1, Sizeof.cl_mem, Pointer.to(indicesBuffer));
		clSetKernelArg(this.surfaceNormalKernel, 2, Sizeof.cl_mem, Pointer.to(surfaceNormalsBuffer));
		long[] global_work_size = {noOfIndices / 3};
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
		float[] surfaceNormalsArray = new float[noOfIndices];
		clEnqueueReadBuffer(
				CLManager.getCommandQueue(),
				surfaceNormalsBuffer,
				CL_TRUE,
				0,
				Sizeof.cl_float * surfaceNormalsArray.length,
				Pointer.to(surfaceNormalsArray),
				0,
				null,
				null);
		System.out.println(Arrays.toString(surfaceNormalsArray));
	}
}
