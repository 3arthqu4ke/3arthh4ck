package me.earth.earthhack.impl.managers.client.macro;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.earth.earthhack.api.config.Jsonable;
import me.earth.earthhack.api.util.bind.Bind;
import me.earth.earthhack.api.util.interfaces.Nameable;
import me.earth.earthhack.impl.managers.chat.CommandManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Macro implements Jsonable, Nameable
{
    protected boolean release;
    protected final String name;
    protected String[] commands;
    protected int index;
    protected Bind bind;

    public Macro(String name, Bind bind, String[] commands)
    {
        this.name = name;
        this.bind = bind;
        this.commands = commands;
    }

    public void execute(CommandManager manager) throws Error
    {
        this.getType().execute(manager, this);
    }

    public String[] getCommands()
    {
        if (commands.length == 0)
        {
            return new String[]{""};
        }

        return commands;
    }

    public Bind getBind()
    {
        return bind;
    }

    public MacroType getType()
    {
        return MacroType.NORMAL;
    }

    public boolean isRelease()
    {
        return release;
    }

    public void setRelease(boolean release)
    {
        this.release = release;
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public boolean equals(Object o)
    {
        return o instanceof Macro && ((Macro) o).getName()
                .equalsIgnoreCase(this.getName());
    }

    @Override
    public int hashCode()
    {
        return name.toLowerCase().hashCode();
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
                    this.bind = Bind.fromString(entry.getValue().getAsString());
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
        JsonObject object = new JsonObject();
        object.add("bind", PARSER.parse(bind.toString()));
        object.add("type", PARSER.parse(this.getType().name()));
        object.add("release", PARSER.parse(this.release + ""));
        for (int i = 0; i < commands.length; i++)
        {
            object.add("command" + (i == 0 ? "" : i),
                       Jsonable.parse(commands[i]));
        }

        return object.toString();
    }

}
