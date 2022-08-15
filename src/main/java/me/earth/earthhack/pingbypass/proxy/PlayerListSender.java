package me.earth.earthhack.pingbypass.proxy;

import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.commands.packet.factory.playerlistitem.SPacketPlayerListItemFactory;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketPlayerListItem;

public class PlayerListSender implements Globals {
    public static void sendPlayerListData(NetworkManager manager, NetHandlerPlayClient client) {
        for (NetworkPlayerInfo playerInfo : client.getPlayerInfoMap()) {
            SPacketPlayerListItem packet = new SPacketPlayerListItem();
            try {
                SPacketPlayerListItemFactory.setAction(packet, SPacketPlayerListItem.Action.ADD_PLAYER);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new IllegalStateException(e);
            }

            SPacketPlayerListItem.AddPlayerData data;
            data = packet.new AddPlayerData(playerInfo.getGameProfile(),
                                            playerInfo.getResponseTime(),
                                            playerInfo.getGameType(),
                                            playerInfo.getDisplayName());
            packet.getEntries().add(data);
            manager.sendPacket(packet);
        }
    }

}
