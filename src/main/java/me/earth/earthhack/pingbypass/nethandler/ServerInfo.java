package me.earth.earthhack.pingbypass.nethandler;

import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.commands.packet.util.DummyServerStatusResponse;
import me.earth.earthhack.impl.util.network.ServerUtil;
import me.earth.earthhack.impl.util.text.TextColor;
import me.earth.earthhack.pingbypass.PingBypass;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.ServerStatusResponse;
import net.minecraft.util.text.TextComponentString;

public class ServerInfo implements Globals {
    public ServerStatusResponse getResponse() {
        ServerStatusResponse response = new ServerStatusResponse();
        response.setFavicon(DummyServerStatusResponse.FAVICON);
        response.setPlayers(new ServerStatusResponse.Players(1, PingBypass.getPlayerCount()));
        response.setVersion(new ServerStatusResponse.Version("1.12.2", 340));
        response.setServerDescription(new TextComponentString(getMotD()));
        return response;
    }

    public String getMotD() {
        if (mc.isSingleplayer()) {
            return TextColor.LIGHT_PURPLE + "SinglePlayer";
        }

        ServerData data = mc.getCurrentServerData();
        // mc.world check is necessary, cause server data could not have been
        // set back to null.
        if (data == null || mc.world == null) {
            return TextColor.RED + "Not connected";
        }

        int ping = ServerUtil.getPingNoPingSpoof();
        int pos;
        if (PingBypass.QUEUE.isOn2b2t()
            && (pos = PingBypass.QUEUE.getPosition()) != -1) {
            return TextColor.GREEN + "2b2t.org" + TextColor.GRAY + ", "
                + TextColor.GOLD + "Queue: " + TextColor.BOLD
                + pos + TextColor.RESET + TextColor.GRAY + ", "
                + TextColor.WHITE + "Ping" + TextColor.GRAY
                + ": " + getPingColor(ping) + TextColor.BOLD + ping;
        }

        return TextColor.GREEN + data.serverIP
            + TextColor.GRAY + ", " + TextColor.WHITE + "Ping"
            + TextColor.GRAY + ": "
            + getPingColor(ping)
            + TextColor.BOLD
            + ping;
    }

    public static String getPingColor(int ping) {
        return ping <= 0
            ? TextColor.WHITE
            : ping <= 25
                ? TextColor.GREEN
                : ping <= 50
                    ? TextColor.YELLOW
                    : ping <= 100
                        ? TextColor.GOLD
                        : TextColor.RED;
    }

}
