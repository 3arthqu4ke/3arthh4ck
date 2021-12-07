package me.earth.earthhack.impl.commands.packet;

import me.earth.earthhack.impl.commands.packet.factory.PacketFactory;
import me.earth.earthhack.impl.commands.packet.generic.GenericArgument;
import net.minecraft.network.Packet;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Represents the interface of a command that creates Packets.
 */
public interface PacketCommand
{
    /**
     * Gets the class of a packet by its name.
     *
     * @param name the name of the packets class.
     * @return the packets class or null.
     */
    Class<? extends Packet<?>> getPacket(String name);

    Map<Class<? extends Packet<?>>, List<GenericArgument<?>>> getGenerics();

    Map<Class<? extends Packet<?>>, PacketFactory> getCustom();

    Set<Class<? extends Packet<?>>> getPackets();

    Map<Class<?>, PacketArgument<?>> getArguments();

    String getName(Class<? extends Packet<?>> packet);

}
