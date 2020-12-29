package net.alevel.asteroids.game;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

import net.alevel.asteroids.engine.GameObject;
import net.alevel.asteroids.engine.ILogic;
import net.alevel.asteroids.engine.Window;
import net.alevel.asteroids.engine.graphics.Camera;
import net.alevel.asteroids.engine.input.Input;
import net.alevel.asteroids.engine.input.enums.NonPrintableChars;
import net.alevel.asteroids.engine.input.enums.SpecialChars;
import net.alevel.asteroids.engine.utils.Pair;
import net.alevel.asteroids.game.objects.ObjectAssembly;
import net.alevel.asteroids.game.objects.Ship;

public class GameLogic implements ILogic {
	public static final float CAMERA_POS_STEP = 0.01f;
	public static final float MOUSE_SENSITIVITY = 0.05f;
	private final Camera camera;
	private final List<GameObject> gameObjects;
	private Ship player;
	private final List<ObjectAssembly> ships;
	
	private GameLogic() {
		this.camera = new Camera();
		this.gameObjects = new ArrayList<GameObject>();
		this.ships = new ArrayList<ObjectAssembly>();
	}
	
	@Override
	public void init(Window window) throws Exception {
		System.out.println(GL11.glGetString(GL11.GL_VERSION));
		
		this.player = new Ship();
		this.player.spawn();
		
		//this.gameObjects.add(aabbTest);
		//this.gameObjects.add(new StaticGameObject(MeshGen.triangularPrism(new Vector2f(0, 0), new Vector2f(1, 0), new Vector2f(0, 1), 10)));
		//this.gameObjects.add(new StaticGameObject(MeshGen.sphere(2)));
	}

	@Override
	public void update(float accumulatedTime, float interval, Input input) {
		final Vector3f cameraInc = new Vector3f();
		if(input.isKeyPressed('W') && !input.isKeyPressed('S'))
			cameraInc.z = -1f;
		else if(input.isKeyPressed('S') && !input.isKeyPressed('W'))
			cameraInc.z = 1f;
		if(input.isKeyPressed('A') && !input.isKeyPressed('D'))
			cameraInc.x = -1f;
		else if(input.isKeyPressed('D') && !input.isKeyPressed('A'))
			cameraInc.x = 1f;
		if(input.isKeyPressed(NonPrintableChars.LEFT_SHIFT) && !input.isKeyPressed(SpecialChars.SPACE))
			cameraInc.y = -1f;
		if(input.isKeyPressed(SpecialChars.SPACE) && !input.isKeyPressed(NonPrintableChars.LEFT_SHIFT))
			cameraInc.y = 1f;
		float posStep;
		if(input.isKeyPressed(NonPrintableChars.LEFT_CTRL))
			posStep = 3;
		else
			posStep = 0.25f;
		camera.movePosition(cameraInc.x * CAMERA_POS_STEP * posStep, cameraInc.y * CAMERA_POS_STEP * posStep, cameraInc.z * CAMERA_POS_STEP * posStep);
		//this.player.translate(cameraInc.x * CAMERA_POS_STEP * posStep, cameraInc.y * CAMERA_POS_STEP * posStep, cameraInc.z * CAMERA_POS_STEP * posStep); //player always in same position as camera
		Vector2f rotVec = input.getDeltaMousePos();
		camera.moveRotation(rotVec.x * MOUSE_SENSITIVITY, rotVec.y * MOUSE_SENSITIVITY, 0);
		
		this.player.rotate(0, 0.1f, 0);
		
		for(int i = 0; i < this.ships.size(); i++) {
			//this.ships.get(i).rotate(0, 0.01f, 0);
			//this.ships.get(i).translate(0, 0.001f, 0);
		}
		
		for(int i = 0; i < this.gameObjects.size(); i++)
			this.gameObjects.get(i).update(accumulatedTime);
	}
	
	@Override
	public Pair<Camera, List<GameObject>> toRender() {
		return new Pair<Camera, List<GameObject>>(this.camera, this.gameObjects);
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
	
	public static GameLogic init() {
		if(instance == null)
			instance = new GameLogic();
		return instance;
	}
	
	public static GameLogic getInstance() {
		return instance;
	}
}

/*AABBf aabb = aabbTest.getBoundingBox();
float[] boundingBoxVertices = {
	aabb.maxX, aabb.maxY, aabb.maxZ,
	aabb.minX, aabb.minY, aabb.minZ,
	aabb.maxX, aabb.maxY, aabb.minZ,
	aabb.minX, aabb.maxY, aabb.minZ,
	aabb.minX, aabb.maxY, aabb.maxZ,
	aabb.maxX, aabb.minY, aabb.minZ,
	aabb.maxX, aabb.minY, aabb.maxZ,
	aabb.minX, aabb.minY, aabb.maxZ,
};
//System.out.println(aabbTest.getModelBoundingBox());
//System.out.println(aabb);
//System.out.println(aabbTest.getPosition() + " | " + aabbTest.getRotation() + " | " + aabbTest.getScale() + " | " + aabbTest.getBoundingBox());
//System.out.println(Arrays.toString(boundingBoxVertices));

int[] indices = {
	0, 2, 3,
	0, 4, 3,
	2, 5, 1,
	2, 3, 1,
	2, 5, 6,
	2, 0, 6,
	3, 4, 1,
	4, 7, 1,
	0, 6, 7,
	0, 4, 7,
	5, 6, 7,
	5, 1, 7,
};
this.gameObjects.remove(this.boundingBox);
this.boundingBox = new StaticGameObject(new Mesh(boundingBoxVertices, boundingBoxVertices, boundingBoxVertices, indices));
this.gameObjects.add(this.boundingBox);*/



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
o2.setPosition(0, -1, -2);

this.gameObjects.add(o);
this.gameObjects.add(o1);
this.gameObjects.add(o2);*/



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

/*this.tempProjectile = new Projectile(WavefrontMeshLoader.loadMesh("/models/cube.obj"));
this.tempProjectile.setHorizontalAngleProjected(270).setPosition(0, 0, 0).setScale(0.05f);
this.gameObjects.add(this.tempProjectile);
this.tempPhysicalObject = new PhysicalObject(WavefrontMeshLoader.loadMesh("/models/cube.obj")); 
this.tempPhysicalObject.setPosition(0, 0, -1).setScale(0.1f);
this.gameObjects.add(this.tempPhysicalObject);*/