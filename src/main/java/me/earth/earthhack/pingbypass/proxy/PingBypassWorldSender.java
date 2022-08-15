package me.earth.earthhack.pingbypass.proxy;

import io.netty.buffer.Unpooled;
import me.earth.earthhack.impl.commands.packet.factory.playerlistheaderfooter.SPacketPlayerListHeaderFooterFactory;
import me.earth.earthhack.impl.util.text.TextColor;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.*;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.GameType;
import net.minecraft.world.WorldType;

import java.util.Collections;

public class PingBypassWorldSender {
    public static void sendWorld(NetworkManager manager) {
        manager.sendPacket(new SPacketJoinGame(1337, GameType.SPECTATOR, false, 1 /* end */, EnumDifficulty.PEACEFUL, 1, WorldType.DEFAULT, true));
        manager.sendPacket(new SPacketCustomPayload("MC|Brand", (new PacketBuffer(
            Unpooled.buffer())).writeString("PingBypass")));
        manager.sendPacket(new SPacketServerDifficulty(EnumDifficulty.PEACEFUL, true));
        manager.sendPacket(new SPacketPlayerAbilities()); // empty with all values to false / 0
        manager.sendPacket(new SPacketHeldItemChange(0));

        SPacketPlayerListHeaderFooter headerFooter = new SPacketPlayerListHeaderFooter();
        try {
            SPacketPlayerListHeaderFooterFactory.setHeaderFooter(
                headerFooter,
                new TextComponentString(TextColor.RAINBOW + "Welcome to PingBypass!"),
                new TextComponentString(TextColor.LIGHT_PURPLE + "Use the proxy commands to send commands to the server!"));
            manager.sendPacket(headerFooter);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        manager.sendPacket(new SPacketChat(new TextComponentString(
            "<" + TextColor.RAINBOW + "PingBypass" + TextColor.WHITE
                + "> Use the proxy command to send commands to the server.")));

        manager.sendPacket(new SPacketPlayerPosLook(
            0.0, 240.0, 0.0, 0.0f, 0.0f, Collections.emptySet(), 0));
    }

}
