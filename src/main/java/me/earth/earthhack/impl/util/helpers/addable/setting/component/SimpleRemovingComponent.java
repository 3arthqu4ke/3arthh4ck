package me.earth.earthhack.impl.util.helpers.addable.setting.component;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.impl.gui.chat.clickevents.SmartClickEvent;
import me.earth.earthhack.impl.gui.chat.components.SettingComponent;
import me.earth.earthhack.impl.gui.chat.components.SimpleComponent;
import me.earth.earthhack.impl.gui.chat.factory.ComponentFactory;
import me.earth.earthhack.impl.modules.client.commands.Commands;
import me.earth.earthhack.impl.util.helpers.addable.setting.SimpleRemovingSetting;
import me.earth.earthhack.impl.util.text.TextColor;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.ClickEvent;

public class SimpleRemovingComponent
        extends SettingComponent<Boolean, SimpleRemovingSetting>
{
    public SimpleRemovingComponent(SimpleRemovingSetting setting)
    {
        super(setting);
        SimpleComponent value = new SimpleComponent("Remove");
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
                                + " \""
                                + setting.getName()
                                + "\" remove";
                    }
                });
        }

        this.appendSibling(value);
    }

    @Override
    public String getText()
    {
        return super.getText() + TextColor.RED;
    }

    @Override
    public String getUnformattedComponentText()
    {
        return this.getText();
    }

    @Override
    public TextComponentString createCopy()
    {
        return ComponentFactory.create(setting);
    }

}
