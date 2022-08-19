package me.earth.earthhack.impl.commands.hidden;

import me.earth.earthhack.api.command.Command;
import me.earth.earthhack.api.command.Completer;
import me.earth.earthhack.api.command.PossibleInputs;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.client.commands.Commands;
import me.earth.earthhack.impl.util.text.ChatUtil;
import me.earth.earthhack.impl.util.text.TextColor;
import me.earth.earthhack.pingbypass.input.Mouse;
import net.minecraft.client.gui.GuiChat;

public class HModulesCommand extends Command implements Globals
{
    public HModulesCommand()
    {
        super(new String[][]{{"hiddenmodule"}}, true);
    }

    @Override
    public void execute(String[] args)
    {
        if (args.length > 1)
        {
            String name = args[1];
            Module module = Managers.MODULES.getObject(name);
            if (module != null)
            {
                if (Mouse.isButtonDown(1))
                {
                    mc.displayGuiScreen(new GuiChat(
                            Commands.getPrefix()
                                    + module.getName()
                                    + " "));
                }
                else
                {
                    module.toggle();
                }
            }
            else
            {
                ChatUtil.sendMessage(TextColor.RED + "An error occurred.");
            }
        }
    }

    @Override
    public PossibleInputs getPossibleInputs(String[] args)
    {
        return PossibleInputs.empty();
    }

    @Override
    public Completer onTabComplete(Completer completer)
    {
        completer.setMcComplete(true);
        return completer;
    }

}
