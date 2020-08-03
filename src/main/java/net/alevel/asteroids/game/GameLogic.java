package net.alevel.asteroids.game;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

import net.alevel.asteroids.engine.GameObject;
import net.alevel.asteroids.engine.ILogic;
import net.alevel.asteroids.engine.Window;
import net.alevel.asteroids.engine.graphics.Camera;
import net.alevel.asteroids.engine.graphics.Mesh;
import net.alevel.asteroids.engine.graphics.WavefrontMeshLoader;
import net.alevel.asteroids.engine.input.Input;
import net.alevel.asteroids.engine.input.enums.MouseBtns;
import net.alevel.asteroids.engine.input.enums.NonPrintableChars;
import net.alevel.asteroids.engine.input.enums.SpecialChars;
import net.alevel.asteroids.engine.utils.Pair;

public class GameLogic implements ILogic {
	public static final float CAMERA_POS_STEP = 0.01f;
	public static final float MOUSE_SENSITIVITY = 0.05f;
	private final Camera camera;
	private GameObject[] gameObjects;
	
	public GameLogic() {
		camera = new Camera();
	}
	
	@Override
	public void init(Window window) throws Exception {
		System.out.println(GL11.glGetString(GL11.GL_VERSION));
		
		Mesh mesh = WavefrontMeshLoader.loadMesh("/models/bunny.obj");
		mesh.setColour(new Vector3f(0f, 1f, 0f));
		GameObject o = new GameObject(mesh);
		o.setScale(1.5f);
		o.setPosition(0, 0, -2);
		
		Mesh mesh1 = WavefrontMeshLoader.loadMesh("/models/bunny.obj");
		mesh1.setColour(new Vector3f(0f, 1f, 0f));
		GameObject o1 = new GameObject(mesh1);
		o1.setScale(1.5f);
		o1.setPosition(0, 1, -2);
		
		Mesh mesh2 = WavefrontMeshLoader.loadMesh("/models/bunny.obj");
		mesh2.setColour(new Vector3f(0f, 1f, 0f));
		GameObject o2 = new GameObject(mesh2);
		o2.setScale(1.5f);
		o2.setPosition(0, -1, -2);
		gameObjects = new GameObject[] {o, o1, o2};
	}

	@Override
	public void update(float interval, Input input) {
		final Vector3f cameraInc = new Vector3f();
		if(input.isKeyPressed('W'))
			cameraInc.z = -0.5f;
		else if(input.isKeyPressed('S'))
			cameraInc.z = 0.5f;
		if(input.isKeyPressed('A'))
			cameraInc.x = -0.5f;
		else if(input.isKeyPressed('D'))
			cameraInc.x = 0.5f;
		if(input.isKeyPressed(NonPrintableChars.LEFT_SHIFT))
			cameraInc.y = -0.5f;
		else if(input.isKeyPressed(SpecialChars.SPACE))
			cameraInc.y = 0.5f;
		camera.movePosition(cameraInc.x * CAMERA_POS_STEP, cameraInc.y * CAMERA_POS_STEP, cameraInc.z * CAMERA_POS_STEP);
		
		if(input.isMouseBtnPressed(MouseBtns.RIGHT_CLICK)) {
			Vector2f rotVec = input.getDeltaMousePos();
			camera.moveRotation(rotVec.x * MOUSE_SENSITIVITY, rotVec.y * MOUSE_SENSITIVITY, 0);
		}
	}
	
	@Override
	public Pair<Camera, GameObject[]> toRender() {
		return new Pair<Camera, GameObject[]>(this.camera, this.gameObjects);
	}

	@Override
	public void cleanUp() {
		for(GameObject o : gameObjects)
			o.getMesh().cleanUp();
	}

}
