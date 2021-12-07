package me.earth.earthhack.impl.core.mixins.network.client;

import me.earth.earthhack.impl.core.ducks.network.ICPacketUseEntity;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CPacketUseEntity.class)
public abstract class MixinCPacketUseEntity implements ICPacketUseEntity
{
    private Entity entity;

    @Override
    @Accessor(value = "entityId")
    public abstract void setEntityId(int entityId);

    @Override
    @Accessor(value = "action")
    public abstract void setAction(CPacketUseEntity.Action action);

    @Override
    @Accessor(value = "hitVec")
    public abstract void setVec(Vec3d vec3d);

    @Override
    @Accessor(value = "hand")
    public abstract void setHand(EnumHand hand);

    @Override
    @Accessor(value = "entityId")
    public abstract int getEntityID();

    @Override
    @Accessor(value = "action")
    public abstract CPacketUseEntity.Action getAction();

    @Override
    @Accessor(value = "hitVec")
    public abstract Vec3d getHitVec();

    @Override
    @Accessor(value = "hand")
    public abstract EnumHand getHand();

    @Override
    public Entity getAttackedEntity()
    {
        return entity;
    }

    @Inject(
        method = "<init>(Lnet/minecraft/entity/Entity;)V",
        at = @At("RETURN"))
    public void initHook(Entity entity, CallbackInfo info)
    {
        this.entity = entity;
    }

}
