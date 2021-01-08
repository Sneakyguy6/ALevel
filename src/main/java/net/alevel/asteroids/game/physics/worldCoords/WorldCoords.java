package net.alevel.asteroids.game.physics.worldCoords;

import static org.jocl.CL.CL_BUFFER_CREATE_TYPE_REGION;
import static org.jocl.CL.CL_MEM_COPY_HOST_PTR;
import static org.jocl.CL.CL_MEM_READ_ONLY;
import static org.jocl.CL.CL_MEM_READ_WRITE;
import static org.jocl.CL.clBuildProgram;
import static org.jocl.CL.clCreateBuffer;
import static org.jocl.CL.clCreateKernel;
import static org.jocl.CL.clCreateProgramWithSource;
import static org.jocl.CL.clCreateSubBuffer;
import static org.jocl.CL.clEnqueueNDRangeKernel;
import static org.jocl.CL.clFinish;
import static org.jocl.CL.clReleaseKernel;
import static org.jocl.CL.clReleaseMemObject;
import static org.jocl.CL.clReleaseProgram;
import static org.jocl.CL.clSetKernelArg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.jocl.Pointer;
import org.jocl.Sizeof;
import org.jocl.cl_buffer_region;
import org.jocl.cl_kernel;
import org.jocl.cl_mem;
import org.jocl.cl_program;
import org.joml.Vector3f;

import net.alevel.asteroids.engine.cl.CLManager;
import net.alevel.asteroids.game.physics.RigidObject;

public class WorldCoords {
	private static cl_program program;
	private static cl_kernel transformKernel;
	private static cl_kernel rotMatKernel;
	//private static cl_mem transformedFloatsMem;
	/**Uses OpenCL to calculate as many vertices at the same time (depends on amount of memory and number of processing units
	 * @param objects the list of all game objects in the world
	 * @throws IOException if it fails to load program
	 */
	public static void getAllWorldCoordinates(List<RigidObject> rigidObjects) throws IOException {
		if(transformKernel == null) //there shouldn't be a case where some of the kernels are null. So if the first one is null, then all are null so load kernels
			loadKernels();		 //exceptions are unhandled so if anything fails then the program should crash
		
		int numOfVertices = 0;
		for(RigidObject i : rigidObjects)
			numOfVertices += i.getMesh().getVertices().length;
		float[] floats = new float[numOfVertices];
		int[] objectIndices = new int[numOfVertices / 3]; //an array telling which vertex belongs to which object
		float[] objectPositions = new float[rigidObjects.size() * 3];
		float[] objectRotations = new float[rigidObjects.size() * 3]; //each object has 2 float3 vector properties (2 * 3 = 6)
		/*E.g. floats = {1, 0, 0, 1, 1, 1}
		 * 	   objectIndices = {0, 1}
		 * 	   objectProperties = {0, 1, 0, 1, 1, 1, 0, 0, 0, 1, 1, 0}
		 * It will apply the object properties of object 0 to the first vector. The second vector will have object 1's properties applied to it
		 */
		
		int i = 0, i2 = 0;
		for(int objectIndex = 0; objectIndex < rigidObjects.size(); objectIndex++) {
			RigidObject o = rigidObjects.get(objectIndex);
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
		
		cl_mem floatsMem = clCreateBuffer(CLManager.getContext(), CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, Sizeof.cl_float * floats.length, Pointer.to(floats), null);
		cl_mem objectIndicesMem = clCreateBuffer(CLManager.getContext(), CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, Sizeof.cl_int * objectIndices.length, Pointer.to(objectIndices), null);
		cl_mem objectPositionsMem = clCreateBuffer(CLManager.getContext(), CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, Sizeof.cl_float * objectPositions.length, Pointer.to(objectPositions), null);
		cl_mem objectRotationsMem = clCreateBuffer(CLManager.getContext(), CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, Sizeof.cl_float * objectRotations.length, Pointer.to(objectRotations), null);
		cl_mem objectRotationsMatMem = clCreateBuffer(CLManager.getContext(), CL_MEM_READ_WRITE, Sizeof.cl_float * objectIndices.length * 9, null, null);
		cl_mem transformedFloatsMem = clCreateBuffer(CLManager.getContext(), CL_MEM_READ_WRITE, Sizeof.cl_float * floats.length, null, null);
		
		clSetKernelArg(rotMatKernel, 0, Sizeof.cl_mem, Pointer.to(objectRotationsMem));
		clSetKernelArg(rotMatKernel, 1, Sizeof.cl_mem, Pointer.to(objectRotationsMatMem));
		
		clSetKernelArg(transformKernel, 0, Sizeof.cl_mem, Pointer.to(floatsMem));
		clSetKernelArg(transformKernel, 1, Sizeof.cl_mem, Pointer.to(objectIndicesMem));
		clSetKernelArg(transformKernel, 2, Sizeof.cl_mem, Pointer.to(objectPositionsMem));
		clSetKernelArg(transformKernel, 3, Sizeof.cl_mem, Pointer.to(objectRotationsMatMem));
		clSetKernelArg(transformKernel, 4, Sizeof.cl_mem, Pointer.to(transformedFloatsMem));
		
		long[] global_work_size = {objectRotations.length / 3};
		//long[] local_work_size = {3};
		clEnqueueNDRangeKernel(
				CLManager.getCommandQueue(),
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
				CLManager.getCommandQueue(),
				transformKernel,
				global_work_size.length,
				null,
				global_work_size,
				null,
				0,
				null,
				null);
		clFinish(CLManager.getCommandQueue());
		
		for(int j = 0, c = 0; j < rigidObjects.size(); j++) {
			RigidObject o = rigidObjects.get(j);
			int length = o.getMesh().getVertices().length;
			o.setWorldVerticesMem(clCreateSubBuffer(transformedFloatsMem, CL_MEM_READ_ONLY, CL_BUFFER_CREATE_TYPE_REGION, new cl_buffer_region(Sizeof.cl_float * c, Sizeof.cl_float * length), null));
			//System.out.print(c);
			c += length;
			//System.out.println(" " + c);
		}
		//System.out.println(Arrays.toString(objectIndices) + " | ");
		//clFinish(CLManager.getCommandQueue());
		clReleaseMemObject(transformedFloatsMem);
	}
	
	private static void loadKernels() throws IOException {
		String programString = "";
		//String path = WorldCoords.class.getName().replace('.', '/') + ".cl";
		//System.out.println(path);
		try(BufferedReader br = new BufferedReader(new InputStreamReader(WorldCoords.class.getResourceAsStream("WorldCoords.cl")))) {
			String line;
			while((line = br.readLine()) != null)
				programString += line + "\n";
		}
		System.out.println(programString);
		program = clCreateProgramWithSource(CLManager.getContext(),
				1,
				new String[] {programString},
				null,
				null);
		clBuildProgram(program, 0, null, null, null, null);
		transformKernel = clCreateKernel(program, "tranformVectors", null);
		rotMatKernel = clCreateKernel(program, "getRotationMatrix", null);
	}
	
	public static void cleanUp() {
		clReleaseKernel(transformKernel);
		clReleaseKernel(rotMatKernel);
		clReleaseProgram(program);
	}
}
