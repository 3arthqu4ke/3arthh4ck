package me.earth.earthhack.impl.managers.client.macro;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.earth.earthhack.api.config.Jsonable;
import me.earth.earthhack.api.util.bind.Bind;
import me.earth.earthhack.impl.Earthhack;
import me.earth.earthhack.impl.managers.chat.CommandManager;

import java.util.*;

/**
 * A Macro representing another Macro for the sake of being
 * able to stack {@link FlowMacro}s and {@link CombinedMacro}s.
 */
public class DelegateMacro extends Macro
{
    private MacroType delegated = MacroType.DELEGATE;

    public DelegateMacro(String name, String macro)
    {
        super(name, Bind.none(), new String[]{"macro use " + macro});
    }

    /**
     * Returns <tt>true</tt> if another Macro exists within all the
     * macros of the given MacroManager which is not a DelegateMacro
     * and which references this Macro directly or indirectly.
     *
     * @param macros the MacroManager with the macros to check.
     * @return <tt>true</tt> if this macro is referenced.
     */
    public boolean isReferenced(MacroManager macros)
    {
        return isReferenced(macros, new HashSet<>());
    }

    private boolean isReferenced(MacroManager macros,
                                 Set<Macro> checked)
    {
        checked.add(this);
        for (Macro m : macros.getRegistered())
        {
            if (checked.contains(m))
            {
                continue;
            }

            for (String command : m.commands)
            {
                if (command.toLowerCase()
                        .contains(this.getName().toLowerCase()))
                {
                    if (m instanceof DelegateMacro)
                    {
                        if (((DelegateMacro) m).isReferenced(macros, checked))
                        {
                            return true;
                        }

                        // checked.clear(); Isnt required from
                        // my understanding here just leaving
                        // this here cause im not sure
                    }
                    else
                    {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    @Override
    public void execute(CommandManager manager) throws Error
    {
        this.delegated.execute(manager, this);
    }

    @Override
    public MacroType getType()
    {
        return MacroType.DELEGATE;
    }

    @Override
    public void fromJson(JsonElement element)
    {
        JsonObject object = element.getAsJsonObject();
        List<String> commands = new ArrayList<>();
        for (Map.Entry<String, JsonElement> entry : object.entrySet())
        {
            switch (entry.getKey().toLowerCase())
            {
                case "bind":
                    this.bind = Bind.fromString(
                                            entry.getValue().getAsString());
                    break;
                case "delegated":
                    this.delegated =  MacroType.fromString(
                                            entry.getValue().getAsString());
                    break;
                case "release":
                    this.release = Boolean.parseBoolean(
                                            entry.getValue().getAsString());
                case "type":
                    break;
                default:
                    commands.add(entry.getValue().getAsString());
            }
        }

        this.commands = commands.toArray(new String[0]);
    }

    @Override
    public String toJson()
    {
        return DelegateMacro.delegateToJson(delegated, commands);
    }

    public static DelegateMacro delegate(String name, Macro macro)
    {
        return new DelegateMacro(name, "")
        {
            @Override
            public void execute(CommandManager manager) throws Error
            {
                macro.execute(manager);
            }

            @Override
            public String[] getCommands()
            {
                return macro.getCommands();
            }

            @Override
            public void fromJson(JsonElement element)
            {
                Earthhack.getLogger().info("Anonymous delegates " + getName()
                    + " fromJson method was called. This shouldn't happen.");
            }

            @Override
            public String toJson()
            {
                return DelegateMacro.delegateToJson(macro.getType(),
                                                    macro.commands);
            }
        };
    }

    private static String delegateToJson(MacroType type, String[] commands)
    {
        JsonObject object = new JsonObject();
        object.add("bind", PARSER.parse("NONE"));
        object.add("type", PARSER.parse("DELEGATE"));
        object.add("delegated", PARSER.parse(type.name()));
        for (int i = 0; i < commands.length; i++)
        {
            object.add("command" + (i == 0 ? "" : i),
                    Jsonable.parse(commands[i]));
        }

        return object.toString();
    }

}
