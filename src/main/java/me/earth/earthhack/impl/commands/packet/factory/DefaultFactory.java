package me.earth.earthhack.impl.commands.packet.factory;

import me.earth.earthhack.api.command.Completer;
import me.earth.earthhack.api.command.PossibleInputs;
import me.earth.earthhack.api.util.TextUtil;
import me.earth.earthhack.impl.commands.packet.PacketArgument;
import me.earth.earthhack.impl.commands.packet.PacketCommand;
import me.earth.earthhack.impl.commands.packet.exception.ArgParseException;
import me.earth.earthhack.impl.commands.packet.generic.GenericArgument;
import me.earth.earthhack.impl.commands.packet.util.BufferUtil;
import me.earth.earthhack.impl.util.helpers.command.CustomCompleterResult;
import me.earth.earthhack.impl.util.text.TextColor;
import net.minecraft.network.Packet;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class DefaultFactory implements PacketFactory
{
    private final PacketCommand command;

    public DefaultFactory(PacketCommand command)
    {
        this.command = command;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Packet<?> create(Class<? extends Packet<?>> clazz, String[] args)
            throws ArgParseException
    {
        int ctrIndex;

        try
        {
            ctrIndex = Integer.parseInt(args[2]);
        }
        catch (NumberFormatException e)
        {
            throw new ArgParseException(
                    "Couldn't parse constructor index from " + args[2] + "!");
        }

        Constructor<?>[] ctrs = clazz.getDeclaredConstructors();
        if (ctrIndex < 0 || ctrIndex >= ctrs.length)
        {
            throw new ArgParseException("Constructor index out of bounds!" +
                    " Expected 0"
                    + (ctrs.length == 0 ? "" : "-" + (ctrs.length - 1))
                    + " but found: " + ctrIndex);
        }

        Constructor<? extends Packet<?>> ctr =
                (Constructor<? extends Packet<?>>) ctrs[ctrIndex];

        Class<?>[] types = ctr.getParameterTypes();
        if (args.length - 3 != types.length)
        {
            throw new ArgParseException("Expected " + types.length
                    + " parameters but found: " + (args.length - 3) + "!");
        }

        Object[] parameters = parseParameters(ctr, clazz, args, types);

        try
        {
            return instantiate(ctr, parameters);
        }
        catch (IllegalArgumentException
                | InstantiationException
                | IllegalAccessException
                | InvocationTargetException
                | NoSuchFieldException e)
        {
            BufferUtil.release(parameters);
            throw new ArgParseException(
                    "Couldn't instantiate Packet: " + e.getMessage());
        }
    }

    @Override
    public PossibleInputs getInputs(Class<? extends Packet<?>> clazz,
                                    String[] args)
    {
        PossibleInputs inputs = PossibleInputs.empty();
        if (args.length == 2)
        {
            String name = command.getName(clazz);
            return inputs.setRest(" <index>")
                    .setCompletion(TextUtil.substring(name, args[1].length()));
        }

        Constructor<?>[] ctrs = clazz.getDeclaredConstructors();
        if (args[2].isEmpty()) // this no work :(
        {
            int index = -1;
            for (int i = 0; i < ctrs.length; i++)
            {
                if (index == -1
                    || ctrs[i].getParameterTypes().length != 0
                            && ctrs[index].getParameterTypes().length == 0)
                {
                    index = i;
                }
            }

            return inputs.setCompletion(index + "")
                         .setRest(getRest(args, ctrs[index], clazz));
        }

        int index;

        try
        {
            index = Integer.parseInt(args[2]);
        }
        catch (NumberFormatException e)
        {
            return inputs.setRest(
                    TextColor.RED + " <Can't parse constructor index!>");
        }

        if (index < 0 || index >= ctrs.length)
        {
            return inputs.setRest(TextColor.RED
                    + " <Constructor index out of bounds! Expected 0"
                    + (ctrs.length == 0 ? "" : "-" + (ctrs.length - 1))
                    + " but found: " + index + ">");
        }

        Constructor<?> ctr = ctrs[index];
        int i = args.length - 3;
        Class<?>[] types = ctr.getParameterTypes();
        if (i > types.length)
        {
            return inputs.setRest(TextColor.RED
                    + " <Too many parameters! Expected " + types.length
                    + " but found: " + i + ">");
        }

        PossibleInputs compl = getCompletion(args, ctr, clazz);
        return inputs.setCompletion(compl.getCompletion())
                     .setRest(compl.getRest() + getRest(args, ctr, clazz));
    }

    @Override
    public CustomCompleterResult onTabComplete(Completer completer)
    {
        return CustomCompleterResult.PASS;
    }

    protected Packet<?> instantiate(Constructor<? extends Packet<?>> ctr,
                                    Object[] parameters)
            throws IllegalArgumentException,
            InvocationTargetException,
            InstantiationException,
            IllegalAccessException, NoSuchFieldException
    {
        ctr.setAccessible(true);
        return ctr.newInstance(parameters);
    }

    protected Object[] parseParameters(Constructor<? extends Packet<?>> ctr,
                                       Class<? extends Packet<?>> clazz,
                                       String[] args,
                                       Class<?>...types)
            throws ArgParseException
    {
        Object[] parameters = new Object[types.length];
        for (int i = 0; i < types.length; i++)
        {
            PacketArgument<?> argument = getArgument(ctr, types[i], clazz, i);
            if (argument == null)
            {
                throw new ArgParseException("Couldn't find Argument for "
                        + types[i].getName() + "!");
            }

            try
            {
                Object o = argument.fromString(args[i + 3]);
                parameters[i] = o;
            }
            catch (ArgParseException e)
            {
                BufferUtil.release(parameters);
                throw e;
            }
        }

        return parameters;
    }

    protected PossibleInputs getCompletion(String[] args,
                                           Constructor<?> ctr,
                                           Class<? extends Packet<?>> clazz)
    {
        int i = args.length - 4;
        Class<?>[] types = ctr.getParameterTypes();
        if (i < 0 || i >= types.length)
        {
            return PossibleInputs.empty();
        }

        PacketArgument<?> argument = getArgument(ctr, types[i], clazz, i);
        if (argument == null)
        {
            return PossibleInputs.empty();
        }

        return argument.getPossibleInputs(args[args.length - 1]);
    }

    protected String getRest(String[] args,
                             Constructor<?> ctr,
                             Class<? extends Packet<?>> clazz)
    {
        int start = args.length - 3;
        Class<?>[] types = ctr.getParameterTypes();
        if (start < 0 || start >= types.length)
        {
            return "";
        }

        StringBuilder builder = new StringBuilder(" ");
        for (int i = start; i < types.length; i++)
        {
            PacketArgument<?> argument = getArgument(ctr, types[i], clazz, i);
            if (argument == null)
            {
                return TextColor.RED + " <Couldn't find Argument for "
                        + types[i].getName() + "!>";
            }

            builder.append(argument.getPossibleInputs(null).getRest())
                   .append(" ");
        }

        return builder.toString();
    }

    protected PacketArgument<?> getArgument(Constructor<?> ctr,
                                            Class<?> type,
                                            Class<? extends Packet<?>> clazz,
                                            int i)
    {
        List<GenericArgument<?>> gs = getGeneric(clazz);
        if (gs != null)
        {
            for (GenericArgument<?> g : gs)
            {
                if (g.getConstructor().equals(ctr) && i == g.getArgIndex())
                {
                    return g;
                }
            }
        }

        return getArgumentForType(type);
    }

    protected List<GenericArgument<?>> getGeneric(
            Class<? extends Packet<?>> clazz)
    {
        return command.getGenerics().get(clazz);
    }

    protected PacketArgument<?> getArgumentForType(Class<?> type)
    {
        return command.getArguments().get(type);
    }
}
