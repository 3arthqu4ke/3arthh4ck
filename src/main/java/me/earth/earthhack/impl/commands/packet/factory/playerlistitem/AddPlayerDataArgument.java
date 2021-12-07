package me.earth.earthhack.impl.commands.packet.factory.playerlistitem;

import com.mojang.authlib.GameProfile;
import me.earth.earthhack.api.command.PossibleInputs;
import me.earth.earthhack.impl.commands.packet.AbstractArgument;
import me.earth.earthhack.impl.commands.packet.PacketArgument;
import me.earth.earthhack.impl.commands.packet.arguments.EnumArgument;
import me.earth.earthhack.impl.commands.packet.arguments.GameProfileArgument;
import me.earth.earthhack.impl.commands.packet.arguments.IntArgument;
import me.earth.earthhack.impl.commands.packet.arguments.TextComponentArgument;
import me.earth.earthhack.impl.commands.packet.exception.ArgParseException;
import net.minecraft.network.play.server.SPacketPlayerListItem;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.GameType;

public class AddPlayerDataArgument
        extends AbstractArgument<SPacketPlayerListItem.AddPlayerData>
{
    private static final GameProfileArgument GAME_PROFILE_ARGUMENT =
            new GameProfileArgument();
    private static final IntArgument INT_ARGUMENT =
            new IntArgument();
    private static final PacketArgument<GameType> GAME_TYPE_ARGUMENT =
            new EnumArgument<>(GameType.class);
    private static final TextComponentArgument TEXT_COMPONENT_ARGUMENT =
            new TextComponentArgument();
    private static final SPacketPlayerListItem PACKET =
            new SPacketPlayerListItem();

    public AddPlayerDataArgument()
    {
        super(SPacketPlayerListItem.AddPlayerData.class);
    }

    @Override
    public SPacketPlayerListItem.AddPlayerData fromString(String argument)
            throws ArgParseException
    {
        String[] split = argument.split(",");
        if (split.length < 4)
        {
            throw new ArgParseException(
                    "Expected 3+ Arguments for EntityPlayerMP, but found: "
                            + split.length + "!");
        }

        GameProfile profile = GAME_PROFILE_ARGUMENT.fromString(
                                                    split[0] + "," + split[1]);
        int latency = INT_ARGUMENT.fromString(split[2]);
        GameType gameType = GAME_TYPE_ARGUMENT.fromString(split[3]);

        ITextComponent component = null;
        if (split.length >= 5)
        {
            StringBuilder builder = new StringBuilder(split[4]);
            for (int i = 5; i < split.length; i++)
            {
                builder.append(",").append(split[i]);
            }

            component = TEXT_COMPONENT_ARGUMENT.fromString(builder.toString());
        }

        return PACKET.new AddPlayerData(profile, latency, gameType, component);
    }

    @Override
    public PossibleInputs getPossibleInputs(String argument)
    {
        PossibleInputs inputs = PossibleInputs.empty();
        if (argument == null || argument.isEmpty())
        {
            return inputs.setRest(
                "<AddPlayerData:GameProfile,latency,GameType,TextComponent>");
        }

        String[] split = argument.split(",");
        switch (split.length)
        {
            case 0:
                return inputs.setRest(
                  "<AddPlayerData:GameProfile,latency,GameType,TextComponent>");
            case 1:
                PossibleInputs g =
                        GAME_PROFILE_ARGUMENT.getPossibleInputs(split[0]);

                return inputs.setCompletion(g.getCompletion())
                    .setRest(g.getRest() + ",latency,GameType,TextComponent");
            case 2:
                PossibleInputs g2 =
                    GAME_PROFILE_ARGUMENT.getPossibleInputs(split[0] + split[1]);

                return inputs.setCompletion(g2.getCompletion())
                     .setRest(g2.getRest() + ",latency,GameType,TextComponent");
            case 3:
                return inputs.setRest(",GameType,TextComponent");
            case 4:
                PossibleInputs type =
                        GAME_TYPE_ARGUMENT.getPossibleInputs(split[2]);

                return inputs.setCompletion(type.getCompletion())
                             .setRest(type.getRest() + ",TextComponent");
            default:
        }

        return inputs;
    }

}

