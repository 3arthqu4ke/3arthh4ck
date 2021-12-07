package me.earth.earthhack.impl.commands.packet.arguments;

import me.earth.earthhack.api.command.PossibleInputs;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.commands.packet.AbstractArgument;
import me.earth.earthhack.impl.commands.packet.exception.ArgParseException;
import net.minecraft.entity.item.EntityXPOrb;

public class EntityXPOrbArgument extends AbstractArgument<EntityXPOrb>
        implements Globals
{
    public EntityXPOrbArgument()
    {
        super(EntityXPOrb.class);
    }

    @Override
    public EntityXPOrb fromString(String argument) throws ArgParseException
    {
        if (mc.world == null || mc.player == null)
        {
            throw new ArgParseException("Minecraft.World was null!");
        }

        String[] split = argument.split(",");
        if (split.length == 0)
        {
            throw new ArgParseException("XP-Orb was empty!");
        }

        int id;

        try
        {
            id = Integer.parseInt(split[0]);
        }
        catch (NumberFormatException e)
        {
            throw new ArgParseException(
                    "Could not parse XP-ID from " + split[0] + "!");
        }

        int amount = 1;
        if (split.length > 1)
        {
            try
            {
                amount = Integer.parseInt(split[1]);
            }
            catch (NumberFormatException e)
            {
                throw new ArgParseException(
                        "Could not parse XP-Amount from " + split[1] + "!");
            }
        }

        double x = split.length > 2 ? tryParse(split[2], "x") : mc.player.posX;
        double y = split.length > 3 ? tryParse(split[3], "y") : mc.player.posY;
        double z = split.length > 4 ? tryParse(split[4], "z") : mc.player.posZ;

        EntityXPOrb entity = new EntityXPOrb(mc.world, x, y, z, amount);
        entity.setEntityId(id);
        return entity;
    }

    @Override
    public PossibleInputs getPossibleInputs(String argument)
    {
        PossibleInputs inputs = PossibleInputs.empty();
        if (argument == null || argument.isEmpty())
        {
            return inputs.setRest("<XP-Orb:id,amount,x,y,z>");
        }

        String[] split = argument.split(",");
        switch (split.length)
        {
            case 0:
                return inputs.setRest("<XP-Orb:id,amount,x,y,z>");
            case 1:
                return inputs.setCompletion(",").setRest("amount,x,y,z>");
            case 2:
                return inputs.setCompletion(",").setRest("x,y,z>");
            case 3:
                return inputs.setCompletion(",").setRest("y,z>");
            case 4:
                return inputs.setCompletion(",").setRest("z>");
            default:
        }

        return inputs;
    }

    private double tryParse(String string, String message)
            throws ArgParseException
    {
        try
        {
            return Double.parseDouble(string);
        }
        catch (NumberFormatException e)
        {
            throw new ArgParseException(
                    "Could not parse " + message + " from " + string);
        }
    }

}
