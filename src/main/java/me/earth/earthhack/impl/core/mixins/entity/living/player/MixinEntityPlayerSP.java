package me.earth.earthhack.impl.core.mixins.entity.living.player;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.cache.SettingCache;
import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.impl.core.ducks.entity.IEntityPlayerSP;
import me.earth.earthhack.impl.event.events.misc.UpdateEvent;
import me.earth.earthhack.impl.event.events.movement.BlockPushEvent;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.event.events.network.PreMotionUpdateEvent;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.rotationbypass.Compatibility;
import me.earth.earthhack.impl.modules.misc.portals.Portals;
import me.earth.earthhack.impl.modules.movement.autosprint.AutoSprint;
import me.earth.earthhack.impl.modules.movement.autosprint.mode.SprintMode;
import me.earth.earthhack.impl.modules.movement.elytraflight.ElytraFlight;
import me.earth.earthhack.impl.modules.movement.elytraflight.mode.ElytraMode;
import me.earth.earthhack.impl.modules.player.spectate.Spectate;
import me.earth.earthhack.impl.modules.player.xcarry.XCarry;
import me.earth.earthhack.impl.util.minecraft.MovementUtil;
import me.earth.earthhack.pingbypass.PingBypass;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovementInput;
import net.minecraft.util.math.AxisAlignedBB;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityPlayerSP.class)
public abstract class MixinEntityPlayerSP extends MixinAbstractClientPlayer
        implements IEntityPlayerSP
{
    private static final ModuleCache<Spectate> SPECTATE =
        Caches.getModule(Spectate.class);
    private static final ModuleCache<ElytraFlight> ELYTRA_FLIGHT =
        Caches.getModule(ElytraFlight.class);
    private static final ModuleCache<AutoSprint> SPRINT =
        Caches.getModule(AutoSprint.class);
    private static final ModuleCache<XCarry> XCARRY =
        Caches.getModule(XCarry.class);
    private static final ModuleCache<Portals> PORTALS =
        Caches.getModule(Portals.class);
    private static final SettingCache<Boolean, BooleanSetting, Portals> CHAT =
        Caches.getSetting(Portals.class, BooleanSetting.class, "Chat", true);
    private static final ModuleCache<Compatibility> ROTATION_BYPASS =
        Caches.getModule(Compatibility.class);

    @Shadow
    public MovementInput movementInput;
    @Shadow
    @Final
    public NetHandlerPlayClient connection;

    private final Minecraft mc = Minecraft.getMinecraft();
    private MotionUpdateEvent.Riding riding;
    private MotionUpdateEvent motionEvent = new MotionUpdateEvent();

    @Override
    @Accessor(value = "lastReportedPosX")
    public abstract double getLastReportedX();

    @Override
    @Accessor(value = "lastReportedPosY")
    public abstract double getLastReportedY();

    @Override
    @Accessor(value = "lastReportedPosZ")
    public abstract double getLastReportedZ();

    @Override
    @Accessor(value = "lastReportedYaw")
    public abstract float getLastReportedYaw();

    @Override
    @Accessor(value = "lastReportedPitch")
    public abstract float getLastReportedPitch();

    @Override
    @Accessor(value = "prevOnGround")
    public abstract boolean getLastOnGround();

    @Override
    @Accessor(value = "lastReportedPosX")
    public abstract void setLastReportedX(double x);

    @Override
    @Accessor(value = "lastReportedPosY")
    public abstract void setLastReportedY(double y);

    @Override
    @Accessor(value = "lastReportedPosZ")
    public abstract void setLastReportedZ(double z);

    @Override
    @Accessor(value = "lastReportedYaw")
    public abstract void setLastReportedYaw(float yaw);

    @Override
    @Accessor(value = "lastReportedPitch")
    public abstract void setLastReportedPitch(float pitch);

    @Override
    @Accessor(value = "positionUpdateTicks")
    public abstract int getPositionUpdateTicks();

    @Override
    @Accessor(value = "horseJumpPower")
    public abstract void setHorseJumpPower(float jumpPower);

    @Override
    public void superUpdate()
    {
        super.onUpdate();
    }

    @Override
    public void invokeUpdateWalkingPlayer()
    {
        this.onUpdateWalkingPlayer();
    }

    @Override
    public boolean isNoInterping()
    {
        return false;
    }

    @Shadow
    protected abstract void onUpdateWalkingPlayer();

    /**
     * target = {@link InventoryPlayer#setItemStack(ItemStack)}.
     */
    @Redirect(
        method = "closeScreenAndDropStack",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/player/InventoryPlayer;" +
                     "setItemStack(Lnet/minecraft/item/ItemStack;)V"))
    public void setItemStackHook(InventoryPlayer inventory, ItemStack stack)
    {
        if (!XCARRY.isEnabled() || !(mc.currentScreen instanceof GuiInventory))
        {
            inventory.setItemStack(stack);
        }
    }

    @Redirect(
        method = "onLivingUpdate",
        at = @At(
            value = "INVOKE",
            target = "net/minecraft/client/entity/EntityPlayerSP.isElytraFlying()Z"))
    public boolean onLivingUpdateHook(EntityPlayerSP player)
    {
        return ELYTRA_FLIGHT.isEnabled()
                && ELYTRA_FLIGHT.get().getMode() == ElytraMode.Packet
                || player.isElytraFlying();
    }

    @ModifyArg(
        method = "setSprinting",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/entity/AbstractClientPlayer;setSprinting(Z)V"))
    public boolean setSprintingHook(boolean sprinting)
    {
        if (SPRINT.isEnabled() && AutoSprint.canSprintBetter() && (SPRINT.get().getMode() == SprintMode.Rage && MovementUtil.isMoving()))
        {
            return true;
        }

        return sprinting;
    }

    @Inject(
        method = "onUpdate",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/entity/EntityPlayerSP;rotationYaw:F",
            shift = At.Shift.BEFORE),
        cancellable = true)
    public void ridingHook_1(CallbackInfo info)
    {
        this.riding = new MotionUpdateEvent.Riding(
            Stage.PRE,
            this.posX,
            this.getEntityBoundingBox().minY,
            this.posZ,
            this.rotationYaw,
            this.rotationPitch,
            this.onGround,
            this.moveStrafing,
            this.moveForward,
            this.movementInput.jump,
            this.movementInput.sneak);

        if (!PingBypass.isConnected())
        {
            Bus.EVENT_BUS.post(this.riding);
        }

        if (this.riding.isCancelled())
        {
            info.cancel();
        }
    }

    @Redirect(
        method = "onUpdate",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/entity/EntityPlayerSP;rotationYaw:F"))
    public float ridingHook_2(EntityPlayerSP player)
    {
        return this.riding.getYaw();
    }

    @Redirect(
        method = "onUpdate",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/entity/EntityPlayerSP;rotationPitch:F"))
    public float ridingHook_3(EntityPlayerSP player)
    {
        return this.riding.getPitch();
    }

    @Redirect(
        method = "onUpdate",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/entity/EntityPlayerSP;onGround:Z"))
    public boolean ridingHook_4(EntityPlayerSP player)
    {
        return this.riding.isOnGround();
    }

    @Redirect(
        method = "onUpdate",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/entity/EntityPlayerSP;moveStrafing:F"))
    public float ridingHook_5(EntityPlayerSP player)
    {
        return this.riding.getMoveStrafing();
    }

    @Redirect(
        method = "onUpdate",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/entity/EntityPlayerSP;moveForward:F"))
    public float ridingHook_6(EntityPlayerSP player)
    {
        return this.riding.getMoveForward();
    }

    @Redirect(
        method = "onUpdate",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/util/MovementInput;jump:Z"))
    public boolean ridingHook_7(MovementInput input)
    {
        return this.riding.isOnGround();
    }

    @Redirect(
        method = "onUpdate",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/util/MovementInput;sneak:Z"))
    public boolean ridingHook_8(MovementInput input)
    {
        return this.riding.getSneak();
    }

    @Inject(
        method = "onUpdate",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/network/NetHandlerPlayClient;sendPacket(Lnet/minecraft/network/Packet;)V",
            ordinal = 2,
            shift = At.Shift.BY,
            by = 2)) // Inject after the If-Statement
    public void ridingHook_9(CallbackInfo info)
    {
        if (!PingBypass.isConnected())
        {
            Bus.EVENT_BUS.post(new MotionUpdateEvent.Riding(Stage.POST, riding));
        }
    }

    /**
     * {@link AbstractClientPlayer#onUpdate()}
     */
    @Inject(
        method = "onUpdate",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/entity/AbstractClientPlayer;" +
                     "onUpdate()V",
            shift = At.Shift.BEFORE))
    public void onUpdateHook(CallbackInfo info)
    {
        Bus.EVENT_BUS.post(new UpdateEvent());
    }

    @Inject(
        method = "onUpdate",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/entity/EntityPlayerSP;onUpdateWalkingPlayer()V",
            shift = At.Shift.BEFORE))
    public void onUpdateWalkingPlayerPre(CallbackInfo ci)
    {
        Bus.EVENT_BUS.post(new PreMotionUpdateEvent());
        if (ROTATION_BYPASS.isEnabled())
        {
            motionEvent = new MotionUpdateEvent(Stage.PRE,
                                                this.posX,
                                                this.getEntityBoundingBox().minY,
                                                this.posZ,
                                                this.rotationYaw,
                                                this.rotationPitch,
                                                this.onGround);
            if (!PingBypass.isConnected())
            {
                Bus.EVENT_BUS.post(motionEvent);
                posX = motionEvent.getX();
                posY = motionEvent.getY();
                posZ = motionEvent.getZ();
                rotationYaw = motionEvent.getRotationYaw();
                rotationPitch = motionEvent.getRotationPitch();
                onGround = motionEvent.isOnGround();
            }
        }
    }

    @Inject(
        method = "onUpdateWalkingPlayer",
        at = @At(value = "HEAD"),
        cancellable = true)
    public void onUpdateWalkingPlayer_Head(CallbackInfo callbackInfo)
    {
        if (!ROTATION_BYPASS.isEnabled())
        {
            motionEvent = new MotionUpdateEvent(Stage.PRE,
                                                this.posX,
                                                this.getEntityBoundingBox().minY,
                                                this.posZ,
                                                this.rotationYaw,
                                                this.rotationPitch,
                                                this.onGround);
            if (!PingBypass.isConnected())
            {
                Bus.EVENT_BUS.post(motionEvent);
            }
        }

        if (motionEvent.isCancelled())
        {
            callbackInfo.cancel();
        }
    }

    @Inject(
        method = "onUpdate",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/entity/EntityPlayerSP;onUpdateWalkingPlayer()V",
            shift = At.Shift.AFTER))
    public void onUpdateWalkingPlayerPost(CallbackInfo ci)
    {
        if (ROTATION_BYPASS.isEnabled() && !ROTATION_BYPASS.returnIfPresent(
            Compatibility::isShowingRotations, false)
            && !PingBypass.isConnected())
        {
            // maybe someone else changed our position in the meantime
            if (posX == motionEvent.getX())
            {
                posX = motionEvent.getInitialX();
            }

            if (posY == motionEvent.getY())
            {
                posY = motionEvent.getInitialY();
            }

            if (posZ == motionEvent.getZ())
            {
                posZ = motionEvent.getInitialZ();
            }

            if (rotationYaw == motionEvent.getRotationYaw())
            {
                rotationYaw = motionEvent.getInitialYaw();
            }

            if (rotationPitch == motionEvent.getRotationPitch())
            {
                rotationPitch = motionEvent.getInitialPitch();
            }

            if (onGround == motionEvent.isOnGround())
            {
                onGround = motionEvent.isInitialOnGround();
            }
        }
    }

    @Redirect(
        method = "onUpdateWalkingPlayer",
        at = @At(
            value = "FIELD",
            target = "net/minecraft/client/entity/EntityPlayerSP.posX:D"))
    public double posXHook(EntityPlayerSP entityPlayerSP)
    {
        return motionEvent.getX();
    }

    @Redirect(
        method = "onUpdateWalkingPlayer",
        at = @At(
            value = "FIELD",
            target = "net/minecraft/util/math/AxisAlignedBB.minY:D"))
    public double minYHook(AxisAlignedBB axisAlignedBB)
    {
        return motionEvent.getY();
    }

    @Redirect(
        method = "onUpdateWalkingPlayer",
        at = @At(
            value = "FIELD",
            target = "net/minecraft/client/entity/EntityPlayerSP.posZ:D"))
    public double posZHook(EntityPlayerSP entityPlayerSP)
    {
        return motionEvent.getZ();
    }

    @Redirect(
        method = "onUpdateWalkingPlayer",
        at = @At(
            value = "FIELD",
            target = "net/minecraft/client/entity/EntityPlayerSP.rotationYaw:F"))
    public float rotationYawHook(EntityPlayerSP entityPlayerSP)
    {
        return motionEvent.getYaw();
    }

    @Redirect(
        method = "onUpdateWalkingPlayer",
        at = @At(
            value = "FIELD",
            target = "net/minecraft/client/entity/EntityPlayerSP.rotationPitch:F"))
    public float rotationPitchHook(EntityPlayerSP entityPlayerSP)
    {
        return motionEvent.getPitch();
    }

    @Redirect(
        method = "onUpdateWalkingPlayer",
        at = @At(
            value = "FIELD",
            target = "net/minecraft/client/entity/EntityPlayerSP.onGround:Z"))
    public boolean onGroundHook(EntityPlayerSP entityPlayerSP)
    {
        return motionEvent.isOnGround();
    }

    @Inject(
        method = "onUpdateWalkingPlayer",
        at = @At(value = "RETURN"))
    public void onUpdateWalkingPlayer_Return(CallbackInfo callbackInfo)
    {
        if (!PingBypass.isConnected())
        {
            MotionUpdateEvent event = new MotionUpdateEvent(Stage.POST, motionEvent);
            event.setCancelled(motionEvent.isCancelled());
            Bus.EVENT_BUS.postReversed(event, null);
        }
    }

    @Inject(
        method = "pushOutOfBlocks",
        at = @At(value = "HEAD"),
        cancellable = true)
    public void pushOutOfBlocksHook(CallbackInfoReturnable<Boolean> cir)
    {
        BlockPushEvent event = new BlockPushEvent();
        Bus.EVENT_BUS.post(event);

        if (event.isCancelled())
        {
            cir.cancel();
        }
    }

    @Redirect(
        method = "onLivingUpdate",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiScreen;doesGuiPauseGame()Z",
            ordinal = 0))
    public boolean doesGuiPauseGameHook(GuiScreen guiScreen)
    {
        if (PORTALS.isEnabled() && CHAT.getValue())
        {
            return true;
        }

        return guiScreen.doesGuiPauseGame();
    }

    @Inject(
        method = "isCurrentViewEntity",
        at = @At("HEAD"),
        cancellable = true)
    public void isCurrentViewEntityHook(CallbackInfoReturnable<Boolean> cir)
    {
        if (!isSpectator() && SPECTATE.isEnabled())
        {
            cir.setReturnValue(true);
        }
    }

}
