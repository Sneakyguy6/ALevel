package net.alevel.asteroids.game;

import java.util.HashSet;
import java.util.Set;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

import net.alevel.asteroids.engine.GameObject;
import net.alevel.asteroids.engine.ILogic;
import net.alevel.asteroids.engine.Window;
import net.alevel.asteroids.engine.graphics.Camera;
import net.alevel.asteroids.engine.graphics.WavefrontMeshLoader;
import net.alevel.asteroids.engine.input.Input;
import net.alevel.asteroids.engine.input.enums.MouseBtns;
import net.alevel.asteroids.engine.input.enums.NonPrintableChars;
import net.alevel.asteroids.engine.input.enums.SpecialChars;
import net.alevel.asteroids.engine.utils.Pair;
import net.alevel.asteroids.game.physics.Collision;
import net.alevel.asteroids.game.physics.PhysicalObject;

public class GameLogic implements ILogic {
	public static final float CAMERA_POS_STEP = 0.01f;
	public static final float MOUSE_SENSITIVITY = 0.05f;
	private final Camera camera;
	private final Set<GameObject> gameObjects;
	private float accumulatedTime;
	
	private GameLogic() {
		this.camera = new Camera();
		this.accumulatedTime = 0;
		this.gameObjects = new HashSet<GameObject>();
	}
	
	@Override
	public void init(Window window) throws Exception {
		System.out.println(GL11.glGetString(GL11.GL_VERSION));
		Collision.init();
		/*Mesh mesh = WavefrontMeshLoader.loadMesh("/models/bunny.obj");
		mesh.setColour(new Vector3f(0f, 1f, 0f));
		GameObject o = new StaticGameObject(mesh);
		o.setScale(1.5f);
		o.setPosition(0, 0, -2);
		
		Mesh mesh1 = WavefrontMeshLoader.loadMesh("/models/bunny.obj");
		mesh1.setColour(new Vector3f(0f, 1f, 0f));
		GameObject o1 = new StaticGameObject(mesh1);
		o1.setScale(1.5f);
		o1.setPosition(0, 1, -2);
		
		Mesh mesh2 = WavefrontMeshLoader.loadMesh("/models/bunny.obj");
		mesh2.setColour(new Vector3f(0f, 1f, 0f));
		GameObject o2 = new StaticGameObject(mesh2);
		o2.setScale(1.5f);
		o2.setPosition(0, -1, -2);*/
		
		/*this.gameObjects = new GameObject[8];
		for(int i = 0; i < 6; i++)
			this.gameObjects[i] = new StaticGameObject(WavefrontMeshLoader.loadMesh("/models/bunny.obj"));
		this.gameObjects.setPosition(0, 0, -1).getMesh().setColour(new Vector3f(0, 1, 0));
		this.gameObjects[1].setPosition(0, 0, 1).setRotation(0f, 180f, 0f).getMesh().setColour(new Vector3f(1, 0, 0));
		this.gameObjects[2].setPosition(0, -1, 0).getMesh().setColour(new Vector3f(0, 0, 1));
		this.gameObjects[3].setPosition(0, 1, 0).getMesh().setColour(new Vector3f(0, 1, 1));
		this.gameObjects[4].setPosition(-1, 0, 0).getMesh().setColour(new Vector3f(1, 1, 0));
		this.gameObjects[5].setPosition(1, 0, 0).getMesh().setColour(new Vector3f(1, 0, 1));
		
		//this.gameObjects[6] = new Projectile(WavefrontMeshLoader.loadMesh("/models/bunny.obj"));
		//this.gameObjects[6].setPosition(0, 1, 1).getMesh().setColour(new Vector3f(1, 0.5f, 0));
		
		this.gameObjects[6] = new Projectile(WavefrontMeshLoader.loadMesh("/models/cube.obj"));
		((Projectile) this.gameObjects[6]).setHorizontalAngleProjected(270).setPosition(0, 0, 0).setScale(0.05f);
		this.gameObjects[7] = new PhysicalObject(WavefrontMeshLoader.loadMesh("/models/cube.obj"));
		this.gameObjects[7].setPosition(0, 0, -1).setScale(0.1f);*/
		GameObject temp = new Projectile(WavefrontMeshLoader.loadMesh("/models/cube.obj"));
		((Projectile) temp).setHorizontalAngleProjected(270).setPosition(0, 0, 0).setScale(0.05f);
		this.gameObjects.add(temp);
		temp = new PhysicalObject(WavefrontMeshLoader.loadMesh("/models/cube.obj")); 
		temp.setPosition(0, 0, -1).setScale(0.1f);
		this.gameObjects.add(temp);
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
		
		this.accumulatedTime += interval;
		for(GameObject i : this.gameObjects)
			i.update(this.accumulatedTime);
		Collision.getInstance().checkForCollisions();
	}
	
	@Override
	public Pair<Camera, Set<GameObject>> toRender() {
		return new Pair<Camera, Set<GameObject>>(this.camera, this.gameObjects);
	}

	@Override
	public void cleanUp() {
		for(GameObject o : gameObjects)
			o.getMesh().cleanUp();
	}
	
	public void addObject(GameObject o) {
		this.gameObjects.add(o);
	}
	
	public void removeObject(GameObject o) {
		this.gameObjects.remove(o);
	}
	
	private static GameLogic instance;
	
	public static void init() {
		if(instance == null)
			instance = new GameLogic();
	}
	
	public static GameLogic getInstance() {
		return instance;
	}
}
