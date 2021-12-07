package me.earth.earthhack.impl.commands.packet.arguments;

import me.earth.earthhack.api.command.PossibleInputs;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.commands.packet.AbstractArgument;
import me.earth.earthhack.impl.commands.packet.PacketArgument;
import me.earth.earthhack.impl.commands.packet.exception.ArgParseException;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import static me.earth.earthhack.impl.commands.packet.exception.ArgParseException.tryDouble;
import static me.earth.earthhack.impl.commands.packet.exception.ArgParseException.tryLong;

public class EntityPaintingArgument extends AbstractArgument<EntityPainting>
        implements Globals
{
    private static final PacketArgument<EnumFacing> FACING_ARGUMENT =
            new EnumArgument<>(EnumFacing.class);

    public EntityPaintingArgument()
    {
        super(EntityPainting.class);
    }

    @Override
    public EntityPainting fromString(String argument) throws ArgParseException
    {
        if (mc.world == null || mc.player == null)
        {
            throw new ArgParseException("Minecraft.World was null!");
        }

        String[] split = argument.split(",");
        if (split.length < 1)
        {
            throw new ArgParseException(
                    "At least define an ID for the Painting!");
        }

        int id = (int) tryLong(split[0], "id");
        double x = split.length > 1 ? tryDouble(split[1], "x") : mc.player.posX;
        double y = split.length > 2 ? tryDouble(split[2], "y") : mc.player.posY;
        double z = split.length > 3 ? tryDouble(split[3], "z") : mc.player.posZ;

        EnumFacing facing = split.length > 4
                ? FACING_ARGUMENT.fromString(split[4])
                : EnumFacing.UP;

        BlockPos pos = new BlockPos(x, y, z);
        EntityPainting painting = new EntityPainting(mc.world, pos, facing);
        painting.setEntityId(id);
        return painting;
    }

    @Override
    public PossibleInputs getPossibleInputs(String argument)
    {
        PossibleInputs inputs = PossibleInputs.empty();
        if (argument == null || argument.isEmpty())
        {
            return inputs.setRest("<Painting:id,x,y,z,facing>");
        }

        String[] split = argument.split(",");
        switch (split.length)
        {
            case 0:
                return inputs.setRest("<Painting:id,x,y,z,facing>");
            case 1:
                return inputs.setCompletion(",").setRest("x,y,z,facing>");
            case 2:
                return inputs.setCompletion(",").setRest("y,z,facing>");
            case 3:
                return inputs.setCompletion(",").setRest("z,facing>");
            case 4:
                return inputs.setCompletion(",").setRest("facing>");
            case 5:
                return FACING_ARGUMENT.getPossibleInputs(argument);
            default:
        }

        return inputs;
    }

}
