package me.earth.earthhack.impl.core.mixins.gui;

import me.earth.earthhack.pingbypass.PingBypass;
import me.earth.earthhack.pingbypass.nethandler.PbNetHandler;
import me.earth.earthhack.pingbypass.protocol.s2c.S2CActualServerPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.NetworkManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiConnecting.class)
public abstract class MixinGuiConnecting {
    @Inject(method = "<init>(Lnet/minecraft/client/gui/GuiScreen;Lnet/minecraft/client/Minecraft;Ljava/lang/String;I)V", at = @At("RETURN"))
    public void initHook(GuiScreen parent, Minecraft mcIn, String hostName, int port, CallbackInfo ci) {
        if (PingBypass.isServer()) {
            mcIn.setServerData(new ServerData(hostName, hostName, false));
        }
    }

    @Inject(method = "connect", at = @At("HEAD"))
    public void connectHook(String ip, int port, CallbackInfo ci) {
        NetworkManager manager;
        if (PingBypass.isServer()
            && PingBypass.isConnected()
            && (manager = PingBypass.getNetworkManager()) != null) {
            manager.sendPacket(new S2CActualServerPacket(ip));
            manager.setNetHandler(new PbNetHandler(manager));
        }
    }

}
