package me.earth.earthhack.impl.modules.misc.logger;

import me.earth.earthhack.api.module.data.DefaultData;

final class LoggerData extends DefaultData<Logger>
{
    public LoggerData(Logger module)
    {
        super(module);
        register(module.filter, "Filters Packets if on. " +
                "Use the command <logger <add/del> <packet>> to add packets" +
                " to filter. Then decide if those packets should be" +
                " white/blacklisted with the List-Type setting.");
        register(module.incoming,
                "Logs packets coming in from the server.");
        register(module.outgoing,
                "Logs packets that are being send to the server.");
        register(module.info, "Logs all fields of the packet.");
        register(module.deobfuscate, "Deobfuscates the fields of packets.");
        register(module.stackTrace, "Prints the StackTrace so you can" +
                " see where a Packet comes from.");
        register(module.mode, "-Normal, the normal PacketLogger." +
                "\n-Buffer, Can bypass certain \"obfuscation\" techniques.");
    }

    @Override
    public int getColor()
    {
        return 0xffffffff;
    }

    @Override
    public String getDescription()
    {
        return "Logs incoming/outgoing packets.";
    }

}
