package net.alevel.asteroids.game.physics.worldCoords;

import static net.alevel.asteroids.game.cl.CLUtil.loadProgram;
import static org.jocl.CL.CL_BUFFER_CREATE_TYPE_REGION;
import static org.jocl.CL.CL_MEM_COPY_HOST_PTR;
import static org.jocl.CL.CL_MEM_READ_ONLY;
import static org.jocl.CL.CL_MEM_READ_WRITE;
import static org.jocl.CL.clCreateBuffer;
import static org.jocl.CL.clCreateKernel;
import static org.jocl.CL.clCreateSubBuffer;
import static org.jocl.CL.clEnqueueNDRangeKernel;
import static org.jocl.CL.clFinish;
import static org.jocl.CL.clReleaseMemObject;
import static org.jocl.CL.clSetKernelArg;
import static org.jocl.CL.setExceptionsEnabled;

import java.io.IOException;
import java.util.List;

import org.jocl.Pointer;
import org.jocl.Sizeof;
import org.jocl.cl_buffer_region;
import org.jocl.cl_command_queue;
import org.jocl.cl_context;
import org.jocl.cl_kernel;
import org.jocl.cl_mem;
import org.jocl.cl_program;
import org.joml.Vector3f;

import net.alevel.asteroids.game.cl.CLManager;
import net.alevel.asteroids.game.physics.RigidObject;
import net.alevel.asteroids.game.physics.pipeline.PipelineBuffer;
import net.alevel.asteroids.game.physics.pipeline.Releasable;

/**Calculates the world coordinates of each model vertex.
 * In other words, it applies an object's position, scale, and rotation to work out where the positions are of each vertex in its mesh.<br>
 * This uses OpenCL. It calculates the world coordinates on a GPU (or whatever device is specified by {@link CLManager}) as each vertex position can be calculated in parallel.
 */
public class WorldCoordinates implements Releasable {
	private final cl_program program;
	private final cl_kernel rotMatKernel;
	private final cl_kernel transformKernel;
	
	private final cl_context context;
	private final cl_command_queue commandQueue;
	
	private cl_mem worldCoords;
	private cl_mem worldCoordsIndices;
	private int[] objectSubBufferPointers;
	private int numberOfVertices;
	
	public WorldCoordinates() throws IOException {
		this.context = CLManager.getContext();
		this.commandQueue = CLManager.getCommandQueue();
		
		this.program = loadProgram("WorldCoordinates.cl", WorldCoordinates.class, this.context);
		
		this.transformKernel = clCreateKernel(this.program, "tranformVectors", null);
		this.rotMatKernel = clCreateKernel(this.program, "getRotationMatrix", null);
	}
	
	public void calc(List<RigidObject> objects, PipelineBuffer pipeline) {
		setExceptionsEnabled(true);
		
		this.numberOfVertices = 0;
		for(RigidObject i : objects)
			this.numberOfVertices += i.getMesh().getVertices().length;
		float[] floats = new float[this.numberOfVertices];
		int[] objectIndices = new int[this.numberOfVertices / 3]; //an array telling which vertex belongs to which object
		float[] objectPositions = new float[objects.size() * 3];
		float[] objectRotations = new float[objects.size() * 3]; //each object has 2 float3 vector properties (2 * 3 = 6)
		/*E.g. floats = {1, 0, 0, 1, 1, 1}
		 * 	   objectIndices = {0, 1}
		 * 	   objectProperties = {0, 1, 0, 1, 1, 1, 0, 0, 0, 1, 1, 0}
		 * It will apply the object properties of object 0 to the first vector. The second vector will have object 1's properties applied to it
		 */
		this.numberOfVertices /= 3; //because before this number represented the size of the float array which is 3x the number of vertices
		
		int i = 0, i2 = 0;
		for(int objectIndex = 0; objectIndex < objects.size(); objectIndex++) {
			RigidObject o = objects.get(objectIndex);
			float[] vertices = o.getMesh().getVertices();
			for(int j = 0; j < vertices.length; j += 3, i += 3, i2++) {
				floats[i] = vertices[j];
				floats[i + 1] = vertices[j + 1];
				floats[i + 2] = vertices[j + 2];
				objectIndices[i2] = objectIndex;
			}
			Vector3f position = o.getPosition();
			Vector3f rotation = o.getRotation();
			objectPositions[objectIndex * 3] = position.x;
			objectPositions[objectIndex * 3 + 1] = position.y;
			objectPositions[objectIndex * 3 + 2] = position.z;
			objectRotations[objectIndex * 3] = rotation.x;
			objectRotations[objectIndex * 3 + 1] = rotation.y;
			objectRotations[objectIndex * 3 + 2] = rotation.z;
		}
		
		//System.out.println(floats.length);
		cl_mem floatsMem = clCreateBuffer(this.context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, Sizeof.cl_float * floats.length, Pointer.to(floats), null);
		cl_mem objectIndicesMem = clCreateBuffer(this.context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, Sizeof.cl_int * objectIndices.length, Pointer.to(objectIndices), null);
		cl_mem objectPositionsMem = clCreateBuffer(this.context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, Sizeof.cl_float * objectPositions.length, Pointer.to(objectPositions), null);
		cl_mem objectRotationsMem = clCreateBuffer(this.context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, Sizeof.cl_float * objectRotations.length, Pointer.to(objectRotations), null);
		
		cl_mem objectRotationsMatMem = clCreateBuffer(this.context, CL_MEM_READ_WRITE, Sizeof.cl_float * objectRotations.length * 3, null, null);
		cl_mem transformedVerticesMem = clCreateBuffer(this.context, CL_MEM_READ_WRITE, Sizeof.cl_float * floats.length, null, null);
		
		clSetKernelArg(this.rotMatKernel, 0, Sizeof.cl_mem, Pointer.to(objectRotationsMem));
		clSetKernelArg(this.rotMatKernel, 1, Sizeof.cl_mem, Pointer.to(objectRotationsMatMem));
		
		clSetKernelArg(this.transformKernel, 0, Sizeof.cl_mem, Pointer.to(floatsMem));
		clSetKernelArg(this.transformKernel, 1, Sizeof.cl_mem, Pointer.to(objectIndicesMem));
		clSetKernelArg(this.transformKernel, 2, Sizeof.cl_mem, Pointer.to(objectPositionsMem));
		clSetKernelArg(this.transformKernel, 3, Sizeof.cl_mem, Pointer.to(objectRotationsMatMem));
		clSetKernelArg(this.transformKernel, 4, Sizeof.cl_mem, Pointer.to(transformedVerticesMem));
		
		long[] global_work_size = {objectRotations.length / 3};
		//long[] local_work_size = {3};
		clEnqueueNDRangeKernel(
				this.commandQueue,
				rotMatKernel,
				global_work_size.length,
				null,
				global_work_size,
				null,
				0,
				null,
				null);
		//clFinish(CLManager.getCommandQueue());
		global_work_size[0] = objectIndices.length;
		clEnqueueNDRangeKernel(
				this.commandQueue,
				transformKernel,
				global_work_size.length,
				null,
				global_work_size,
				null,
				0,
				null,
				null);
		clFinish(this.commandQueue);
		
		this.objectSubBufferPointers = new int[objects.size()];
		for(int j = 0, c = 0; j < objects.size(); j++) {
			RigidObject o = objects.get(j);
			int length = o.getMesh().getVertices().length;
			o.setWorldVerticesMem(clCreateSubBuffer(transformedVerticesMem, CL_MEM_READ_ONLY, CL_BUFFER_CREATE_TYPE_REGION, new cl_buffer_region(Sizeof.cl_float * c, Sizeof.cl_float * length), null));
			//System.out.print(c);
			this.objectSubBufferPointers[j] = c;
			c += length;
			//System.out.println(" " + c);
		}
		clFinish(this.commandQueue); //makes sure that all vertices for all objects have been processed
		//The CPU will continue the pipeline and queue more instructions regardless of whether the GPU is finished or not
		//clReleaseMemObject(transformedVerticesMem);
		this.worldCoords = transformedVerticesMem;
		this.worldCoordsIndices = objectIndicesMem;
		clReleaseMemObject(floatsMem);
		//clReleaseMemObject(objectIndicesMem);
		clReleaseMemObject(objectPositionsMem);
		clReleaseMemObject(objectRotationsMem);
		clReleaseMemObject(objectRotationsMatMem);
	}
	
	public void release() {
		clReleaseMemObject(this.worldCoords);
		clReleaseMemObject(this.worldCoordsIndices);
	}
	
	public int[] getSubBufferPointers() {
		return this.objectSubBufferPointers;
	}

	public cl_mem getWorldCoords() {
		return worldCoords;
	}

	public cl_mem getWorldCoordsIndices() {
		return worldCoordsIndices;
	}
	
	public int getNoOfVertices() {
		return this.numberOfVertices;
	}
}
