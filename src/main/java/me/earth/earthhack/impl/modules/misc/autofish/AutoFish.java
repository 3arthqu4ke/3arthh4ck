package me.earth.earthhack.impl.modules.misc.autofish;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.core.ducks.IMinecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.item.ItemFishingRod;

public class AutoFish extends Module
{
    protected final Setting<Boolean> openInv =
            register(new BooleanSetting("OpenInventory", true));
    protected final Setting<Float> delay     =
            register(new NumberSetting<>("Delay", 15.0f, 10.0f, 25.0f));
    protected final Setting<Double> range    =
            register(new NumberSetting<>("SoundRange", 2.0, 0.1, 5.0));

    protected boolean splash;
    protected int delayCounter;
    protected int splashTicks;
    protected int timeout;

    public AutoFish()
    {
        super("AutoFish", Category.Misc);
        this.listeners.add(new ListenerSound(this));
        this.listeners.add(new ListenerTick(this));
    }

    @Override
    protected void onEnable()
    {
        splash = false;
        splashTicks = 0;
        delayCounter = 0;
        timeout = 0;
    }

    protected void click()
    {
        if (!mc.player.inventory.getCurrentItem().isEmpty()
                || mc.player.inventory.getCurrentItem().getItem()
                                                instanceof ItemFishingRod)
        {
            if (openInv.getValue()
                    || mc.currentScreen instanceof GuiChat
                    || mc.currentScreen == null)
            {
                ((IMinecraft) mc).click(IMinecraft.Click.RIGHT);
                delayCounter = delay.getValue().intValue();
                timeout = 0;
            }
        }
    }

}
