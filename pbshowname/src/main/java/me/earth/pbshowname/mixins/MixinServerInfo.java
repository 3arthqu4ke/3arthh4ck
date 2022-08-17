package me.earth.pbshowname.mixins;

import me.earth.earthhack.impl.util.text.TextColor;
import me.earth.earthhack.pingbypass.nethandler.ServerInfo;
import me.earth.pbshowname.PbShowName;
import net.minecraft.client.multiplayer.ServerData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = ServerInfo.class, remap = false)
public abstract class MixinServerInfo {
    @ModifyConstant(
        method = "getMotD",
        constant = @Constant(stringValue = TextColor.RED + "Not connected"),
        remap = false)
    private String notConnectedHook(String constant) {
        String name = PbShowName.getName();
        return name == null
            ? constant
            : TextColor.AQUA + name + TextColor.GRAY + ", " + TextColor.RED
                + "Not connected";
    }

    @ModifyConstant(
        method = "getMotD",
        constant = @Constant(stringValue = TextColor.GREEN + "2b2t.org"
                                            + TextColor.GRAY + ", "
                                            + TextColor.GOLD + "Queue: "
                                            + TextColor.BOLD),
        remap = false)
    private String twoBHook(String constant) {
        String name = PbShowName.getName();
        return name == null
            ? constant
            : TextColor.AQUA + name + TextColor.GRAY + ", "
                + TextColor.GREEN + "2b2t.org"
                + TextColor.GRAY + ", "
                + TextColor.GOLD + "Queue: "
                + TextColor.BOLD;
    }

    @Redirect(
        method = "getMotD",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/multiplayer/ServerData;serverIP:Ljava/lang/String;",
            remap = true))
    private String getMotDHook(ServerData instance) {
        String name = PbShowName.getName();
        return name == null
            ? instance.serverIP
            : TextColor.AQUA + name + TextColor.GRAY + ", " + TextColor.GREEN
                + instance.serverIP;
    }

}
