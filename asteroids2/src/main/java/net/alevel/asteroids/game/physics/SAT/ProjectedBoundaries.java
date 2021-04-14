package net.alevel.asteroids.game.physics.SAT;

import static org.jocl.CL.*;

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

public class ProjectedBoundaries extends PipelineableCLFunction {
	private final cl_kernel projectPointsKernel;
	private final cl_kernel boundariesKernel;
	
	public ProjectedBoundaries(SAT sat) {
		super(sat);
		this.projectPointsKernel = clCreateKernel(super.program, "getProjectedVertices", null);
		this.boundariesKernel = clCreateKernel(super.program, "getBoundaries", null);
	}
	
	@Override
	public void pipeFunction(PipelineBuffer pipelineBuffer, PipelineBuffer globalPipelineBuffer, List<RigidObject> rigidObjects) {
		setExceptionsEnabled(true);
		
		cl_mem surfaceNormalsBuffer = ((PipeLineableClMem) pipelineBuffer.get(0)).getBuffer();
		int noOfSurfaceNormals = (int) pipelineBuffer.get(1);
		WorldCoordinates worldCoords = (WorldCoordinates) globalPipelineBuffer.get(0);
		int[] subBufferPointers = worldCoords.getSubBufferPointers();
		
		//cl_mem boundaries = clCreateBuffer(super.context, CL_MEM_READ_WRITE, rigidObjects.size() * noOfSurfaceNormals * 2 * Sizeof.cl_float, null, null);
		cl_mem maxBoundariesTemp = clCreateBuffer(super.context, CL_MEM_READ_WRITE, worldCoords.getNoOfVertices() * noOfSurfaceNormals * Sizeof.cl_float, null, null);
		cl_mem minBoundariesTemp = clCreateBuffer(super.context, CL_MEM_READ_WRITE, worldCoords.getNoOfVertices() * noOfSurfaceNormals * Sizeof.cl_float, null, null);
		//cl_mem subBufPointBuffer = clCreateBuffer(super.context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, subBufferPointers.length * Sizeof.cl_int, Pointer.to(subBufferPointers), null);
		
		clSetKernelArg(this.projectPointsKernel, 0, Sizeof.cl_mem, Pointer.to(surfaceNormalsBuffer));
		clSetKernelArg(this.projectPointsKernel, 1, Sizeof.cl_mem, Pointer.to(worldCoords.getWorldCoords()));
		clSetKernelArg(this.projectPointsKernel, 2, Sizeof.cl_mem, Pointer.to(minBoundariesTemp));
		clSetKernelArg(this.projectPointsKernel, 3, Sizeof.cl_mem, Pointer.to(maxBoundariesTemp));
		
		clSetKernelArg(this.boundariesKernel, 0, Sizeof.cl_mem, Pointer.to(maxBoundariesTemp));
		clSetKernelArg(this.boundariesKernel, 1, Sizeof.cl_mem, Pointer.to(minBoundariesTemp));
		//clSetKernelArg(this.boundariesKernel, 2, Sizeof.cl_mem, Pointer.to(subBufPointBuffer));
		//clSetKernelArg(this.boundariesKernel, 3, Sizeof.cl_mem, Pointer.to(boundaries));
		
		clEnqueueNDRangeKernel(
				super.commandQueue,
				this.projectPointsKernel,
				1,
				null,
				new long[] {noOfSurfaceNormals},
				new long[] {worldCoords.getNoOfVertices() * noOfSurfaceNormals},
				0,
				null,
				null
		);
		clFinish(super.commandQueue);
		
		for(int i = 0; i < subBufferPointers.length; i++) {
			clEnqueueNDRangeKernel(
					super.commandQueue,
					this.boundariesKernel,
					1,
					new long[] {subBufferPointers[i]},
					new long[] {(i + 1) == subBufferPointers.length ? worldCoords.getNoOfVertices() - subBufferPointers[i] : subBufferPointers[i + 1] - subBufferPointers[i]},
					new long[] {worldCoords.getNoOfVertices()},
					0,
					null,
					null);
		}
		//clFinish(super.commandQueue);
		
		float[] maxBoundariesArray = new float[worldCoords.getNoOfVertices() * noOfSurfaceNormals];
		float[] minBoundariesArray = new float[worldCoords.getNoOfVertices() * noOfSurfaceNormals];
		clEnqueueReadBuffer(
				super.commandQueue,
				maxBoundariesTemp,
				CL_TRUE,
				0,
				Sizeof.cl_float * maxBoundariesArray.length,
				Pointer.to(maxBoundariesArray),
				0,
				null,
				null);
		clEnqueueReadBuffer(
				super.commandQueue,
				minBoundariesTemp,
				CL_TRUE,
				0,
				Sizeof.cl_float * minBoundariesArray.length,
				Pointer.to(minBoundariesArray),
				0,
				null,
				null);
		
		
		clReleaseMemObject(minBoundariesTemp);
		clReleaseMemObject(maxBoundariesTemp);
	}
}
