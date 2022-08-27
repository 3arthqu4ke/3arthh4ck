package me.earth.earthhack.impl.core.mixins.entity;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.cache.SettingCache;
import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.core.ducks.entity.IEntity;
import me.earth.earthhack.impl.core.ducks.entity.IEntityNoInterp;
import me.earth.earthhack.impl.event.events.misc.ReachEvent;
import me.earth.earthhack.impl.event.events.movement.MoveEvent;
import me.earth.earthhack.impl.event.events.movement.OnGroundEvent;
import me.earth.earthhack.impl.event.events.movement.StepEvent;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.management.Management;
import me.earth.earthhack.impl.modules.misc.nointerp.NoInterp;
import me.earth.earthhack.impl.modules.movement.autosprint.AutoSprint;
import me.earth.earthhack.impl.modules.movement.autosprint.mode.SprintMode;
import me.earth.earthhack.impl.modules.movement.step.Step;
import me.earth.earthhack.impl.modules.movement.velocity.Velocity;
import me.earth.earthhack.impl.modules.render.norender.NoRender;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.minecraft.entity.EntityType;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Supplier;

@Mixin(Entity.class)
public abstract class MixinEntity implements IEntity, Globals
{
    private static final ModuleCache<NoRender>
            NO_RENDER = Caches.getModule(NoRender.class);
    private static final ModuleCache<AutoSprint>
        SPRINT = Caches.getModule(AutoSprint.class);
    private static final ModuleCache<Velocity>
        VELOCITY = Caches.getModule(Velocity.class);
    private static final ModuleCache<NoInterp>
        NOINTERP = Caches.getModule(NoInterp.class);
    private static final SettingCache<Boolean, BooleanSetting, Velocity>
        NO_PUSH = Caches.getSetting
            (Velocity.class, BooleanSetting.class, "NoPush", false);
    private static final SettingCache<Boolean, BooleanSetting, Step>
        STEP_COMP = Caches.getSetting
            (Step.class, BooleanSetting.class, "Compatibility", false);

    private static final SettingCache
        <Integer, NumberSetting<Integer>, Management> DEATH_TIME =
        Caches.getSetting(Management.class, Setting.class, "DeathTime", 500);

    @Shadow
    public double posX;
    @Shadow
    public double posY;
    @Shadow
    public double posZ;
    @Shadow
    public double motionX;
    @Shadow
    public double motionY;
    @Shadow
    public double motionZ;
    @Shadow
    public float rotationYaw;
    @Shadow
    public float rotationPitch;
    @Shadow
    public boolean onGround;
    @Shadow
    public World world;
    @Shadow
    public double prevPosX;
    @Shadow
    public double prevPosY;
    @Shadow
    public double prevPosZ;
    @Shadow
    public double lastTickPosX;
    @Shadow
    public double lastTickPosY;
    @Shadow
    public double lastTickPosZ;
    @Shadow
    protected EntityDataManager dataManager;
    @Shadow
    public float stepHeight;
    @Shadow
    public boolean isDead;
    @Shadow
    public float width;
    @Shadow
    public float prevRotationYaw;
    @Shadow
    public float prevRotationPitch;
    @Shadow
    public float height;

    @Unique
    private long oldServerX;
    @Unique
    private long oldServerY;
    @Unique
    private long oldServerZ;

    private final StopWatch pseudoWatch = new StopWatch();
    private MoveEvent moveEvent;
    private Float prevHeight;
    private Supplier<EntityType> type;
    private boolean pseudoDead;
    private long stamp;
    private boolean dummy;

    @Shadow
    public abstract AxisAlignedBB getEntityBoundingBox();
    @Shadow
    public abstract boolean isSneaking();
    @Shadow
    protected abstract boolean getFlag(int flag);
    @Shadow
    public abstract boolean equals(Object p_equals_1_);
    @Shadow
    protected abstract void setRotation(float yaw, float pitch);
    @Shadow
    public abstract boolean isRiding();

    @Shadow
    public boolean noClip;

    @Shadow
    public abstract void move(MoverType type, double x, double y,
                                      double z);
    @Shadow
    public abstract String getName();

    @Override
    @Accessor(value = "isInWeb")
    public abstract boolean inWeb();

    @Override
    public EntityType getType()
    {
        return type.get();
    }

    @Override
    public long getDeathTime()
    {
        // TODO!!!
        return 0;
    }

    @Override
    public void setOldServerPos(long x, long y, long z)
    {
        this.oldServerX = x;
        this.oldServerY = y;
        this.oldServerZ = z;
    }

    @Override
    public long getOldServerPosX()
    {
        return oldServerX;
    }

    @Override
    public long getOldServerPosY()
    {
        return oldServerY;
    }

    @Override
    public long getOldServerPosZ()
    {
        return oldServerZ;
    }

    @Override
    public boolean isPseudoDead()
    {
        if (pseudoDead
                && !isDead
                && pseudoWatch.passed(DEATH_TIME.getValue()))
        {
            pseudoDead = false;
        }

        return pseudoDead;
    }

    @Override
    public void setPseudoDead(boolean pseudoDead)
    {
        this.pseudoDead = pseudoDead;
        if (pseudoDead)
        {
            pseudoWatch.reset();
        }
    }

    @Override
    public StopWatch getPseudoTime()
    {
        return pseudoWatch;
    }

    @Override
    public long getTimeStamp()
    {
        return stamp;
    }

    @Override
    public boolean isDummy()
    {
        return dummy;
    }

    @Override
    public void setDummy(boolean dummy)
    {
        this.dummy = dummy;
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    public void ctrHook(CallbackInfo info)
    {
        this.type = EntityType.getEntityType(Entity.class.cast(this));
        this.stamp = System.currentTimeMillis();
    }

    @Inject(
        method = "createRunningParticles",
        at = @At("HEAD"),
        cancellable = true)
    public void createRunningParticlesHook(CallbackInfo ci)
    {
        //noinspection ConstantConditions
        if (EntityPlayerSP.class.isInstance(this)
                && SPRINT.isEnabled()
                && SPRINT.get().getMode() == SprintMode.Rage)
        {
            ci.cancel();
        }
    }

    @Inject(
        method = "move",
        at = @At("HEAD"),
        cancellable = true)
    public void moveEntityHook_Head(MoverType type,
                                    double x,
                                    double y,
                                    double z,
                                    CallbackInfo ci)
    {
        //noinspection ConstantConditions
        if (EntityPlayerSP.class.isInstance(this))
        {
            this.moveEvent = new MoveEvent(type, x, y, z, this.isSneaking());
            Bus.EVENT_BUS.post(this.moveEvent);
            if (moveEvent.isCancelled()) {
                ci.cancel();
            }
        }
    }

    @ModifyVariable(
        method = "move",
        at = @At(
            value = "HEAD"),
        ordinal = 0)
    private double setX(double x)
    {
        return this.moveEvent != null ? this.moveEvent.getX() : x;
    }

    @ModifyVariable(
        method = "move",
        at = @At("HEAD"),
        ordinal = 1)
    private double setY(double y)
    {
        return this.moveEvent != null ? this.moveEvent.getY() : y;
    }

    @ModifyVariable(
        method = "move",
        at = @At("HEAD"),
        ordinal = 2)
    private double setZ(double z)
    {
        return this.moveEvent != null ? this.moveEvent.getZ() : z;
    }

    @Redirect(
        method = "move",
        at = @At(
            value = "INVOKE",
            target = "net/minecraft/entity/Entity.isSneaking()Z",
            ordinal = 0))
    public boolean isSneakingHook(Entity entity)
    {
        return this.moveEvent != null
                    ? this.moveEvent.isSneaking()
                    : entity.isSneaking();
    }

    @Inject(
        method = "move",
        at = @At(
                value = "FIELD",
                target = "net/minecraft/entity/Entity.onGround:Z",
                ordinal = 1))
    public void onGroundHook(MoverType type,
                              double x,
                              double y,
                              double z,
                              CallbackInfo info)
    {
        //noinspection ConstantConditions
        if (EntityPlayerSP.class.isInstance(this) && !STEP_COMP.getValue()) {
            StepEvent event = new StepEvent(Stage.PRE,
                                            this.getEntityBoundingBox(),
                                            this.stepHeight);
            Bus.EVENT_BUS.post(event);
            this.prevHeight = this.stepHeight;
            this.stepHeight = event.getHeight();
        }
    }

    @Inject(
        method = "move",
        at = @At(
            value = "FIELD",
            target = "net/minecraft/entity/Entity.stepHeight:F",
            ordinal = 3,
            shift = At.Shift.BEFORE))
    public void onGroundHookComp(MoverType type,
                                  double x,
                                  double y,
                                  double z,
                                  CallbackInfo info) {
        //noinspection ConstantConditions
        if (EntityPlayerSP.class.isInstance(this) && STEP_COMP.getValue()) {
            StepEvent event = new StepEvent(Stage.PRE,
                                            this.getEntityBoundingBox(),
                                            this.stepHeight);
            Bus.EVENT_BUS.post(event);
            this.prevHeight = this.stepHeight;
            this.stepHeight = event.getHeight();
        }
    }

    @Inject(
        method = "move",
        at = @At(
            value = "FIELD",
            target = "net/minecraft/entity/Entity.onGround:Z",
            ordinal = 2,
            shift = At.Shift.AFTER))
    public void onGroundHook2(MoverType type,
                               double x,
                               double y,
                               double z,
                               CallbackInfo info)
    {
        //noinspection ConstantConditions
        if (EntityPlayerSP.class.isInstance(this))
        {
            OnGroundEvent event = new OnGroundEvent();
            Bus.EVENT_BUS.post(event);
            this.onGround = this.onGround || event.isCancelled();
        }
    }

    /**
     * target = {@link Entity#setEntityBoundingBox(AxisAlignedBB)}
     */
    @Inject(
        method = "move",
        at = @At(
            value = "INVOKE",
            target = "net/minecraft/entity/Entity.setEntityBoundingBox"
                     + "(Lnet/minecraft/util/math/AxisAlignedBB;)V",
            ordinal = 7,
            shift = At.Shift.AFTER))
    public void setEntityBoundingBoxHook(MoverType type,
                                          double x,
                                          double y,
                                          double z,
                                          CallbackInfo info)
    {
        //noinspection ConstantConditions
        if (EntityPlayerSP.class.isInstance(this) && !STEP_COMP.getValue())
        {
            StepEvent event = new StepEvent(Stage.POST,
                                            this.getEntityBoundingBox(),
                                            this.prevHeight != null
                                                    ? this.prevHeight
                                                    : 0.0F);
            Bus.EVENT_BUS.postReversed(event, null);
        }
    }

    @Inject(
        method = "move",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/profiler/Profiler;endSection()V"))
    public void stepCompHook(MoverType type, double x, double y, double z, CallbackInfo ci)
    {
        //noinspection ConstantConditions
        if (EntityPlayerSP.class.isInstance(this) && STEP_COMP.getValue())
        {
            StepEvent event = new StepEvent(Stage.POST,
                                            this.getEntityBoundingBox(),
                                            this.prevHeight != null
                                                ? this.prevHeight
                                                : 0.0F);
            Bus.EVENT_BUS.postReversed(event, null);
        }
    }

    @Inject(method = "setPositionAndRotation", at = @At("RETURN"))
    public void setPositionAndRotationHook(double x,
                                            double y,
                                            double z,
                                            float yaw,
                                            float pitch,
                                            CallbackInfo ci)
    {
        if (this instanceof IEntityNoInterp)
        {
            ((IEntityNoInterp) this).setNoInterpX(x);
            ((IEntityNoInterp) this).setNoInterpY(y);
            ((IEntityNoInterp) this).setNoInterpZ(z);
        }
    }

    @Inject(
        method = "move",
        at = @At(
            value = "INVOKE",
            target = "net/minecraft/entity/Entity.resetPositionToBB()V",
            ordinal = 1))
    public void resetPositionToBBHook(MoverType type,
                                       double x,
                                       double y,
                                       double z,
                                       CallbackInfo info)
    {
        //noinspection ConstantConditions
        if (EntityPlayerSP.class.isInstance(this) && this.prevHeight != null)
        {
            this.stepHeight = this.prevHeight;
            this.prevHeight = null;
        }
    }

    @Inject(
        method = "move",
        at = @At("RETURN"))
    public void moveEntityHook_Return(MoverType type,
                                      double x,
                                      double y,
                                      double z,
                                      CallbackInfo info)
    {
        this.moveEvent = null;
    }

    @Inject(
        method = "getCollisionBorderSize",
        at = @At("RETURN"),
        cancellable = true)
    public void getCollisionBorderSizeHook(CallbackInfoReturnable<Float> info)
    {
        ReachEvent event = new ReachEvent(0.0f, info.getReturnValue());
        Bus.EVENT_BUS.post(event);

        if (event.isCancelled())
        {
            info.setReturnValue(event.getHitBox());
        }
    }

    @Redirect(
        method = "applyEntityCollision",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/Entity;addVelocity(DDD)V"))
    public void addVelocityHook(Entity entity, double x, double y, double z)
    {
        if (entity != null && (!VELOCITY.isEnabled()
                                || !NO_PUSH.getValue()
                                || !entity.equals(mc.player)))
        {
            entity.addVelocity(x, y, z);
        }
    }

    @Inject(method = "setDead", at = @At("RETURN"))
    public void setDeadHook(CallbackInfo ci)
    {
        if (NOINTERP.isPresent() && NOINTERP.get().shouldFixDeathJitter())
        {
            removeInterpolation();
            // schedule as well in case this was called on a different thread.
            mc.addScheduledTask(this::removeInterpolation);
        }
    }

    @Inject(method = "canRenderOnFire", at = @At("HEAD"), cancellable = true)
    public void canRenderOnFireHook(CallbackInfoReturnable<Boolean> cir)
    {
        if (NO_RENDER.isEnabled() && NO_RENDER.get().noEntityFire()) cir.setReturnValue(false);
    }

    private void removeInterpolation()
    {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        this.lastTickPosX = this.posX;
        this.lastTickPosY = this.posY;
        this.lastTickPosZ = this.posZ;

        this.prevHeight = this.height;

        this.prevRotationPitch = this.rotationPitch;
        this.prevRotationYaw = this.rotationYaw;

        if (this instanceof IEntityNoInterp)
        {
            ((IEntityNoInterp) this).setNoInterpX(this.posX);
            ((IEntityNoInterp) this).setNoInterpY(this.posY);
            ((IEntityNoInterp) this).setNoInterpZ(this.posZ);
        }
    }

}
