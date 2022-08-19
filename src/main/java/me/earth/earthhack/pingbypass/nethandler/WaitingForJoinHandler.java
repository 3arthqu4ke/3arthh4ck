package me.earth.earthhack.pingbypass.nethandler;

import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.pingbypass.protocol.ProtocolFactory;
import me.earth.earthhack.pingbypass.protocol.ProtocolFactoryImpl;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.network.play.client.CPacketCustomPayload;
import net.minecraft.network.play.client.CPacketKeepAlive;
import net.minecraft.network.play.server.SPacketKeepAlive;

public class WaitingForJoinHandler extends BaseNetHandler
    implements IPbNetHandler, Globals {
    private final ProtocolFactory factory = new ProtocolFactoryImpl();

    public WaitingForJoinHandler(NetworkManager manager) {
        super(manager, 100_000);
    }

    /*@Override
    public void update() {
        WorldClient world = mc.world;
        EntityPlayerSP player = mc.player;
        if (world != null && player != null) {
            // TODO: this is wonky
            networkManager.sendPacket(new S2CPositionPacket(player.posX, player.posY, player.posZ, player.rotationYaw, player.rotationPitch, player.onGround));
            networkManager.setNetHandler(new PbNetHandler(networkManager));
            WorldSender.sendWorld(world, networkManager);
        }
    }*/

    @Override
    public void processKeepAlive(CPacketKeepAlive packetIn) {
        timer.reset();
        networkManager.sendPacket(new SPacketKeepAlive(0));
    }

    @Override
    public void processChatMessage(CPacketChatMessage packetIn) {

    }

    @Override
    public void processCustomPayload(CPacketCustomPayload packetIn) {
        if ("PingBypass".equalsIgnoreCase(packetIn.getChannelName())) {
            factory.handle(packetIn.getBufferData(), networkManager);
        }
    }

}
