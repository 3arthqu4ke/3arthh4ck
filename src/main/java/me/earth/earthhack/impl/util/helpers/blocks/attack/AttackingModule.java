package me.earth.earthhack.impl.util.helpers.blocks.attack;

import me.earth.earthhack.impl.util.helpers.blocks.modes.Pop;

public interface AttackingModule
{
    Pop getPop();

    int getPopTime();

    double getRange();

    double getTrace();

}
