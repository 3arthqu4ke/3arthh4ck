package me.earth.earthhack.impl.managers.client.macro;

import me.earth.earthhack.api.util.bind.Bind;

public class CombinedMacro extends Macro
{
    public CombinedMacro(String name,
                         Bind bind,
                         Macro...macros)
    {
        super(name, bind, MacroUtil.concatenateCommands(macros));
    }

    @Override
    public MacroType getType()
    {
        return MacroType.COMBINED;
    }

}
