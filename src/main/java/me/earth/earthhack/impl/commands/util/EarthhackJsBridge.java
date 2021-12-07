package me.earth.earthhack.impl.commands.util;

import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.client.commands.Commands;

@SuppressWarnings("unused")
public class EarthhackJsBridge implements Globals
{
    public void command(String command)
    {
        mc.addScheduledTask(() ->
                Managers.COMMANDS
                        .applyCommand(Commands.getPrefix() + command));
    }

    public boolean isEnabled(String module)
    {
        return Managers.MODULES.getObject(module).isEnabled();
    }

}
