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
	private final cl_kernel kernel;
	
	public ProjectedBoundaries(SAT sat) {
		super(sat);
		this.kernel = clCreateKernel(super.program, "getProjectedBoundaries", null);
	}
	
	@Override
	public void pipeFunction(PipelineBuffer pipelineBuffer, PipelineBuffer globalPipelineBuffer, List<RigidObject> rigidObjects) {
		setExceptionsEnabled(true);
		
		cl_mem surfaceNormalsBuffer = ((PipeLineableClMem) pipelineBuffer.get(0)).getBuffer();
		int noOfSurfaceNormals = (int) pipelineBuffer.get(1);
		WorldCoordinates worldCoords = (WorldCoordinates) globalPipelineBuffer.get(0);
		int[] subBufferPointers = worldCoords.getSubBufferPointers();
		
		cl_mem boundaries = clCreateBuffer(super.context, CL_MEM_READ_WRITE, rigidObjects.size() * noOfSurfaceNormals * 2, null, null);
		
		clSetKernelArg(this.kernel, 0, Sizeof.cl_mem, Pointer.to(surfaceNormalsBuffer));
		clSetKernelArg(this.kernel, 1, Sizeof.cl_mem, Pointer.to(worldCoords.getWorldCoords()));
		clSetKernelArg(this.kernel, 2, Sizeof.cl_mem, Pointer.to(boundaries));
		clSetKernelArg(this.kernel, 3, Sizeof.cl_mem, null);
		clSetKernelArg(this.kernel, 4, Sizeof.cl_mem, null);
		
		for(int i = 0; i < subBufferPointers.length; i++) {
			clEnqueueNDRangeKernel(
					super.commandQueue,
					this.kernel,
					1,
					new long[] {subBufferPointers[i]},
					new long[] {(i + 1) == subBufferPointers.length ? worldCoords.getNoOfVertices() - subBufferPointers[i] : subBufferPointers[i + 1] - subBufferPointers[i]},
					new long[] {noOfSurfaceNormals},
					0,
					null,
					null);
		}
	}
}
