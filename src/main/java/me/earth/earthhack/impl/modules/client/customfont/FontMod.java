package me.earth.earthhack.impl.modules.client.customfont;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Complexity;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.api.setting.settings.StringSetting;
import me.earth.earthhack.impl.gui.chat.clickevents.SmartClickEvent;
import me.earth.earthhack.impl.gui.chat.components.SimpleComponent;
import me.earth.earthhack.impl.gui.chat.components.SuppliedComponent;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.managers.thread.scheduler.Scheduler;
import me.earth.earthhack.impl.modules.client.commands.Commands;
import me.earth.earthhack.impl.modules.client.customfont.mode.FontStyle;
import me.earth.earthhack.impl.util.text.ChatIDs;
import me.earth.earthhack.impl.util.text.TextColor;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.event.ClickEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FontMod extends Module
{
    protected final Setting<String> fontName     =
            register(new StringSetting("Font", "Verdana"));
    protected final Setting<FontStyle> fontStyle =
            register(new EnumSetting<>("FontStyle", FontStyle.Plain));
    protected final Setting<Integer> fontSize    =
            register(new NumberSetting<>("FontSize", 18, 1, 64));
    protected final Setting<Boolean> antiAlias   =
            register(new BooleanSetting("AntiAlias", true));
    protected final Setting<Boolean> metrics     =
            register(new BooleanSetting("Metrics", true));
    protected final Setting<Boolean> shadow      =
            register(new BooleanSetting("Shadow", true));
    protected final Setting<Boolean> showFonts   =
            register(new BooleanSetting("Fonts", false));

    public final Setting<Boolean> changeHeight =
        register(new BooleanSetting("Height-Change", false))
            .setComplexity(Complexity.Expert);
    public final Setting<Integer> heightSub =
        register(new NumberSetting<>("Height-Subtract", 8, -10, 10))
            .setComplexity(Complexity.Expert);
    public final Setting<Integer> heightFactor =
        register(new NumberSetting<>("Height-Factor", 2, 1, 4))
            .setComplexity(Complexity.Expert);
    public final Setting<Integer> heightAdd =
        register(new NumberSetting<>("Height-Add", 0, -10, 10))
            .setComplexity(Complexity.Expert);

    protected final List<String> fonts = new ArrayList<>();

    public FontMod()
    {
        super("CustomFont", Category.Client);
        Collections.addAll(fonts,
                           GraphicsEnvironment.getLocalGraphicsEnvironment()
                                              .getAvailableFontFamilyNames());
        registerObservers();
        this.setData(new FontData(this));
    }

    private void registerObservers()
    {
        for (Setting<?> setting : this.getSettings())
        {
            if (!setting.equals(showFonts))
            {
                setting.addObserver(e ->
                        Scheduler.getInstance().schedule(this::setFont));
            }
        }

        showFonts.addObserver(event ->
        {
            if (event.getValue())
            {
                event.setCancelled(true);
                sendFonts();
            }
        });
    }

    public void sendFonts()
    {
        SimpleComponent component =
                new SimpleComponent("Available Fonts: ");
        component.setWrap(true);

        for (int i = 0; i < fonts.size(); i++)
        {
            String font = fonts.get(i);
            if (font != null)
            {
                int finalI = i;
                component.appendSibling(
                        new SuppliedComponent(() ->
                                (font.equals(fontName.getValue())
                                        ? TextColor.GREEN
                                        : TextColor.RED)
                                        + font
                                        + (finalI == fonts.size() - 1
                                        ? ""
                                        : ", "))
                                .setStyle(new Style()
                                        .setClickEvent(new SmartClickEvent
                                                (ClickEvent.Action.RUN_COMMAND)
                                        {
                                            @Override
                                            public String getValue()
                                            {
                                                return Commands.getPrefix()
                                                        + "CustomFont Font "
                                                        + "\"" + font + "\"";
                                            }
                                        })));
            }
        }

        Managers.CHAT.sendDeleteComponent(
                component, "Fonts", ChatIDs.MODULE);
    }

    private void setFont()
    {
        //noinspection MagicConstant
        Managers.TEXT.setFontRenderer(
                new Font(fontName.getValue(),
                         fontStyle.getValue().getFontStyle(),
                         fontSize.getValue()),
                antiAlias.getValue(),
                metrics.getValue());
    }

}
