package me.earth.earthhack.impl.managers.client.macro;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MacroUtil
{
    public static String[] concatenateCommands(Macro...macros)
    {
        List<String> commands = new ArrayList<>();
        for (Macro macro : macros)
        {
            commands.addAll(Arrays.asList(macro.getCommands()));
        }

        return commands.toArray(new String[0]);
    }

}
