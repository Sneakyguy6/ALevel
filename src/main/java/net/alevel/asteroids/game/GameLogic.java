package net.alevel.asteroids.game;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_SHIFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

import net.alevel.asteroids.engine.GameObject;
import net.alevel.asteroids.engine.ILogic;
import net.alevel.asteroids.engine.MouseInput;
import net.alevel.asteroids.engine.Window;
import net.alevel.asteroids.engine.graphics.Camera;
import net.alevel.asteroids.engine.graphics.Mesh;
import net.alevel.asteroids.engine.graphics.Renderer;
import net.alevel.asteroids.engine.graphics.WavefrontMeshLoader;

public class GameLogic implements ILogic {
	public static final float CAMERA_POS_STEP = 0.05f;
	public static final float MOUSE_SENSITIVITY = 0.2f;
	private final Vector3f cameraInc;
	private final Camera camera;
	private final Renderer renderer;
	private GameObject[] gameObjects;
	
	public GameLogic() {
		this.renderer = new Renderer();
		this.camera = new Camera();
		this.cameraInc = new Vector3f();
	}
	
	@Override
	public void init(Window window) throws Exception {
		this.renderer.initShaderProgram(window);
		System.out.println(GL11.glGetString(GL11.GL_VERSION));
		
		Mesh mesh = WavefrontMeshLoader.loadMesh("/models/bunny.obj");
		mesh.setColour(new Vector3f(0f, 1f, 0f));
		GameObject o = new GameObject(mesh);
		o.setScale(1.5f);
		o.setPosition(0, 0, -2);
		this.gameObjects = new GameObject[] {o};
	}

	@Override
	public void input(Window window, MouseInput mouseInput) {
		this.cameraInc.set(0, 0, 0);
		if(window.isKeyPressed(GLFW_KEY_W))
			this.cameraInc.z = -0.5f;
		if(window.isKeyPressed(GLFW_KEY_S))
			this.cameraInc.z = 0.5f;
		if(window.isKeyPressed(GLFW_KEY_A))
			this.cameraInc.x = -0.5f;
		if(window.isKeyPressed(GLFW_KEY_D))
			this.cameraInc.x = 0.5f;
		if(window.isKeyPressed(GLFW_KEY_LEFT_SHIFT))
			this.cameraInc.y = -0.5f;
		if(window.isKeyPressed(GLFW_KEY_SPACE))
			this.cameraInc.y = 0.5f;
	}

	@Override
	public void update(float interval, MouseInput mouseInput) {
		camera.movePosition(cameraInc.x * CAMERA_POS_STEP, cameraInc.y * CAMERA_POS_STEP, cameraInc.z * CAMERA_POS_STEP);
		
		if(mouseInput.isRightBtnPressed()) {
			Vector2f rotVec = mouseInput.getDisplayVec();
			this.camera.moveRotation(rotVec.x * MOUSE_SENSITIVITY, rotVec.y * MOUSE_SENSITIVITY, 0);
		}
	}

	@Override
	public void render(Window window) {
		this.renderer.render(window, this.camera, this.gameObjects);
	}

	@Override
	public void cleanUp() {
		this.renderer.cleanUp();
		for(GameObject o : this.gameObjects)
			o.getMesh().cleanUp();
	}

}
