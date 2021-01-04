package net.alevel.asteroids.engine.cl;

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
import static org.jocl.CL.clReleaseKernel;
import static org.jocl.CL.clReleaseMemObject;
import static org.jocl.CL.clReleaseProgram;
import static org.jocl.CL.clSetKernelArg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

import org.jocl.CL;
import org.jocl.Pointer;
import org.jocl.Sizeof;
import org.jocl.cl_kernel;
import org.jocl.cl_mem;
import org.jocl.cl_program;

public class GetMinMaxPoints {
	public static void run() throws IOException {
		CL.setExceptionsEnabled(true);
		String programString = "";
		try(BufferedReader br = new BufferedReader(new InputStreamReader(MatrixMulTest.class.getResourceAsStream("/collisions/GetMinMaxPoints.cl")))) {
			String line;
			while((line = br.readLine()) != null)
				programString += line + "\n";
		}
		//System.out.println(programString);
		
		cl_program program = clCreateProgramWithSource(CLManager.getContext(), 1, new String[] {programString}, null, null);
		clBuildProgram(program, 0, null, null, null, null);
		cl_kernel kernel = clCreateKernel(program, "getMinMaxPoints", null);
		
		float[] vertices = {1, 1, 0, 0, 1, 0, 0, 1, 1};
		float[] normals = {0, 1, 0};
		float[] maxMins = new float[vertices.length * 2 / 3];
		System.out.println(vertices.length);
		System.out.println(maxMins.length);
		
		cl_mem verticesMem = clCreateBuffer(CLManager.getContext(), CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, Sizeof.cl_float * vertices.length, Pointer.to(vertices), null);
		cl_mem normalsMem = clCreateBuffer(CLManager.getContext(), CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, Sizeof.cl_float * normals.length, Pointer.to(normals), null);
		cl_mem maxMinsMem = clCreateBuffer(CLManager.getContext(), CL_MEM_READ_WRITE, Sizeof.cl_float * maxMins.length, null, null);
		
		
		clSetKernelArg(kernel, 0, Sizeof.cl_mem, Pointer.to(verticesMem));
		clSetKernelArg(kernel, 1, Sizeof.cl_mem, Pointer.to(normalsMem));
		clSetKernelArg(kernel, 2, Sizeof.cl_mem, Pointer.to(maxMinsMem));
		clSetKernelArg(kernel, 3, Sizeof.cl_float * vertices.length, null);
		clSetKernelArg(kernel, 4, Sizeof.cl_float * vertices.length, null);
		System.out.println("some text " + 90);
		long[] global_work_size = {(normals.length / 3) * (vertices.length / 3)};
		long[] local_work_size = {(vertices.length / 3)};
		clEnqueueNDRangeKernel(
				CLManager.getCommandQueue(),
				kernel,
				global_work_size.length,
				null,
				global_work_size,
				local_work_size,
				0,
				null,
				null);
		
		clEnqueueReadBuffer(
				CLManager.getCommandQueue(),
				maxMinsMem,
				CL_TRUE,
				0,
				Sizeof.cl_float * maxMins.length,
				Pointer.to(maxMins),
				0,
				null,
				null);
		System.out.println(Arrays.toString(maxMins));
		
		clReleaseMemObject(verticesMem);
		clReleaseMemObject(normalsMem);
		clReleaseMemObject(maxMinsMem);
		clReleaseKernel(kernel);
		clReleaseProgram(program);
	}
}
