package me.earth.earthhack.impl.commands.packet.arguments;

import me.earth.earthhack.api.command.PossibleInputs;
import me.earth.earthhack.api.util.TextUtil;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.commands.packet.AbstractArgument;
import me.earth.earthhack.impl.commands.packet.exception.ArgParseException;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import me.earth.earthhack.impl.util.thread.LookUpUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.IAttributeInstance;

public class AttributeArgument extends AbstractArgument<IAttributeInstance>
        implements Globals
{
    public AttributeArgument()
    {
        super(IAttributeInstance.class);
    }

    @Override
    public IAttributeInstance fromString(String argument)
            throws ArgParseException
    {
        if (mc.world == null)
        {
            throw new ArgParseException("Minecraft.World was null!");
        }

        String[] split = argument.split(",");
        if (split.length != 2)
        {
            throw new ArgParseException(
                    "Expected 2 Arguments for IAttributeInstance, but found: "
                            + split.length + "!");
        }

        Entity entity = find(split[0]);
        if (!(entity instanceof EntityLivingBase))
        {
            throw new ArgParseException("Couldn't parse Entity from "
                + split[0]
                + ", it either doesn't exist or is not an EntityLivingBase!");
        }

        IAttributeInstance attribute =
                find((EntityLivingBase) entity, split[1]);

        if (attribute == null)
        {
            throw new ArgParseException(
                "Couldn't parse IAttributeInstance from " + split[1] + "!");
        }

        return attribute;
    }

    @Override
    public PossibleInputs getPossibleInputs(String argument)
    {
        PossibleInputs inputs = PossibleInputs.empty();
        if (argument == null || argument.isEmpty() || mc.world == null)
        {
            return inputs.setRest("<IAttributeInstance:Entity,Attribute>");
        }

        String[] split = argument.split(",");
        if (split.length == 0 || split[0].isEmpty())
        {
            return inputs.setRest("<IAttributeInstance:Entity,Attribute>");
        }

        if (split.length > 2)
        {
            return inputs;
        }

        if ("$closest".startsWith(split[0].toLowerCase()))
        {
            if (split.length == 1)
            {
                return inputs.setCompletion(
                        TextUtil.substring("$closest", split[0].length()));
            }
        }

        String s = LookUpUtil.findNextPlayerName(split[0]);
        if (split.length == 1)
        {
            if (s != null)
            {
                return inputs.setCompletion(
                        TextUtil.substring(s, split[0].length()));
            }
            else
            {
                return inputs;
            }
        }

        Entity entity = find(split[0]);
        if (!(entity instanceof EntityLivingBase))
        {
            return inputs;
        }

        IAttributeInstance attribute =
                find((EntityLivingBase) entity, split[1]);
        if (attribute != null)
        {
            return inputs.setCompletion(
                TextUtil.substring(
                    attribute.getAttribute().getName(), split[1].length()));
        }

        return inputs;
    }

    private Entity find(String name)
    {
        if ("$closest".startsWith(name.toLowerCase()))
        {
            return EntityUtil.getClosestEnemy();
        }

        Entity entity = null;
        String s = LookUpUtil.findNextPlayerName(name);
        if (s != null)
        {
            entity = mc.world.getPlayerEntityByName(s);
        }

        if (entity == null)
        {
            try
            {
                int id = (int) Long.parseLong(name);
                entity = mc.world.getEntityByID(id);
            }
            catch (NumberFormatException e)
            {
                return null;
            }
        }

        return entity;
    }

    private IAttributeInstance find(EntityLivingBase base, String name)
    {
        for (IAttributeInstance instance :
                base.getAttributeMap().getAllAttributes())
        {
            if (TextUtil.startsWith(instance.getAttribute().getName(), name))
            {
                return instance;
            }
        }

        return null;
    }

}
