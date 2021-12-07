package me.earth.earthhack.impl.core.transfomer.patch;

import me.earth.earthhack.tweaker.launch.Argument;
import me.earth.earthhack.tweaker.launch.ArgumentManager;
import me.earth.earthhack.tweaker.launch.DevArguments;
import org.objectweb.asm.tree.ClassNode;

public abstract class ArgumentPatch extends FinishingPatch
{
    private final String argument;

    public ArgumentPatch(String name, String transformed, String argument)
    {
        super(name, transformed);
        this.argument = argument;
    }

    protected abstract void applyPatch(ClassNode node);

    @Override
    public void apply(ClassNode node)
    {
        ArgumentManager dev = DevArguments.getInstance();
        Argument<Boolean> arg = dev.getArgument(argument);
        if (arg != null && !arg.getValue())
        {
            setFinished(true);
            return;
        }

        this.applyPatch(node);
    }

}
