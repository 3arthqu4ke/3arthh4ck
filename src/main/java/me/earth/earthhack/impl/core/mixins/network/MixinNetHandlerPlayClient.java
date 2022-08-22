package me.earth.earthhack.impl.core.mixins.network;

import com.mojang.authlib.GameProfile;
import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.cache.SettingCache;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.impl.core.ducks.network.INetHandlerPlayClient;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.management.Management;
import me.earth.earthhack.impl.modules.misc.packets.Packets;
import me.earth.earthhack.impl.util.thread.Locks;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.client.CPacketResourcePackStatus;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.network.play.server.SPacketResourcePackSend;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(NetHandlerPlayClient.class)
public abstract class MixinNetHandlerPlayClient implements INetHandlerPlayClient
{
    private static final SettingCache
        <Boolean, BooleanSetting, Management> ACTIVE =
        Caches.getSetting(Management.class, BooleanSetting.class, "MotionService", true);
    private static final ModuleCache<Packets> PACKETS =
            Caches.getModule(Packets.class);
    @Shadow
    @Final
    private NetworkManager netManager;

    @Override
    @Accessor(value = "doneLoadingTerrain")
    public abstract boolean isDoneLoadingTerrain();

    @Override
    @Accessor(value = "doneLoadingTerrain")
    public abstract void setDoneLoadingTerrain(boolean loaded);

    @Override
    @Accessor(value = "profile")
    public abstract void setGameProfile(GameProfile gameProfile);

    @Redirect(
        method = "handleEntityTeleport",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/Entity;" +
                    "setPositionAndRotationDirect(DDDFFIZ)V",
            ordinal = 0))
    public void setPositionAndRotationDirectHook(Entity entity,
                                                  double x,
                                                  double y,
                                                  double z,
                                                  float yaw,
                                                  float pitch,
                                                  int posRotationIncrements,
                                                  boolean teleport)
    {
        if (posRotationIncrements == 0
            && PACKETS.returnIfPresent(Packets::areMiniTeleportsActive, false))
        {
            entity.setPositionAndRotation(x, y, z, yaw, pitch);
        }
        else
        {
            entity.setPositionAndRotationDirect(x,
                                                y,
                                                z,
                                                yaw,
                                                pitch,
                                                posRotationIncrements,
                                                teleport);
        }
    }

    @Redirect(
        method = "handleTeams",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/scoreboard/Scoreboard;removeTeam(Lnet/minecraft/scoreboard/ScorePlayerTeam;)V"))
    public void getScoreboardHook(Scoreboard scoreboard, ScorePlayerTeam playerTeam)
    {
        if (scoreboard != null && playerTeam != null)
        {
            scoreboard.removeTeam(playerTeam);
        }
    }

    @Inject(
        method = "handleResourcePack",
        at = @At("HEAD"),
        cancellable = true)
    public void validateResourcePackHook(SPacketResourcePackSend packetIn,
                                          CallbackInfo ci)
    {
        //noinspection ConstantConditions
        if (packetIn.getURL() == null || packetIn.getHash() == null)
        {
            this.netManager.sendPacket(
                new CPacketResourcePackStatus(
                    CPacketResourcePackStatus.Action.FAILED_DOWNLOAD));
            ci.cancel();
        }
    }

    @Inject(
        method = "handlePlayerPosLook",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/network/NetworkManager;sendPacket(Lnet/minecraft/network/Packet;)V",
            ordinal = 1,
            shift = At.Shift.BEFORE))
    public void handlePlayerPosLookHook(SPacketPlayerPosLook packetIn, CallbackInfo ci)
    {
        Managers.ROTATION.setBlocking(true);
    }

    @Inject(
        method = "handlePlayerPosLook",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/network/NetworkManager;sendPacket(Lnet/minecraft/network/Packet;)V",
            ordinal = 1,
            shift = At.Shift.AFTER))
    public void handlePlayerPosLookHookPost(SPacketPlayerPosLook packetIn, CallbackInfo ci)
    {
        Managers.ROTATION.setBlocking(false);
    }

    @Redirect(
        method = "handleHeldItemChange",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/entity/player/InventoryPlayer;currentItem:I"))
    public void handleHeldItemChangeHook(InventoryPlayer inventoryPlayer, int value)
    {
        Locks.acquire(Locks.PLACE_SWITCH_LOCK,
                      () -> inventoryPlayer.currentItem = value);
    }

    @Inject(
        method = "handleEntityVelocity",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/Entity;setVelocity(DDD)V",
            shift = At.Shift.BEFORE),
        locals = LocalCapture.CAPTURE_FAILHARD,
        cancellable = true)
    public void setVelocityHook(SPacketEntityVelocity packetIn,
                                 CallbackInfo ci,
                                 Entity entity0)
    {
        if (ACTIVE.getValue()
            && entity0 instanceof EntityPlayer
            && !(entity0 instanceof EntityPlayerSP))
        {
            ci.cancel();
        }
    }

}
