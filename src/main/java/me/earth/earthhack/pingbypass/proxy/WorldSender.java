package me.earth.earthhack.pingbypass.proxy;

import io.netty.buffer.Unpooled;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.text.TextColor;
import me.earth.earthhack.pingbypass.PingBypass;
import me.earth.earthhack.pingbypass.protocol.s2c.S2CGameProfile;
import me.earth.earthhack.pingbypass.protocol.s2c.S2CPositionPacket;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.*;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.storage.WorldInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorldSender implements Globals {
    private static final Logger LOGGER = LogManager.getLogger(WorldSender.class);

    public static void sendWorld(WorldClient world, EntityPlayerSP playerIn, NetworkManager manager) {
        @SuppressWarnings("ConstantConditions")
        String address = manager.getRemoteAddress() != null ? manager.getRemoteAddress().toString() : "local";
        LOGGER.info("{}[{}] logged in with entity id {} at ({}, {}, {})", playerIn.getName(), address, playerIn.getEntityId(), playerIn.posX, playerIn.posY, playerIn.posZ);
        WorldInfo worldinfo = world.getWorldInfo();

        manager.sendPacket(new S2CGameProfile(playerIn));
        manager.sendPacket(new SPacketJoinGame(playerIn.getEntityId(), mc.playerController.getCurrentGameType(), worldinfo.isHardcoreModeEnabled(), playerIn.dimension, worldinfo.getDifficulty(), 1, worldinfo.getTerrainType(), playerIn.hasReducedDebug()));

        // TODO: it seems that SPacketPlayerAbilities might get send before SPacketJoinGame???
        manager.sendPacket(new SPacketPlayerAbilities(playerIn.capabilities));

        manager.sendPacket(new SPacketCustomPayload("MC|Brand", (new PacketBuffer(Unpooled.buffer())).writeString(playerIn.getServerBrand())));
        manager.sendPacket(new SPacketServerDifficulty(worldinfo.getDifficulty(), worldinfo.isDifficultyLocked()));
        manager.sendPacket(new SPacketPlayerAbilities(playerIn.capabilities));
        manager.sendPacket(new SPacketHeldItemChange(playerIn.inventory.currentItem));

        manager.sendPacket(new SPacketEntityStatus(playerIn, getPermLevel(playerIn)));
        manager.sendPacket(new SPacketStatistics(PingBypass.STATISTICS.getMap()));

        // TODO: send recipes

        ScoreboardSender.sendScoreboard(world.getScoreboard(), manager);
        // TODO: send players?  -> this.server.refreshStatusNextTick();

        manager.sendPacket(new SPacketChat(new TextComponentString(TextColor.YELLOW + "You just joined the PingBypass!")));

        PlayerListSender.sendPlayerListData(manager, playerIn.connection);
        TimeAndWeatherSender.updateTimeAndWeatherForPlayer(manager, world);

        /* TODO: ADD RESOURCE PACK!
        if (!this.server.getResourcePackUrl().isEmpty())
        {
            playerIn.loadResourcePack(this.server.getResourcePackUrl(), this.server.getResourcePackHash());
        } */

        for (PotionEffect potioneffect : playerIn.getActivePotionEffects())
        {
            manager.sendPacket(new SPacketEntityEffect(playerIn.getEntityId(), potioneffect));
        }

        // TODO: send ridden entity?

        manager.sendPacket(new SPacketWindowItems(playerIn.openContainer.windowId, playerIn.openContainer.getInventory()));
        manager.sendPacket(new SPacketSetSlot(-1, -1, playerIn.inventory.getItemStack()));

        ChunkSender.sendChunks(world, manager);
        EntitySender.sendEntities(world, manager);

        SPacketEntityMetadata metadata = new SPacketEntityMetadata(playerIn.getEntityId(), playerIn.getDataManager(), true);
        manager.sendPacket(metadata);

        manager.sendPacket(new S2CPositionPacket(
            Managers.POSITION.getX(), Managers.POSITION.getY(), Managers.POSITION.getZ(),
            Managers.ROTATION.getServerYaw(), Managers.ROTATION.getServerPitch(), Managers.POSITION.isOnGround()));
    }

    private static byte getPermLevel(EntityPlayerSP player) {
        byte result;
        if (player.getPermissionLevel() <= 0)
        {
            result = 24;
        }
        else if (player.getPermissionLevel() >= 4)
        {
            result = 28;
        }
        else
        {
            result = (byte) (24 + player.getPermissionLevel());
        }

        return result;
    }


}
