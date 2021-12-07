package me.earth.earthhack.impl.modules.misc.nointeract;

import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.impl.util.helpers.addable.BlockAddingModule;
import me.earth.earthhack.impl.util.helpers.addable.ListType;

public class NoInteract extends BlockAddingModule
{
    protected final Setting<Boolean> sneak =
            register(new BooleanSetting("Sneak", true));

    public NoInteract()
    {
        super("NoInteract",
                Category.Misc,
                s -> "Black/Whitelist " + s.getName() + " interacting.");
        super.listType.setValue(ListType.BlackList);
        this.listeners.add(new ListenerInteract(this));
    }

}
