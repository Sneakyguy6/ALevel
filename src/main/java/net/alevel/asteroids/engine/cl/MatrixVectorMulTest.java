package net.alevel.asteroids.engine.cl;

import static org.jocl.CL.clBuildProgram;
import static org.jocl.CL.clCreateKernel;
import static org.jocl.CL.clCreateProgramWithSource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.jocl.cl_kernel;
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
		float[] matrixArr = new float[9];
		Matrix3f matrix = new Matrix3f().rotate((float) (Math.PI / 2), new Vector3f(0, 1, 0));
		matrix.get(matrixArr);
		
	}
}
