package me.earth.earthhack.impl.modules.client.clickgui;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.ColorSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.api.setting.settings.StringSetting;
import me.earth.earthhack.impl.gui.click.Click;
import me.earth.earthhack.impl.util.client.SimpleData;
import net.minecraft.client.gui.GuiScreen;

import java.awt.*;

public class ClickGui extends Module
{
    public final Setting<Color> color =
            register(new ColorSetting("Color", new Color(0, 80, 255)));
    public final Setting<Boolean> catEars =
            register(new BooleanSetting("CatEars", false));
    public final Setting<Boolean> blur =
            register(new BooleanSetting("Blur", false));
    public final Setting<Integer> blurAmount =
            register(new NumberSetting<>("Blur-Amount", 8, 1, 20));
    public final Setting<Integer> blurSize =
            register(new NumberSetting<>("Blur-Size", 3, 1, 20));
    public final Setting<String> open =
            register(new StringSetting("Open", "+"));
    public final Setting<String> close =
            register(new StringSetting("Close", "-"));
    public final Setting<Boolean> white =
            register(new BooleanSetting("White-Settings", true));
    public final Setting<Boolean> description =
        register(new BooleanSetting("Description", true));
    public final Setting<Boolean> showBind =
            register(new BooleanSetting("Show-Bind", true));
    public final Setting<Boolean> size =
            register(new BooleanSetting("Category-Size", true));
    public final Setting<Integer> descriptionWidth =
        register(new NumberSetting<>("Description-Width", 240, 100, 1000));

    protected boolean fromEvent;
    protected GuiScreen screen;

    public ClickGui()
    {
        super("ClickGui", Category.Client);
        this.listeners.add(new ListenerScreen(this));
        this.setData(new SimpleData(this, "Beautiful ClickGui by oHare"));
    }

    @Override
    protected void onEnable()
    {
        screen = mc.currentScreen;
        // dont save it since some modules add/del settings
        Click gui = new Click();
        gui.init();
        gui.onGuiOpened();
        mc.displayGuiScreen(gui);
    }

    @Override
    protected void onDisable()
    {
        if (!fromEvent)
        {
            mc.displayGuiScreen(screen);
        }

        fromEvent = false;
    }

}
