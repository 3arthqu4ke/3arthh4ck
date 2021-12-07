package me.earth.earthhack.impl.commands;

import me.earth.earthhack.api.command.Command;
import me.earth.earthhack.impl.util.network.ServerUtil;

public class DisconnectCommand extends Command
{
    public DisconnectCommand()
    {
        super(new String[][]{{"disconnect"}});
    }

    @Override
    public void execute(String[] args)
    {
        ServerUtil.disconnectFromMC("Disconnected.");
    }

}
