package me.earth.earthhack.pingbypass.nethandler;

import me.earth.earthhack.impl.util.network.ServerUtil;
import me.earth.earthhack.pingbypass.PingBypass;
import net.minecraft.network.Packet;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.network.play.client.*;
import net.minecraft.util.text.ITextComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SuppressWarnings("NullableProblems")
public interface IPbNetHandler extends INetHandlerPlayServer {
    Logger LOGGER = LogManager.getLogger(IPbNetHandler.class);

    default void handle(Packet<?> packet) {

    }

    @Override
    default void handleAnimation(CPacketAnimation packetIn) {
        this.handle(packetIn);
    }

    @Override
    default void processChatMessage(CPacketChatMessage packetIn) {
        this.handle(packetIn);
    }

    @Override
    default void processTabComplete(CPacketTabComplete packetIn) {
        this.handle(packetIn);
    }

    @Override
    default void processClientStatus(CPacketClientStatus packetIn) {
        this.handle(packetIn);
    }

    @Override
    default void processClientSettings(CPacketClientSettings packetIn) {
        this.handle(packetIn);
    }

    @Override
    default void processConfirmTransaction(CPacketConfirmTransaction packetIn) {
        this.handle(packetIn);
    }

    @Override
    default void processEnchantItem(CPacketEnchantItem packetIn) {
        this.handle(packetIn);
    }

    @Override
    default void processClickWindow(CPacketClickWindow packetIn) {
        this.handle(packetIn);
    }

    @Override
    default void func_194308_a(CPacketPlaceRecipe packetIn) {
        this.handle(packetIn);
    }

    @Override
    default void processCloseWindow(CPacketCloseWindow packetIn) {
        this.handle(packetIn);
    }

    @Override
    default void processCustomPayload(CPacketCustomPayload packetIn) {
        this.handle(packetIn);
    }

    @Override
    default void processUseEntity(CPacketUseEntity packetIn) {
        this.handle(packetIn);
    }

    @Override
    default void processKeepAlive(CPacketKeepAlive packetIn) {
        this.handle(packetIn);
    }

    @Override
    default void processPlayer(CPacketPlayer packetIn) {
        this.handle(packetIn);
    }

    @Override
    default void processPlayerAbilities(CPacketPlayerAbilities packetIn) {
        this.handle(packetIn);
    }

    @Override
    default void processPlayerDigging(CPacketPlayerDigging packetIn) {
        this.handle(packetIn);
    }

    @Override
    default void processEntityAction(CPacketEntityAction packetIn) {
        this.handle(packetIn);
    }

    @Override
    default void processInput(CPacketInput packetIn) {
        this.handle(packetIn);
    }

    @Override
    default void processHeldItemChange(CPacketHeldItemChange packetIn) {
        this.handle(packetIn);
    }

    @Override
    default void processCreativeInventoryAction(
        CPacketCreativeInventoryAction packetIn) {
        this.handle(packetIn);
    }

    @Override
    default void processUpdateSign(CPacketUpdateSign packetIn) {
        this.handle(packetIn);
    }

    @Override
    default void processTryUseItemOnBlock(CPacketPlayerTryUseItemOnBlock packetIn) {
        this.handle(packetIn);
    }

    @Override
    default void processTryUseItem(CPacketPlayerTryUseItem packetIn) {
        this.handle(packetIn);
    }

    @Override
    default void handleSpectate(CPacketSpectate packetIn) {
        this.handle(packetIn);
    }

    @Override
    default void handleResourcePackStatus(CPacketResourcePackStatus packetIn) {
        this.handle(packetIn);
    }

    @Override
    default void processSteerBoat(CPacketSteerBoat packetIn) {
        this.handle(packetIn);
    }

    @Override
    default void processVehicleMove(CPacketVehicleMove packetIn) {
        this.handle(packetIn);
    }

    @Override
    default void processConfirmTeleport(CPacketConfirmTeleport packetIn) {
        this.handle(packetIn);
    }

    @Override
    default void handleRecipeBookUpdate(CPacketRecipeInfo packetIn) {
        this.handle(packetIn);
    }

    @Override
    default void handleSeenAdvancements(CPacketSeenAdvancements packetIn) {
        this.handle(packetIn);
    }

    @Override
    default void onDisconnect(ITextComponent reason) {
        LOGGER.info("Quitting: " + reason.getUnformattedText());
        if (!PingBypass.isStaying()) {
            ServerUtil.disconnectFromMC("PingBypass Client has disconnected.");
        }

        PingBypass.setConnected(false);
        PingBypass.setStay(false);
    }

}
