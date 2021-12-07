package me.earth.earthhack.impl.modules.client.autoconfig;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.impl.gui.chat.clickevents.SmartClickEvent;
import me.earth.earthhack.impl.gui.chat.components.setting.DefaultComponent;
import me.earth.earthhack.impl.modules.client.commands.Commands;
import me.earth.earthhack.impl.util.text.TextColor;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

public class RemovingStringComponent
        extends DefaultComponent<String, RemovingString>
{
    public RemovingStringComponent(RemovingString setting)
    {
        super(setting);
        if (setting.getContainer() instanceof Module)
        {
            Module module = (Module) setting.getContainer();
            HoverEvent event = new HoverEvent(
                    HoverEvent.Action.SHOW_TEXT,
                    new TextComponentString("Removes this Setting"));

            this.appendSibling(new TextComponentString(
                    TextColor.RED + " Remove ")
                .setStyle(new Style()
                    .setHoverEvent(event)
                    .setClickEvent(
                        new SmartClickEvent
                                (ClickEvent.Action.RUN_COMMAND)
                        {
                            @Override
                            public String getValue()
                            {
                                return Commands.getPrefix()
                                        + "hiddensetting "
                                        + module.getName()
                                        + " \""
                                        + setting.getName()
                                        + "\" remove";
                            }
                        })));
        }
    }

    @Override
    public String getText()
    {
        return setting.getName()
                + TextColor.GRAY
                + " : "
                + TextColor.GOLD;
    }

}
