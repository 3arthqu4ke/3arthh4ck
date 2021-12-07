package me.earth.earthhack.impl.modules.player.cleaner;

import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.modules.combat.autoarmor.util.WindowClick;
import me.earth.earthhack.impl.util.client.SimpleData;
import me.earth.earthhack.impl.util.helpers.addable.ItemAddingModule;
import me.earth.earthhack.impl.util.helpers.addable.ListType;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.thread.Locks;

public class Cleaner extends ItemAddingModule<Integer, RemovingInteger>
{
    protected final Setting<Integer> delay =
        register(new NumberSetting<>("Delay", 500, 0, 10000));
    protected final Setting<Boolean> rotate =
        register(new BooleanSetting("Rotate", false));
    protected final Setting<Boolean> prioHotbar =
        register(new BooleanSetting("PrioHotbar", true));
    protected final Setting<Boolean> stack =
        register(new BooleanSetting("Stack", false));
    protected final Setting<Boolean> inInventory =
        register(new BooleanSetting("InInventory", false));
    protected final Setting<Boolean> stackDrag =
        register(new BooleanSetting("StackDrag", true));
    protected final Setting<Boolean> smartStack =
        register(new BooleanSetting("SmartStack", false));
    protected final Setting<Boolean> xCarry =
        register(new BooleanSetting("XCarry", false));
    protected final Setting<Boolean> dragCarry =
        register(new BooleanSetting("DragXCarry", true));
    protected final Setting<Boolean> cleanInLoot =
        register(new BooleanSetting("CleanInLoot", true));
    protected final Setting<Boolean> cleanWithFull =
        register(new BooleanSetting("LootFullInvClean", true));
    protected final Setting<Boolean> sizeCheck =
        register(new BooleanSetting("SizeCheck", true));
    protected final Setting<Integer> minXcarry =
        register(new NumberSetting<>("Min-XCarry", 5, 0, 36));
    protected final Setting<Integer> xCarryStacks =
        register(new NumberSetting<>("XCarry-Stacks", 31, 0, 36));
    protected final Setting<Integer> globalDelay =
        register(new NumberSetting<>("Global-Delay", 500, 0, 10000));

    protected final StopWatch timer = new StopWatch();
    protected WindowClick action;

    public Cleaner()
    {
        super("Cleaner",
                Category.Player,
                s -> new RemovingInteger(s, 0, 0, 36),
                s -> "How many stacks of " + s.getName() + " to allow. " +
                     "When List-Type is Whitelist the value doesn't matter," +
                     " all other items will be dropped.");
        super.listType.setValue(ListType.BlackList);
        this.listeners.add(new ListenerMotion(this));

        SimpleData data = new SimpleData(this, "Cleans up your Inventory.");
        data.register(delay, "Delay between 2 actions.");
        data.register(rotate, "Rotates away when throwing away an item.");
        data.register(prioHotbar, "Prioritizes the Hotbar.");
        data.register(stack, "Stacks Stacks.");
        data.register(inInventory,
                "Manages your Inventory while you are in it.");
        data.register(stackDrag, "Stacks the DragSlot.");
        data.register(smartStack, "");
        data.register(xCarry, "Puts Items in your XCarry");
        data.register(minXcarry, "Minimum amount of stacks you " +
                "need to have of an item for it to be put in your XCarry");
        data.register(xCarryStacks, "Minimum amount of Stacks that you need" +
                " to have in your Inventory for XCarry to be active.");
        this.setData(data);
    }

    public void runAction()
    {
        if (action != null)
        {
            Locks.acquire(Locks.WINDOW_CLICK_LOCK, () ->
            {
                if (action.isValid())
                {
                    action.runClick(mc.playerController);
                }
            });

            timer.reset();
            action = null;
        }
    }

    public StopWatch getTimer()
    {
        return timer;
    }

    public int getDelay()
    {
        return delay.getValue();
    }

}
