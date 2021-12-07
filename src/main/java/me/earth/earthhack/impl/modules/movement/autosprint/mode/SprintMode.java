package me.earth.earthhack.impl.modules.movement.autosprint.mode;

import me.earth.earthhack.api.util.interfaces.Globals;
import net.minecraft.client.settings.KeyBinding;

public enum SprintMode implements Globals
{
    Rage
    {
        @Override
        public void sprint()
        {
            mc.player.setSprinting(true);
        }
    },
    Legit
    {
        @Override
        public void sprint()
        {
            KeyBinding.setKeyBindState(
                    mc.gameSettings.keyBindSprint.getKeyCode(),
                    true);
        }
    };

    public abstract void sprint();

}
