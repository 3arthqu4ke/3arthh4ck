package me.earth.earthhack.impl.commands.hidden;

import me.earth.earthhack.api.command.Command;
import me.earth.earthhack.api.command.Completer;
import me.earth.earthhack.api.command.PossibleInputs;
import me.earth.earthhack.api.util.interfaces.Globals;

// TODO: THIS
public class PhobosDotConfirmCommand extends Command implements Globals {
    public PhobosDotConfirmCommand() {
        super(new String[][]{{"phobos.confirm"}, {"player"}}, true);
    }

    @Override
    public void execute(String[] args) {
        if (args.length > 1) {
            //String player = mc.player
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
