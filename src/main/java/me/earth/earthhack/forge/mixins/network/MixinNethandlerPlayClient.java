package me.earth.earthhack.forge.mixins.network;

import me.earth.earthhack.forge.util.ReplaceNetworkDispatcher;
import me.earth.earthhack.impl.Earthhack;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.NetworkManager;
import net.minecraftforge.fml.common.network.handshake.NetworkDispatcher;
import net.minecraftforge.fml.common.network.internal.FMLNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(NetHandlerPlayClient.class)
public abstract class MixinNethandlerPlayClient
{
    @Redirect(
        method = "handleJoinGame",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraftforge/fml/common/network/handshake/NetworkDispatcher;" +
                    "get(Lnet/minecraft/network/NetworkManager;)" +
                    "Lnet/minecraftforge/fml/common/network/handshake/NetworkDispatcher;",
            remap = false))
    private NetworkDispatcher networkDispatcherHook(NetworkManager manager)
    {
        NetworkDispatcher dispatcher = NetworkDispatcher.get(manager);
        if (dispatcher == null)
        {
            Earthhack.getLogger().warn("NetworkDispatcher Disconnect avoided!");

            try
            {
                FMLNetworkHandler.fmlClientHandshake(manager);
                dispatcher = NetworkDispatcher.get(manager);
            }
            catch (Throwable t)
            {
                t.printStackTrace();
            }

            if (dispatcher == null)
            {
                dispatcher = new ReplaceNetworkDispatcher(manager);
            }
        }

        return dispatcher;
    }

}
