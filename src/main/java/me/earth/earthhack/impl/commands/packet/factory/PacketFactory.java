package me.earth.earthhack.impl.commands.packet.factory;

import me.earth.earthhack.api.command.Completer;
import me.earth.earthhack.api.command.PossibleInputs;
import me.earth.earthhack.impl.commands.packet.exception.ArgParseException;
import me.earth.earthhack.impl.util.helpers.command.CustomCompleterResult;
import net.minecraft.network.Packet;

public interface PacketFactory
{
    Packet<?> create(Class<? extends Packet<?>> clazz, String[] args)
            throws ArgParseException;

    /**
     * @param clazz the type of the packet.
     * @param args always longer than 1.
     * @return PossibleInputs for the packet.
     */
    PossibleInputs getInputs(Class<? extends Packet<?>> clazz, String[] args);

    CustomCompleterResult onTabComplete(Completer completer);

}
