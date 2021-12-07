package me.earth.earthhack.impl.modules.misc.antipotion;

import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.impl.event.events.misc.UpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;

final class ListenerUpdates extends ModuleListener<AntiPotion, UpdateEvent>
{
    public ListenerUpdates(AntiPotion module)
    {
        super(module, UpdateEvent.class);
    }

    @Override
    public void invoke(UpdateEvent event)
    {
        mc.player.getActivePotionEffects().removeIf(effect ->
        {
            Setting<Boolean> setting = module
                    .getSetting(AntiPotion.getPotionString(effect.getPotion()),
                                BooleanSetting.class);

            return setting != null && setting.getValue();
        });
    }

}
