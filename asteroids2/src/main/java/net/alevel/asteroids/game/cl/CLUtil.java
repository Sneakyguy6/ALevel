package net.alevel.asteroids.game.cl;

import static org.jocl.CL.clBuildProgram;
import static org.jocl.CL.clCreateProgramWithSource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.jocl.cl_context;
import org.jocl.cl_program;

/**Contains static methods for general use within OpenCL related functions
 */
public class CLUtil {
	
	/**Loads an openCL source code file (stored within the .jar)
	 * @param path the path to the resource
	 * @param classReference the class who's class loader will be used to load the resource
	 * @param context The OpenCL context
	 * @return An OpenCL program object
	 * @throws IOException
	 */
	public static cl_program loadProgram(String path, Class<?> classReference, cl_context context) throws IOException {
		String programString = "";
		try(BufferedReader br = new BufferedReader(new InputStreamReader(classReference.getResourceAsStream(path)))) {
			String line;
			while((line = br.readLine()) != null)
				programString += line + "\n";
		}
		//System.out.println(programString);
		cl_program program = clCreateProgramWithSource(context,
				1,
				new String[] {programString},
				null,
				null);
		clBuildProgram(program, 0, null, null, null, null);
		return program;
	}
}
