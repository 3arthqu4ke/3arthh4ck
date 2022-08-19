package me.earth.earthhack.impl.modules.combat.autoarmor;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Complexity;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.combat.autoarmor.modes.ArmorMode;
import me.earth.earthhack.impl.modules.combat.autoarmor.util.DesyncClick;
import me.earth.earthhack.impl.modules.combat.autoarmor.util.SingleMendingSlot;
import me.earth.earthhack.impl.modules.combat.autoarmor.util.WindowClick;
import me.earth.earthhack.impl.modules.player.exptweaks.ExpTweaks;
import me.earth.earthhack.impl.modules.player.noinventorydesync.MendingStage;
import me.earth.earthhack.impl.modules.player.suicide.Suicide;
import me.earth.earthhack.impl.modules.player.xcarry.XCarry;
import me.earth.earthhack.impl.util.math.DiscreteTimer;
import me.earth.earthhack.impl.util.math.GuardTimer;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.math.geocache.Sphere;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.DamageUtil;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockUtil;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import me.earth.earthhack.impl.util.thread.Locks;
import me.earth.earthhack.pingbypass.input.Mouse;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemElytra;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.CombatRules;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

// TODO: AutoTakeOff?
// TODO: ShiftClick option
// TODO: Take of all pieces and then mend them piece by piece to prevent desync
public class AutoArmor extends Module
{
    private static final ModuleCache<ExpTweaks> EXP_TWEAKS =
            Caches.getModule(ExpTweaks.class);
    private static final ModuleCache<XCarry> XCARRY =
            Caches.getModule(XCarry.class);
    private static final ModuleCache<Suicide> SUICIDE =
            Caches.getModule(Suicide.class);

    protected final Setting<ArmorMode> mode =
            register(new EnumSetting<>("Mode", ArmorMode.Blast));
    protected final Setting<Boolean> safe =
            register(new BooleanSetting("Safe", true))
                .setComplexity(Complexity.Medium);
    protected final Setting<Integer> delay =
            register(new NumberSetting<>("Delay", 50, 0, 500));
    protected final Setting<Boolean> autoMend =
            register(new BooleanSetting("AutoMend", false))
                .setComplexity(Complexity.Medium);
    protected final Setting<Boolean> singleMend =
            register(new BooleanSetting("SingleMend", false))
                .setComplexity(Complexity.Medium);
    protected final Setting<Integer> mendBlock =
            register(new NumberSetting<>("MendBlock", 0, 0, 500))
                .setComplexity(Complexity.Expert);
    protected final Setting<Integer> postBlock =
            register(new NumberSetting<>("PostBlock", 50, 0, 500))
                .setComplexity(Complexity.Expert);
    protected final Setting<Integer> helmet =
            register(new NumberSetting<>("Helmet%", 80, 1, 100))
                .setComplexity(Complexity.Medium);
    protected final Setting<Integer> chest =
            register(new NumberSetting<>("Chest%", 85, 1, 100))
                .setComplexity(Complexity.Medium);
    protected final Setting<Integer> legs =
            register(new NumberSetting<>("Legs%", 84, 1, 100))
                .setComplexity(Complexity.Medium);
    protected final Setting<Integer> boots =
            register(new NumberSetting<>("Boots%", 80, 1, 100))
                .setComplexity(Complexity.Medium);
    protected final Setting<Boolean> curse =
            register(new BooleanSetting("CurseOfBinding", false))
                .setComplexity(Complexity.Expert);
    protected final Setting<Float> closest      =
        register(new NumberSetting<>("Closest", 12.0f, 0.0f, 30.0f))
            .setComplexity(Complexity.Expert);
    protected final Setting<Float> maxDmg       =
        register(new NumberSetting<>("MaxDmg", 1.5f, 0.0f, 36.0f))
            .setComplexity(Complexity.Expert);
    protected final Setting<Boolean> newVer     =
        register(new BooleanSetting("1.13+", false))
            .setComplexity(Complexity.Medium);
    protected final Setting<Boolean> newVerEntities =
        register(new BooleanSetting("1.13-Entities", false))
            .setComplexity(Complexity.Medium);
    protected final Setting<Boolean> bedCheck   =
        register(new BooleanSetting("BedCheck", false))
            .setComplexity(Complexity.Medium);
    protected final Setting<Boolean> noDesync   =
        register(new BooleanSetting("NoDesync", false))
            .setComplexity(Complexity.Expert);
    protected final Setting<Boolean> illegalSync =
        register(new BooleanSetting("Illegal-Sync", false))
            .setComplexity(Complexity.Expert);
    protected final Setting<Boolean> screenCheck   =
        register(new BooleanSetting("CheckScreen", true))
            .setComplexity(Complexity.Expert);
    protected final Setting<Integer> desyncDelay      =
        register(new NumberSetting<>("DesyncDelay", 2500, 0, 5000))
            .setComplexity(Complexity.Expert);
    protected final Setting<Integer> checkDelay      =
            register(new NumberSetting<>("CheckDelay", 250, 0, 5000))
                .setComplexity(Complexity.Expert);
    protected final Setting<Integer> propertyDelay      =
        register(new NumberSetting<>("PropertyDelay", 500, 0, 5000))
            .setComplexity(Complexity.Expert);
    protected final Setting<Boolean> dragTakeOff =
        register(new BooleanSetting("Drag-Mend", false))
            .setComplexity(Complexity.Expert);
    protected final Setting<Boolean> prioLow =
        register(new BooleanSetting("Prio-Low", true))
            .setComplexity(Complexity.Expert);
    protected final Setting<Float> prioThreshold =
        register(new NumberSetting<>("Prio-Threshold", 40.0f, 0.0f, 100.0f))
            .setComplexity(Complexity.Expert);
    protected final Setting<Boolean> putBack =
        register(new BooleanSetting("Put-Back", false))
            .setComplexity(Complexity.Expert);
    protected final Setting<Boolean> doubleClicks =
        register(new BooleanSetting("Double-Clicks", false))
            .setComplexity(Complexity.Medium);
    protected final Setting<Boolean> wasteLoot =
        register(new BooleanSetting("Waste-Loot", false))
            .setComplexity(Complexity.Expert);
    protected final Setting<Boolean> takeOffLoot =
        register(new BooleanSetting("TakeOff-Loot", false))
            .setComplexity(Complexity.Expert);
    protected final Setting<Boolean> noDuraDesync =
        register(new BooleanSetting("NoDuraDesync", false))
            .setComplexity(Complexity.Expert);
    protected final Setting<Integer> removeTime =
        register(new NumberSetting<>("Remove-Time", 250, 0, 1000))
            .setComplexity(Complexity.Expert);
    protected final Setting<Boolean> dontBlockWhenFull =
        register(new BooleanSetting("DontBlockWhenFull", false))
            .setComplexity(Complexity.Expert);
    protected final Setting<Boolean> softInInv =
        register(new BooleanSetting("SoftInInv", false));
    protected final Setting<Boolean> hotSwap =
        register(new BooleanSetting("HotSwap", false))
            .setComplexity(Complexity.Expert);
    protected final Setting<Boolean> fast =
        register(new BooleanSetting("Unsafe-Fast", false))
            .setComplexity(Complexity.Expert);

    protected final Map<Integer, DesyncClick> desyncMap = new ConcurrentHashMap<>();
    /** Queue that manages the windowClicks */
    protected final Queue<WindowClick> windowClicks = new LinkedList<>();
    /** Timer that manages the SPacketEntityProperties delay for NoDesync. */
    protected final StopWatch propertyTimer = new StopWatch();
    /** Manages DesyncDelay */
    protected final StopWatch desyncTimer = new StopWatch();
    /** Timer to manage the delay */
    protected final DiscreteTimer timer = new GuardTimer();
    /**
     * Manages the queued slots
     */
    protected Set<Integer> queuedSlots = new HashSet<>();
    /**
     * Last clicked Type, for NoDesync.
     */
    protected EntityEquipmentSlot lastType;
    /**
     * Manages the damage settings
     */
    protected final Setting<?>[] damages;
    /**
     * Manages PutBack.
     */
    protected WindowClick putBackClick;
    /**
     * Manages if the inventory itemStack has been set.
     */
    protected boolean stackSet;
    protected MendingStage stage = MendingStage.MENDING;
    protected final StopWatch mendingTimer = new StopWatch();
    protected final SingleMendingSlot[] singleMendingSlots = {
            new SingleMendingSlot(EntityEquipmentSlot.HEAD),
            new SingleMendingSlot(EntityEquipmentSlot.CHEST),
            new SingleMendingSlot(EntityEquipmentSlot.LEGS),
            new SingleMendingSlot(EntityEquipmentSlot.FEET)
    };

    public AutoArmor()
    {
        super("AutoArmor", Category.Combat);
        this.damages = new Setting[]{helmet, chest, legs, boots};
        this.listeners.add(new ListenerTick(this));
        this.listeners.add(new ListenerGameLoop(this));
        this.listeners.add(new ListenerEntityProperties(this));
        this.listeners.add(new ListenerWorldClient(this));
        this.listeners.add(new ListenerSetSlot(this));
        this.listeners.add(new ListenerCPacketUseItem(this));
        this.setData(new AutoArmorData(this));
        timer.reset(delay.getValue());
    }

    @Override
    protected void onEnable()
    {
        stage = MendingStage.MENDING;
        windowClicks.clear();
        queuedSlots.clear();
        putBackClick = null;
        unblockMendingSlots();
    }

    @Override
    protected void onDisable()
    {
        stage = MendingStage.MENDING;
        windowClicks.clear();
        queuedSlots.clear();
        putBackClick = null;
        unblockMendingSlots();
    }

    @Override
    public String getDisplayInfo()
    {
        return mode.getValue().name();
    }

    public void unblockMendingSlots()
    {
        for (SingleMendingSlot mendingSlot : singleMendingSlots)
        {
            mendingSlot.setBlocked(false);
        }
    }

    public boolean isBlockingMending()
    {
        return isEnabled() && mendBlock.getValue() > 0 && stage != MendingStage.MENDING;
    }

    /**
     * @return <tt>true</tt> if AutoArmor is active.
     */
    public boolean isActive()
    {
        return this.isEnabled() && !windowClicks.isEmpty();
    }

    /**
     * Resets the delay timer.
     */
    public void resetTimer()
    {
        timer.reset(delay.getValue());
    }

    /**
     * Creates a new {@link WindowClick} for the given parameters,
     * queues it via {@link AutoArmor#queueClick(WindowClick)} and
     * returns it.
     */
    public WindowClick queueClick(int slot, ItemStack inSlot, ItemStack inDrag)
    {
        return queueClick(slot, inSlot, inDrag, slot);
    }

    /**
     * Creates a new {@link WindowClick} for the given parameters,
     * queues it via {@link AutoArmor#queueClick(WindowClick)} and
     * returns it.
     */
    public WindowClick queueClick(int slot, ItemStack inSlot, ItemStack inDrag, int target)
    {
        WindowClick click = new WindowClick(slot, inSlot, inDrag, target);
        queueClick(click);
        click.setFast(fast.getValue());
        return click;
    }

    /**
     * Queues the given windowClick.
     *
     * @param click the click to queue.
     */
    public void queueClick(WindowClick click)
    {
        windowClicks.add(click);
    }

    /**
     * Polls and runs the first WindowClick from the
     * queued clicks if the timer has passed
     * the delay.
     */
    protected void runClick()
    {
        if (InventoryUtil.validScreen() && mc.playerController != null)
        {
            if (timer.passed(delay.getValue()))
            {
                Locks.acquire(Locks.WINDOW_CLICK_LOCK, () ->
                {
                    Managers.NCP.startMultiClick();
                    WindowClick windowClick = windowClicks.poll();
                    while (windowClick != null)
                    {
                        if (safe.getValue() && !windowClick.isValid())
                        {
                            windowClicks.clear();
                            queuedSlots.clear();
                            Managers.NCP.releaseMultiClick();
                            return;
                        }

                        windowClick.runClick(mc.playerController);
                        desyncMap.put(windowClick.getSlot(),
                                new DesyncClick(windowClick));
                        timer.reset(delay.getValue());

                        if (!windowClick.isDoubleClick())
                        {
                            Managers.NCP.releaseMultiClick();
                            return;
                        }

                        windowClick = windowClicks.poll();
                    }
                });
            }
        }
        else
        {
            windowClicks.clear();
            queuedSlots.clear();
        }
    }

    /**
     * Used for AutoMending, queues a slotClick
     * that empties the drag slot.
     */
    protected ItemStack setStack()
    {
        if (!stackSet)
        {
            ItemStack drag = mc.player.inventory.getItemStack();
            if (!drag.isEmpty())
            {
                int slot = findItem(Items.AIR,
                                    XCARRY.isEnabled(),
                                    queuedSlots);
                if (slot != -1)
                {
                    ItemStack inSlot = InventoryUtil.get(slot);
                    queueClick(slot, drag, inSlot);
                    queuedSlots.add(slot);
                    stackSet = true;
                    return inSlot;
                }

                return null;
            }

            stackSet = true;
            return drag;
        }

        return null;
    }

    /**
     * Returns <tt>false</tt> if the given stack
     * has a binding curse and check is <tt>false</tt>.
     *
     * @param stack the stack to check.
     * @param check if you want to actually check.
     * @return <tt>false</tt> if the stack is cursed.
     */
    public static boolean curseCheck(ItemStack stack, boolean check)
    {
        return !check || !EnchantmentHelper.hasBindingCurse(stack);
    }

    /**
     * Returns <tt>false</tt> if autoMend is off,
     * we are not holding Bottles of experience, we are
     * not using them, or theres a dangerous
     * position/crystal that could kill or deal
     * big damage to us in proximity.
     *
     * @return <tt>true</tt> if we can autoMend.
     */
    boolean canAutoMend()
    {
        if (SUICIDE.returnIfPresent(Suicide::shouldTakeOffArmor, false))
        {
            return takeOffLoot.getValue()
                    || mc.world
                         .getEntitiesWithinAABB(EntityItem.class,
                            RotationUtil.getRotationPlayer()
                                    .getEntityBoundingBox())
                         .isEmpty();
        }

        if (!autoMend.getValue()
                || screenCheck.getValue() && mc.currentScreen != null
                || (!Mouse.isButtonDown(1)
                    || !InventoryUtil.isHolding(Items.EXPERIENCE_BOTTLE))
                    && !(EXP_TWEAKS.isEnabled()
                        && EXP_TWEAKS.returnIfPresent(
                                ExpTweaks::isMiddleClick, false))
                || wasteLoot.getValue()
                    && EXP_TWEAKS.returnIfPresent(e ->
                        e.isWastingLoot(mc.world.loadedEntityList), false)
                || !takeOffLoot.getValue()
                    && !mc.world
                          .getEntitiesWithinAABB(EntityItem.class,
                                        RotationUtil.getRotationPlayer()
                                                    .getEntityBoundingBox())
                          .isEmpty())
        {
            return false;
        }

        EntityPlayer closestPlayer = EntityUtil.getClosestEnemy();
        if (closestPlayer != null
                && closestPlayer.getDistanceSq(mc.player)
                                    < MathUtil.square(closest.getValue() * 2))
        {
            for (Entity entity : mc.world.loadedEntityList)
            {
                if (entity instanceof EntityEnderCrystal
                        && !entity.isDead
                        && mc.player.getDistanceSq(entity) <= 144)
                {
                    double damage =
                        getDamageNoArmor(entity.posX, entity.posY, entity.posZ);

                    if (damage > EntityUtil.getHealth(mc.player) + 1.0
                            || damage > maxDmg.getValue())
                    {
                        return false;
                    }
                }
            }

            BlockPos middle = PositionUtil.getPosition();
            int maxRadius = Sphere.getRadius(10.0);
            for (int i = 1; i < maxRadius; i++)
            {
                BlockPos pos = middle.add(Sphere.get(i));
                if (BlockUtil.canPlaceCrystal(pos,
                                              false,
                                              newVer.getValue(),
                                              mc.world.loadedEntityList,
                                              newVerEntities.getValue(),
                                              0)
                    || bedCheck.getValue()
                        && BlockUtil.canPlaceBed(pos, newVer.getValue()))
                {
                    double damage = getDamageNoArmor(pos.getX() + 0.5,
                                                     pos.getY() + 1,
                                                     pos.getZ() + 0.5);
                    if (damage > EntityUtil.getHealth(mc.player) + 1.0
                            || damage > maxDmg.getValue())
                    {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    /**
     * Returns the damage an explosion of size 6.0 (EndCrystal)
     * would deal to us from the given x, y, z position, if we
     * were wearing only boots.
     *
     * @param x the x coordinate.
     * @param y the y coordinate.
     * @param z the z coordinate.
     * @return damage for the given coordinates
     */
    private double getDamageNoArmor(double x, double y, double z)
    {
        double distance = mc.player.getDistance(x, y, z) / 12.0;
        if (distance > 1.0)
        {
            return 0.0f;
        }
        else
        {
            double density = DamageUtil.getBlockDensity(
                    new Vec3d(x, y, z),
                    mc.player.getEntityBoundingBox(),
                    mc.world,
                    true,
                    true,
                    false,
                    false);

            double densityDistance = distance = (1.0 - distance) * density;
            float damage = DamageUtil.getDifficultyMultiplier((float)
                    ((densityDistance * densityDistance + distance)
                                                / 2.0 * 7.0 * 12.0 + 1.0));

            damage = CombatRules.getDamageAfterAbsorb(damage, 3, 2.0f);

            PotionEffect resistance =
                        mc.player.getActivePotionEffect(MobEffects.RESISTANCE);
            if (resistance != null)
            {
                damage =
                    damage * (25 - (resistance.getAmplifier() + 1) * 5) / 25.0f;
            }

            return Math.max(damage, 0.0f);
        }
    }

    /**
     * Returns the EntityEquipmentSlot belonging to
     * the given slot, or null if the slot isn't an
     * armor equipment slot or doesn't contain a stack
     * that is an instance of ItemArmor.
     *
     * @param slot the slot to check.
     * @return the EntityEquipmentSlot for the slot.
     */
    public static EntityEquipmentSlot fromSlot(int slot)
    {
        switch (slot)
        {
            case 5:
                return EntityEquipmentSlot.HEAD;
            case 6:
                return EntityEquipmentSlot.CHEST;
            case 7:
                return EntityEquipmentSlot.LEGS;
            case 8:
                return EntityEquipmentSlot.FEET;
            default:
                ItemStack stack = InventoryUtil.get(slot);
                return getSlot(stack);
        }
    }

    public static int fromEquipment(EntityEquipmentSlot equipmentSlot)
    {
        switch (equipmentSlot)
        {
            case OFFHAND:
                return 45;
            case FEET:
                return 8;
            case LEGS:
                return 7;
            case CHEST:
                return 6;
            case HEAD:
                return 5;
            default:
        }

        return -1;
    }

    public static EntityEquipmentSlot getSlot(ItemStack stack)
    {
        if (!stack.isEmpty())
        {
            if (stack.getItem() instanceof ItemArmor)
            {
                ItemArmor armor = (ItemArmor) stack.getItem();
                return armor.getEquipmentSlot();
            }
            else if (stack.getItem() instanceof ItemElytra)
            {
                return EntityEquipmentSlot.CHEST;
            }
        }

        return null;
    }

    /**
     * Finds an inventory slot that contains the
     * item and that is not contained in the blacklist.
     *
     * @param item the item to search for.
     * @param xCarry if xCarry should be searched
     * @param blackList slots to skip.
     * @return a slot for the item (-2 if drag) or -1 if not found.
     */
    public static int findItem(Item item,
                               boolean xCarry,
                               Set<Integer> blackList)
    {
        ItemStack drag = mc.player.inventory.getItemStack();
        if (!drag.isEmpty()
                && drag.getItem() == item
                && !blackList.contains(-2))
        {
            return -2;
        }

        for (int i = 9; i < 45; i++)
        {
            ItemStack stack = InventoryUtil.get(i);
            if (stack.getItem() == item && !blackList.contains(i))
            {
                return i;
            }
        }

        if (xCarry)
        {
            for (int i = 1; i < 5; i++)
            {
                ItemStack stack = InventoryUtil.get(i);
                if (stack.getItem() == item && !blackList.contains(i))
                {
                    return i;
                }
            }
        }

        return -1;
    }

    public static int iterateItems(boolean xCarry, Set<Integer> blackList, Function<ItemStack, Boolean> accept)
    {
        ItemStack drag = mc.player.inventory.getItemStack();
        if (!drag.isEmpty() && !blackList.contains(-2) && accept.apply(drag))
        {
            return -2;
        }

        for (int i = 9; i < 45; i++)
        {
            ItemStack stack = InventoryUtil.get(i);
            if (!blackList.contains(i) && accept.apply(stack))
            {
                return i;
            }
        }

        if (xCarry)
        {
            for (int i = 1; i < 5; i++)
            {
                ItemStack stack = InventoryUtil.get(i);
                if (!blackList.contains(i) && accept.apply(stack))
                {
                    return i;
                }
            }
        }

        return -1;
    }

}
