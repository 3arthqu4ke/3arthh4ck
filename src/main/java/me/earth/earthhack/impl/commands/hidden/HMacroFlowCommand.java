package me.earth.earthhack.impl.commands.hidden;

import me.earth.earthhack.api.util.bind.Bind;
import me.earth.earthhack.impl.commands.abstracts.AbstractMultiMacroCommand;
import me.earth.earthhack.impl.managers.client.macro.FlowMacro;
import me.earth.earthhack.impl.managers.client.macro.Macro;

public class HMacroFlowCommand extends AbstractMultiMacroCommand<FlowMacro>
{
    public HMacroFlowCommand()
    {
        super(
           new String[][]{{"flow"}, {"macro"}, {"macro"}, {"..."}},
           "FlowMacro",
           "Please specify 2 or more macros that should flow into each other.");
    }

    @Override
    protected FlowMacro getMacro(String name, Bind bind, Macro... macros)
    {
        return new FlowMacro(name, bind, macros);
    }

}
