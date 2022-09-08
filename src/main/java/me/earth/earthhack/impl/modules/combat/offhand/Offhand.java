package me.earth.earthhack.impl.modules.combat.offhand;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Complexity;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BindSetting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.api.util.bind.Bind;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.pingbypass.PingBypassModule;
import me.earth.earthhack.impl.modules.client.pingbypass.submodules.sautototem.ServerAutoTotem;
import me.earth.earthhack.impl.modules.combat.autoarmor.AutoArmor;
import me.earth.earthhack.impl.modules.combat.offhand.modes.HUDMode;
import me.earth.earthhack.impl.modules.combat.offhand.modes.OffhandMode;
import me.earth.earthhack.impl.modules.player.suicide.Suicide;
import me.earth.earthhack.impl.modules.player.xcarry.XCarry;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.minecraft.DamageUtil;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import me.earth.earthhack.impl.util.network.NetworkUtil;
import me.earth.earthhack.impl.util.network.PacketUtil;
import me.earth.earthhack.impl.util.thread.Locks;
import me.earth.earthhack.pingbypass.input.Mouse;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketHeldItemChange;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

//TODO: MainHand Totem
//TODO: Make this an ItemAddingModule so we can add more modes?
public class Offhand extends Module
{
    private static final ModuleCache<PingBypassModule> PINGBYPASS =
            Caches.getModule(PingBypassModule.class);
    private static final ModuleCache<ServerAutoTotem> AUTOTOTEM =
            Caches.getModule(ServerAutoTotem.class);
    private static final ModuleCache<AutoArmor> AUTO_ARMOR =
            Caches.getModule(AutoArmor.class);
    private static final ModuleCache<XCarry> XCARRY =
            Caches.getModule(XCarry.class);
    private static final ModuleCache<Suicide> SUICIDE =
            Caches.getModule(Suicide.class);

    protected final Setting<Float> health        =
            register(new NumberSetting<>("Health", 14.5f, 0.0f, 36.0f));
    protected final Setting<Float> safeH         =
            register(new NumberSetting<>("SafeHealth", 3.0f, 0.0f, 36.0f));
    protected final Setting<Bind> gappleBind     =
            register(new BindSetting("GappleBind", Bind.none()));
    protected final Setting<Bind> crystalBind    =
            register(new BindSetting("CrystalBind", Bind.none()));
    protected final Setting<Integer> delay       =
            register(new NumberSetting<>("Delay", 25, 0, 500));
    protected final Setting<Boolean> cToTotem    =
            register(new BooleanSetting("Crystal-Totem", true));
    protected final Setting<Boolean> swordGap    =
            register(new BooleanSetting("Sword-Gapple", false));
    protected final Setting<Boolean> swordGapCrystal    =
            register(new BooleanSetting("SwordGapCrystal", false));
    protected final Setting<Boolean> recover     =
            register(new BooleanSetting("RecoverSwitch", true));
    protected final Setting<Boolean> noOGC       =
            register(new BooleanSetting("AntiPlace", true));
    protected final Setting<Boolean> hotbarFill  =
            register(new BooleanSetting("Totem-Hotbar", false));
    protected final Setting<HUDMode> hudMode     =
            register(new EnumSetting<>("HUDMode", HUDMode.Info));
    protected final Setting<Integer> timeOut     =
            register(new NumberSetting<>("TimeOut", 600, 0, 1000));
    protected final Setting<Boolean> crystalsIfNoTotem =
            register(new BooleanSetting("CrystalsIfNoTotem", false));
    protected final Setting<Boolean> async  =
            register(new BooleanSetting("Async-Totem", false));
    protected final Setting<Integer> asyncCheck =
            register(new NumberSetting<>("Async-Check", 100, 0, 1000));
    protected final Setting<Boolean> crystalCheck    =
            register(new BooleanSetting("CrystalCheck", false));
    protected final Setting<Boolean> doubleClicks =
            register(new BooleanSetting("DoubleClicks", false));
    protected final Setting<Boolean> noMove =
            register(new BooleanSetting("NoMove", false));
    protected final Setting<Boolean> cancelActions =
            register(new BooleanSetting("CancelActions", false));
    protected final Setting<Boolean> cancelActive =
            register(new BooleanSetting("CancelActive", false));
    protected final Setting<Boolean> noDoubleGapple =
            register(new BooleanSetting("NoDoubleGapple", false));
    protected final Setting<Boolean> doubleGappleToCrystal =
            register(new BooleanSetting("DoubleGappleToCrystal", false));
    protected final Setting<Boolean> swordGapOverride =
            register(new BooleanSetting("SwordGapOverride", false));
    protected final Setting<Boolean> fixPingBypassAsyncSlot =
            register(new BooleanSetting("FixPingBypassAsyncSlot", true))
                .setComplexity(Complexity.Expert);
    protected final Setting<Boolean> oldCrystalCheck =
            register(new BooleanSetting("OldCrystalCheck", false))
                .setComplexity(Complexity.Expert);

    protected final Map<Item, Integer> lastSlots = new HashMap<>();
    protected final StopWatch setSlotTimer = new StopWatch();
    protected final StopWatch timeOutTimer = new StopWatch();
    protected final StopWatch asyncTimer   = new StopWatch();
    protected final StopWatch timer        = new StopWatch();

    protected OffhandMode mode = OffhandMode.TOTEM;
    protected OffhandMode recovery = null;

    protected volatile int asyncSlot = -1;

    protected boolean swordGapped;
    protected boolean swordGappedWithCrystal;
    protected boolean lookedUp;
    protected boolean pulledFromHotbar;

    protected boolean sneaking;
    protected boolean sprinting;

    public Offhand()
    {
        super("Offhand", Category.Combat);
        this.listeners.add(new ListenerGameLoop(this));
        this.listeners.add(new ListenerKeyboard(this));
        this.listeners.add(new ListenerRightClick(this));
        this.listeners.add(new ListenerTotem(this));
        this.listeners.add(new ListenerSetSlot(this));
        this.setData(new OffhandData(this));
    }

    @Override
    public String getDisplayInfo()
    {
        switch(hudMode.getValue())
        {
            case Info:
                return mode.getName();
            case Name:
                return InventoryUtil.getCount(mode.getItem()) + "";
            default:
        }

        return null;
    }

    @Override
    public String getDisplayName()
    {
        if (hudMode.getValue() == HUDMode.Name)
        {
            if (OffhandMode.TOTEM.equals(mode))
            {
                return "AutoTotem";
            }
            else
            {
                return "Offhand" + mode.getName();
            }
        }

        return super.getDisplayName();
    }

    public void setMode(OffhandMode mode)
    {
        this.mode = mode;
        recovery = mode.equals(OffhandMode.TOTEM) ? recovery : null;
        swordGapped = false;
    }

    public OffhandMode getMode()
    {
        return mode;
    }

    public void forceMode(OffhandMode mode)
    {
        setMode(mode);
        for (int i = 0; i < 3; i++)
        {
            this.getTimer().setTime(10000);
            this.doOffhand();
        }
    }

    public void doOffhand()
    {
        boolean suicide = SUICIDE.returnIfPresent(Suicide::deactivateOffhand, false);
        if (mc.player != null
            && timer.passed(delay.getValue())
            && InventoryUtil.validScreen())
        {
            boolean isCrystal = false;
            if ((mc.player.getHeldItemMainhand().getItem() instanceof ItemSword
                || mc.player.getHeldItemMainhand().getItem() instanceof ItemAxe)
                    && swordGap.getValue()
                    && (mc.player.getHeldItemOffhand().getItem() ==
                                                        Items.GOLDEN_APPLE
                        || swordGapCrystal.getValue()
                            && (isCrystal =
                                mc.player.getHeldItemOffhand().getItem() ==
                                                        Items.END_CRYSTAL)
                        || mc.player.getHeldItemOffhand().getItem() ==
                                                        Items.TOTEM_OF_UNDYING))
            {
                if (Mouse.isButtonDown(1)
                        && (OffhandMode.TOTEM.equals(mode)
                            || swordGapCrystal.getValue() && isCrystal)
                        && mc.currentScreen == null)
                {
                    this.mode = OffhandMode.GAPPLE;
                    swordGapped = true;
                    swordGappedWithCrystal = isCrystal;
                }
                else if (swordGapped
                            && !Mouse.isButtonDown(1)
                            && OffhandMode.GAPPLE.equals(mode))
                {
                    if (swordGappedWithCrystal)
                    {
                        setMode(OffhandMode.CRYSTAL);
                    }
                    else
                    {
                        setMode(OffhandMode.TOTEM);
                    }
                }
            }

            if (!isSafe() && !suicide && !(swordGapped && swordGapOverride.getValue()))
            {
                setRecovery(mode);
                mode = OffhandMode.TOTEM;
            }
            else if (recover.getValue()
                    && recovery != null
                    && timeOutTimer.passed(timeOut.getValue()))
            {
                setMode(recovery);
            }

            int tSlot  = InventoryUtil.findItem(Items.TOTEM_OF_UNDYING, true);
            int hotbar = InventoryUtil.findHotbarItem(Items.TOTEM_OF_UNDYING);
            if (pulledFromHotbar
                && !suicide
                && hotbarFill.getValue()
                && InventoryUtil.findEmptyHotbarSlot() != -1
                && (hotbar == -1 || hotbar == -2)
                && tSlot != -1
                && timer.passed(timeOut.getValue()))
            {
                Locks.acquire(Locks.WINDOW_CLICK_LOCK, () ->
                {
                    if (InventoryUtil.get(tSlot)
                                     .getItem() == Items.TOTEM_OF_UNDYING)
                    {
                        mc.playerController.windowClick(
                                0, tSlot, 0, ClickType.QUICK_MOVE, mc.player);
                    }
                });

                postWindowClick();
                pulledFromHotbar = false;
            }

            if (crystalsIfNoTotem.getValue()
                && mode == OffhandMode.TOTEM
                && InventoryUtil.getCount(Items.TOTEM_OF_UNDYING) == 0
                && InventoryUtil.getCount(Items.END_CRYSTAL) != 0
                && setSlotTimer.passed(250))
            {
                this.mode = OffhandMode.CRYSTAL;
                this.swordGapped = false;
            }

            if (noDoubleGapple.getValue()
             && mode == OffhandMode.GAPPLE
             && mc.player.getHeldItemOffhand().getItem() == Items.GOLDEN_APPLE
             && mc.player.getHeldItemMainhand().getItem() == Items.GOLDEN_APPLE)
            {
                if (doubleGappleToCrystal.getValue())
                {
                    this.mode = OffhandMode.CRYSTAL;
                }
                else
                {
                    this.mode = OffhandMode.TOTEM;
                }

                this.swordGapped = false;
            }

            if (!suicide || mode != OffhandMode.TOTEM) {
                switchToItem(mode.getItem());
            }
        }
    }

    public void setRecovery(OffhandMode recoveryIn)
    {
        if (recover.getValue()
                && recoveryIn != null
                && !recoveryIn.equals(OffhandMode.TOTEM))
        {
            this.recovery = recoveryIn;
            this.timeOutTimer.reset();
        }
    }

    private void switchToItem(Item item)
    {
        ItemStack drag = mc.player.inventory.getItemStack();
        Item dragItem  = drag.getItem();
        Item offhandItem = mc.player.getHeldItemOffhand().getItem();
        if (offhandItem != item)
        {
            if (dragItem == item)
            {
                preWindowClick();
                InventoryUtil.clickLocked(-2, 45, dragItem, offhandItem);
                postWindowClick();
                lookedUp = false;
            }
            else
            {
                int slot;
                Integer last = lastSlots.get(item);
                if (last != null && InventoryUtil.get(last).getItem() == item)
                {
                    slot = last;
                }
                else
                {
                    slot = findItem(item);
                }

                if (slot != -1 && slot != -2)
                {
                    lastSlots.put(item, slot);
                    lookedUp = false;
                    Item slotItem = InventoryUtil.get(slot).getItem();
                    preWindowClick();
                    if (doubleClicks.getValue())
                    {
                        InventoryUtil.clickLocked(
                                slot, 45, slotItem, offhandItem);
                    }
                    else
                    {
                        InventoryUtil.clickLocked(
                            -1, slot, null, slotItem);
                    }

                    postWindowClick();
                }
            }
        }
        else if (!drag.isEmpty() && !lookedUp)
        {
            Integer lastSlot = lastSlots.get(dragItem);

            if (lastSlot != null && InventoryUtil.get(lastSlot).isEmpty())
            {
                preWindowClick();
                InventoryUtil.clickLocked(-2, lastSlot, dragItem,
                        InventoryUtil.get(lastSlot).getItem());
                postWindowClick();
            }
            else
            {
                int slot = findItem(Items.AIR);
                if (slot != -1 && slot != -2)
                {
                    lastSlots.put(dragItem, slot);
                    preWindowClick();
                    InventoryUtil.clickLocked(-2, slot, dragItem,
                            InventoryUtil.get(slot).getItem());
                    postWindowClick();
                }
            }

            lookedUp = true;
        }
    }

    private int findItem(Item item)
    {
        return InventoryUtil.findItem(item, XCARRY.isEnabled());
    }

    public boolean isSafe()
    {
        float playerHealth = EntityUtil.getHealth(mc.player);
        if (crystalCheck.getValue() && mc.player != null && mc.world != null)
        {
            float highestDamage = mc.world.loadedEntityList
                    .stream()
                    .filter(entity -> entity instanceof EntityEnderCrystal)
                    .filter(entity -> entity.getDistanceSq(mc.player) <= 144)
                    .map(DamageUtil::calculate)
                    .max(Comparator.comparing(damage -> damage))
                    .orElse(0.0f);

            if (oldCrystalCheck.getValue()) {
                playerHealth -= highestDamage;
            } else if (highestDamage >= playerHealth) {
                return false;
            }
        }

        return (PINGBYPASS.isEnabled()
                    && PINGBYPASS.get().isOld()
                    && AUTOTOTEM.isEnabled())
                || (Managers.SAFETY.isSafe()
                    && playerHealth >= safeH.getValue()
                    || playerHealth >= health.getValue());
    }

    public void preWindowClick()
    {
        if (noMove.getValue()
                && Managers.POSITION.isOnGround()
                /*&& Math.abs(mc.player.motionY) < 0.1*/)
        {
            /*
             * trick server into thinking that ur not moving when there is almost no leniency on the invmove checks (aka when on ground)
             */
            PacketUtil.doPosition(Managers.POSITION.getX(), Managers.POSITION.getY(), Managers.POSITION.getZ(), Managers.POSITION.isOnGround());
        }

        sneaking = Managers.ACTION.isSneaking();
        sprinting = Managers.ACTION.isSprinting();

        if (cancelActions.getValue())
        {
            if (sneaking)
            {
                PacketUtil.sendAction(CPacketEntityAction.Action.STOP_SNEAKING);
            }

            if (sprinting)
            {
                PacketUtil.sendAction(CPacketEntityAction.Action.STOP_SPRINTING);
            }
        }

        if (cancelActive.getValue())
        {
            NetworkUtil.send(new CPacketHeldItemChange(mc.player.inventory.currentItem));
        }
    }

    public void postWindowClick()
    {
        AUTO_ARMOR.computeIfPresent(AutoArmor::resetTimer);
        timer.reset();

        if (cancelActions.getValue())
        {
            if (sneaking)
            {
                PacketUtil.sendAction(CPacketEntityAction.Action.START_SNEAKING);
            }

            if (sprinting)
            {
                PacketUtil.sendAction(CPacketEntityAction.Action.START_SPRINTING);
            }
        }
    }

    public StopWatch getTimer()
    {
        return timer;
    }

}
