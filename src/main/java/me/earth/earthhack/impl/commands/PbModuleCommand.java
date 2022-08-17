package me.earth.earthhack.impl.commands;

import me.earth.earthhack.api.command.Command;
import me.earth.earthhack.api.command.PossibleInputs;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.commands.util.CommandDescriptions;
import me.earth.earthhack.impl.util.text.ChatUtil;
import me.earth.earthhack.pingbypass.PingBypass;

public class PbModuleCommand extends Command implements Globals
{
    private final ModuleCommand moduleCommand =
        new ModuleCommand(PingBypass.MODULES);

    public PbModuleCommand()
    {
        super(new String[][]{{"pb"}, {"module"}, {"setting"}, {"value"}});
        CommandDescriptions.register(
            this, "Configure the modules you see in the PB-Gui.");
    }

    @Override
    public void execute(String[] args)
    {
        String[] array = getActualArgs(args);
        if (array.length == 0) {
            ChatUtil.sendMessage(
                "Use this command to configure the modules which will run" +
                    " on the PingBypass server.");
            return;
        }

        moduleCommand.execute(array);
    }

    @Override
    public PossibleInputs getPossibleInputs(String[] args) {
        if (args.length <= 1) {
            return super.getPossibleInputs(args);
        }

        String[] array = getActualArgs(args);
        return moduleCommand.getPossibleInputs(array);
    }

    // TODO: tab complete from the ModuleCommand?

    private String[] getActualArgs(String[] args) {
        if (args.length == 0) {
            return new String[0];
        }

        String[] array = new String[args.length - 1];
        System.arraycopy(args, 1, array, 0, array.length);
        return array;
    }

}
