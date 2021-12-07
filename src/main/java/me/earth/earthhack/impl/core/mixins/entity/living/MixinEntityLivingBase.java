package me.earth.earthhack.impl.core.mixins.entity.living;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.impl.core.ducks.entity.IEntityLivingBase;
import me.earth.earthhack.impl.core.ducks.entity.IEntityNoInterp;
import me.earth.earthhack.impl.core.ducks.entity.IEntityRemoteAttack;
import me.earth.earthhack.impl.core.mixins.entity.MixinEntity;
import me.earth.earthhack.impl.event.events.misc.DeathEvent;
import me.earth.earthhack.impl.event.events.movement.LiquidJumpEvent;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.misc.nointerp.NoInterp;
import me.earth.earthhack.impl.modules.movement.elytraflight.ElytraFlight;
import me.earth.earthhack.impl.modules.movement.elytraflight.mode.ElytraMode;
import me.earth.earthhack.impl.modules.player.fasteat.FastEat;
import me.earth.earthhack.impl.modules.player.fasteat.mode.FastEatMode;
import me.earth.earthhack.impl.modules.player.spectate.Spectate;
import me.earth.earthhack.impl.util.minecraft.ICachedDamage;
import me.earth.earthhack.impl.util.minecraft.MotionTracker;
import me.earth.earthhack.impl.util.thread.EnchantmentUtil;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityLivingBase.class)
public abstract class MixinEntityLivingBase extends MixinEntity
        implements IEntityLivingBase, IEntityNoInterp,
                        ICachedDamage, IEntityRemoteAttack
{
    private static final ModuleCache<ElytraFlight> ELYTRA_FLIGHT =
            Caches.getModule(ElytraFlight.class);
    private static final ModuleCache<FastEat> FAST_EAT =
            Caches.getModule(FastEat.class);
    private static final ModuleCache<NoInterp> NOINTERP =
            Caches.getModule(NoInterp.class);
    private static final ModuleCache<Spectate> SPECTATE =
            Caches.getModule(Spectate.class);

    @Shadow
    @Final
    private static DataParameter<Float> HEALTH;
    @Shadow
    public float moveStrafing;
    @Shadow
    public float moveForward;
    @Shadow
    protected int activeItemStackUseCount;
    @Shadow
    protected ItemStack activeItemStack;

    protected double noInterpX;
    protected double noInterpY;
    protected double noInterpZ;
    protected int noInterpPositionIncrements;
    protected float noInterpPrevSwing;
    protected float noInterpSwingAmount;
    protected float noInterpSwing;
    protected float lowestDura = Float.MAX_VALUE;
    protected boolean noInterping = true;

    protected int armorValue = Integer.MAX_VALUE;
    protected float armorToughness = Float.MAX_VALUE;
    protected int explosionModifier = Integer.MAX_VALUE;

    @Shadow
    public abstract IAttributeInstance getEntityAttribute(IAttribute attribute);

    @Shadow
    public abstract int getTotalArmorValue();

    @Shadow
    public abstract Iterable<ItemStack> getArmorInventoryList();

    @Shadow
    public abstract boolean isServerWorld();

    @Override
    @Invoker(value = "getArmSwingAnimationEnd")
    public abstract int armSwingAnimationEnd();

    @Override
    @Accessor(value = "ticksSinceLastSwing")
    public abstract int getTicksSinceLastSwing();

    @Override
    @Accessor(value = "activeItemStackUseCount")
    public abstract int getActiveItemStackUseCount();

    @Override
    @Accessor(value = "ticksSinceLastSwing")
    public abstract void setTicksSinceLastSwing(int ticks);

    @Override
    @Accessor(value = "activeItemStackUseCount")
    public abstract void setActiveItemStackUseCount(int count);

    @Override
    public boolean getElytraFlag()
    {
        return this.getFlag(7);
    }

    @Override
    public double getNoInterpX()
    {
        return isNoInterping() ? noInterpX : posX;
    }

    @Override
    public double getNoInterpY()
    {
        return isNoInterping() ? noInterpY: posY;
    }

    @Override
    public double getNoInterpZ()
    {
        return isNoInterping() ? noInterpZ : posZ;
    }

    @Override
    public void setNoInterpX(double x)
    {
        this.noInterpX = x;
    }

    @Override
    public void setNoInterpY(double y)
    {
        this.noInterpY = y;
    }

    @Override
    public void setNoInterpZ(double z)
    {
        this.noInterpZ = z;
    }

    @Override
    public int getPosIncrements()
    {
        return noInterpPositionIncrements;
    }

    @Override
    public void setPosIncrements(int posIncrements)
    {
        this.noInterpPositionIncrements = posIncrements;
    }

    @Override
    public float getNoInterpSwingAmount()
    {
        return noInterpSwingAmount;
    }

    @Override
    public float getNoInterpSwing()
    {
        return noInterpSwing;
    }

    @Override
    public float getNoInterpPrevSwing()
    {
        return noInterpPrevSwing;
    }

    @Override
    public void setNoInterpSwingAmount(float noInterpSwingAmount)
    {
        this.noInterpSwingAmount = noInterpSwingAmount;
    }

    @Override
    public void setNoInterpSwing(float noInterpSwing)
    {
        this.noInterpSwing = noInterpSwing;
    }

    @Override
    public void setNoInterpPrevSwing(float noInterpPrevSwing)
    {
        this.noInterpPrevSwing = noInterpPrevSwing;
    }

    @Override
    public boolean isNoInterping()
    {
        EntityPlayerSP player = mc.player;
        return !this.isRiding()
                && noInterping
                && (player == null || !player.isRiding());
    }

    @Override
    public void setNoInterping(boolean noInterping)
    {
        this.noInterping = noInterping;
    }

    @Override
    public void setLowestDura(float lowest)
    {
        this.lowestDura = lowest;
    }

    @Override
    public float getLowestDurability()
    {
        return lowestDura;
    }

    @Override
    public int getArmorValue()
    {
        return shouldCache()
                ? armorValue
                : this.getTotalArmorValue();
    }

    @Override
    public float getArmorToughness()
    {
        return shouldCache()
                ? armorToughness
                : (float) this
                    .getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS)
                    .getAttributeValue();
    }

    @Override
    public int getExplosionModifier(DamageSource source)
    {
        return shouldCache()
                ? explosionModifier
                : EnchantmentUtil.getEnchantmentModifierDamage(
                        this.getArmorInventoryList(), source);
    }

    @Redirect(
        method = "attackEntityFrom",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/world/World;isRemote:Z"))
    private boolean isRemoteHook(World world)
    {
        if (world.isRemote)
        {
            return !shouldRemoteAttack();
        }

        return false;
    }

    @Redirect(
        method = "onUpdate",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/entity/EntityLivingBase;posX:D"))
    private double posXHookOnUpdate(EntityLivingBase base)
    {
        if (NoInterp.update(NOINTERP.get(), base))
        {
            return ((IEntityNoInterp) base).getNoInterpX();
        }

        return base.posX;
    }

    @Redirect(
        method = "onUpdate",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/entity/EntityLivingBase;posZ:D"))
    private double posZHookOnUpdate(EntityLivingBase base)
    {
        if (NOINTERP.isEnabled()
                && base instanceof IEntityNoInterp
                && ((IEntityNoInterp) base).isNoInterping()
                && NOINTERP.get().isSilent())
        {
            return ((IEntityNoInterp) base).getNoInterpZ();
        }

        return base.posZ;
    }

    @Inject(method = "isElytraFlying", at = @At("HEAD"), cancellable = true)
    private void isElytraFlyingHook(CallbackInfoReturnable<Boolean> info)
    {
        //noinspection ConstantConditions
        if (EntityPlayerSP.class.isInstance(this)
                && ELYTRA_FLIGHT.isEnabled()
                && ELYTRA_FLIGHT.get().getMode() == ElytraMode.Packet)
        {
            info.setReturnValue(false);
        }
    }

    @Inject(
        method = "notifyDataManagerChange",
        at = @At("RETURN"))
    public void notifyDataManagerChangeHook(DataParameter<?> key,
                                            CallbackInfo info)
    {
        if (key.equals(HEALTH)
                && this.dataManager.get(HEALTH) <= 0.0
                && this.world != null
                && this.world.isRemote)
        {
            Bus.EVENT_BUS.post(new DeathEvent(
                    EntityLivingBase.class.cast(this)));
        }
    }

    @Inject(
        method = "handleJumpWater",
        at = @At("HEAD"),
        cancellable = true)
    private void handleJumpWaterHook(CallbackInfo info)
    {
        LiquidJumpEvent event =
                new LiquidJumpEvent(EntityLivingBase.class.cast(this));

        Bus.EVENT_BUS.post(event);
        if (event.isCancelled())
        {
            info.cancel();
        }
    }

    @Inject(
        method = "handleJumpLava",
        at = @At("HEAD"),
        cancellable = true)
    private void handleJumpLavaHook(CallbackInfo info)
    {
        LiquidJumpEvent event =
                new LiquidJumpEvent(EntityLivingBase.class.cast(this));

        Bus.EVENT_BUS.post(event);
        if (event.isCancelled())
        {
            info.cancel();
        }
    }

    @Redirect(
        method = "onItemUseFinish",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/EntityLivingBase;resetActiveHand()V"))
    private void resetActiveHandHook(EntityLivingBase base)
    {
        if (world.isRemote
                && FAST_EAT.isEnabled()
                && base instanceof EntityPlayerSP
                && !mc.isSingleplayer()
                && FAST_EAT.get().getMode() == FastEatMode.NoDelay
                && this.activeItemStack.getItem() instanceof ItemFood)
        {
            this.activeItemStackUseCount = 0;
            ((EntityPlayerSP) base).connection
                .sendPacket(new CPacketPlayerTryUseItem(base.getActiveHand()));
        }
        else
        {
            base.resetActiveHand();
        }
    }

    @Inject(method = "swingArm", at = @At("HEAD"))
    private void swingArmHook(EnumHand hand, CallbackInfo ci)
    {
        //noinspection ConstantConditions
        if (EntityPlayerSP.class.isInstance(this) && SPECTATE.isEnabled())
        {
            EntityPlayer player = SPECTATE.get().getFake();
            if (player != null)
            {
                player.swingArm(hand);
            }
        }
    }

    @Inject(
        method = "setPositionAndRotationDirect",
        at = @At("RETURN"))
    private void setPositionAndRotationDirectHook(double x,
                                                  double y,
                                                  double z,
                                                  float yaw,
                                                  float pitch,
                                                  int posRotationIncrements,
                                                  boolean teleport,
                                                  CallbackInfo ci)
    {
        if (NOINTERP.isEnabled())
        {
            NoInterp.handleNoInterp(NOINTERP.get(),
                                    Entity.class.cast(this),
                                    posRotationIncrements,
                                    x,
                                    y,
                                    z,
                                    yaw,
                                    pitch);
        }
    }

    @Redirect(
            method = "travel",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/EntityLivingBase;isServerWorld()Z"
            )
    )
    public boolean travelHook(EntityLivingBase entityLivingBase)
    {
        return isServerWorld() || entityLivingBase instanceof MotionTracker;
    }

}
