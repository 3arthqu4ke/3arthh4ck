package me.earth.earthhack.impl.modules.combat.autocrystal.modes;

import net.minecraft.util.EnumHand;

public enum SwingType
{
    None
    {
        @Override
        public EnumHand getHand()
        {
            return null;
        }
    },
    MainHand
    {
        @Override
        public EnumHand getHand()
        {
            return EnumHand.MAIN_HAND;
        }
    },
    OffHand
    {
        @Override
        public EnumHand getHand()
        {
            return EnumHand.OFF_HAND;
        }
    };

    public static final String DESCRIPTION =
            "-None, will not swing clientsided.\n-Swings " +
            " with your MainHand.\n-OffHand, will swing with your Offhand.";


    public abstract EnumHand getHand();

}
