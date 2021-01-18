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
import java.util.Random;

import org.jocl.Pointer;
import org.jocl.Sizeof;
import org.jocl.cl_kernel;
import org.jocl.cl_mem;
import org.jocl.cl_program;

public class MatrixMulTest {
	public static void run() throws IOException {
		String programString = "";
		try(BufferedReader br = new BufferedReader(new InputStreamReader(MatrixMulTest.class.getResourceAsStream("/collisions/MatVecMul.cl")))) {
			String line;
			while((line = br.readLine()) != null)
				programString += line + "\n";
		}
		//System.out.println(programString);
		
		cl_program program = clCreateProgramWithSource(CLManager.getContext(), 1, new String[] {programString}, null, null);
		clBuildProgram(program, 0, null, null, null, null);
		cl_kernel kernel = clCreateKernel(program, "matrixMultiply", null);
		float[] srcArrayA = new float[32];/*{
			1, 0, 1, 3,
		    0, 1, 0, 2,
		    1, 0, 1, 1,
		};*/
		float[] srcArrayB = new float[32];/*{
		    2, 3, 2,
		    1, 2, 1,
		    3, 1, 2,
		    1, 2, 3,
		};*/
		Random rng = new Random();
		for(int i = 0; i < 32; i++) {
			srcArrayA[i] = rng.nextInt(9) + 1;
			srcArrayB[i] = rng.nextInt(9) + 1;
		}
		float[] srcArrayC = new float[16];
		cl_mem matAMem = clCreateBuffer(CLManager.getContext(), CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, Sizeof.cl_float * srcArrayA.length, Pointer.to(srcArrayA), null);
		cl_mem matBMem = clCreateBuffer(CLManager.getContext(), CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, Sizeof.cl_float * srcArrayB.length, Pointer.to(srcArrayB), null);
		cl_mem matCMem = clCreateBuffer(CLManager.getContext(), CL_MEM_READ_WRITE, Sizeof.cl_float * 256, null, null);
		cl_mem matSizes = clCreateBuffer(CLManager.getContext(), CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, Sizeof.cl_int * 4, Pointer.to(new int[] {8, 4, 4, 8}), null);
		clSetKernelArg(kernel, 0, Sizeof.cl_mem, Pointer.to(matAMem));
		clSetKernelArg(kernel, 1, Sizeof.cl_mem, Pointer.to(matBMem));
		clSetKernelArg(kernel, 2, Sizeof.cl_mem, Pointer.to(matCMem));
		clSetKernelArg(kernel, 3, Sizeof.cl_mem, Pointer.to(matSizes));
		
		long[] global_work_size = {4, 4};
		clEnqueueNDRangeKernel(
				CLManager.getCommandQueue(),
				kernel,
				global_work_size.length,
				null,
				global_work_size,
				null,
				0,
				null,
				null);
		
		clEnqueueReadBuffer(
				CLManager.getCommandQueue(),
				matCMem,
				CL_TRUE,
				0,
				Sizeof.cl_float * srcArrayC.length,
				Pointer.to(srcArrayC),
				0,
				null,
				null);
		System.out.println(Arrays.toString(srcArrayA));
		System.out.println(Arrays.toString(srcArrayB));
		System.out.println(Arrays.toString(srcArrayC));
		
		clReleaseMemObject(matAMem);
		clReleaseMemObject(matBMem);
		clReleaseMemObject(matCMem);
		clReleaseKernel(kernel);
		clReleaseProgram(program);
	}
}
