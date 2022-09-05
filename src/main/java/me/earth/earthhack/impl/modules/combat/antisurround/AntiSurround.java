package me.earth.earthhack.impl.modules.combat.antisurround;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Complexity;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.ColorSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.combat.antisurround.util.AntiSurroundFunction;
import me.earth.earthhack.impl.modules.combat.autocrystal.HelperLiquids;
import me.earth.earthhack.impl.modules.combat.autocrystal.util.MineSlots;
import me.earth.earthhack.impl.modules.combat.legswitch.LegSwitch;
import me.earth.earthhack.impl.modules.player.noglitchblocks.NoGlitchBlocks;
import me.earth.earthhack.impl.util.client.ModuleUtil;
import me.earth.earthhack.impl.util.helpers.blocks.ObbyListenerModule;
import me.earth.earthhack.impl.util.helpers.blocks.modes.Pop;
import me.earth.earthhack.impl.util.helpers.render.BlockESPBuilder;
import me.earth.earthhack.impl.util.helpers.render.IAxisESP;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.math.RayTraceUtil;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.DamageUtil;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.minecraft.Swing;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.states.BlockStateHelper;
import me.earth.earthhack.impl.util.minecraft.blocks.states.IBlockStateHelper;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import me.earth.earthhack.impl.util.network.PacketUtil;
import me.earth.earthhack.impl.util.text.TextColor;
import me.earth.earthhack.impl.util.thread.Locks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemAnvilBlock;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import java.awt.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class AntiSurround extends ObbyListenerModule<ListenerObby>
{
    protected static final ModuleCache<LegSwitch> LEG_SWITCH =
            Caches.getModule(LegSwitch.class);
    private static final ModuleCache<NoGlitchBlocks> NOGLITCHBLOCKS =
            Caches.getModule(NoGlitchBlocks.class);

    protected final Setting<Double> range =
        register(new NumberSetting<>("Range", 5.25, 0.1, 6.0));
    protected final Setting<Boolean> async =
        register(new BooleanSetting("Async", false))
            .setComplexity(Complexity.Expert);
    protected final Setting<Boolean> instant = // TODO
        register(new BooleanSetting("Instant", false))
            .setComplexity(Complexity.Medium);
    protected final Setting<Boolean> persistent =
        register(new BooleanSetting("Persistent", true))
            .setComplexity(Complexity.Medium);
    protected final Setting<Boolean> obby =
        register(new BooleanSetting("Obby", false))
            .setComplexity(Complexity.Medium);
    protected final Setting<Boolean> digSwing =
        register(new BooleanSetting("DigSwing", false))
            .setComplexity(Complexity.Expert);
    protected final Setting<Boolean> newVer =
        register(new BooleanSetting("1.13+", false))
            .setComplexity(Complexity.Medium);
    protected final Setting<Boolean> newVerEntities =
        register(new BooleanSetting("1.13-Entities", false))
            .setComplexity(Complexity.Medium);
    protected final Setting<Boolean> onGround =
        register(new BooleanSetting("OnGround", true))
            .setComplexity(Complexity.Expert);
    protected final Setting<Float> minDmg =
        register(new NumberSetting<>("MinDamage", 5.0f, 0.0f, 36.0f))
            .setComplexity(Complexity.Medium);
    protected final Setting<Integer> itemDeathTime =
        register(new NumberSetting<>("ItemDeathTime", 100, 0, 1000))
            .setComplexity(Complexity.Expert);
    protected final Setting<Boolean> pickaxeOnly =
        register(new BooleanSetting("HoldingPickaxe", false))
            .setComplexity(Complexity.Medium);
    protected final Setting<Boolean> anvil =
        register(new BooleanSetting("Anvil", false))
            .setComplexity(Complexity.Medium);
    protected final Setting<Boolean> drawEsp =
        register(new BooleanSetting("ESP", true))
            .setComplexity(Complexity.Medium);
    protected final Setting<Boolean> preCrystal =
        register(new BooleanSetting("PreCrystal", false))
            .setComplexity(Complexity.Expert);
    protected final Setting<Color> color =
        register(new ColorSetting("Color", new Color(255, 255, 255, 75)))
            .setComplexity(Complexity.Medium);
    protected final Setting<Color> outline =
        register(new ColorSetting("Outline", new Color(255, 255, 255, 240)))
            .setComplexity(Complexity.Medium);
    protected final Setting<Float> lineWidth =
        register(new NumberSetting<>("LineWidth", 1.5f, 0.0f, 10.0f))
            .setComplexity(Complexity.Expert);
    protected final Setting<Float> height =
        register(new NumberSetting<>("ESP-Height", 1.0f, -1.0f, 1.0f))
            .setComplexity(Complexity.Medium);
    protected final Setting<Boolean> normal =
        register(new BooleanSetting("Normal", true))
            .setComplexity(Complexity.Expert);
    protected final Setting<Boolean> stopOnObby =
        register(new BooleanSetting("StopOnObby", false))
            .setComplexity(Complexity.Expert);

    // TODO: don't register this until we found implemented
    //  a way to attack the block x ticks before we break it
    protected final Setting<Float> minMine =
        new NumberSetting<>("MinMine", 1.0f, 0.0f, 10.0f);

    protected final AtomicBoolean semiActive = new AtomicBoolean();
    protected final AtomicBoolean active = new AtomicBoolean();
    protected volatile long semiActiveTime;
    protected final IAxisESP esp;

    protected int crystalSwitchBackSlot = -1;
    protected int crystalSlot = -1;
    protected int toolSlot    = -1;
    protected int obbySlot    = -1;
    protected EntityPlayer target;
    protected volatile BlockPos semiPos;
    protected BlockPos crystalPos;
    protected BlockPos playerPos;
    protected BlockPos pos;
    protected boolean hasMined;
    protected boolean isAnvil;
    protected boolean mine;
    protected int ticks;

    public AntiSurround()
    {
        super("AntiSurround", Category.Combat);
        this.listeners.clear(); // Remove DisablingModule listeners
        this.listeners.add(this.listener);
        this.listeners.add(new ListenerBlockBreak(this));
        this.listeners.add(new ListenerBlockChange(this));
        this.listeners.add(new ListenerBlockMulti(this));
        this.listeners.add(new ListenerRender(this));
        this.listeners.add(new ListenerDigging(this));
        this.listeners.add(new ListenerDiggingNoEvent(this));
        // lock attacking cause the entire module is about it
        this.attack.setValue(true);
        this.unregister(this.attack);
        this.attack.addObserver(e -> e.setCancelled(true));
        this.attackAny.setValue(false);
        this.unregister(this.attackAny);
        this.unregister(this.attackRange);
        this.unregister(this.attackTrace);
        this.breakDelay.setValue(50);
        this.attackAny.addObserver(e -> e.setCancelled(true));
        this.pop.setValue(Pop.Time);
        this.cooldown.setValue(0);

        this.esp = new BlockESPBuilder()
                        .withColor(color)
                        .withOutlineColor(outline)
                        .withLineWidth(lineWidth)
                        .build();
    }

    @Override
    public String getDisplayInfo()
    {
        EntityPlayer target = this.target;
        if (target != null)
        {
            return target.getName();
        }

        return null;
    }

    @Override
    protected void onEnable()
    {
        if (NOGLITCHBLOCKS.returnIfPresent(NoGlitchBlocks::noBreak, false))
        {
            ModuleUtil.sendMessage(this, TextColor.RED + "NoGlitchBlocks -" +
                " Break is active. This can cause issues with AntiSurround!");
        }

        super.onEnable();
        reset();
    }

    @Override
    protected void onDisable()
    {
        super.onDisable();
        reset();
    }

    @Override
    protected boolean checkNull()
    {
        packets.clear();
        blocksPlaced = 0;
        return mc.player != null && mc.world != null;
    }

    @Override
    public boolean execute()
    {
        EnumFacing facing;
        if (!packets.isEmpty() && mine)
        {
            BlockPos pos = this.pos;
            facing = RayTraceUtil.getFacing(
                    RotationUtil.getRotationPlayer(), pos, true);

            EnumFacing finalFacing = facing;
            Locks.acquire(Locks.PLACE_SWITCH_LOCK, () ->
            {
                // TODO: fix this, I dont like this, too many switches!
                int lastSlot = mc.player.inventory.currentItem;
                cooldownBypass.getValue().switchTo(toolSlot);
                if (!isAnvil)
                {
                    PacketUtil.startDigging(pos, finalFacing);
                }

                PacketUtil.stopDigging(pos, finalFacing);
                hasMined = false;

                if (digSwing.getValue())
                {
                    Swing.Packet.swing(EnumHand.MAIN_HAND);
                }

                cooldownBypass.getValue().switchBack(lastSlot, toolSlot);
            });
        }

        lastSlot = -1;
        boolean execute = false;
        if (!packets.isEmpty())
        {
            execute = super.execute();
        }
        else if (!post.isEmpty())
        {
            Locks.acquire(Locks.PLACE_SWITCH_LOCK, () ->
            {
                int lastSlot = mc.player.inventory.currentItem;
                post.forEach(Runnable::run);
                cooldownBypass.getValue().switchBack(lastSlot, crystalSwitchBackSlot != -1 ? crystalSwitchBackSlot : lastSlot);
                crystalSwitchBackSlot = -1;
            });

            post.clear();
        }

        mine = false;
        return execute;
    }

    @Override
    protected ListenerObby createListener()
    {
        return new ListenerObby(this);
    }

    public boolean holdingCheck()
    {
        return pickaxeOnly.getValue()
            && !(mc.player.getHeldItemMainhand().getItem()
                    instanceof ItemPickaxe);
    }

    public boolean isActive()
    {
        return this.isEnabled() && (active.get() || semiActive.get());
    }

    public void reset()
    {
        semiActive.set(false);
        active.set(false);
        slot        = -1;
        crystalSlot = -1;
        toolSlot    = -1;
        obbySlot    = -1;
        semiPos     = null;
        target      = null;
        pos         = null;
        crystalPos  = null;
        mine        = false;
        hasMined    = false;
    }

    public boolean onBlockBreak(BlockPos pos,
                                List<EntityPlayer> players,
                                List<Entity> entities)
    {
        return onBlockBreak(pos, players, entities, this::placeSync);
    }

    public boolean onBlockBreak(BlockPos pos,
                                List<EntityPlayer> players,
                                List<Entity> entities,
                                AntiSurroundFunction function)
    {
        if (LEG_SWITCH.returnIfPresent(LegSwitch::isActive, false))
        {
            return false;
        }

        MineSlots slots = HelperLiquids.getSlots(onGround.getValue());
        if (slots.getDamage() < minMine.getValue()
                    && !(isAnvil = anvilCheck(slots))
                || slots.getToolSlot() == -1
                || slots.getBlockSlot() == -1)
        {
            return false;
        }

        int crystalSlot = InventoryUtil.findHotbarItem(Items.END_CRYSTAL);
        if (crystalSlot == -1)
        {
            return false;
        }

        int obbySlot    = InventoryUtil.findHotbarBlock(Blocks.OBSIDIAN);

        IBlockStateHelper helper = new BlockStateHelper();
        helper.addBlockState(pos, Blocks.AIR.getDefaultState());

        Entity blocking = getBlockingEntity(pos, entities);
        if (blocking != null
                && !(blocking instanceof EntityEnderCrystal))
        {
            return false;
        }

        for (EnumFacing facing : EnumFacing.HORIZONTALS)
        {
            BlockPos offset = pos.offset(facing);
            if (!mc.world.getBlockState(offset).getMaterial().isReplaceable())
            {
                continue;
            }

            EntityPlayer found = null;
            AxisAlignedBB offsetBB = new AxisAlignedBB(offset);
            for (EntityPlayer player : players)
            {
                if (player == null
                        || EntityUtil.isDead(player)
                        || player.equals(mc.player)
                        || player.equals(RotationUtil.getRotationPlayer())
                        || Managers.FRIENDS.contains(player)
                        || !player.getEntityBoundingBox().intersects(offsetBB))
                {
                    continue;
                }

                found = player;
                break;
            }

            if (found == null)
            {
                continue;
            }

            BlockPos opposite = pos.offset(facing.getOpposite());
            BlockPos down     = opposite.down();
            if (BlockUtil.getDistanceSq(down)
                    > MathUtil.square(range.getValue()))
            {
                continue;
            }

            if (BlockUtil.canPlaceCrystalReplaceable(down,
                    true, newVer.getValue(), entities,
                    newVerEntities.getValue(), 0))
            {
                IBlockState state = mc.world.getBlockState(down);
                if ((!obby.getValue() || obbySlot == -1)
                        && state.getBlock() != Blocks.OBSIDIAN
                        && state.getBlock() != Blocks.BEDROCK)
                {
                    continue;
                }

                helper.addBlockState(down, Blocks.OBSIDIAN.getDefaultState());
                float damage = DamageUtil.calculate(down, found, helper);
                helper.delete(down);
                if (damage < minDmg.getValue())
                {
                    continue;
                }

                BlockPos on = null;
                EnumFacing onFacing = null;
                for (EnumFacing off : EnumFacing.values())
                {
                    on = pos.offset(off);
                    if (BlockUtil.getDistanceSq(on)
                            <= MathUtil.square(range.getValue())
                            && !mc.world.getBlockState(on)
                            .getMaterial()
                            .isReplaceable())
                    {
                        onFacing = off.getOpposite();
                        break;
                    }
                }

                if (onFacing == null) // TODO: helping blocks?
                {
                    continue;
                }

                function.accept(pos, down, on, onFacing, obbySlot,
                    slots, crystalSlot, blocking, found, true);
                return true;
            }
        }

        return false;
    }

    protected Entity getBlockingEntity(BlockPos pos, List<Entity> entities)
    {
        Entity blocking = null;
        AxisAlignedBB bb = new AxisAlignedBB(pos);
        for (Entity entity : entities)
        {
            if (entity == null
                || EntityUtil.isDead(entity)
                || !entity.preventEntitySpawning
                || !entity.getEntityBoundingBox().intersects(bb))
            {
                continue;
            }

            if (entity instanceof EntityEnderCrystal)
            {
                blocking = entity;
                continue;
            }

            return entity;
        }

        return blocking;
    }

    public synchronized boolean placeSync(BlockPos pos,
                                          BlockPos down,
                                          BlockPos on,
                                          EnumFacing onFacing,
                                          int obbySlot,
                                          MineSlots slots,
                                          int crystalSlot,
                                          Entity blocking,
                                          EntityPlayer found,
                                          boolean execute)
    {
        // check again, this time synchronized
        if (active.get()
            || LEG_SWITCH.returnIfPresent(LegSwitch::isActive, false))
        {
            return false;
        }

        this.obbySlot    = obbySlot;
        this.slot        = slots.getBlockSlot();
        this.toolSlot    = slots.getToolSlot();
        this.crystalSlot = crystalSlot;
        this.crystalPos  = down;
        this.pos         = pos;
        this.target      = found;
        this.playerPos   = PositionUtil.getPosition(found);

        this.active.set(true);
        this.placeBlock(on, onFacing);
        if (blocking != null)
        {
            this.attacking = new CPacketUseEntity(blocking);
        }

        if (execute && (blocking != null || semiPos == null))
        {
            Locks.acquire(Locks.PLACE_SWITCH_LOCK, this::execute);
        }

        return true;
    }

    public boolean anvilCheck(MineSlots slots)
    {
        int slot = slots.getBlockSlot();
        if (slot == -1 || !anvil.getValue())
        {
            return false;
        }

        ItemStack stack = mc.player.inventory.getStackInSlot(slot);
        return stack.getItem() instanceof ItemAnvilBlock;
    }

}
