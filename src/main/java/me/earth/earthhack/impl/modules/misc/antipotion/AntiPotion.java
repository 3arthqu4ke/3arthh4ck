package me.earth.earthhack.impl.modules.misc.antipotion;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.Potion;

public class AntiPotion extends Module
{
    public AntiPotion()
    {
        super("AntiPotion", Category.Misc);
        AntiPotionData data = new AntiPotionData(this);
        this.setData(data);
        for (Potion potion : Potion.REGISTRY)
        {
            boolean value = potion == MobEffects.LEVITATION;
            String name = getPotionString(potion);
            Setting<?> s = register(
                    new BooleanSetting(name, value));

            data.register(s, "Removes " + name + " potion effects.");
        }

        this.listeners.add(new ListenerUpdates(this));
    }

    public static String getPotionString(Potion potion)
    {
        return I18n.format(potion.getName());
    }

}
