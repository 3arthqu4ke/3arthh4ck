package me.earth.earthhack.impl.modules.player.exptweaks;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BindSetting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.api.util.bind.Bind;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.client.SimpleData;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.DamageUtil;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.minecraft.KeyBoardUtil;
import me.earth.earthhack.impl.util.thread.Locks;
import me.earth.earthhack.pingbypass.input.Mouse;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;

import java.util.List;

import static net.minecraft.enchantment.EnchantmentHelper.getEnchantmentLevel;

public class ExpTweaks extends Module
{
    protected final Setting<Boolean> feetExp =
        register(new BooleanSetting("FeetExp", false));
    protected final Setting<Integer> expPackets =
        register(new NumberSetting<>("ExpPackets", 0, 0, 64));
    protected final Setting<Boolean> wasteStop =
        register(new BooleanSetting("WasteStop", false));
    protected final Setting<Boolean> simpleWasteStop =
        register(new BooleanSetting("SimpleWasteStop", false));
    protected final Setting<Boolean> wasteIfFull =
        register(new BooleanSetting("WasteIfInvFull", false));
    protected final Setting<Integer> stopDura =
        register(new NumberSetting<>("Stop-Dura", 100, 0, 100));
    protected final Setting<Integer> wasteIf =
        register(new NumberSetting<>("WasteIf", 30, 0, 100));
    protected final Setting<Boolean> wasteLoot =
        register(new BooleanSetting("WasteLoot", true));
    protected final Setting<Boolean> packetsInLoot =
        register(new BooleanSetting("PacketsInLoot", true));
    protected final Setting<Double> grow =
        register(new NumberSetting<>("Grow", 0.0, 0.0, 5.0));
    protected final Setting<Boolean> middleClickExp =
        register(new BooleanSetting("MiddleClickExp", false));
    protected final Setting<Integer> mcePackets =
        register(new NumberSetting<>("MCE-Packets", 0, 0, 64));
    protected final Setting<Boolean> silent =
        register(new BooleanSetting("Silent", true));
    protected final Setting<Boolean> silentOnlyWhenUsing =
        register(new BooleanSetting("SilentOnlyWhenUsing", false));
    protected final Setting<Boolean> whileEating =
        register(new BooleanSetting("WhileEating", true));
    protected final Setting<Boolean> xCarry =
        register(new BooleanSetting("XCarry", false));
    protected final Setting<Boolean> allowDragSlot =
        register(new BooleanSetting("AllowDragSlot", false));
    protected final Setting<Bind> mceBind =
        register(new BindSetting("MCE-Bind", Bind.none()));
    protected final Setting<Boolean> pickBlock =
        register(new BooleanSetting("CancelPickBlock", true));
    protected final Setting<Boolean> normalMC =
        register(new BooleanSetting("CancelDefaultMiddleClick", true));

    protected boolean justCancelled;
    protected boolean isMiddleClick;
    protected int lastSlot = -1;

    public ExpTweaks()
    {
        super("ExpTweaks", Category.Player);
        this.listeners.add(new ListenerMotion(this));
        this.listeners.add(new ListenerUseItem(this));
        this.listeners.add(new ListenerPickBlock(this));
        this.listeners.add(new ListenerMiddleClick(this));

        SimpleData data = new SimpleData(this,
                "Tweaks for Experience Orbs/Bottles.");
        data.register(feetExp,
                "Will silently look at your feet when you are mending.");
        data.register(expPackets,
                "Sends more packets to make mending faster. " +
                        "10 is a good value, but can waste exp!");
        data.register(wasteStop,
                "Will stop you from throwing Experience if " +
                        "your Armor has full durability.");
        data.register(wasteIf, "Will not use WasteStop if you one of your" +
                " armor pieces has less durability (%) than this value.");
        data.register(wasteLoot,
                "Wastes Exp when you are standing in Exp Bottles.");
        data.register(simpleWasteStop, "Stops you from wasting EXP when you don't hold any items with Mending.");

        this.setData(data);
    }

    @Override
    protected void onEnable()
    {
        isMiddleClick = false;
        justCancelled = false;
        lastSlot = -1;
    }

    @Override
    protected void onDisable()
    {
        if (lastSlot != -1)
        {
            Locks.acquire(Locks.PLACE_SWITCH_LOCK, () ->
                            InventoryUtil.switchTo(lastSlot));
            lastSlot = -1;
        }
    }

    public boolean isMiddleClick()
    {
        return middleClickExp.getValue()
                && (Mouse.isButtonDown(2) && mceBind.getValue().getKey() == -1
                        || KeyBoardUtil.isKeyDown(mceBind));
    }

    public boolean isWastingLoot(List<Entity> entities)
    {
        if (entities != null)
        {
            AxisAlignedBB bb = RotationUtil
                    .getRotationPlayer()
                    .getEntityBoundingBox()
                    .grow(grow.getValue(), grow.getValue(), grow.getValue());

            for (Entity entity : entities)
            {
                if (entity instanceof EntityItem
                        && !entity.isDead
                        && ((EntityItem) entity)
                            .getItem()
                            .getItem() == Items.EXPERIENCE_BOTTLE
                        && entity.getEntityBoundingBox()
                                 .intersects(bb))
                {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean isSimpleWasting()
    {
        return getEnchantmentLevel(Enchantments.MENDING, mc.player.getHeldItem(EnumHand.OFF_HAND)) == 0
            && getEnchantmentLevel(Enchantments.MENDING, mc.player.getHeldItem(EnumHand.MAIN_HAND)) == 0
            && getEnchantmentLevel(Enchantments.MENDING, InventoryUtil.get(5)) == 0
            && getEnchantmentLevel(Enchantments.MENDING, InventoryUtil.get(6)) == 0
            && getEnchantmentLevel(Enchantments.MENDING, InventoryUtil.get(7)) == 0
            && getEnchantmentLevel(Enchantments.MENDING, InventoryUtil.get(8)) == 0;
    }

    public boolean isWasting()
    {
        if (isSimpleWasting())
        {
            return true;
        }

        int airSlot;
        if (wasteIfFull.getValue() && ((airSlot = InventoryUtil.findItem(Items.AIR, xCarry.getValue())) == -1
                || !allowDragSlot.getValue() && airSlot < 0))
        {
            return false;
        }

        if (wasteLoot.getValue())
        {
            List<Entity> entities = Managers.ENTITIES.getEntitiesAsync();
            if (isWastingLoot(entities))
            {
                return false;
            }
        }

        boolean empty = true;
        boolean full = false;
        for (int i = 5; i < 9; i++)
        {
            ItemStack stack = mc.player.inventoryContainer
                                       .getSlot(i)
                                       .getStack();
            if (!stack.isEmpty())
            {
                empty = false;
                float percent = DamageUtil.getPercent(stack);
                if (percent >= stopDura.getValue())
                {
                    full = true;
                }
                else if (percent <= wasteIf.getValue())
                {
                    return false;
                }
            }
        }

        return empty || full;
    }

    public boolean cancelShrink()
    {
        boolean just = justCancelled;
        justCancelled = false;
        return this.isEnabled()
                && this.wasteStop.getValue()
                && just;
    }

}
