package me.earth.earthhack.impl.core.transfomer;

import me.earth.earthhack.impl.core.Core;
import me.earth.earthhack.impl.core.transfomer.patch.EarthhackPatcher;
import me.earth.earthhack.impl.core.transfomer.patch.patches.*;
import net.minecraft.launchwrapper.IClassTransformer;
import org.spongepowered.asm.mixin.MixinEnvironment;

//@IFMLLoadingPlugin.SortingIndex(Integer.MAX_VALUE)
public class EarthhackTransformer implements IClassTransformer
{
    private boolean changingPriority = true;
    private int reentrancy;

    public EarthhackTransformer()
    {
        MixinEnvironment.getEnvironment(MixinEnvironment.Phase.DEFAULT)
                .addTransformerExclusion(EarthhackTransformer.class.getName());
        MixinEnvironment.getEnvironment(MixinEnvironment.Phase.PREINIT)
                .addTransformerExclusion(EarthhackTransformer.class.getName());
        MixinEnvironment.getEnvironment(MixinEnvironment.Phase.INIT)
                .addTransformerExclusion(EarthhackTransformer.class.getName());
        MixinEnvironment.getEnvironment(MixinEnvironment.Phase.DEFAULT)
                .addTransformerExclusion(EarthhackTransformer.class.getName());

        PatchManager patches = EarthhackPatcher.getInstance();
        patches.addPatch(new InventoryPlayerPatch());
        patches.addPatch(new PlayerControllerMPPatch());
        patches.addPatch(new Vec3iPatch());
        patches.addPatch(new BlockPosPatch());
        patches.addPatch(new EnumFacingPatch());
        patches.addPatch(new EntityPatch());
        loadReentrantClasses();

        Core.LOGGER.info("Transformer instantiated.");
    }

    @Override
    public byte[] transform(String name,
                            String transformed,
                            byte[] b)
    {
        reentrancy++;
        if (reentrancy > 1) {
            Core.LOGGER.warn(
                "Transformer is reentrant on class: "
                        + name + " : " + transformed + ".");
        }

        if (transformed.equals("net.minecraft.client.entity.EntityPlayerSP"))
        {
            Core.LOGGER.info("Done changing MixinPriority.");
            changingPriority = false;
        }

        byte[] r = EarthhackPatcher.getInstance().transform(name, transformed, b);
        reentrancy--;
        return r;
    }

    private void loadReentrantClasses()
    {
        try
        {
            Class.forName("me.earth.earthhack.impl.core.util.AsmUtil");
            Class.forName("me.earth.earthhack.impl.util.misc.ReflectionUtil");
            String pack = "me.earth.earthhack.impl.core.transfomer.patch.";
            Class.forName(pack + "patches.BlockPosPatch$OffsetPatch");
            Class.forName(pack + "patches.BlockPosPatch$Direction");
            Class.forName(pack + "patches.BlockPosPatch$1");
        }
        catch (ClassNotFoundException e)
        {
            throw new RuntimeException(e);
        }
    }

}
