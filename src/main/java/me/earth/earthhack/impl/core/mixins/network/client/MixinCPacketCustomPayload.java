package me.earth.earthhack.impl.core.mixins.network.client;

import io.netty.buffer.ByteBuf;
import me.earth.earthhack.pingbypass.nethandler.PasswordHandler;
import me.earth.earthhack.pingbypass.nethandler.PbNetHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.network.play.client.CPacketCustomPayload;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CPacketCustomPayload.class)
public abstract class MixinCPacketCustomPayload {
    @Redirect(method = "writePacketData", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/PacketBuffer;writeBytes(Lio/netty/buffer/ByteBuf;)Lio/netty/buffer/ByteBuf;"))
    private ByteBuf writeHook(PacketBuffer instance, ByteBuf p_writeBytes_1_) {
        try {
            return instance.writeBytes(p_writeBytes_1_);
        } catch (Exception e) {
            throw new RuntimeException("Refcount on: " + this.getClass().getName(), e);
        }
    }

    @Inject(method = "processPacket(Lnet/minecraft/network/play/INetHandlerPlayServer;)V", at = @At("HEAD"), cancellable = true)
    public void processPacketHook(INetHandlerPlayServer handler, CallbackInfo ci) {
        // TODO: WHATS WITH SPACKETCUSTOMPAYLOAD???
        if (handler instanceof PbNetHandler || handler instanceof PasswordHandler) {
            handler.processCustomPayload(CPacketCustomPayload.class.cast(this));
            ci.cancel(); // forge would release the buffer afterwards but we want to send this packet again and write the buffer again
        }
    }

}
