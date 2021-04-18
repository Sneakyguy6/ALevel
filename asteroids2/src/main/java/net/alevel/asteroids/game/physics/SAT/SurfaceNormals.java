package net.alevel.asteroids.game.physics.SAT;

import static org.jocl.CL.CL_MEM_COPY_HOST_PTR;
import static org.jocl.CL.CL_MEM_READ_ONLY;
import static org.jocl.CL.CL_MEM_READ_WRITE;
import static org.jocl.CL.CL_TRUE;
import static org.jocl.CL.clCreateBuffer;
import static org.jocl.CL.clCreateKernel;
import static org.jocl.CL.clEnqueueNDRangeKernel;
import static org.jocl.CL.clEnqueueReadBuffer;
import static org.jocl.CL.clSetKernelArg;
import static org.jocl.CL.setExceptionsEnabled;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.jocl.Pointer;
import org.jocl.Sizeof;
import org.jocl.cl_kernel;
import org.jocl.cl_mem;

import net.alevel.asteroids.game.physics.RigidObject;
import net.alevel.asteroids.game.physics.pipeline.PipeLineableClMem;
import net.alevel.asteroids.game.physics.pipeline.PipelineBuffer;
import net.alevel.asteroids.game.physics.pipeline.PipelineableCLFunction;
import net.alevel.asteroids.game.physics.worldCoords.WorldCoordinates;

/**Calculates all the surface normals in the world. These will be the axis.
 * It takes the world coordinates from the pipeline buffer (which is 1 float buffer containing all the world coordinates for all objects)
 * and calculates the surface normals using sub buffer pointers which point to the first index of each object's set of world coordinates in the buffer
 */
public class SurfaceNormals extends PipelineableCLFunction {
	private final cl_kernel surfaceNormalKernel;
	
	public SurfaceNormals(SAT sat) throws IOException {
		super(sat);
		this.surfaceNormalKernel = clCreateKernel(super.program, "getSurfaceNormals", null);
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
		
		cl_mem indicesBuffer = clCreateBuffer(super.context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, Sizeof.cl_int * noOfIndices, Pointer.to(indicesArray), null);
		cl_mem surfaceNormalsBuffer = clCreateBuffer(super.context, CL_MEM_READ_WRITE, Sizeof.cl_float * noOfIndices, null, null);
		
		clSetKernelArg(this.surfaceNormalKernel, 0, Sizeof.cl_mem, Pointer.to(worldCoordinates.getWorldCoords()));
		clSetKernelArg(this.surfaceNormalKernel, 1, Sizeof.cl_mem, Pointer.to(indicesBuffer));
		clSetKernelArg(this.surfaceNormalKernel, 2, Sizeof.cl_mem, Pointer.to(surfaceNormalsBuffer));
		long[] global_work_size = {noOfIndices / 3};
		clEnqueueNDRangeKernel(
				super.commandQueue,
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
				super.commandQueue,
				surfaceNormalsBuffer,
				CL_TRUE,
				0,
				Sizeof.cl_float * surfaceNormalsArray.length,
				Pointer.to(surfaceNormalsArray),
				0,
				null,
				null);
		System.out.println(Arrays.toString(surfaceNormalsArray));
		pipelineBuffer.add(0, new PipeLineableClMem(surfaceNormalsBuffer));
		pipelineBuffer.add(1, noOfIndices / 3); //number of surface normals needed for other pipeline functions
	}
}
