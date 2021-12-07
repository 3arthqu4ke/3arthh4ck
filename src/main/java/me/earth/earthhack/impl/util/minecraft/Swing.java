package me.earth.earthhack.impl.util.minecraft;

import net.minecraft.util.EnumHand;

public enum Swing
{
    None
        {
            @Override
            public void swing(EnumHand hand)
            {
                /* Nothing */
            }
        },
    Packet
        {
            @Override
            public void swing(EnumHand hand)
            {
                ArmUtil.swingPacket(hand);
            }
        },
    Full
        {
            @Override
            public void swing(EnumHand hand)
            {
                ArmUtil.swingArm(hand);
            }
        },
    Client
        {
            @Override
            public void swing(EnumHand hand)
            {
                ArmUtil.swingArmNoPacket(hand);
            }
        };

    public static final String DESCRIPTION =
        "-None won't swing at all." +
        "\n-Packet will swing on the server." +
        "\n-Full will swing both on client and server." +
        "\n-Client will only swing client-sided.";

    public abstract void swing(EnumHand hand);

}
