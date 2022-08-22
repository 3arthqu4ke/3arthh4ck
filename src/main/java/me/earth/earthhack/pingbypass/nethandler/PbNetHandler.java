package me.earth.earthhack.pingbypass.nethandler;

import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.Earthhack;
import me.earth.earthhack.impl.core.ducks.network.IC00Handshake;
import me.earth.earthhack.impl.modules.client.pbteleport.PbTeleport;
import me.earth.earthhack.impl.util.network.ServerUtil;
import me.earth.earthhack.impl.util.thread.Locks;
import me.earth.earthhack.pingbypass.PingBypass;
import me.earth.earthhack.pingbypass.protocol.ProtocolFactory;
import me.earth.earthhack.pingbypass.protocol.ProtocolFactoryImpl;
import me.earth.earthhack.pingbypass.protocol.c2s.C2SActualPos;
import me.earth.earthhack.pingbypass.protocol.s2c.S2CActualServerPacket;
import me.earth.earthhack.pingbypass.proxy.PingBypassWorldSender;
import me.earth.earthhack.pingbypass.proxy.WorldSender;
import me.earth.earthhack.pingbypass.util.MotionUpdateHelper;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.inventory.Container;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.SPacketDisconnect;
import net.minecraft.network.play.server.SPacketKeepAlive;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public class PbNetHandler extends BaseNetHandler
    implements IPbNetHandler, Globals {
    private final ProtocolFactory factory = new ProtocolFactoryImpl();

    public PbNetHandler(NetworkManager networkManager) {
        super(networkManager);
    }

    @Override
    public void handle(Packet<?> packet) {
        // we need to schedule in order to keep the packet order
        mc.addScheduledTask(() -> {
            NetHandlerPlayClient client = mc.getConnection();
            if (client != null) {
                send(packet, client);
            }
        });

        timer.reset();
    }

    @Override
    public void processKeepAlive(CPacketKeepAlive packetIn) {
        timer.reset();
        networkManager.sendPacket(new SPacketKeepAlive(ServerUtil.getPingNoPingSpoof()));
    }

    @Override
    public void processInput(CPacketInput packetIn) {
        // handled by the MotionUpdateHelper
        mc.addScheduledTask(() -> PingBypass.PACKET_INPUT.onInput(packetIn));
    }

    @Override
    public void processCustomPayload(CPacketCustomPayload packetIn) {
        if ("PingBypass".equalsIgnoreCase(packetIn.getChannelName())) {
            factory.handle(packetIn.getBufferData(), networkManager);
        } else {
            this.handle(packetIn);
        }
    }

    @Override
    public void processCloseWindow(CPacketCloseWindow packetIn) {
        this.handle(packetIn);
        mc.addScheduledTask(() -> mc.player.closeScreen());
    }

    @Override
    public void processHeldItemChange(CPacketHeldItemChange packetIn) {
        /* TODO: test this instead of sending the packet?
            Would prevent an unnecessary packet from getting sent
        mc.addScheduledTask(() -> {
            if (mc.player != null) {
                Locks.acquire(Locks.PLACE_SWITCH_LOCK, () -> {
                    mc.player.inventory.currentItem = packetIn.getSlotId();
                    InventoryUtil.syncItem();
                });
            }
        }*/

        this.handle(packetIn);
        mc.addScheduledTask(() -> {
            if (mc.player != null) {
                Locks.acquire(Locks.PLACE_SWITCH_LOCK,
                              () -> mc.player.inventory.currentItem
                                  = packetIn.getSlotId());
            }
        });
    }

    @Override
    public void processEntityAction(CPacketEntityAction packetIn) {
        mc.addScheduledTask(() -> {
            EntityPlayerSP player = mc.player;
            if (mc.player == null) {
                return;
            }

            switch (packetIn.getAction()) {
                case START_SNEAKING:
                    player.setSneaking(true);
                    break;
                case STOP_SNEAKING:
                    player.setSneaking(false);
                    break;
                case START_SPRINTING:
                    player.setSprinting(true);
                    break;
                case STOP_SPRINTING:
                    player.setSprinting(false);
                    break;
                default:
                    break;
            }
        });

        this.handle(packetIn);
    }

    @Override
    public void processConfirmTeleport(CPacketConfirmTeleport packetIn) {
        timer.reset();
        if (!PbTeleport.isBlocking()) {
            this.handle(packetIn);
        }
    }

    @Override
    public void processPlayer(CPacketPlayer packetIn) {
        timer.reset();
        if (PbTeleport.isBlocking()) {
            if (PbTeleport.shouldPerformMotionUpdate()) {
                MotionUpdateHelper.makeMotionUpdate(
                    PbTeleport.shouldSpoofRotations());
            }

            return;
        }

        mc.addScheduledTask(() -> {
            C2SActualPos actual = PingBypass.PACKET_SERVICE.getActualPos();
            if (mc.player != null) {
                if (actual == null || !actual.isValid(packetIn)) {
                    this.handle(packetIn);
                    return;
                }

                // TODO: we need some position/rotation manager
                //  will add the PbRotations module to solve this for now
                double x = packetIn.getX(mc.player.posX);
                double y = packetIn.getY(mc.player.posY);
                double z = packetIn.getZ(mc.player.posZ);
                float yaw = packetIn.getYaw(mc.player.rotationYaw);
                float pit = packetIn.getPitch(mc.player.rotationPitch);
                boolean onGround = packetIn.isOnGround();
                MotionUpdateHelper.makeMotionUpdate(
                    x, y, z, yaw, pit, onGround, true);
            }
        });
    }

    @Override
    public void processClickWindow(CPacketClickWindow packetIn) {
        timer.reset();
        mc.addScheduledTask(() -> {
            if (mc.player == null) {
                return;
            }

            Container container = packetIn.getWindowId() == mc.player.openContainer.windowId ? mc.player.openContainer : mc.player.inventoryContainer;
            try {
                short id = container.getNextTransactionID(mc.player.inventory);
                /* TODO: check itemStack equals! ItemStack itemstack = */ container.slotClick(packetIn.getSlotId(), packetIn.getUsedButton(), packetIn.getClickType(), mc.player);
                CPacketClickWindow packet = new CPacketClickWindow(packetIn.getWindowId(), packetIn.getSlotId(), packetIn.getUsedButton(), packetIn.getClickType(), packetIn.getClickedItem(), id);
                PingBypass.WINDOW_CLICK.authorize(packet);
                mc.player.connection.sendPacket(packet);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void processPlayerDigging(CPacketPlayerDigging packetIn) {
        IPbNetHandler.super.processPlayerDigging(packetIn);
    }

    @Override
    public void onDisconnect(ITextComponent reason) {
        IPbNetHandler.super.onDisconnect(reason);
    }

    private void send(Packet<?> packet, NetHandlerPlayClient client) {
        PingBypass.PACKET_MANAGER.authorize(packet);
        client.sendPacket(packet);
    }

    @SuppressWarnings("unchecked")
    public static void onLogin(NetworkManager networkManager, IC00Handshake handshake) {
        mc.addScheduledTask(Locks.wrap(Locks.PINGBYPASS_PACKET_LOCK, () -> {
            if (PingBypass.isConnected()) {
                TextComponentString reason = new TextComponentString("This PingBypass is currently in use!");
                networkManager.sendPacket(new SPacketDisconnect(reason), o -> networkManager.closeChannel(reason));
                return;
            }

            // I think the NetworkManager could get disconnected between
            // scheduling this task and it actually running on the mainThread.
            // With an unlucky timing PingBypass adds a NetworkManager which is
            // not getting ticked anymore and thus will not call onDisconnect
            if (!networkManager.isChannelOpen()) {
                Earthhack.getLogger().warn("Client got disconnected between login threads!");
                return;
            }

            PingBypass.setConnected(true);
            PingBypass.setNetworkManager(networkManager);
            WorldClient world = mc.world;
            EntityPlayerSP player = mc.player;
            if (handshake != null && handshake.getIp() != null) {
                try {
                    PingBypass.DISCONNECT_SERVICE.setAllow(true);
                    ServerUtil.disconnectFromMC("Joining other server...");
                } finally {
                    PingBypass.DISCONNECT_SERVICE.setAllow(false);
                }

                String ip = handshake.getIp().contains("\0FML\0") ? handshake.getIp().split("\0")[0] : handshake.getIp();
                int port = handshake.getPort();
                networkManager.setNetHandler(new PbNetHandler(networkManager));
                networkManager.sendPacket(new S2CActualServerPacket(ip));
                GuiScreen screen = mc.currentScreen == null ? new GuiMainMenu() : mc.currentScreen;
                mc.displayGuiScreen(new GuiConnecting(screen, mc, ip, port));
            } else if (world == null || player == null) {
                networkManager.setNetHandler(new WaitingForJoinHandler(networkManager));
                PingBypassWorldSender.sendWorld(networkManager);
            } else {
                networkManager.setNetHandler(new PbNetHandler(networkManager));
                ServerData data = mc.getCurrentServerData();
                if (data != null && data.serverIP != null) {
                    networkManager.sendPacket(new S2CActualServerPacket(data.serverIP));
                }

                WorldSender.sendWorld(world, player, networkManager);
            }
        }));
    }

}
