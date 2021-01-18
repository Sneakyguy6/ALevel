package net.alevel.asteroids.engine.cl;

import static org.jocl.CL.CL_MEM_COPY_HOST_PTR;
import static org.jocl.CL.CL_MEM_READ_ONLY;
import static org.jocl.CL.CL_MEM_READ_WRITE;
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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.jocl.Pointer;
import org.jocl.Sizeof;
import org.jocl.cl_kernel;
import org.jocl.cl_mem;
import org.jocl.cl_program;

public class CLProgram implements AutoCloseable {
	private cl_kernel kernal;
	private cl_program program;
	private List<cl_mem> memories;
	
	private CLProgram initProgram(String programString, String name) {
		this.program = clCreateProgramWithSource(CLManager.getContext(), 1, new String[] {programString}, null, null);
		clBuildProgram(this.program, 0, null, null, null, null);
		this.kernal = clCreateKernel(this.program, name, null);
		this.memories = new ArrayList<cl_mem>();
		return this;
	}
	
	public CLProgram exec(long[] global_work_size) {
		//System.out.println(this.memories);
		for(int i = 0; i < this.memories.size(); i++)
			clSetKernelArg(this.kernal, i, Sizeof.cl_mem, Pointer.to(this.memories.get(i)));
		clEnqueueNDRangeKernel(
				CLManager.getCommandQueue(),
				this.kernal,
				global_work_size.length,
				null,
				global_work_size,
				null,
				0,
				null,
				null);
		return this;
	}
	
	public CLProgram addFloatMemory(float[] arr, long... flags) {
		long orFlag = 0;
		if(flags.length == 0)
			orFlag = CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR;
		else
			for(long i : flags)
				orFlag |= i;
		int[] err = new int[1];
		cl_mem mem = clCreateBuffer(CLManager.getContext(), orFlag, Sizeof.cl_float * arr.length, Pointer.to(arr), err);
		//System.out.println(Arrays.toString(err));
		//System.out.println(mem);
		//System.out.println("Context: " + CLManager.getContext());
		this.memories.add(mem);
		return this;
	}
	
	public CLProgram addEmptyMemory(int size, long... flags) {
		long orFlag = 0;
		if(flags.length == 0)
			orFlag = CL_MEM_READ_WRITE;
		else
			for(long i : flags)
				orFlag |= i;
		this.memories.add(clCreateBuffer(CLManager.getContext(), orFlag, Sizeof.cl_float * size, null, null));
		return this;
	}
	
	public CLProgram readFloatBuffer(float[] arr, int index, boolean pauseThread) {
		clEnqueueReadBuffer(
				CLManager.getCommandQueue(),
				this.memories.get(index),
				pauseThread,
				0,
				Sizeof.cl_float * arr.length,
				Pointer.to(arr),
				0,
				null,
				null);
		return this;
	}
	
	/**For use if you have additional buffers to add.
	 * @param program
	 * @param name
	 */
	public CLProgram setArgs(Consumer<List<cl_mem>> argSetup) {
		List<cl_mem> buffers = new ArrayList<cl_mem>();
		argSetup.accept(buffers);
		this.memories.addAll(buffers);
		return this;
	}
	
	@Override
	public void close() {
		for(int i = 0; i < this.memories.size(); i++)
			clReleaseMemObject(this.memories.get(i));
		clReleaseKernel(this.kernal);
		clReleaseProgram(this.program);
		this.memories = null;
		this.kernal = null;
		this.program = null;
	}
	
	public CLProgram(File file, String name) throws FileNotFoundException, IOException {
		String programString = "";
		try(BufferedReader br = new BufferedReader(new FileReader(file))) {
			String line;
			while((line = br.readLine()) != null)
				programString += line + "\n";
		}
		this.initProgram(programString, name);
	}
	
	public CLProgram(InputStream in, String name) throws IOException {
		String programString = "";
		try(BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
			String line;
			while((line = br.readLine()) != null)
				programString += line + "\n";
		}
		this.initProgram(programString, name);
	}
	
	public CLProgram(String program, String name) {
		this.initProgram(program, name);
	}
}
