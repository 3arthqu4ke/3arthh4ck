package me.earth.earthhack.impl.commands;

import me.earth.earthhack.api.command.Command;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.util.text.ChatUtil;
import me.earth.earthhack.impl.util.text.TextColor;
import me.earth.earthhack.pingbypass.PingBypass;

public class QuitCommand extends Command implements Globals
{
    public QuitCommand()
    {
        // TODO: abrupt option which makes
        //  minecraft stop without saving config etc?
        super(new String[][]{{"quit"}});
    }

    @Override
    public void execute(String[] args)
    {
        if (PingBypass.isServer() && !PingBypass.CONFIG.enableQuitCommand())
        {
            ChatUtil.sendMessage(
                TextColor.RED + "The Quit command is not enabled" +
                    " on this PingBypass server!");
            return;
        }

        mc.shutdown();
    }

}
