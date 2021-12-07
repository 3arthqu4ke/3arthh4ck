package me.earth.earthhack.impl.modules.misc.antipackets;

import me.earth.earthhack.api.module.data.DefaultData;

final class AntiPacketData extends DefaultData<AntiPackets>
{
    public AntiPacketData(AntiPackets antiPackets)
    {
        super(antiPackets);
    }

    @Override
    public int getColor()
    {
        return 0xffff001D;
    }

    @Override
    public String getDescription()
    {
        return "Cancel packets that you receive (SPackets) or send (CPackets).";
    }

}
