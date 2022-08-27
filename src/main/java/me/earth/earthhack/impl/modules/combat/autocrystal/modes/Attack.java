package me.earth.earthhack.impl.modules.combat.autocrystal.modes;

import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import net.minecraft.init.Items;

import java.util.function.Supplier;

public enum Attack
{
    /** Always attacks crystals */
    Always(() -> true, () -> true),
    /** Only attacks if we hold Endcrystals */
    Crystal(() -> InventoryUtil.isHolding(Items.END_CRYSTAL),
            () -> InventoryUtil.isHolding(Items.END_CRYSTAL)),
    /** Similar to Crystal but always run the calc */
    Calc(() -> true, () -> InventoryUtil.isHolding(Items.END_CRYSTAL));

    final Supplier<Boolean> shouldCalc;
    final Supplier<Boolean> shouldAttack;

    Attack(Supplier<Boolean> shouldCalc, Supplier<Boolean> shouldAttack)
    {
        this.shouldAttack = shouldAttack;
        this.shouldCalc   = shouldCalc;
    }

    public boolean shouldCalc()
    {
        return shouldCalc.get();
    }

    public boolean shouldAttack()
    {
        return shouldAttack.get();
    }

}
