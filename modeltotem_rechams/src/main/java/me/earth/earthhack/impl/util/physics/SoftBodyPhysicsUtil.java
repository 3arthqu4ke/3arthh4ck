package me.earth.earthhack.impl.util.physics;

import com.jme3.bullet.PhysicsSoftSpace;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.infos.IndexedMesh;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.bullet.objects.PhysicsSoftBody;
import com.jme3.bullet.util.NativeSoftBodyUtil;
import com.jme3.math.Vector3f;
import me.earth.earthhack.impl.util.math.geocache.Sphere;
import me.earth.earthhack.impl.util.render.model.IModel;
import me.earth.earthhack.impl.util.render.model.Mesh;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SoftBodyPhysicsUtil
{

	private static final BlockPos.MutableBlockPos mPos = new BlockPos.MutableBlockPos();

	public static PhysicsSoftBody createSoftBodyPhysicsMeshFromModel(IModel model)
	{
		PhysicsSoftBody body = new PhysicsSoftBody();
		for (Mesh mesh : model.getMeshes())
		{
			Vector3f[] vectors = new Vector3f[mesh.getVertices().size()];
			for (int i = 0; i < vectors.length; i++)
			{
				org.joml.Vector3f vector = mesh.getVerticesAsVector().get(i);
				vectors[i] = new Vector3f(vector.x, vector.y, vector.z);
			}
			IndexedMesh indexedMesh = new IndexedMesh(vectors, mesh.getIndices().array());
			NativeSoftBodyUtil.appendFromNativeMesh(indexedMesh, body);
		}
		return body;
	}

	/**
	 * Gets a soft space with rigid bodies representing entities and non-air blocks within a radius of 4 around the given origin in the given world
	 * @param world world to check for entities and non-air blocks within
	 * @param origin origin to start from
	 * @return new space that models the given world
	 */
	public static PhysicsSoftSpace getSoftSpaceFromWorld(World world, BlockPos origin)
	{
		PhysicsSoftSpace space = new PhysicsSoftSpace(new Vector3f(origin.getX() - 3, origin.getY() - 3, origin.getZ() - 3), new Vector3f(origin.getX() + 3, origin.getY() + 3, origin.getZ() + 3), PhysicsSpace.BroadphaseType.SIMPLE);
		int maxRadius = Sphere.getRadius(3);
		for (int i = 1; i < maxRadius; i++)
		{
			mPos.setPos(origin.add(Sphere.get(i)));
			if (world.getBlockState(mPos).getBlock() != Blocks.AIR)
			{
				BoxCollisionShape shape = new BoxCollisionShape(0.5f);
				shape.setMargin(0.001f);
				PhysicsRigidBody body = new PhysicsRigidBody(shape);
				body.setMass(0.0f);
				body.setGravity(new Vector3f(0.0f, 0.0f, 0.0f));
				space.add(body);
			}
		}



		return space;
	}

	public static PhysicsSoftSpace updateSoftSpace(World world, BlockPos origin, PhysicsSoftSpace space)
	{
		for (PhysicsRigidBody body : space.getRigidBodyList())
		{

		}
		return null;
	}

	public static PhysicsSoftBody createClothBody(IndexedMesh mesh)
	{
		PhysicsSoftBody body = new PhysicsSoftBody();
		NativeSoftBodyUtil.appendFromNativeMesh(mesh, body);
		body.getSoftMaterial().setAngularStiffness(0.0f); // cloth does not have angular stiffness
		// body.copyFaces()

		return body;
	}

}
