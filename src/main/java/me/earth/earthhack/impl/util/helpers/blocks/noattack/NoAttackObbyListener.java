package me.earth.earthhack.impl.util.helpers.blocks.noattack;

import me.earth.earthhack.impl.util.helpers.blocks.ObbyListener;

public abstract class NoAttackObbyListener<T extends NoAttackObbyListenerModule<?>>
        extends ObbyListener<T>
{
    public NoAttackObbyListener(T module, int priority)
    {
        super(module, priority);
    }

    @Override
    protected boolean attackCrystalFirst()
    {
        return false;
    }

}
