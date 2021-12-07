package me.earth.earthhack.impl.commands;

import me.earth.earthhack.api.command.Command;
import me.earth.earthhack.api.command.PossibleInputs;
import me.earth.earthhack.api.util.TextUtil;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.commands.util.CommandDescriptions;
import me.earth.earthhack.impl.util.text.ChatUtil;
import me.earth.earthhack.impl.util.text.TextColor;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.GameType;

import java.util.Objects;

public class GameModeCommand extends Command implements Globals
{
    public GameModeCommand()
    {
        super(new String[][]{{"gamemode"},
                {"survival", "creative", "adventure", "spectator"},
                {"fake"}});
        CommandDescriptions.register(this, "Allows you to change or," +
                " if the 3rd argument is \"fake\", fake your gamemode.");
    }

    @Override
    public void execute(String[] args)
    {
        if (mc.player == null)
        {
            ChatUtil.sendMessage(TextColor.RED
                    + "You need to be ingame to use this command!");
            return;
        }

        if (args.length == 1)
        {
            ChatUtil.sendMessage(TextColor.RED + "Specify a gamemode.");
        }
        else
        {
            GameType gameType = tryToParseFromID(args[1]);
            if (gameType == null)
            {
                gameType = getGameTypeStartingWith(args[1]);
                if (gameType == null)
                {
                    ChatUtil.sendMessage(TextColor.RED
                                     + "GameType " + TextColor.WHITE + args[1]
                                     + TextColor.RED + " not found.");
                    return;
                }
            }

            boolean fake = false;
            if (args.length > 2)
            {
                fake = args[2].equalsIgnoreCase("fake");
            }

            if (fake && mc.playerController != null)
            {
                // when spectator we could actually use freecam here instead
                ChatUtil.sendMessage(TextColor.GREEN
                    + "Setting your Client GameType to: " +
                    TextColor.AQUA + TextUtil.capitalize(gameType.getName()));

                mc.playerController.setGameType(gameType);
            }
            else if (mc.player != null)
            {
                if (mc.isSingleplayer())
                {
                    EntityPlayerMP player = Objects.requireNonNull(
                            mc.getIntegratedServer())
                              .getPlayerList()
                              .getPlayerByUUID(mc.player.getUniqueID());
                    //noinspection ConstantConditions
                    if (player != null)
                    {
                        player.setGameType(gameType);
                        ChatUtil.sendMessage(TextColor.GREEN
                                + "Gamemode set to "
                                + TextColor.WHITE
                                + gameType.getName()
                                + TextColor.GREEN
                                + ".");
                        return;
                    }
                }

                String message = "/gamemode " + gameType.getName();
                ChatUtil.sendMessage(TextColor.GREEN + message);
                mc.player.sendChatMessage(message);
            }
        }
    }

    @Override
    public PossibleInputs getPossibleInputs(String[] args)
    {
        PossibleInputs inputs = super.getPossibleInputs(args);
        if (args.length == 2)
        {
            boolean isNumber = true;
            GameType gameType = tryToParseFromID(args[1]);
            if (gameType == null)
            {
                isNumber = false;
                gameType = getGameTypeStartingWith(args[1]);
            }

            if (gameType == null)
            {
                return inputs.setCompletion("")
                             .setRest(TextColor.RED + " not found");
            }

            if (isNumber)
            {
                return inputs.setRest(" (" + gameType.getName() + ") <fake>");
            }

            if (gameType.getName().equalsIgnoreCase(args[1]))
            {
                return inputs.setRest(" <fake>");
            }

            return inputs.setCompletion(
                            TextUtil.substring(gameType.getName(),
                                                   args[1].length()))
                         .setRest("");
        }

        return inputs;
    }

    private GameType getGameTypeStartingWith(String arg)
    {
        for (GameType gameType : GameType.values())
        {
            if (gameType.getName().startsWith(arg.toLowerCase()))
            {
                return gameType;
            }
        }

        return null;
    }

    @SuppressWarnings("ConstantConditions")
    private GameType tryToParseFromID(String idString)
    {
        try
        {
            int id = Integer.parseInt(idString);
            return GameType.parseGameTypeWithDefault(id, null);
        }
        catch (NumberFormatException ignored)
        {
            return null;
        }
    }

}
