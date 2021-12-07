package me.earth.earthhack.impl.managers.client.macro;

import me.earth.earthhack.impl.managers.chat.CommandManager;
import me.earth.earthhack.impl.modules.client.commands.Commands;

import java.util.function.BiConsumer;

public enum MacroType
{
    NORMAL((c, m) ->
    {
        if (m.commands.length > 0)
        {
            c.applyCommand(Commands.getPrefix() + m.commands[0]);
        }
    }),
    COMBINED((c, m) ->
    {
        if (m.commands.length > 0)
        {
            for (String command : m.commands)
            {
                c.applyCommand(Commands.getPrefix() + command);
            }
        }
    }),
    DELEGATE(COMBINED::execute),
    FLOW((c, m) ->
    {
        if (m.commands.length == 0)
        {
            return;
        }

        c.applyCommand(Commands.getPrefix() + m.commands[m.index]);
        m.index++;

        if (m.index >= m.commands.length)
        {
            m.index = 0;
        }
    });

    private final BiConsumer<CommandManager, Macro> behaviour;

    MacroType(BiConsumer<CommandManager, Macro> behaviour)
    {
        this.behaviour = behaviour;
    }

    public void execute(CommandManager manager, Macro macro)
    {
        behaviour.accept(manager, macro);
    }

    public static MacroType fromString(String name)
    {
        switch (name.toLowerCase())
        {
            case "normal":
                return NORMAL;
            case "combined":
                return COMBINED;
            case "flow":
                return FLOW;
            case "delegate":
                return DELEGATE;
        }

        throw new IllegalArgumentException("Couldn't parse MacroType: " + name);
    }

}
