package me.earth.earthhack.impl.modules.combat.legswitch;

import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.managers.minecraft.combat.util.SoundObserver;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.ACRotate;
import me.earth.earthhack.impl.modules.combat.legswitch.modes.LegAutoSwitch;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import net.minecraft.init.Items;
import net.minecraft.network.play.server.SPacketSoundEffect;

final class ListenerSound extends SoundObserver implements Globals
{
    private final LegSwitch module;

    public ListenerSound(LegSwitch module)
    {
        super(module.soundRemove::getValue);
        this.module = module;
    }

    @Override
    public void onChange(SPacketSoundEffect value)
    {
        if (module.soundStart.getValue()
                && (InventoryUtil.isHolding(Items.END_CRYSTAL)
                    || module.autoSwitch.getValue() != LegAutoSwitch.None)
                && (module.rotate.getValue() == ACRotate.None
                    || module.rotate.getValue() == ACRotate.Break))
        {
            module.startCalculation();
        }
    }

    public boolean shouldBeNotified()
    {
        return module.soundStart.getValue();
    }

}
