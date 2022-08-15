package me.earth.earthhack.impl.core.mixins.network.server;

import me.earth.earthhack.pingbypass.util.DataManagerUtil;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SPacketSpawnMob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.io.IOException;
import java.util.List;

@Mixin(SPacketSpawnMob.class)
public abstract class MixinSPacketSpawnMob
{
    @Shadow
    private List<EntityDataManager.DataEntry<?>> dataManagerEntries;

    // when we send this packet from PingBypass to the client it has been serialized one time and the manager is null.
    @Redirect(method = "writePacketData", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/datasync/EntityDataManager;writeEntries(Lnet/minecraft/network/PacketBuffer;)V"))
    private void writeEntries(EntityDataManager manager, PacketBuffer buffer) throws IOException
    {
        if (manager == null)
        {
            DataManagerUtil.writeDataEntries(buffer, this.dataManagerEntries);
        }
        else
        {
            manager.writeEntries(buffer);
        }
    }

}
