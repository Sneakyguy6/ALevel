package net.alevel.asteroids.engine.cl;

import static org.jocl.CL.CL_CONTEXT_PLATFORM;
import static org.jocl.CL.*;
import static org.jocl.CL.clCreateCommandQueueWithProperties;
import static org.jocl.CL.clCreateContext;
import static org.jocl.CL.clGetDeviceIDs;
import static org.jocl.CL.clGetPlatformIDs;
import static org.jocl.CL.clReleaseCommandQueue;
import static org.jocl.CL.clReleaseContext;

import org.jocl.CL;
import org.jocl.cl_command_queue;
import org.jocl.cl_context;
import org.jocl.cl_context_properties;
import org.jocl.cl_device_id;
import org.jocl.cl_platform_id;
import org.jocl.cl_queue_properties;

public class CLManager {
	private static cl_context context;
	private static cl_queue_properties queueProperties;
	private static cl_command_queue commandQueue;

	/**
	 * Creates a CL context for the kernals.<br>
	 * NOTE: These kernals will run on the GPU
	 */
	public static void init() {
		// The platform, device type and device number
		// that will be used
		final int platformIndex = 0;
		final long deviceType = CL_DEVICE_TYPE_GPU;
		final int deviceIndex = 0;
		final int[] err = new int[1];

		// Enable exceptions and subsequently omit error checks in this sample
		CL.setExceptionsEnabled(true);

		// Obtain the number of platforms
		int numPlatformsArray[] = new int[1];
		clGetPlatformIDs(0, null, numPlatformsArray);
		int numPlatforms = numPlatformsArray[0];

		// Obtain a platform ID
		cl_platform_id platforms[] = new cl_platform_id[numPlatforms];
		clGetPlatformIDs(platforms.length, platforms, null);
		cl_platform_id platform = platforms[platformIndex];

		// Initialize the context properties
		cl_context_properties contextProperties = new cl_context_properties();
		contextProperties.addProperty(CL_CONTEXT_PLATFORM, platform);

		// Obtain the number of devices for the platform
		int numDevicesArray[] = new int[1];
		clGetDeviceIDs(platform, deviceType, 0, null, numDevicesArray);
		//System.out.println(Arrays.toString(numDevicesArray));
		int numDevices = numDevicesArray[0];

		// Obtain a device ID
		cl_device_id devices[] = new cl_device_id[numDevices];
		clGetDeviceIDs(platform, deviceType, numDevices, devices, null);
		cl_device_id device = devices[deviceIndex];

		// Create a context for the selected device
		context = clCreateContext(contextProperties, 1, new cl_device_id[] { device }, null, null, err);
		//System.out.println("Context err: " + err[0]);

		// Create a command-queue for the selected device
		queueProperties = new cl_queue_properties();
		commandQueue = clCreateCommandQueueWithProperties(context, device, queueProperties, null);
		CL.clFinish(commandQueue);
	}
	
	public static void cleanUp() {
		clReleaseCommandQueue(commandQueue);
		clReleaseContext(context);
	}

	public static cl_context getContext() {
		return context;
	}

	public static cl_queue_properties getQueueProperties() {
		return queueProperties;
	}

	public static cl_command_queue getCommandQueue() {
		return commandQueue;
	}
}
