package me.earth.earthhack.impl.commands.packet.arguments;

import me.earth.earthhack.api.command.PossibleInputs;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.commands.packet.AbstractArgument;
import me.earth.earthhack.impl.commands.packet.exception.ArgParseException;
import net.minecraft.entity.player.PlayerCapabilities;

import static me.earth.earthhack.impl.commands.packet.exception.ArgParseException.tryDouble;

public class PlayerCapabilitiesArgument extends
        AbstractArgument<PlayerCapabilities> implements Globals
{
    private static final BooleanArgument BOOLEAN = new BooleanArgument();

    public PlayerCapabilitiesArgument()
    {
        super(PlayerCapabilities.class);
    }

    @Override
    public PlayerCapabilities fromString(String argument)
            throws ArgParseException
    {
        if (mc.player == null)
        {
            throw new ArgParseException("Minecraft.Player was null!");
        }

        String[] split = argument.split(",");
        boolean damage = split.length > 0
                ? Boolean.parseBoolean(split[0])
                : mc.player.capabilities.disableDamage;
        boolean flying = split.length > 1
                ? Boolean.parseBoolean(split[1])
                : mc.player.capabilities.isFlying;
        boolean allow = split.length > 2
                ? Boolean.parseBoolean(split[2])
                : mc.player.capabilities.allowFlying;
        boolean creative = split.length > 3
                ? Boolean.parseBoolean(split[3])
                : mc.player.capabilities.isCreativeMode;
        boolean edit = split.length > 4
                ? Boolean.parseBoolean(split[4])
                : mc.player.capabilities.allowEdit;

        float flySpeed = split.length > 5
                ? (float) tryDouble(split[5], "speed")
                : mc.player.capabilities.getFlySpeed();

        float walkSpeed = split.length > 6
                ? (float) tryDouble(split[5], "walk")
                : mc.player.capabilities.getWalkSpeed();


        PlayerCapabilities playerCapabilities = new PlayerCapabilities();
        playerCapabilities.disableDamage  = damage;
        playerCapabilities.isFlying       = flying;
        playerCapabilities.allowFlying    = allow;
        playerCapabilities.isCreativeMode = creative;
        playerCapabilities.allowEdit      = edit;

        playerCapabilities.setFlySpeed(flySpeed);
        playerCapabilities.setPlayerWalkSpeed(walkSpeed);
        return playerCapabilities;
    }

    @Override
    public PossibleInputs getPossibleInputs(String argument)
    {
        PossibleInputs inputs = PossibleInputs.empty();
        if (argument == null || argument.isEmpty())
        {
            return inputs.setRest("<PlayerCapabilities:damage,flying,allow," +
                    "creative,edit,speed,walk>");
        }

        String[] split = argument.split(",");
        if (split.length == 0)
        {
            return inputs.setRest("<PlayerCapabilities:damage,flying,allow," +
                    "creative,edit,speed,walk>");
        }

        int length = split.length;
        if (split[split.length - 1].isEmpty())
        {
            length -= 1;
        }

        switch (length)
        {
            case 0:
                return inputs.setRest("damage,flying,allow," +
                        "creative,edit,speed,walk>");
            case 1:
                inputs.setRest("flying,allow,creative,edit,speed,walk>");
                break;
            case 2:
                inputs.setRest("allow,creative,edit,speed,walk>");
                break;
            case 3:
                inputs.setRest("creative,edit,speed,walk>");
                break;
            case 4:
                inputs.setRest("edit,speed,walk");
                break;
            case 5:
                inputs.setRest("speed,walk");
            case 6:
                inputs.setRest("walk");
            default:
        }

        if (split.length < 6)
        {
            String last = split[split.length - 1];
            if (last.isEmpty())
            {
                return inputs;
            }

            PossibleInputs bool = BOOLEAN.getPossibleInputs(last);
            inputs.setCompletion(bool.getCompletion() + ",");
        }
        else if (length < 7)
        {
            inputs.setCompletion(",");
        }

        return inputs;
    }

}
