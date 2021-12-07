package me.earth.earthhack.impl.util.helpers.addable;

import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.impl.util.helpers.addable.setting.SimpleRemovingSetting;

import java.util.function.Function;

public class RemovingItemAddingModule
        extends ItemAddingModule<Boolean, SimpleRemovingSetting>
{
    public RemovingItemAddingModule(
                                String name,
                                Category category,
                                Function<Setting<?>, String> settingDescription)
    {
        super(name,
                category,
                SimpleRemovingSetting::new,
                settingDescription);
    }

}
