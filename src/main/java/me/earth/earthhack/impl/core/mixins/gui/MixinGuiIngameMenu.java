package me.earth.earthhack.impl.core.mixins.gui;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.pingbypass.PingBypassModule;
import me.earth.earthhack.pingbypass.protocol.c2s.C2SStayPacket;
import net.minecraft.client.gui.*;
import net.minecraft.realms.RealmsBridge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiIngameMenu.class)
public class MixinGuiIngameMenu extends GuiScreen {
    private static final int BUTTON_ID = 1235235;
    private static final ModuleCache<PingBypassModule> PING_BYPASS =
        Caches.getModule(PingBypassModule.class);

    @Inject(method = "initGui", at = @At("RETURN"))
    public void initGuiHook(CallbackInfo ci) {
        if (PING_BYPASS.isEnabled() && !PING_BYPASS.get().isOld()) {
            // TODO: i think other mods might add another button here too. Add that button dynamically?
            this.buttonList.add(new GuiButton(BUTTON_ID, this.width / 2 - 100, this.height / 4 + 152, "Disconnect from PingBypass"));
        }
    }

    @Inject(method = "actionPerformed", at = @At("HEAD"), cancellable = true)
    public void actionPerformedHook(GuiButton button, CallbackInfo ci) {
        if (button.id == BUTTON_ID) {
            if (mc.player != null
                && PING_BYPASS.isEnabled()
                && !PING_BYPASS.get().isOld()) {
                mc.player.connection.sendPacket(new C2SStayPacket());
                boolean flag = this.mc.isIntegratedServerRunning();
                boolean flag1 = this.mc.isConnectedToRealms();
                button.enabled = false;
                this.mc.world.sendQuittingDisconnectingPacket();
                this.mc.loadWorld(null);

                if (flag)
                {
                    this.mc.displayGuiScreen(new GuiMainMenu());
                }
                else if (flag1)
                {
                    RealmsBridge realmsbridge = new RealmsBridge();
                    realmsbridge.switchToRealms(new GuiMainMenu());
                }
                else
                {
                    this.mc.displayGuiScreen(new GuiMultiplayer(new GuiMainMenu()));
                }
            }

            ci.cancel();
        }
    }

}
