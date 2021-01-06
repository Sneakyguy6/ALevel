package net.alevel.asteroids.game.physics;

import static org.jocl.CL.CL_MEM_COPY_HOST_PTR;
import static org.jocl.CL.CL_MEM_READ_ONLY;
import static org.jocl.CL.CL_MEM_READ_WRITE;
import static org.jocl.CL.CL_TRUE;
import static org.jocl.CL.clCreateBuffer;
import static org.jocl.CL.clCreateKernel;
import static org.jocl.CL.clCreateProgramWithSource;
import static org.jocl.CL.clEnqueueNDRangeKernel;
import static org.jocl.CL.clEnqueueReadBuffer;
import static org.jocl.CL.clSetKernelArg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jocl.CL;
import org.jocl.Pointer;
import org.jocl.Sizeof;
import org.jocl.cl_command_queue;
import org.jocl.cl_context;
import org.jocl.cl_kernel;
import org.jocl.cl_mem;
import org.jocl.cl_program;
import org.joml.Matrix3f;
import org.joml.Vector3f;

import net.alevel.asteroids.engine.GameObject;
import net.alevel.asteroids.engine.cl.CLManager;

public class SATCL {
	private static cl_context context;
	//private static cl_queue_properties queueProperties;
	private static cl_command_queue commandQueue;
	
	private static cl_program wholeProgram;
	private static cl_kernel matVecMulKernel;
	//private static cl_kernel matMatMulKernel;
	private static cl_kernel vectorAddKernel;
	private static cl_kernel getSurfaceNormalKernel;
	private static cl_kernel getMinMaxKernel;
	private static cl_kernel testCollisionKernel;
	
	public static void init() throws IOException {
		CL.setExceptionsEnabled(true);
		
		context = CLManager.getContext();
		//queueProperties = CLManager.getQueueProperties();
		commandQueue = CLManager.getCommandQueue();
		
		wholeProgram = clCreateProgramWithSource(context,
				4,
				new String[] {
						readClProgram("MatVecMul.cl"),
						readClProgram("GetSurfaceNormals.cl"),
						readClProgram("GetMinMaxPoints.cl"),
						readClProgram("TestCollisions.cl")},
				null,
				null);
		matVecMulKernel = clCreateKernel(wholeProgram, "matrixVectorMultiply", null);
		//matMatMulKernel = clCreateKernel(wholeProgram, "matrixMultiply", null);
		vectorAddKernel = clCreateKernel(wholeProgram, "vectorAdd", null);
		getSurfaceNormalKernel = clCreateKernel(wholeProgram, "getSurfaceNormals", null);
		getMinMaxKernel = clCreateKernel(wholeProgram, "getMinMaxPoints", null);
		testCollisionKernel = clCreateKernel(wholeProgram, "SAT", null);
	}
	
	public static void testCollisions(List<GameObject> objects) {
		for(int i = 0; i < objects.size(); i++) {
			for(int j = i + 1; j < objects.size(); j++) {
				GameObject o1 = objects.get(i),
						   o2 = objects.get(j);
				if(!(o1 instanceof RigidObject) || !(o2 instanceof RigidObject)) //check that they have the ability to collide
					continue;
				RigidObject rigid1 = (RigidObject) o1,
							rigid2 = (RigidObject) o2;
				if(checkIfCollide(rigid1, rigid2)) {
					rigid1.onCollision(rigid2);
					rigid2.onCollision(rigid1);
				}
			}
		}
	}
	
	public static boolean checkIfCollide(RigidObject o1, RigidObject o2) {
		//get world vertices
		float[] o1ModelVertices = o1.getMesh().getVertices();
		cl_mem o1ModelVerticesMem = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, Sizeof.cl_float * o1ModelVertices.length, Pointer.to(o1ModelVertices), null);
		float[] o2ModelVertices = o2.getMesh().getVertices();
		cl_mem o2ModelVerticesMem = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, Sizeof.cl_float * o2ModelVertices.length, Pointer.to(o2ModelVertices), null);
		
		Matrix3f o1Transform = new Matrix3f()
				.rotateX(o1.getRotation().x)
				.rotateY(o1.getRotation().y)
				.rotateZ(o1.getRotation().z);
		float[] o1TransformArr = new float[9];
		o1Transform.get(o1TransformArr);
		Matrix3f o2Transform = new Matrix3f()
				.rotateX(o2.getRotation().x)
				.rotateY(o2.getRotation().y)
				.rotateZ(o2.getRotation().z);
		float[] o2TransformArr = new float[9];
		o2Transform.get(o2TransformArr);
		float[] o1PosVec = {o1.getPosition().x, o1.getPosition().y, o1.getPosition().z};
		float[] o2PosVec = {o2.getPosition().x, o2.getPosition().y, o2.getPosition().z};
		cl_mem transformMatDimensionsMem = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, Sizeof.cl_int * 2, Pointer.to(new int[] {3, 3}), null);
		cl_mem o1TransformMem = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, Sizeof.cl_float * 9, Pointer.to(o1TransformArr), null);
		cl_mem o2TransformMem = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, Sizeof.cl_float * 9, Pointer.to(o2TransformArr), null);
		cl_mem o1PosVecMem = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, Sizeof.cl_float * o1PosVec.length, Pointer.to(o1PosVec), null);
		cl_mem o2PosVecMem = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, Sizeof.cl_float * o2PosVec.length, Pointer.to(o2PosVec), null);
		cl_mem o1WorldVerticesMem = clCreateBuffer(context, CL_MEM_READ_WRITE, Sizeof.cl_float * o1ModelVertices.length, null, null);
		cl_mem o2WorldVerticesMem = clCreateBuffer(context, CL_MEM_READ_WRITE, Sizeof.cl_float * o2ModelVertices.length, null, null);
		
		clSetKernelArg(matVecMulKernel, 0, Sizeof.cl_mem, Pointer.to(o1TransformMem));
		clSetKernelArg(matVecMulKernel, 1, Sizeof.cl_mem, Pointer.to(transformMatDimensionsMem));
		clSetKernelArg(matVecMulKernel, 2, Sizeof.cl_mem, Pointer.to(o1ModelVerticesMem));
		clSetKernelArg(matVecMulKernel, 3, Sizeof.cl_mem, Pointer.to(o1WorldVerticesMem));
		
		clSetKernelArg(vectorAddKernel, 0, Sizeof.cl_mem, Pointer.to(o1WorldVerticesMem));
		clSetKernelArg(vectorAddKernel, 1, Sizeof.cl_mem, Pointer.to(o1PosVecMem));
		clSetKernelArg(vectorAddKernel, 2, Sizeof.cl_mem, Pointer.to(o1WorldVerticesMem));
		
		long[] global_work_size = {o1ModelVertices.length};
		long[] local_work_size = {3};
		clEnqueueNDRangeKernel(
				commandQueue,
				matVecMulKernel,
				global_work_size.length,
				null,
				global_work_size,
				local_work_size,
				0,
				null,
				null);
		//global_work_size = new long[] {o1ModelVertices.length};
		clEnqueueNDRangeKernel(
				commandQueue,
				vectorAddKernel,
				global_work_size.length,
				null,
				global_work_size,
				null,
				0,
				null,
				null);
		CL.clFinish(commandQueue);
		
		clSetKernelArg(matVecMulKernel, 0, Sizeof.cl_mem, Pointer.to(o2TransformMem));
		clSetKernelArg(matVecMulKernel, 1, Sizeof.cl_mem, Pointer.to(transformMatDimensionsMem));
		clSetKernelArg(matVecMulKernel, 2, Sizeof.cl_mem, Pointer.to(o2ModelVerticesMem));
		clSetKernelArg(matVecMulKernel, 3, Sizeof.cl_mem, Pointer.to(o2WorldVerticesMem));
		
		clSetKernelArg(vectorAddKernel, 0, Sizeof.cl_mem, Pointer.to(o2WorldVerticesMem));
		clSetKernelArg(vectorAddKernel, 1, Sizeof.cl_mem, Pointer.to(o2PosVecMem));
		clSetKernelArg(vectorAddKernel, 2, Sizeof.cl_mem, Pointer.to(o2WorldVerticesMem));
		
		global_work_size = new long[] {o2ModelVertices.length};
		clEnqueueNDRangeKernel(
				commandQueue,
				matVecMulKernel,
				global_work_size.length,
				null,
				global_work_size,
				local_work_size,
				0,
				null,
				null);
		//CL.clFinish(commandQueue);
		//global_work_size = new long[] {o1ModelVertices.length};
		clEnqueueNDRangeKernel(
				commandQueue,
				vectorAddKernel,
				global_work_size.length,
				null,
				global_work_size,
				null,
				0,
				null,
				null);
		CL.clFinish(commandQueue);
		
		
		//by this point, I should have all the transformed vertices
		//now I get the surface normals
		int[] o1Indices = o1.getMesh().getIndices();
		int[] o2Indices = o2.getMesh().getIndices();
		cl_mem o1IndicesMem = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, Sizeof.cl_float * o1Indices.length, Pointer.to(o1Indices), null);
		cl_mem o2IndicesMem = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, Sizeof.cl_float * o2Indices.length, Pointer.to(o2Indices), null);
		cl_mem o1SurfaceNormalsMem = clCreateBuffer(context, CL_MEM_READ_WRITE, Sizeof.cl_float * o1Indices.length, null, null); //length is getIndices / 3 (3 indices per triangle) * 3 (each normal is 3 floats)
		cl_mem o2SurfaceNormalsMem = clCreateBuffer(context, CL_MEM_READ_WRITE, Sizeof.cl_float * o2Indices.length, null, null);
		
		clSetKernelArg(getSurfaceNormalKernel, 0, Sizeof.cl_mem, Pointer.to(o1WorldVerticesMem));
		clSetKernelArg(getSurfaceNormalKernel, 1, Sizeof.cl_mem, Pointer.to(o1IndicesMem));
		clSetKernelArg(getSurfaceNormalKernel, 2, Sizeof.cl_mem, Pointer.to(o1SurfaceNormalsMem));
		global_work_size = new long[] {o1Indices.length / 3};
		clEnqueueNDRangeKernel( //calculate o1 surface normals
				commandQueue,
				matVecMulKernel,
				global_work_size.length,
				null,
				global_work_size,
				null,
				0,
				null,
				null);
		CL.clFinish(commandQueue);
		
		clSetKernelArg(getSurfaceNormalKernel, 0, Sizeof.cl_mem, Pointer.to(o2WorldVerticesMem));
		clSetKernelArg(getSurfaceNormalKernel, 1, Sizeof.cl_mem, Pointer.to(o2IndicesMem));
		clSetKernelArg(getSurfaceNormalKernel, 2, Sizeof.cl_mem, Pointer.to(o2SurfaceNormalsMem));
		global_work_size = new long[] {o1Indices.length / 3};
		clEnqueueNDRangeKernel( //calculate o2 surface normals
				commandQueue,
				matVecMulKernel,
				global_work_size.length,
				null,
				global_work_size,
				null,
				0,
				null,
				null);
		CL.clFinish(commandQueue);
		
		float[] o1SurfaceNormals = new float[o1Indices.length];
		float[] o2SurfaceNormals = new float[o2Indices.length];
		clEnqueueReadBuffer( //read o1 surface normals memory
				CLManager.getCommandQueue(),
				o1SurfaceNormalsMem,
				CL_TRUE,
				0,
				Sizeof.cl_float * o1SurfaceNormals.length,
				Pointer.to(o1SurfaceNormals),
				0,
				null,
				null);
		clEnqueueReadBuffer( //read o2 surface normals memory
				CLManager.getCommandQueue(),
				o2SurfaceNormalsMem,
				CL_TRUE,
				0,
				Sizeof.cl_float * o2SurfaceNormals.length,
				Pointer.to(o2SurfaceNormals),
				0,
				null,
				null);
		Set<Vector3f> surfaceNormalsSet = new HashSet<Vector3f>();
		for(int i = 0; i < o1SurfaceNormals.length; i += 3)
			surfaceNormalsSet.add(new Vector3f(o1SurfaceNormals[i], o1SurfaceNormals[i + 1], o1SurfaceNormals[i + 2]));
		for(int i = 0; i < o2SurfaceNormals.length; i += 3)
			surfaceNormalsSet.add(new Vector3f(o2SurfaceNormals[i], o2SurfaceNormals[i + 1], o2SurfaceNormals[i + 2]));
		float[] finalSurfaceNormals = new float[surfaceNormalsSet.size() * 3];
		int i = 0;
		for(Vector3f normal : surfaceNormalsSet) { //this should remove any duplicates and combine all the normals into 1. These are the axes
			finalSurfaceNormals[i] = normal.x;
			finalSurfaceNormals[i + 1] = normal.y;
			finalSurfaceNormals[i + 2] = normal.z;
			i += 3;
		};
		cl_mem finalSurfaceNormalsMem = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, Sizeof.cl_float * finalSurfaceNormals.length, Pointer.to(finalSurfaceNormals), null);
		
		
		//now i calculate the max and min points for each object for each axis
		cl_mem tempMem = clCreateBuffer(context, CL_MEM_READ_WRITE, Sizeof.cl_float * (o1ModelVertices.length / 3), null, null); //debug
		cl_mem tempMem2 = clCreateBuffer(context, CL_MEM_READ_WRITE, Sizeof.cl_float * o1ModelVertices.length, null, null); //debug
		cl_mem tempMem3 = clCreateBuffer(context, CL_MEM_READ_WRITE, Sizeof.cl_float * o1ModelVertices.length, null, null); //debug
		
		cl_mem o1MinMaxPoints = clCreateBuffer(context, CL_MEM_READ_WRITE, Sizeof.cl_float * (finalSurfaceNormals.length * 2 / 3), null, null);
		cl_mem o2MinMaxPoints = clCreateBuffer(context, CL_MEM_READ_WRITE, Sizeof.cl_float * (finalSurfaceNormals.length * 2 / 3), null, null);
		clSetKernelArg(getMinMaxKernel, 0, Sizeof.cl_mem, Pointer.to(o1WorldVerticesMem));
		clSetKernelArg(getMinMaxKernel, 1, Sizeof.cl_mem, Pointer.to(finalSurfaceNormalsMem));
		clSetKernelArg(getMinMaxKernel, 2, Sizeof.cl_mem, Pointer.to(o1MinMaxPoints));
		clSetKernelArg(getMinMaxKernel, 3, Sizeof.cl_float * o1ModelVertices.length, null);
		clSetKernelArg(getMinMaxKernel, 4, Sizeof.cl_float * o1ModelVertices.length, null);
		clSetKernelArg(getMinMaxKernel, 5, Sizeof.cl_mem, Pointer.to(tempMem)); //debug
		clSetKernelArg(getMinMaxKernel, 6, Sizeof.cl_mem, Pointer.to(tempMem2)); //debug
		clSetKernelArg(getMinMaxKernel, 7, Sizeof.cl_mem, Pointer.to(tempMem3)); //debug
		global_work_size = new long[] {(finalSurfaceNormals.length / 3) * (o1ModelVertices.length / 3)};
		local_work_size = new long[] {(o1ModelVertices.length / 3)};
		clEnqueueNDRangeKernel( //calculate o2 surface normals
				commandQueue,
				matVecMulKernel,
				global_work_size.length,
				null,
				global_work_size,
				local_work_size,
				0,
				null,
				null);
		CL.clFinish(commandQueue);
		clSetKernelArg(getMinMaxKernel, 0, Sizeof.cl_mem, Pointer.to(o2WorldVerticesMem));
		clSetKernelArg(getMinMaxKernel, 1, Sizeof.cl_mem, Pointer.to(finalSurfaceNormalsMem));
		clSetKernelArg(getMinMaxKernel, 2, Sizeof.cl_mem, Pointer.to(o2MinMaxPoints));
		clSetKernelArg(getMinMaxKernel, 3, Sizeof.cl_float * o2ModelVertices.length, null);
		clSetKernelArg(getMinMaxKernel, 4, Sizeof.cl_float * o2ModelVertices.length, null);
		global_work_size = new long[] {(finalSurfaceNormals.length / 3) * (o2ModelVertices.length / 3)};
		local_work_size = new long[] {(o2ModelVertices.length / 3)};
		clEnqueueNDRangeKernel( //calculate o2 surface normals
				commandQueue,
				matVecMulKernel,
				global_work_size.length,
				null,
				global_work_size,
				local_work_size,
				0,
				null,
				null);
		CL.clFinish(commandQueue);
		
		
		//I now have my max and min points and now just need to put them through the SAT algorithm to get the final outcome
		return false;
	}
	
	private static String readClProgram(String name) throws IOException {
		String out = "";
		try(BufferedReader br = new BufferedReader(new InputStreamReader(SATCL.class.getResourceAsStream("/collisions/" + name)))) {
			String line;
			while((line = br.readLine()) != null)
				out += line + "\n";
		}
		return out;
	}
}
