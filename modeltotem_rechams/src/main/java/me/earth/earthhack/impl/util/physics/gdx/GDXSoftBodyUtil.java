package me.earth.earthhack.impl.util.physics.gdx;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btAxisSweep3;
import com.badlogic.gdx.physics.bullet.collision.btCollisionDispatcher;
import com.badlogic.gdx.physics.bullet.collision.btDefaultCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.dynamics.btSequentialImpulseConstraintSolver;
import com.badlogic.gdx.physics.bullet.softbody.btSoftBody;
import com.badlogic.gdx.physics.bullet.softbody.btSoftBodyRigidBodyCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.softbody.btSoftRigidDynamicsWorld;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;


/**
 * Utility methods for LibGDX Bullet Physics bindings
 */
public class GDXSoftBodyUtil
{

	// TODO
	public static btSoftRigidDynamicsWorld createSoftPhysicsWorld(World world, Vec3i center)
	{
		btDefaultCollisionConfiguration collisionConfiguration = new btSoftBodyRigidBodyCollisionConfiguration();
		btCollisionDispatcher dispatcher = new btCollisionDispatcher(collisionConfiguration);
		btAxisSweep3 broadphase = new btAxisSweep3(new Vector3(-1000, -1000, -1000), new Vector3(1000, 1000, 1000), 1024);
		btSequentialImpulseConstraintSolver solver = new btSequentialImpulseConstraintSolver();
		btSoftRigidDynamicsWorld dynamicsWorld = new btSoftRigidDynamicsWorld(dispatcher, broadphase, solver, collisionConfiguration);
		return dynamicsWorld;
	}

	public static btSoftBody getCapeBody()
	{

		// btSoftBody body = new btSoftBody(null);
		return null;
	}

	/*public static btSoftBody getSoftBodyFromMesh(Mesh mesh)
	{

	}*/

}
