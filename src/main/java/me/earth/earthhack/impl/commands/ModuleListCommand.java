package me.earth.earthhack.impl.commands;

import me.earth.earthhack.api.command.Command;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.util.interfaces.Displayable;
import me.earth.earthhack.impl.commands.util.CommandDescriptions;
import me.earth.earthhack.impl.core.ducks.util.IStyle;
import me.earth.earthhack.impl.gui.chat.AbstractTextComponent;
import me.earth.earthhack.impl.gui.chat.clickevents.SmartClickEvent;
import me.earth.earthhack.impl.gui.chat.components.SimpleComponent;
import me.earth.earthhack.impl.gui.chat.components.SuppliedComponent;
import me.earth.earthhack.impl.gui.chat.util.ChatComponentUtil;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.client.commands.Commands;
import me.earth.earthhack.impl.util.text.ChatIDs;
import me.earth.earthhack.impl.util.text.TextColor;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ModuleListCommand extends Command
{
    public ModuleListCommand()
    {
        super(new String[][]{{"modules"}});
        CommandDescriptions.register(this, "List all modules in the client." +
                " Leftclick a module to toggle it. Middleclick a module to" +
                " open the chatgui and get a list of its settings.");
    }

    @Override
    public void execute(String[] args)
    {
        Managers.CHAT.sendDeleteComponent(getComponent(),
                                          "moduleListCommand",
                                          ChatIDs.MODULE);
    }

    public static ITextComponent getComponent()
    {
        AbstractTextComponent component = new SimpleComponent("Modules: ");
        component.setWrap(true);

        List<Module> moduleList = Managers
                                    .MODULES
                                    .getRegistered()
                                    .stream()
                                    .sorted(Comparator
                                        .comparing(Displayable::getDisplayName))
                                    .collect(Collectors.toList());

        for (int i = 0; i < moduleList.size(); i++)
        {
            Module module = moduleList.get(i);
            if (module != null)
            {
                int finalI = i;
                ITextComponent sibling =
                    new SuppliedComponent(() ->
                        (module.isEnabled()
                                ? TextColor.GREEN
                                : TextColor.RED) + module.getName()
                                + (finalI == moduleList.size() - 1 ? "" : ", "))
                    .setWrap(true);

                Style style = new Style()
                        .setHoverEvent(
                            ChatComponentUtil.setOffset(
                                new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                    new TextComponentString(module.getData()
                                                        .getDescription()))))
                        .setClickEvent(new SmartClickEvent(
                                ClickEvent.Action.RUN_COMMAND)
                        {
                            @Override
                            public String getValue()
                            {
                                return Commands.getPrefix()
                                        + "toggle "
                                        + module.getName();
                            }
                        });

                ((IStyle) style).setSuppliedInsertion(() ->
                        Commands.getPrefix()
                        + module.getName());

                ((IStyle) style).setMiddleClickEvent(new SmartClickEvent
                        (ClickEvent.Action.RUN_COMMAND)
                {
                    @Override
                    public String getValue()
                    {
                        return Commands.getPrefix()
                                + module.getName();
                    }
                });

                sibling.setStyle(style);
                component.appendSibling(sibling);
            }
        }

        return component;
    }

}
