package me.earth.earthhack.impl.gui.chat.components.setting;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.util.EnumHelper;
import me.earth.earthhack.impl.gui.chat.clickevents.SmartClickEvent;
import me.earth.earthhack.impl.gui.chat.components.SettingComponent;
import me.earth.earthhack.impl.gui.chat.components.values.EnumHoverComponent;
import me.earth.earthhack.impl.gui.chat.components.values.ValueComponent;
import me.earth.earthhack.impl.gui.chat.factory.IComponentFactory;
import me.earth.earthhack.impl.modules.client.commands.Commands;
import me.earth.earthhack.impl.util.text.TextColor;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

public class EnumComponent<A extends Enum<A>>
        extends SettingComponent<A, EnumSetting<A>>
{
    public static final IComponentFactory<?, ?> FACTORY =
            new EnumComponentFactory<>();

    public EnumComponent(EnumSetting<A> setting)
    {
        super(setting);
        if (!(setting.getContainer() instanceof Module))
        {
            this.appendSibling(
                    new ValueComponent(setting).setStyle(this.getStyle()));
            return;
        }

        this.appendSibling(new ValueComponent(setting)
                .setStyle(
                    new Style()
                        .setHoverEvent(new HoverEvent(
                                HoverEvent.Action.SHOW_TEXT,
                                new EnumHoverComponent<>(setting)))
                        .setClickEvent(new SmartClickEvent
                                (ClickEvent.Action.RUN_COMMAND)
                        {
                            @Override
                            public String getValue()
                            {
                                Enum<?> next = EnumHelper
                                        .next(setting.getValue());
                                return Commands.getPrefix()
                                        + "hiddensetting "
                                        + ((Module) setting.getContainer())
                                            .getName()
                                        + " "
                                        + "\"" + setting.getName() + "\""
                                        + " "
                                        + next.name();
                            }
                        })));
    }

    @Override
    public String getText()
    {
        return super.getText() + TextColor.AQUA;
    }

    private static final class EnumComponentFactory<F extends Enum<F>>
            implements IComponentFactory<F, EnumSetting<F>>
    {
        @Override
        public SettingComponent<F, EnumSetting<F>>
                                            create(EnumSetting<F> setting)
        {
            return new EnumComponent<>(setting);
        }
    }

}
