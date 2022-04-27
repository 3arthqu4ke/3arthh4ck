package me.earth.earthhack.impl.util.physics.gdx;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.*;
import com.badlogic.gdx.physics.bullet.dynamics.btSequentialImpulseConstraintSolver;
import com.badlogic.gdx.physics.bullet.linearmath.btIDebugDraw;
import com.badlogic.gdx.physics.bullet.softbody.*;
import me.earth.earthhack.impl.util.math.geocache.Sphere;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

/**
 * Java-esque design doesn't really apply here because the libgdx bindings are very close to the native library
 * For that reason, we have this stupid wrapper class that neatly encapsulates all of the world stuff we need
 * @author megyn
 */
public class EntityLocalPhysicsWorld
{

	private static final BlockPos.MutableBlockPos MUTABLE_POS = new BlockPos.MutableBlockPos();

	private final btSoftRigidDynamicsWorld world;
	private final btSoftBodyWorldInfo worldInfo;
	private final Entity entity;

	public EntityLocalPhysicsWorld(World world, Entity entity)
	{
		this.entity = entity;
		float posX = (float) entity.posX;
		float posY = (float) entity.posY;
		float posZ = (float) entity.posZ;

		btDefaultCollisionConfiguration collisionConfiguration = new btSoftBodyRigidBodyCollisionConfiguration();
		btCollisionDispatcher dispatcher = new btCollisionDispatcher(collisionConfiguration);
		// When creating a btSoftRigidDynamicsWorld, the two vectors passed here will be the minimum and maximum coordinates, respectively.
		btAxisSweep3 broadphase = new btAxisSweep3(new Vector3(posX - 5, posY - 5, posZ - 5), new Vector3(posX + 5, posY + 5, posZ + 5), 1024);
		btSequentialImpulseConstraintSolver solver = new btSequentialImpulseConstraintSolver();
		this.world = new btSoftRigidDynamicsWorld(dispatcher, broadphase, solver, collisionConfiguration);

		btCompoundShape shape = new btCompoundShape();
		int radius = Sphere.getRadius(6.0);
		for (int i = 0; i < radius; i++)
		{
			Vec3i vec = Sphere.get(i);
			MUTABLE_POS.setPos(vec);
			if (world.getBlockState(MUTABLE_POS).getBlock() != Blocks.AIR)
			{
				btBoxShape box = new btBoxShape(new Vector3(0.5f, 0.5f, 0.5f));
				// the center of a block is the given position with 0.5f added to each value
				// TODO: get dimensions from block model! (easy!)
				shape.addChildShape(new Matrix4().translate(vec.getX() + 0.5f, vec.getY() + 0.5f, vec.getZ() + 0.5f), box);
			}
		}
		btCollisionObject object = new btCollisionObject();
		object.setCollisionShape(shape);
		this.world.addCollisionObject(object); // this represents terrain
		this.world.setGravity(new Vector3(0, 9.81f, 0));

		worldInfo = new btSoftBodyWorldInfo();
		worldInfo.setBroadphase(broadphase);
		worldInfo.setDispatcher(dispatcher);
		worldInfo.getSparsesdf().Initialize();

		// TODO: cape uvs
		AxisAlignedBB bb = entity.getEntityBoundingBox();
		Vector3 vector00 = new Vector3((float) bb.minX, (float) bb.minY, (float) bb.minZ),
				vector01 = new Vector3((float) bb.minX, (float) bb.maxY, (float) bb.minZ),
				vector10 = new Vector3((float) bb.maxX, (float) bb.minY, (float) bb.minZ),
				vector11 = new Vector3((float) bb.minX, (float) bb.maxY, (float) bb.minZ);
		btSoftBody body = btSoftBodyHelpers.CreatePatch(worldInfo, vector00, vector10, vector01, vector11, 15, 15, 15, false);
		body.takeOwnership();
		body.setTotalMass(50);

		btIDebugDraw drawer = new btIDebugDraw();
		this.world.setDebugDrawer(drawer);
		this.world.addSoftBody(body);
	}

}
