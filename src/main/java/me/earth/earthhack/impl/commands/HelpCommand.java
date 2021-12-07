package me.earth.earthhack.impl.commands;

import me.earth.earthhack.api.command.Command;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.commands.gui.CommandGui;
import me.earth.earthhack.impl.commands.util.CommandDescriptions;
import me.earth.earthhack.impl.gui.chat.clickevents.RunnableClickEvent;
import me.earth.earthhack.impl.gui.chat.util.ChatComponentUtil;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.client.commands.Commands;
import me.earth.earthhack.impl.util.text.ChatUtil;
import me.earth.earthhack.impl.util.text.TextColor;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.HoverEvent;

import java.util.Iterator;

public class HelpCommand extends Command implements Globals
{
    public HelpCommand()
    {
        super(new String[][]{{"help"}});
        CommandDescriptions.register(this,
                "Get a list and help for all commands.");
    }

    @Override
    public void execute(String[] args)
    {
        ITextComponent component =
                new TextComponentString("Following commands are available: ");

        Iterator<Command> it = Managers.COMMANDS.getRegistered().iterator();
        while (it.hasNext())
        {
            Command command = it.next();
            if (command != null)
            {
                ITextComponent sibling =
                        new TextComponentString(TextColor.AQUA
                                                + command.getName()
                                                + TextColor.WHITE
                                                + (it.hasNext() ? ", " : ""));

                String descr = CommandDescriptions.getDescription(command);
                HoverEvent event = new HoverEvent(
                        HoverEvent.Action.SHOW_TEXT,
                        new TextComponentString(descr == null
                                                    ? "A command."
                                                    : descr));
                ChatComponentUtil.setOffset(event);
                Style style = new Style().setHoverEvent(event);
                if (command instanceof ModuleCommand)
                {
                    style.setClickEvent(new RunnableClickEvent(() ->
                        setText(Commands.getPrefix() + "AutoCrystal")));
                }
                else
                {
                    style.setClickEvent(new RunnableClickEvent(() ->
                        setText(Commands.getPrefix() + command.getName())));
                }

                sibling.setStyle(style);
                component.appendSibling(sibling);
            }
        }

        ChatUtil.sendComponent(component);
    }

    private void setText(String text)
    {
        GuiScreen current = mc.currentScreen;
        if (current instanceof CommandGui)
        {
            ((CommandGui) current).setText(text);
        }
        else
        {
            mc.displayGuiScreen(new GuiChat(text));
        }
    }

}
