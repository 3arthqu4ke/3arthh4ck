package me.earth.earthhack.impl.commands.packet.arguments;

import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.commands.packet.AbstractArgument;
import me.earth.earthhack.impl.commands.packet.exception.ArgParseException;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.CombatTracker;

public class CombatTrackerArgument extends AbstractArgument<CombatTracker>
        implements Globals
{
    public CombatTrackerArgument()
    {
        super(CombatTracker.class);
    }

    @Override
    public CombatTracker fromString(String argument) throws ArgParseException
    {
        if (mc.world == null)
        {
            throw new ArgParseException("Minecraft.World was null!");
        }

        EntityLivingBase entity = AbstractEntityArgument.getEntity(argument,
                                                        EntityLivingBase.class);
        if (entity == null)
        {
            throw new ArgParseException(
                    "Could not parse " + argument + " to EntityLivingBase!");
        }

        return entity.getCombatTracker();
    }

}
