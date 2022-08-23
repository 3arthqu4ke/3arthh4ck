package me.earth.earthhack.impl.modules.player.fakeplayer.util;

import com.mojang.authlib.GameProfile;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.core.ducks.entity.IEntityOtherPlayerMP;
import me.earth.earthhack.impl.core.ducks.entity.IEntityRemoteAttack;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityTracker;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

import java.util.function.BooleanSupplier;

public class EntityPlayerAttack extends EntityOtherPlayerMP
        implements Globals, IEntityOtherPlayerMP, IEntityRemoteAttack
{
    private BooleanSupplier remoteSupplier = () -> true;

    @SuppressWarnings("unused")
    public EntityPlayerAttack(World worldIn)
    {
        this(worldIn, mc.player.getGameProfile());
    }

    public EntityPlayerAttack(World worldIn, GameProfile gameProfileIn)
    {
        super(worldIn, gameProfileIn);
    }

    @Override
    public void onUpdate()
    {
        super.onUpdate();
        this.serverPosX = EntityTracker.getPositionLong(this.posX);
        this.serverPosY = EntityTracker.getPositionLong(this.posY);
        this.serverPosZ = EntityTracker.getPositionLong(this.posZ);
    }

    @Override
    protected void markVelocityChanged()
    {
        /* NOOP */
    }

    @Override
    public boolean shouldRemoteAttack()
    {
        return remoteSupplier.getAsBoolean();
    }

    @Override
    @SuppressWarnings("NullableProblems")
    public void knockBack(Entity entityIn, float strength,
                          double xRatio, double zRatio)
    {
        // NOOP
    }

    @Override
    public boolean returnFromSuperAttack(DamageSource source, float amount)
    {
        IEntityOtherPlayerMP.super.returnFromSuperAttack(source, amount);
        return true;
    }

    @Override
    public boolean shouldAttackSuper()
    {
        return true;
    }

    public void setRemoteSupplier(BooleanSupplier remoteSupplier)
    {
        this.remoteSupplier = remoteSupplier;
    }

}