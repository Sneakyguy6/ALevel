package net.alevel.asteroids.game;

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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(float interval, MouseInput mouseInput) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void render(Window window) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void cleanUp() {
		// TODO Auto-generated method stub
		
	}

}
