package me.earth.earthhack.impl.commands.hidden;

import me.earth.earthhack.api.util.bind.Bind;
import me.earth.earthhack.impl.commands.abstracts.AbstractMultiMacroCommand;
import me.earth.earthhack.impl.managers.client.macro.CombinedMacro;
import me.earth.earthhack.impl.managers.client.macro.Macro;

public class HMacroCombineCommand extends
        AbstractMultiMacroCommand<CombinedMacro>
{
    public HMacroCombineCommand()
    {
        super(new String[][]{{"combine"}, {"macro"}, {"macro"}, {"..."}},
              "CombinedMacro",
              "Please specify 2 or more macros to combine.");
    }

    @Override
    protected CombinedMacro getMacro(String name, Bind bind, Macro... macros)
    {
        return new CombinedMacro(name, bind, macros);
    }

}
