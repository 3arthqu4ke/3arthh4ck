package me.earth.earthhack.impl.commands;

import me.earth.earthhack.api.command.Command;
import me.earth.earthhack.impl.commands.util.CommandDescriptions;
import me.earth.earthhack.impl.util.text.ChatUtil;
import me.earth.earthhack.impl.util.text.TextColor;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Paths;

public class FolderCommand extends Command
{
    public FolderCommand()
    {
        super(new String[][]{{"folder"}});
        CommandDescriptions.register(this, "Opens the 3arthh4ck folder.");
    }

    @Override
    public void execute(String[] args)
    {
        try
        {
            Desktop.getDesktop().open(Paths.get("earthhack").toFile());
        }
        catch (IOException e)
        {
            ChatUtil.sendMessage(TextColor.RED + "An error occurred.");
            e.printStackTrace();
        }
    }

}