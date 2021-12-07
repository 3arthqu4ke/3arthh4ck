package me.earth.earthhack.impl.managers.client.macro;

import me.earth.earthhack.api.util.bind.Bind;

public class FlowMacro extends Macro
{
    public FlowMacro(String name, Bind bind, Macro...macros)
    {
        super(name, bind, MacroUtil.concatenateCommands(macros));
    }

    public MacroType getType()
    {
        return MacroType.FLOW;
    }

}
