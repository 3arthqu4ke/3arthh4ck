package me.earth.earthhack.impl.modules.player.fasteat;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.modules.player.fasteat.mode.FastEatMode;
import me.earth.earthhack.impl.util.client.SimpleData;
import net.minecraft.item.ItemBucketMilk;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;

public class FastEat extends Module
{
    protected final Setting<FastEatMode> mode =
            register(new EnumSetting<>("Mode", FastEatMode.Packet));
    protected final Setting<Float> speed =
            register(new NumberSetting<>("Speed", 15.0f, 1.0f, 25.0f));
    protected final Setting<Boolean> cancel =
            register(new BooleanSetting("Cancel-Digging", false));

    public FastEat()
    {
        super("FastEat", Category.Player);
        this.listeners.add(new ListenerUpdate(this));
        this.listeners.add(new ListenerTryUseItem(this));
        this.listeners.add(new ListenerDigging(this));

        SimpleData data = new SimpleData(this, "Exploits that make you fat.");
        data.register(mode, "Different Modes. " +
                "NoDelay won't lagback, the others might.");
        data.register(speed, "Speed for mode Packet.");
        data.register(cancel, "Makes it so that you just need to click" +
                " once and the item will be eaten.");
        this.setData(data);
    }

    @Override
    public String getDisplayInfo()
    {
        return mode.getValue().name();
    }

    public FastEatMode getMode()
    {
        return mode.getValue();
    }

    protected boolean isValid(ItemStack stack)
    {
        return stack != null
                && mc.player.isHandActive()
                && (stack.getItem() instanceof ItemFood
                    || stack.getItem() instanceof ItemPotion
                    || stack.getItem() instanceof ItemBucketMilk);
    }

}
