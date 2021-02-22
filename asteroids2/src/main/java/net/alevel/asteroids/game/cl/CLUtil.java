package net.alevel.asteroids.game.cl;

import static org.jocl.CL.clBuildProgram;
import static org.jocl.CL.clCreateProgramWithSource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.jocl.cl_context;
import org.jocl.cl_program;

public class CLUtil {
	public static cl_program loadProgram(String path, Class<?> classReference, cl_context context) throws IOException {
		String programString = "";
		try(BufferedReader br = new BufferedReader(new InputStreamReader(classReference.getResourceAsStream(path)))) {
			String line;
			while((line = br.readLine()) != null)
				programString += line + "\n";
		}
		cl_program program = clCreateProgramWithSource(context,
				1,
				new String[] {programString},
				null,
				null);
		clBuildProgram(program, 0, null, null, null, null);
		return program;
	}
}
