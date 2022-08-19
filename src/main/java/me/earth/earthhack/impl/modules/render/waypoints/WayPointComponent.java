package me.earth.earthhack.impl.modules.render.waypoints;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.impl.gui.chat.clickevents.SmartClickEvent;
import me.earth.earthhack.impl.gui.chat.components.SettingComponent;
import me.earth.earthhack.impl.gui.chat.components.SimpleComponent;
import me.earth.earthhack.impl.gui.chat.factory.ComponentFactory;
import me.earth.earthhack.impl.modules.client.commands.Commands;
import me.earth.earthhack.impl.util.text.TextColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

public class WayPointComponent extends
        SettingComponent<BlockPos, WayPointSetting>
{
    public WayPointComponent(WayPointSetting setting)
    {
        super(setting);

        SimpleComponent remove = new SimpleComponent(TextColor.RED + "Remove");
        remove.setStyle(new Style()
              .setHoverEvent(ComponentFactory.getHoverEvent(setting)));

        if (setting.getContainer() instanceof Module)
        {
            remove.getStyle().setClickEvent(
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

        SimpleComponent value;
        if (setting.isCorrupted())
        {
            value = new SimpleComponent(TextColor.RED + "Corrupted ");
            value.setStyle(new Style().setHoverEvent(
                new HoverEvent(
                    HoverEvent.Action.SHOW_TEXT,
                    new TextComponentString(
                        "This settings config got corrupted!"))));
        }
        else
        {
            BlockPos pos = setting.getValue();
            String t = setting.getType().toString().substring(0, 1);
            value = new SimpleComponent("(" + t + "," + pos.getX() + ","
                                        + pos.getY() + "," + pos.getZ() + ") ");
            value.setStyle(new Style()
                  .setHoverEvent(ComponentFactory.getHoverEvent(setting)));
        }

        this.appendSibling(value);
        this.appendSibling(remove);
    }

    @Override
    public String getText()
    {
        return super.getText() + TextColor.WHITE;
    }

}
