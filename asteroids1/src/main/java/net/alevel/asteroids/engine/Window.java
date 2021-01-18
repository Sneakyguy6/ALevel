package net.alevel.asteroids.engine;

import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MAJOR;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MINOR;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_CORE_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_FORWARD_COMPAT;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwGetKey;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetFramebufferSizeCallback;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPos;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.system.MemoryUtil.NULL;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

public class Window {
	private long windowHandle; //ID of the window handler
	private int width; //width and height variables will be used in the projection matrix
	private int height;
	private boolean resized;
	
	public Window() {
		this.width = 600; //width and height are measured in pixels
		this.height = 600;
		this.resized = false;
	}
	
	public void init() { //width and height variables are to do with the size (in pixels) of the window
		GLFWErrorCallback.createPrint(System.err).set(); //sets the GL error stream to the system stream
		
		if(!glfwInit()) //starts the GLFW library so OpenGL can now be interfaced with
			throw new RuntimeException("Unable to start GLFW!");
		
		//set window hints (window settings)
		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_VISIBLE, GL_FALSE); //window will stay hidden after creation
		glfwWindowHint(GLFW_RESIZABLE, GL_TRUE); //this window 'can' be resized
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
		glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
		glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);
		
		GLFWVidMode vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor()); //gets resolution of main monitor
		
		//this statement creates the window and at the same time checks if creation was successful
		if((this.windowHandle = glfwCreateWindow(vidMode.width(), vidMode.height() - 30, "Asteroids", NULL, NULL)) == NULL)
			throw new RuntimeException("Failed to create new window");
		
		//this gets called every time the window is resized. This means the width and height will be updated
		glfwSetFramebufferSizeCallback(this.windowHandle, (window, width, height) -> {
			this.width = width;
			this.height = height;
			this.resized = true;
		});
		
		//setup a key callback. It will be called every time a key is presses, repeated or released
		glfwSetKeyCallback(this.windowHandle, (window, key, scanCode, action, mods) -> {
			if(key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
				glfwSetWindowShouldClose(window, true); //this will be detected in the rendering loop
		});
		
		glfwSetWindowPos(this.windowHandle, 0, 30);
		
		glfwMakeContextCurrent(this.windowHandle); //tells OpenGL to use this window
		
		glfwSwapInterval(1); //1 means it will use vsync (max FPS is monitor refresh rate)
		
		glfwShowWindow(this.windowHandle); //everything is setup so can now open the window
		
		GL.createCapabilities();
		
		glClearColor(0f, 0f, 0f, 0f); //background colour (the colour where nothing is rendered) will be black
		
		glEnable(GL_DEPTH_TEST); //tells OpenGL to draw the farthest vertices first and then work its way towards the camera. It has no concept of layers
		
		GL30.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE); //draws meshes as wireframes (triangles have no fill). This is for testing
	}
	

	public void setClearColour(float r, float b, float g, float alpha) {
		glClearColor(r, g, b, alpha);
	}
	
	public boolean isKeyPressed(int keyCode) {
		return glfwGetKey(this.windowHandle, keyCode) == GLFW_PRESS;
	}
	
	public boolean windowShouldClose() {
		return glfwWindowShouldClose(this.windowHandle);
	}

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
    
    public boolean isResized() {
        return resized;
    }

    public void setResized(boolean resized) {
        this.resized = resized;
    }
    
    public long getWindowHandle() {
    	return this.windowHandle;
    }

    public void update() { //This method is what causes the pixels to change in the window
        glfwSwapBuffers(windowHandle);
        glfwPollEvents();
    }
}
