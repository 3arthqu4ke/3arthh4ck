package me.earth.earthhack.impl.modules.player.replenish;

import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.util.client.SimpleData;
import me.earth.earthhack.impl.util.helpers.addable.ListType;
import me.earth.earthhack.impl.util.helpers.addable.RemovingItemAddingModule;
import me.earth.earthhack.impl.util.math.StopWatch;
import net.minecraft.item.ItemStack;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Replenish extends RemovingItemAddingModule
{
    protected final Setting<Integer> threshold =
            register(new NumberSetting<>("Threshold", 10, 0, 64));
    protected final Setting<Integer> minSize   =
            register(new NumberSetting<>("MinSize", 54, 0, 64));
    protected final Setting<Integer> delay     =
            register(new NumberSetting<>("Delay", 50, 0, 500));
    protected final Setting<Boolean> putBack   =
            register(new BooleanSetting("PutBack", true));
    protected final Setting<Boolean> replenishInLoot   =
            register(new BooleanSetting("ReplenishInLoot", true));
    protected final Setting<Boolean> inInvWithMiddleClick   =
            register(new BooleanSetting("InInvWithMiddleClick", false));

    /** A list, permanently kept at size 9, resembling the hotbar. */
    protected final List<ItemStack> hotbar = new CopyOnWriteArrayList<>();
    /** Timer to handle the delay with. */
    protected final StopWatch timer = new StopWatch();

    public Replenish()
    {
        super("Replenish",
         Category.Player,
         s -> "Black/Whitelists " + s.getName() + " from getting replenished.");
        super.listType.setValue(ListType.BlackList);
        this.listeners.add(new ListenerTick(this));
        this.listeners.add(new ListenerLogout(this));
        this.listeners.add(new ListenerDeath(this));
        this.listeners.add(new ListenerWorldClient(this));
        SimpleData data = new SimpleData(this,
                "Makes you never run out of items in your hotbar.");
        data.register(threshold, "If a stack in your hotbar reaches this" +
                " threshold it will be replenished.");
        //data.register(minSize, ""); TODO: forgot and dont understand what this does wtf
        data.register(delay, "Delay between each time items are moved around" +
                " in your Inventory. Low delays might cause Inventory desync.");
        data.register(putBack, "If this setting isn't enabled some items " +
                "might end up in your dragslot.");
        this.setData(data);
        clear();
    }

    @Override
    protected void onEnable()
    {
        clear();
    }

    /**
     * Resets the hotBarList, fills all indexes from 0 to 9
     * with {@link ItemStack#EMPTY}.
     */
    public void clear()
    {
        if (hotbar.isEmpty())
        {
            for (int i = 0; i < 9; i++)
            {
                hotbar.add(ItemStack.EMPTY);
            }
        }
        else
        {
            for (int i = 0; i < 9; i++)
            {
                hotbar.set(i, ItemStack.EMPTY);
            }
        }
    }

}
