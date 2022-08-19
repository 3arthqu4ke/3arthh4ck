package me.earth.earthhack.impl.commands.packet.factory.playerlistitem;

import me.earth.earthhack.api.command.PossibleInputs;
import me.earth.earthhack.impl.commands.packet.PacketArgument;
import me.earth.earthhack.impl.commands.packet.PacketCommand;
import me.earth.earthhack.impl.commands.packet.arguments.EnumArgument;
import me.earth.earthhack.impl.commands.packet.array.FunctionArrayArgument;
import me.earth.earthhack.impl.commands.packet.exception.ArgParseException;
import me.earth.earthhack.impl.commands.packet.factory.DefaultFactory;
import me.earth.earthhack.impl.commands.packet.generic.GenericArgument;
import me.earth.earthhack.impl.commands.packet.generic.GenericIterableArgument;
import me.earth.earthhack.impl.util.misc.ReflectionUtil;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketPlayerListItem;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class SPacketPlayerListItemFactory extends DefaultFactory
{
    private static final
        PacketArgument<SPacketPlayerListItem.AddPlayerData>
            ARGUMENT;
    private static final
        PacketArgument<SPacketPlayerListItem.Action>
            ACTION_ARGUMENT;
    private static final
        PacketArgument<SPacketPlayerListItem.AddPlayerData[]>
            ARRAY_ARGUMENT;
    private static final
        GenericIterableArgument<SPacketPlayerListItem.AddPlayerData>
            ITERABLE_ARGUMENT;
    private static final
        List<GenericArgument<?>>
            GENERICS;
    static
    {
        ARGUMENT =
            new AddPlayerDataArgument();

        ACTION_ARGUMENT =
            new EnumArgument<>(SPacketPlayerListItem.Action.class);

        ARRAY_ARGUMENT =
            new FunctionArrayArgument<>(
                SPacketPlayerListItem.AddPlayerData[].class, ARGUMENT,
                SPacketPlayerListItem.AddPlayerData[]::new);
        try
        {
            Constructor<SPacketPlayerListItem> ctr = SPacketPlayerListItem.class
                .getDeclaredConstructor(SPacketPlayerListItem.Action.class,
                                        Iterable.class);

            ITERABLE_ARGUMENT = new GenericIterableArgument<>(ctr, 1, ARGUMENT);
        }
        catch (NoSuchMethodException e)
        {
            throw new IllegalStateException(
                "Couldn't find Iterable Constructor in SPacketPlayerListItem!");
        }

        GENERICS = new ArrayList<>(1);
        GENERICS.add(ITERABLE_ARGUMENT);
    }

    public SPacketPlayerListItemFactory(PacketCommand command)
    {
        super(command);
    }

    @Override
    public Packet<?> create(Class<? extends Packet<?>> clazz, String[] args)
            throws ArgParseException
    {
        if (!SPacketPlayerListItem.class.isAssignableFrom(clazz))
        {
            throw new IllegalStateException("This definitely shouldn't happen!"
                    + " SPacketPlayerListFactory got: " + clazz.getName());
        }

        return super.create(clazz, args);
    }

    @Override
    public PossibleInputs getInputs(Class<? extends Packet<?>> clazz,
                                    String[] args)
    {
        if (!SPacketPlayerListItem.class.isAssignableFrom(clazz))
        {
            throw new IllegalStateException("This definitely shouldn't happen!"
                    + " SPacketPlayerListFactory got: " + clazz.getName());
        }

        return super.getInputs(clazz, args);
    }

    protected Packet<?> instantiate(Constructor<? extends Packet<?>> ctr,
                                    Object[] parameters)
            throws NoSuchFieldException, IllegalAccessException
    {
        SPacketPlayerListItem packet = new SPacketPlayerListItem();
        setAction(packet, (SPacketPlayerListItem.Action) parameters[0]);

        for (int i = 1; i < parameters.length; i++)
        {
            Object o = parameters[i];
            if (o == null)
            {
                continue;
            }

            if (o.getClass().isArray())
            {
                for(int j = 0; j < Array.getLength(o); j++)
                {
                    packet.getEntries().add(
                        (SPacketPlayerListItem.AddPlayerData) Array.get(o, j));
                }
            }
            else if (o instanceof Iterable)
            {
                for (Object data : (Iterable<?>) o)
                {
                    packet.getEntries().add(
                        (SPacketPlayerListItem.AddPlayerData) data);
                }
            }
        }

        return packet;
    }

    public static void setAction(SPacketPlayerListItem packet, SPacketPlayerListItem.Action actionIn)
        throws NoSuchFieldException, IllegalAccessException
    {
        Field action = ReflectionUtil.getField(
            SPacketPlayerListItem.class,
            "action", "field_179770_a", "a");
        action.setAccessible(true);
        action.set(packet, actionIn);
    }

    @Override
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
                if (i == g.getArgIndex())
                {
                    return g;
                }
            }
        }

        return getArgumentForType(type);
    }

    @Override
    protected List<GenericArgument<?>> getGeneric(
            Class<? extends Packet<?>> clazz)
    {
        return GENERICS;
    }

    @Override
    protected PacketArgument<?> getArgumentForType(Class<?> type)
    {
        if (SPacketPlayerListItem.Action.class.isAssignableFrom(type))
        {
            return ACTION_ARGUMENT;
        }

        if (Iterable.class.isAssignableFrom(type))
        {
            return ITERABLE_ARGUMENT;
        }

        if (SPacketPlayerListItem.AddPlayerData[].class.isAssignableFrom(type))
        {
            return ARRAY_ARGUMENT;
        }

        return null;
    }

}
