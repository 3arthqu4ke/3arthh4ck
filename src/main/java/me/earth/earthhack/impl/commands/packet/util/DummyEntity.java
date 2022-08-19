package me.earth.earthhack.impl.commands.packet.util;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class DummyEntity extends Entity implements Dummy
{
    public DummyEntity(World worldIn)
    {
        super(worldIn);
    }

    @Override
    protected void entityInit() { }

    @Override
    @SuppressWarnings("NullableProblems")
    protected void readEntityFromNBT(NBTTagCompound compound) { }

    @Override
    @SuppressWarnings("NullableProblems")
    protected void writeEntityToNBT(NBTTagCompound compound) { }

    @Override
    public boolean isDummy() { return true; }

}
