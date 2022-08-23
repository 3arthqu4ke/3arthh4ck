package me.earth.earthhack.pingbypass.nethandler;

import me.earth.earthhack.impl.core.ducks.network.IC00Handshake;
import me.earth.earthhack.pingbypass.PingBypass;
import me.earth.earthhack.pingbypass.protocol.PbPacket;
import me.earth.earthhack.pingbypass.protocol.ProtocolFactory;
import me.earth.earthhack.pingbypass.protocol.ProtocolFactoryImpl;
import me.earth.earthhack.pingbypass.protocol.c2s.C2SPasswordPacket;
import me.earth.earthhack.pingbypass.protocol.s2c.S2CPasswordPacket;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.client.CPacketCustomPayload;
import net.minecraft.util.ITickable;
import net.minecraft.util.text.TextComponentString;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class PasswordHandler extends BaseNetHandler
    implements IPbNetHandler, ITickable {
    private final ProtocolFactory factory = new ProtocolFactoryImpl();
    private final AtomicBoolean connected = new AtomicBoolean();
    private final IC00Handshake handshake;

    public PasswordHandler(NetworkManager networkManager, IC00Handshake handshake) {
        super(networkManager);
        this.handshake = handshake;
    }

    @Override
    public void update() {
        if (!connected.get()) {
            networkManager.sendPacket(new S2CPasswordPacket());
        }

        super.update();
    }

    @Override
    public void processCustomPayload(CPacketCustomPayload packetIn) {
        synchronized (connected) {
            if ("PingBypass".equalsIgnoreCase(packetIn.getChannelName()) && !connected.getAndSet(true)) {
                try {
                    PbPacket<?> packet = factory.convert(packetIn.getBufferData());
                    String password = PingBypass.CONFIG.getPassword();
                    if (password == null || password.isEmpty()) {
                        throw new IllegalStateException("PingBypass requires a password!");
                    }

                    if (packet instanceof C2SPasswordPacket) {
                        if (password.equals(((C2SPasswordPacket) packet).getString())) {
                            PbNetHandler.onLogin(networkManager, handshake);
                            return;
                        }

                        Thread.sleep(1 + new Random().nextInt(10)); // timing attacks idk
                        this.disconnect(new TextComponentString("Wrong password!"));
                    } else {
                        this.disconnect(new TextComponentString("Unexpected PingBypass packet: " + packet.getClass()));
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                    this.disconnect(new TextComponentString(e.getMessage()));
                } catch (IOException e) {
                    this.disconnect(new TextComponentString(e.getMessage()));
                } finally {
                    packetIn.getBufferData().release();
                }
            }
        }
    }

}
