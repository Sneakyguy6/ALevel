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

import org.jocl.Pointer;
import org.jocl.Sizeof;
import org.jocl.cl_kernel;
import org.jocl.cl_mem;
import org.jocl.cl_program;
import org.joml.Matrix3f;
import org.joml.Vector3f;

public class MatrixVectorMulTest {
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
		cl_kernel kernel = clCreateKernel(program, "matrixVectorMultiply", null);
		float[] vectors = {1, 0, 0, 0, 0, 1};
		//Matrix3f matrix = new Matrix3f(1, 2, 1, 2, 3, 1, 1, 1, 1);
		Matrix3f matrix = new Matrix3f().rotate((float) (Math.PI / 2), new Vector3f(0, 1, 0));
		float[] matrixArr = new float[9];
		matrix.get(matrixArr);
		float[] transformedVectors = new float[vectors.length];
		cl_mem matMem = clCreateBuffer(CLManager.getContext(), CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, Sizeof.cl_float * matrixArr.length, Pointer.to(matrixArr), null);
		cl_mem matSizes = clCreateBuffer(CLManager.getContext(), CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, Sizeof.cl_int * 2, Pointer.to(new int[] {3, 3}), null);
		cl_mem vectorsMem = clCreateBuffer(CLManager.getContext(), CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, Sizeof.cl_float * vectors.length, Pointer.to(vectors), null);
		cl_mem out = clCreateBuffer(CLManager.getContext(), CL_MEM_READ_WRITE, Sizeof.cl_float * vectors.length, null, null);
		
		clSetKernelArg(kernel, 0, Sizeof.cl_mem, Pointer.to(matMem));
		clSetKernelArg(kernel, 1, Sizeof.cl_mem, Pointer.to(matSizes));
		clSetKernelArg(kernel, 2, Sizeof.cl_mem, Pointer.to(vectorsMem));
		clSetKernelArg(kernel, 3, Sizeof.cl_mem, Pointer.to(out));
		
		long[] global_work_size = {vectors.length};
		long[] local_work_size = {3};
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
				out,
				CL_TRUE,
				0,
				Sizeof.cl_float * transformedVectors.length,
				Pointer.to(transformedVectors),
				0,
				null,
				null);
		System.out.println(Arrays.toString(transformedVectors));
		
		clReleaseMemObject(matMem);
		clReleaseMemObject(matSizes);
		clReleaseMemObject(vectorsMem);
		clReleaseKernel(kernel);
		clReleaseProgram(program);
	}
}
