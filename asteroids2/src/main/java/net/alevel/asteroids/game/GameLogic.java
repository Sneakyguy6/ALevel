package net.alevel.asteroids.game;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

import net.alevel.asteroids.engine.ILogic;
import net.alevel.asteroids.engine.Window;
import net.alevel.asteroids.engine.graphics.Camera;
import net.alevel.asteroids.engine.graphics.Mesh;
import net.alevel.asteroids.engine.input.Input;
import net.alevel.asteroids.engine.input.enums.NonPrintableChars;
import net.alevel.asteroids.engine.input.enums.SpecialChars;
import net.alevel.asteroids.engine.objects.GameObject;
import net.alevel.asteroids.engine.objects.NonRenderableObject;
import net.alevel.asteroids.engine.utils.Pair;
import net.alevel.asteroids.game.cl.CLManager;
import net.alevel.asteroids.game.noise.Perlin;
import net.alevel.asteroids.game.objects.GameObjects;
import net.alevel.asteroids.game.objects.ModifiableMesh;
import net.alevel.asteroids.game.objects.ObjectAssembly;
import net.alevel.asteroids.game.objects.Ship;
import net.alevel.asteroids.game.objects.StaticGameObject;
import net.alevel.asteroids.game.objects.shapes.Grid;
import net.alevel.asteroids.game.objects.shapes.MeshGen;
import net.alevel.asteroids.game.physics.Physics;
import net.alevel.asteroids.game.physics.RigidObject;

public class GameLogic implements ILogic {
	public static final float CAMERA_POS_STEP = 0.01f;
	public static final float MOUSE_SENSITIVITY = 0.05f;
	private final Camera camera;
	
	private final GameObjects gameObjects;
	//private final List<GameObject> gameObjects;
	private Ship player;
	private RigidObject[] rigidObjects;
	private final List<ObjectAssembly> ships;
	
	private final Physics physics;
	
	private GameLogic() throws IOException {
		CLManager.init();
		this.camera = new Camera();
		this.gameObjects = new GameObjects(); // ArrayList<GameObject>();
		this.ships = new ArrayList<ObjectAssembly>();
		this.physics = new Physics();
	}
	
	@Override
	public void init(Window window) throws Exception {
		System.out.println(GL11.glGetString(GL11.GL_VERSION));
		//SATCL.init();
		
		//MatrixMulTest.run();
		//SurfaceNormalsTest.run();
		//SurfaceNormalsTest.runJava();
		//MatrixVectorMulTest.run();
		//GetMinMaxPoints.run();
		
		this.player = new Ship();
		this.rigidObjects = new RigidObject[] {
				new RigidObject(MeshGen.cube(1, 1, 1)),
				//new RigidObject(MeshGen.cube(1, 1, 1))
		};
		//this.rigidObjects[0].setPosition(0, 0, 10);
		this.rigidObjects[0].setPosition(0, 0, -10);
		//this.gameObjects.spawnAll(this.rigidObjects);
		
		ModifiableMesh grid = Grid.create(50, 50, 1);
		Perlin noise = new Perlin(10, 10, new Random().nextLong());
		//Random rng = new Random();
		//System.out.println(noise.get(0.05, 0.05));
		for(int i = 0; i < 50; i++) {
			for(int j = 0; j < 50; j++) {
				float height = (float) (noise.get((double) i / 10, (double) j / 10) * 10);
				//System.out.println(((double)(i / 100)) + " " + ((double)(j / 100)) + " => " + height);
				grid.changePosition((((i * 50) + j) * 3) + 1, height);
			}
		}
		
		this.gameObjects.spawnObject(new StaticGameObject(new Mesh(grid.getPositions(), grid.getPositions(), grid.getPositions(), grid.getIndices())));
		//Grid.debug(this.gameObjects);
		
		/*ModifiableMesh asteroid = MeshGen.modifiableSphere(5, 2); //resolution to use is 6
		//System.out.println(asteroid.getPositions().length);
		Perlin noise = new Perlin(asteroid.getPositions().length / 2, 2, new Random().nextLong());
		for(int i = 0; i < asteroid.getPositions().length / 2; i++) {
			for(int j = 0; j < 2; j++)
				asteroid.changePosition(i + j, (float) (asteroid.getPositions()[i + j] * ((noise.get((double) i / 10, (double) j / 10) + 0.5) * 1)));
		}*/
		
		//this.gameObjects.spawnObject(new StaticGameObject(new Mesh(asteroid.getPositions(), new float[0], new float[0], asteroid.getIndices())));
		//this.gameObjects.spawnObject(this.player);
		//this.player.spawn();
		//this.gameObjects.add(new StaticGameObject(MeshGen.triangularPrism(new Vector2f(0, 0), new Vector2f(1, 0), new Vector2f(0, 1), 10)));
		//this.gameObjects.add(new StaticGameObject(MeshGen.sphere(2)));
	}

	@Override
	public void update(float accumulatedTime, float interval, Input input) throws IOException {
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
			posStep = 20f;
		else
			posStep = 10f;
		camera.movePosition(cameraInc.x * CAMERA_POS_STEP * posStep, cameraInc.y * CAMERA_POS_STEP * posStep, cameraInc.z * CAMERA_POS_STEP * posStep);
		//this.player.translate(cameraInc.x * CAMERA_POS_STEP * posStep, cameraInc.y * CAMERA_POS_STEP * posStep, cameraInc.z * CAMERA_POS_STEP * posStep); //player always in same position as camera
		Vector2f rotVec = input.getDeltaMousePos();
		camera.moveRotation(rotVec.x * MOUSE_SENSITIVITY, rotVec.y * MOUSE_SENSITIVITY, 0);
		
		this.player.rotate(0, 0.1f, 0);
		
		//this.rigidObjects[0].translate(0, 0, -.01f).rotate(0, (float) Math.PI / 3, 0);
		this.rigidObjects[0].translate(0, 0, .01f);
		
		this.physics.onUpdate(this.gameObjects.getRigidObjects());
		
		List<NonRenderableObject> objects = this.gameObjects.getAllObjects();
		for(int i = 0; i < objects.size(); i++)
			objects.get(i).update(accumulatedTime);
		for(int i = 0; i < objects.size(); i++)
			objects.get(i).onUpdateFinish(interval);
	}
	
	@Override
	public Pair<Camera, List<GameObject>> toRender() {
		return new Pair<Camera, List<GameObject>>(this.camera, this.gameObjects.getRenderableObjects());
	}

	@Override
	public void cleanUp() {
		for(NonRenderableObject o : gameObjects.getAllObjects())
			o.cleanUp();
		//SATCL.cleanUp();
		//WorldCoords.cleanUp();
	}
	
	public void addObject(GameObject o) {
		this.gameObjects.spawnObject(o);
	}
	
	public void removeObject(GameObject o) {
		this.gameObjects.despawnObject(o);
	}
	
	private static GameLogic instance;
	
	public static GameLogic init() throws IOException {
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