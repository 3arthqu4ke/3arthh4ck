package me.earth.earthhack.impl.util.physics;

import com.jme3.bullet.PhysicsSoftSpace;
import com.jme3.math.Vector3f;

public class MinecraftSoftPhysicsSpace extends PhysicsSoftSpace
{

	/**
	 * Instantiate a MinecraftSoftPhysicsSpace with a sequential-impulse solver. Must be
	 * invoked on the designated physics thread.
	 *
	 * @param worldMin       the desired minimum coordinate values (not null,
	 *                       unaffected, default=(-10k,-10k,-10k))
	 * @param worldMax       the desired maximum coordinate values (not null,
	 *                       unaffected, default=(10k,10k,10k))
	 * @param broadphaseType which broadphase accelerator to use (not null)
	 */
	public MinecraftSoftPhysicsSpace(Vector3f worldMin, Vector3f worldMax, BroadphaseType broadphaseType)
	{
		super(worldMin, worldMax, broadphaseType);
	}

	public void tick()
	{

	}

}
