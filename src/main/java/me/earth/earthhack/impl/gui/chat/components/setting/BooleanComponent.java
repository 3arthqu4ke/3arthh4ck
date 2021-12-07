package me.earth.earthhack.impl.gui.chat.components.setting;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.impl.gui.chat.clickevents.SmartClickEvent;
import me.earth.earthhack.impl.gui.chat.components.SettingComponent;
import me.earth.earthhack.impl.gui.chat.components.values.ValueComponent;
import me.earth.earthhack.impl.gui.chat.factory.ComponentFactory;
import me.earth.earthhack.impl.modules.client.commands.Commands;
import me.earth.earthhack.impl.util.text.TextColor;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.event.ClickEvent;

public class BooleanComponent extends SettingComponent<Boolean, BooleanSetting>
{
    public BooleanComponent(BooleanSetting setting)
    {
        super(setting);

        ValueComponent value = new ValueComponent(setting);
        value.setStyle(new Style()
                .setHoverEvent(ComponentFactory.getHoverEvent(setting)));

        if (setting.getContainer() instanceof Module)
        {
            value.getStyle().setClickEvent(
                    new SmartClickEvent(ClickEvent.Action.RUN_COMMAND)
                    {
                        @Override
                        public String getValue()
                        {
                            return Commands.getPrefix()
                                    + "hiddensetting "
                                    + ((Module) setting.getContainer())
                                        .getName()
                                    + " "
                                    + "\"" + setting.getName() + "\""
                                    + " "
                                    + !setting.getValue();
                        }
                    });
        }

        this.appendSibling(value);
    }

    @Override
    public String getText()
    {
        return super.getText()
                + (setting.getValue() ? TextColor.GREEN : TextColor.RED);
    }

}
