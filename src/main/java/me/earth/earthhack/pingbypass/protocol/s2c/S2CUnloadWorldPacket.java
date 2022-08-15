package me.earth.earthhack.pingbypass.protocol.s2c;

import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.core.ducks.network.INetHandlerPlayClient;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.pingbypass.PingBypass;
import me.earth.earthhack.pingbypass.protocol.ProtocolIds;
import me.earth.earthhack.pingbypass.protocol.S2CPacket;
import net.minecraft.client.gui.*;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.TextComponentString;

import java.io.IOException;

public class S2CUnloadWorldPacket extends S2CPacket implements Globals {
    private String message;

    public S2CUnloadWorldPacket() {
        super(ProtocolIds.S2C_UNLOAD_WORLD);
    }

    public S2CUnloadWorldPacket(String message) {
        super(ProtocolIds.S2C_UNLOAD_WORLD);
        this.message = message;
    }

    @Override
    public void readInnerBuffer(PacketBuffer buf) throws IOException {
        this.message = buf.readString(Short.MAX_VALUE);
    }

    @Override
    public void writeInnerBuffer(PacketBuffer buf) throws IOException {
        buf.writeString(message);
    }

    @Override
    public void execute(NetworkManager networkManager) {
        mc.addScheduledTask(() -> {
            INetHandler netHandler = networkManager.getNetHandler();
            if (netHandler instanceof INetHandlerPlayClient) {
                ((INetHandlerPlayClient) netHandler).setDoneLoadingTerrain(false);
            }

            PingBypass.UNLOADED_TICK_SERVICE.setNetworkManager(networkManager);
            mc.loadWorld(null, message);
            mc.displayGuiScreen(new GuiConnectingUnloadWorld(networkManager,
                                                             message));
        });
    }

    private static final class GuiConnectingUnloadWorld extends GuiScreen {
        private final StopWatch timer = new StopWatch().reset();
        private final NetworkManager networkManager;
        private final String message;

        private GuiConnectingUnloadWorld(
            NetworkManager networkManager, String message) {
            this.networkManager = networkManager;
            this.message = message;
        }

        @Override
        public void initGui() {
            this.buttonList.clear();
            this.buttonList.add(new GuiButton(
                1337, this.width / 2 - 100,
                Math.min(this.height / 2 + this.fontRenderer.FONT_HEIGHT / 2
                             + this.fontRenderer.FONT_HEIGHT, this.height - 30),
                "Disconnect"));
        }

        @Override
        protected void actionPerformed(GuiButton button) throws IOException {
            if (button.id == 1337) {
                networkManager.closeChannel(
                    new TextComponentString("PingBypass connection aborted!"));
                mc.displayGuiScreen(
                    new GuiDisconnected(new GuiMultiplayer(new GuiMainMenu()),
                                        "connect.failed",
                                        new TextComponentString(
                                            "PingBypass connection aborted!")));
            }
        }

        @Override
        protected void keyTyped(char typedChar, int keyCode) throws IOException {
            if (keyCode == 1) {
                networkManager.closeChannel(
                    new TextComponentString("PingBypass connection aborted!"));
                mc.displayGuiScreen(
                    new GuiDisconnected(new GuiMultiplayer(new GuiMainMenu()),
                                        "connect.failed",
                                        new TextComponentString(
                                            "PingBypass connection aborted!")));
            }
        }

        @Override
        public void drawScreen(int mouseX, int mouseY, float partialTicks) {
            if (timer.passed(30_000)) {
                networkManager.closeChannel(
                    new TextComponentString("PingBypass timed out!"));
            }

            this.drawBackground(0);
            this.drawCenteredString(
                this.fontRenderer, message, this.width / 2,
                this.height / 2 - 50, 16777215);
            super.drawScreen(mouseX, mouseY, partialTicks);
        }

        @Override
        public boolean doesGuiPauseGame() {
            return false;
        }
    }

}
