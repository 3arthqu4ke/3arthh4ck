package me.earth.earthhack.impl.core.mixins.gui;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.autoconfig.AutoConfig;
import me.earth.earthhack.impl.modules.client.pingbypass.PingBypassModule;
import me.earth.earthhack.impl.modules.client.pingbypass.guis.GuiAddPingBypass;
import me.earth.earthhack.impl.modules.client.pingbypass.guis.GuiButtonPingBypassOptions;
import me.earth.earthhack.impl.modules.client.pingbypass.guis.GuiConnectingPingBypass;
import me.earth.earthhack.impl.util.text.TextColor;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.ServerData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiMultiplayer.class)
public abstract class MixinGuiMultiPlayer extends GuiScreen
{
    private static final ModuleCache<PingBypassModule> PINGBYPASS =
            Caches.getModule(PingBypassModule.class);
    private static final ModuleCache<AutoConfig> CONFIG =
            Caches.getModule(AutoConfig.class);

    private GuiButton pingBypassButton;

    @Inject(
        method = "createButtons",
        at = @At("HEAD"))
    public void createButtonsHook(CallbackInfo info)
    {
        this.buttonList.add(new GuiButtonPingBypassOptions(1339,
                                                           width - 24,
                                                           5));
        pingBypassButton = addButton(new GuiButton(1336,
                                                   width - 126,
                                                   5,
                                                   100,
                                                   20,
                                                   getDisplayString()));
    }

    @Inject(
        method = "actionPerformed",
        at = @At("HEAD"),
        cancellable = true)
    protected void actionPerformed(GuiButton button, CallbackInfo info)
    {
        if (button.enabled)
        {
            if (button.id == pingBypassButton.id)
            {
                PINGBYPASS.toggle();
                pingBypassButton.displayString = getDisplayString();
                info.cancel();
            }
            else if (button.id == 1339)
            {
                mc.displayGuiScreen(new GuiAddPingBypass(this));
                info.cancel();
            }
        }
    }

    @Inject(
        method = "confirmClicked",
        at = @At("HEAD"),
        cancellable = true)
    public void confirmClickedHook(boolean result, int id, CallbackInfo info)
    {
        if (id == pingBypassButton.id)
        {
            mc.displayGuiScreen(this);
            info.cancel();
        }
    }

    @Inject(
        method = "connectToServer",
        at = @At("HEAD"),
        cancellable = true)
    public void connectToServerHook(ServerData data, CallbackInfo info)
    {
        if (CONFIG.isEnabled())
        {
            CONFIG.get().onConnect(data.serverIP);
        }

        if (PINGBYPASS.isEnabled())
        {
            mc.displayGuiScreen(new GuiConnectingPingBypass(this, mc, data));
            info.cancel();
        }
    }

    private String getDisplayString()
    {
        return "PingBypass: " + (PINGBYPASS.isEnabled()
                ? TextColor.GREEN + "On"
                : TextColor.RED + "Off");
    }

}
