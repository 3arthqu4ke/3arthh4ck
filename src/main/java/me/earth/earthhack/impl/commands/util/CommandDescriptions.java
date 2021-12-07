package me.earth.earthhack.impl.commands.util;

import me.earth.earthhack.api.command.Command;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CommandDescriptions
{
    private static final Map<String, String> descriptions =
            new ConcurrentHashMap<>();

    public static void register(Command command, String description)
    {
        descriptions.put(command.getName(), description);
    }

    public static String getDescription(Command command)
    {
        return descriptions.get(command.getName());
    }

}
