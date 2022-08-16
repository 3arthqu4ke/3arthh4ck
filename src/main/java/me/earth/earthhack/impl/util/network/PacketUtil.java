package me.earth.earthhack.impl.util.network;

import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.core.ducks.network.ICPacketUseEntity;
import me.earth.earthhack.impl.core.ducks.network.INetHandlerPlayClient;
import me.earth.earthhack.impl.core.mixins.network.IEnumConnectionState;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.pingbypass.PingBypass;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

import java.util.Set;

@SuppressWarnings("unused")
public class PacketUtil implements Globals
{
    public static Set<Class<? extends Packet<?>>> getAllPackets()
    {
        return ((IEnumConnectionState) EnumConnectionState.HANDSHAKING)
                        .getStatesByClass()
                        .keySet();
    }

    public static void handlePosLook(SPacketPlayerPosLook packetIn,
                                     Entity entity,
                                     boolean noRotate)
    {
        handlePosLook(packetIn, entity, noRotate, false);
    }

    public static void handlePosLook(SPacketPlayerPosLook packet,
                                     Entity entity,
                                     boolean noRotate,
                                     boolean event)
    {
        double x    = packet.getX();
        double y    = packet.getY();
        double z    = packet.getZ();
        float yaw   = packet.getYaw();
        float pitch = packet.getPitch();

        if (packet.getFlags().contains(SPacketPlayerPosLook.EnumFlags.X))
        {
            x += entity.posX;
        }
        else
        {
            entity.motionX = 0.0D;
        }

        if (packet.getFlags().contains(SPacketPlayerPosLook.EnumFlags.Y))
        {
            y += entity.posY;
        }
        else
        {
            entity.motionY = 0.0D;
        }

        if (packet.getFlags().contains(SPacketPlayerPosLook.EnumFlags.Z))
        {
            z += entity.posZ;
        }
        else
        {
            entity.motionZ = 0.0D;
        }

        if (packet.getFlags().contains(SPacketPlayerPosLook.EnumFlags.X_ROT))
        {
            pitch += entity.rotationPitch;
        }

        if (packet.getFlags().contains(SPacketPlayerPosLook.EnumFlags.Y_ROT))
        {
            yaw += entity.rotationYaw;
        }

        entity.setPositionAndRotation(x, y, z,
                noRotate ? entity.rotationYaw : yaw,
                noRotate ? entity.rotationPitch : pitch);

        Packet<?> confirm = new CPacketConfirmTeleport(packet.getTeleportId());
        CPacketPlayer posRot  = positionRotation(entity.posX,
                                                 entity.getEntityBoundingBox()
                                                       .minY,
                                                 entity.posZ,
                                                 yaw,
                                                 pitch,
                                                 false);

        if (event)
        {
            NetworkUtil.send(confirm);
            Managers.ROTATION.setBlocking(true);
            NetworkUtil.send(posRot);
            Managers.ROTATION.setBlocking(false);
        }
        else
        {
            NetworkUtil.sendPacketNoEvent(confirm);
            NetworkUtil.sendPacketNoEvent(posRot);
        }

        // might be called async
        mc.addScheduledTask(PacketUtil::loadTerrain);
    }

    public static void startDigging(BlockPos pos, EnumFacing facing)
    {
        mc.player.connection.sendPacket(new CPacketPlayerDigging(
                CPacketPlayerDigging.Action.START_DESTROY_BLOCK, pos, facing));
    }

    public static void stopDigging(BlockPos pos, EnumFacing facing)
    {
        mc.player.connection.sendPacket(new CPacketPlayerDigging(
                CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, pos, facing));
    }

    public static void loadTerrain()
    {
        // This might get called asynchronously so better be safe
        mc.addScheduledTask(() ->
        {
            if (!((INetHandlerPlayClient) mc.player.connection)
                    .isDoneLoadingTerrain())
            {
                mc.player.prevPosX = mc.player.posX;
                mc.player.prevPosY = mc.player.posY;
                mc.player.prevPosZ = mc.player.posZ;
                ((INetHandlerPlayClient) mc.player.connection)
                        .setDoneLoadingTerrain(true);

                mc.displayGuiScreen(null);
            }
        });
    }

    /**
     * Produces a {@link CPacketUseEntity} for the given id.
     *
     * @param id the id the packet should attack.
     * @return a packet that will attack the entity when send.
     */
    @SuppressWarnings("ConstantConditions")
    public static CPacketUseEntity attackPacket(int id)
    {
        CPacketUseEntity packet = new CPacketUseEntity();
        ((ICPacketUseEntity) packet).setEntityId(id);
        ((ICPacketUseEntity) packet).setAction(CPacketUseEntity.Action.ATTACK);

        return packet;
    }

    public static void sneak(boolean sneak)
    {
        PingBypass.sendToActualServer(
            new CPacketEntityAction(
                mc.player,
                sneak
                    ? CPacketEntityAction.Action.START_SNEAKING
                    : CPacketEntityAction.Action.STOP_SNEAKING));
    }

    public static void attack(Entity entity)
    {
        mc.player.connection.sendPacket(
                new CPacketUseEntity(entity));
        mc.player.connection.sendPacket(
                new CPacketAnimation(EnumHand.MAIN_HAND));
    }

    public static void attack(int id)
    {
        mc.player.connection.sendPacket(
                attackPacket(id));
        mc.player.connection.sendPacket(
                new CPacketAnimation(EnumHand.MAIN_HAND));
    }

    public static void swing(int slot)
    {
        mc.player.connection.sendPacket(
                new CPacketAnimation(InventoryUtil.getHand(slot)));
    }

    public static void place(BlockPos on,
                             EnumFacing facing,
                             int slot,
                             float x,
                             float y,
                             float z)
    {
        place(on, facing, InventoryUtil.getHand(slot), x, y, z);
    }

    public static void place(BlockPos on,
                             EnumFacing facing,
                             EnumHand hand,
                             float x,
                             float y,
                             float z)
    {
        mc.player.connection.sendPacket(
                new CPacketPlayerTryUseItemOnBlock(on, facing, hand, x, y, z));
    }

    public static void teleport(int id)
    {
        mc.player.connection.sendPacket(new CPacketConfirmTeleport(id));
    }

    public static void sendAction(CPacketEntityAction.Action action)
    {
        PingBypass.sendToActualServer(
                new CPacketEntityAction(mc.player, action));
    }

    public static void click(int windowIdIn,
                             int slotIdIn,
                             int usedButtonIn,
                             ClickType modeIn,
                             ItemStack clickedItemIn,
                             short actionNumberIn)
    {
        mc.player.connection.sendPacket(new CPacketClickWindow(windowIdIn,
                                                               slotIdIn,
                                                               usedButtonIn,
                                                               modeIn,
                                                               clickedItemIn,
                                                               actionNumberIn));
    }

    /*--------------- Utility for creating CPacketPlayers ---------------*/

    public static CPacketPlayer onGround(boolean onGround)
    {
        return new CPacketPlayer(onGround);
    }

    public static CPacketPlayer position(double x, double y, double z)
    {
        return position(x, y, z, mc.player.onGround);
    }

    public static CPacketPlayer position(double x,
                                         double y,
                                         double z,
                                         boolean onGround)
    {
        return new CPacketPlayer.Position(x, y, z, onGround);
    }

    public static CPacketPlayer rotation(float yaw,
                                         float pitch,
                                         boolean onGround)
    {
        return new CPacketPlayer.Rotation(yaw, pitch, onGround);
    }

    public static CPacketPlayer positionRotation(double x,
                                                 double y,
                                                 double z,
                                                 float yaw,
                                                 float pitch,
                                                 boolean onGround)
    {
        return new CPacketPlayer.PositionRotation(x, y, z, yaw, pitch, onGround);
    }

    /*--------------- Utility for sending CPacketPlayers ---------------*/

    public static void doY(double y, boolean onGround)
    {
        doY(mc.player, y, onGround);
    }

    public static void doY(Entity entity, double y, boolean onGround)
    {
        doPosition(entity.posX, y, entity.posZ, onGround);
    }

    public static void doPosition(double x,
                                  double y,
                                  double z,
                                  boolean onGround)
    {
        Packet<?> packet = position(x, y, z, onGround);
        PingBypass.mayAuthorize(packet);
        mc.player.connection.sendPacket(packet);
    }

    public static void doPositionNoEvent(double x,
                                  double y,
                                  double z,
                                  boolean onGround)
    {
        NetworkUtil.sendPacketNoEvent(position(x, y, z, onGround));
    }

    public static void doRotation(float yaw,
                                  float pitch,
                                  boolean onGround)
    {
        Packet<?> packet = rotation(yaw, pitch, onGround);
        PingBypass.mayAuthorize(packet);
        mc.player.connection.sendPacket(packet);
    }

    public static void doPosRot(double x,
                                double y,
                                double z,
                                float yaw,
                                float pitch,
                                boolean onGround)
    {
        Packet<?> packet = positionRotation(x, y, z, yaw, pitch, onGround);
        PingBypass.mayAuthorize(packet);
        mc.player.connection.sendPacket(packet);
    }

    public static void doPosRotNoEvent(double x,
                                double y,
                                double z,
                                float yaw,
                                float pitch,
                                boolean onGround)
    {
        NetworkUtil.sendPacketNoEvent(
                positionRotation(x, y, z, yaw, pitch, onGround));
    }

}
