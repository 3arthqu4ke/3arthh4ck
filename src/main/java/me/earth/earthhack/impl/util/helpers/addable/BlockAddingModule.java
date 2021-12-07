package me.earth.earthhack.impl.util.helpers.addable;

import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import net.minecraft.item.ItemBlock;

import java.util.function.Function;

public class BlockAddingModule extends RemovingItemAddingModule
{
    public BlockAddingModule(String name,
                             Category category,
                             Function<Setting<?>, String> settingDescription)
    {
        super(name, category, settingDescription);
    }

    @Override
    public String getItemStartingWith(String name)
    {
        return ItemAddingModule.getItemStartingWithDefault(name,
                i -> i instanceof ItemBlock);
    }

}
