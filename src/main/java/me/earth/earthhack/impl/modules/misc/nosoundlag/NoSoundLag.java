package me.earth.earthhack.impl.modules.misc.nosoundlag;

import com.google.common.collect.Sets;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.managers.minecraft.combat.util.SimpleSoundObserver;
import me.earth.earthhack.impl.managers.minecraft.combat.util.SoundObserver;
import me.earth.earthhack.impl.util.client.SimpleData;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundEvent;

import java.util.Set;

public class NoSoundLag extends Module
{
    protected static final Set<SoundEvent> SOUNDS = Sets.newHashSet
    (
        SoundEvents.ITEM_ARMOR_EQUIP_GENERIC,
        SoundEvents.ITEM_ARMOR_EQIIP_ELYTRA,
        SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND,
        SoundEvents.ITEM_ARMOR_EQUIP_IRON,
        SoundEvents.ITEM_ARMOR_EQUIP_GOLD,
        SoundEvents.ITEM_ARMOR_EQUIP_CHAIN,
        SoundEvents.ITEM_ARMOR_EQUIP_LEATHER
    );

    protected final Setting<Boolean> sounds =
            register(new BooleanSetting("Sounds", true));
    protected final Setting<Boolean> crystals =
            register(new BooleanSetting("Crystals", false));

    protected final SoundObserver observer =
            new SimpleSoundObserver(crystals::getValue);

    public NoSoundLag()
    {
        super("NoSoundLag", Category.Misc);
        this.listeners.add(new ListenerSound(this));
        this.setData(new SimpleData(this,
                "Prevents lag caused by spamming certain sounds."));
    }

    @Override
    protected void onEnable()
    {
        Managers.SET_DEAD.addObserver(observer);
    }

    @Override
    protected void onDisable()
    {
        Managers.SET_DEAD.removeObserver(observer);
    }

}

