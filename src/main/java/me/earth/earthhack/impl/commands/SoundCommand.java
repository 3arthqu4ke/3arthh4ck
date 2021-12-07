package me.earth.earthhack.impl.commands;

import me.earth.earthhack.api.command.Command;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.commands.util.CommandDescriptions;
import me.earth.earthhack.impl.core.mixins.audio.ISoundHandler;
import me.earth.earthhack.impl.util.text.ChatUtil;
import me.earth.earthhack.impl.util.text.TextColor;
import net.minecraft.client.audio.SoundManager;

public class SoundCommand extends Command implements Globals
{
    public SoundCommand()
    {
        super(new String[][]{{"sound"}});
        CommandDescriptions.register(this, "Reloads the SoundSystem.");
    }

    @Override
    public void execute(String[] args)
    {
        try
        {
            SoundManager soundManager = ((ISoundHandler) mc.getSoundHandler())
                                                           .getManager();
            soundManager.reloadSoundSystem();
            ChatUtil.sendMessage(TextColor.GREEN + "Reloaded SoundSystem.");
        }
        catch (Exception e)
        {
            ChatUtil.sendMessage(TextColor.RED
                                    + "Couldn't reload sound: "
                                    + e.getMessage());
            e.printStackTrace();
        }
    }

}
