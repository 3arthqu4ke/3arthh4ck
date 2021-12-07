package me.earth.earthhack.impl.core.transfomer.patch;

import org.objectweb.asm.tree.ClassNode;

public class FinishingPatch extends AbstractPatch
{
    private boolean finished;

    public FinishingPatch(String name, String transformed)
    {
        super(name, transformed);
    }

    @Override
    public void apply(ClassNode node)
    {
        setFinished(true);
    }

    @Override
    public boolean isFinished()
    {
        return finished;
    }

    protected void setFinished(boolean finished)
    {
        this.finished = finished;
    }

}
