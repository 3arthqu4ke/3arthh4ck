package me.earth.earthhack.impl.util.helpers.blocks;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Complexity;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.player.noglitchblocks.NoGlitchBlocks;
import me.earth.earthhack.impl.util.helpers.blocks.data.BlockPlacingData;
import me.earth.earthhack.impl.util.helpers.blocks.modes.PlaceSwing;
import me.earth.earthhack.impl.util.helpers.blocks.modes.RayTraceMode;
import me.earth.earthhack.impl.util.helpers.blocks.modes.Rotate;
import me.earth.earthhack.impl.util.helpers.disabling.DisablingModule;
import me.earth.earthhack.impl.util.math.DiscreteTimer;
import me.earth.earthhack.impl.util.math.GuardTimer;
import me.earth.earthhack.impl.util.math.RayTraceUtil;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.CooldownBypass;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.minecraft.Swing;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockingType;
import me.earth.earthhack.impl.util.minecraft.blocks.SpecialBlocks;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import me.earth.earthhack.pingbypass.PingBypass;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A module specialized on placing Obsidian.
 */
// TODO: VERY IMPORTANT: REPLACE ALL IS_REPLACEABLE CHECKS WITH CAN_COLLIDE! (?)
// TODO: check if we run out of blocks, before sending all the packets...
public abstract class BlockPlacingModule extends DisablingModule
{
    private static final ModuleCache<NoGlitchBlocks> NO_GLITCH_BLOCKS =
            Caches.getModule(NoGlitchBlocks.class);
    // Make these like not public?
    public final Setting<Integer> blocks =
            register(new NumberSetting<>("Blocks/Place", 4, 1, 10));
    public final Setting<Integer> delay  =
            register(new NumberSetting<>("Delay", 25, 0, 1000))
                .setComplexity(Complexity.Medium);
    public final Setting<Rotate> rotate  =
            register(new EnumSetting<>("Rotations", Rotate.None));
    public final Setting<Boolean> packet =
            register(new BooleanSetting("Packet", true))
                .setComplexity(Complexity.Medium);
    public final Setting<Boolean> swing  =
            register(new BooleanSetting("Swing", false))
                .setComplexity(Complexity.Medium);
    public final Setting<CooldownBypass> cooldownBypass =
            register(new EnumSetting<>("Cooldown-Bypass", CooldownBypass.None))
                .setComplexity(Complexity.Medium);
    public final Setting<Boolean> stackPacket =
            register(new BooleanSetting("StackPacket", false))
                .setComplexity(Complexity.Expert);
    public final Setting<Boolean> smartSneak =
            register(new BooleanSetting("Smart-Sneak", false))
                .setComplexity(Complexity.Expert);
    public final Setting<PlaceSwing> placeSwing =
            register(new EnumSetting<>("PlaceSwing", PlaceSwing.Always))
                .setComplexity(Complexity.Medium);
    public final Setting<BlockingType> blockingType =
            register(new EnumSetting<>("Blocking", BlockingType.Strict))
                .setComplexity(Complexity.Expert);
    public final Setting<RayTraceMode> smartRay =
            register(new EnumSetting<>("Raytrace", RayTraceMode.Fast))
                .setComplexity(Complexity.Expert);

    /** Timer for the delay setting.*/
    public final DiscreteTimer timer = new GuardTimer(500).reset(getDelay());
    /** Packets to send after rotations have been spoofed.*/
    public final List<Packet<?>> packets = new ArrayList<>();
    /** Stuff that is executed after all packets have been sent. */
    public final List<Runnable> post = new ArrayList<>();
    /** Counts blocks placed to be limited by the blocks setting. */
    public int blocksPlaced =  0;
    /** The hotbar slot Obsidian/Echests are in. */
    public int slot = -1;
    /** Last Slot in case we wish to switch to a specific slot. */
    public int lastSlot = -1;
    /** The rotations to the placed block. */
    public float[] rotations;
    protected int crystalSlot = -1;

    protected BlockPlacingModule(String name, Category category)
    {
        super(name, category);
        this.setData(new BlockPlacingData<>(this));
    }

    @Override
    protected void onEnable()
    {
        checkNull();
    }

    /**
     * Places a block on the given
     * position with the given facing.
     *
     * @param on the position to place on.
     * @param facing the facing.
     */
    public void placeBlock(BlockPos on, EnumFacing facing)
    {
        Entity from = getPlayerForRotations();
        float[] r =
            RotationUtil.getRotations(on, facing, from);
        RayTraceResult result =
            RayTraceUtil.getRayTraceResultWithEntity(r[0], r[1], from);

        placeBlock(on, facing, r, result.hitVec);
    }

    public void placeBlock(BlockPos on,
                           EnumFacing facing,
                           float[] helpingRotations,
                           Vec3d hitVec)
    {
        if (rotations == null && (rotate.getValue() == Rotate.Normal
                || (blocksPlaced == 0
                        && rotate.getValue() == Rotate.Packet)))
        {
            rotations = helpingRotations;
        }
        else if (rotate.getValue() == Rotate.Packet)
        {
            packets.add(new CPacketPlayer.Rotation(helpingRotations[0],
                                                   helpingRotations[1],
                                                   getPlayer().onGround));
        }

        float[] f = RayTraceUtil.hitVecToPlaceVec(on, hitVec);
        EnumHand hand = InventoryUtil.getHand(slot);
        packets.add(new CPacketPlayerTryUseItemOnBlock(on,
                                                       facing,
                                                       hand,
                                                       f[0],
                                                       f[1],
                                                       f[2]));
        if (placeSwing.getValue() == PlaceSwing.Always)
        {
            packets.add(new CPacketAnimation(InventoryUtil.getHand(slot)));
        }

        // Simulate PlayerControllerMP behaviour.
        if (!packet.getValue()
                && !(NO_GLITCH_BLOCKS.isPresent()
                && NO_GLITCH_BLOCKS.get().noPlace()))
        {
            ItemStack stack = slot == -2
                    ? mc.player.getHeldItemOffhand()
                    : mc.player.inventory.getStackInSlot(slot);
            mc.addScheduledTask(() ->
                placeClient(stack, on, hand, facing, f[0], f[1], f[2]));
        }

        blocksPlaced++;
    }

    /**
     * Basically what happens in PlayerControllerMP
     * when NoGlitchBlocks isn't enabled.
     *
     * @param stack the stack to place with.
     * @param pos the position to place on.
     * @param hand the hand to place with.
     * @param facing the facing of the pos we place on.
     * @param hitX hit vec x.
     * @param hitY hit vec y.
     * @param hitZ hit vec z.
     */
    public void placeClient(ItemStack stack,
                             BlockPos pos,
                             EnumHand hand,
                             EnumFacing facing,
                             float hitX,
                             float hitY,
                             float hitZ)
    {
        if (stack.getItem() instanceof ItemBlock)
        {
            ItemBlock itemBlock = (ItemBlock) stack.getItem();
            Block block          = itemBlock.getBlock();
            IBlockState iblockstate = mc.world.getBlockState(pos);
            Block iBlock = iblockstate.getBlock();

            if (!iBlock.isReplaceable(mc.world, pos))
            {
                pos = pos.offset(facing);
            }

            if (!stack.isEmpty() && mc.player.canPlayerEdit(pos, facing, stack)
                    && mc.world.mayPlace(block, pos, false, facing, null))
            {
                int i = itemBlock.getMetadata(stack.getMetadata());
                IBlockState placeState = block.getStateForPlacement(mc.world,
                                                                    pos,
                                                                    facing,
                                                                    hitX,
                                                                    hitY,
                                                                    hitZ,
                                                                    i,
                                                                    mc.player,
                                                                    hand);
                if (itemBlock.placeBlockAt(stack,
                                            mc.player,
                                            mc.world,
                                            pos,
                                            facing,
                                            hitX,
                                            hitY,
                                            hitZ,
                                            placeState))
                {
                    placeState = mc.world.getBlockState(pos);
                    SoundType soundtype = placeState
                                                .getBlock()
                                                .getSoundType(placeState,
                                                                mc.world,
                                                                pos,
                                                                mc.player);
                    mc.world.playSound(mc.player,
                                        pos,
                                        soundtype.getPlaceSound(),
                                        SoundCategory.BLOCKS,
                                        (soundtype.getVolume() + 1.0F) / 2.0F,
                                        soundtype.getPitch() * 0.8F);

                    if (!mc.player.isCreative() && stackPacket.getValue())
                    {
                        stack.shrink(1);
                    }
                }
            }
        }
    }

    /**
     * Sends all packets that have been added via
     * {@link BlockPlacingModule#placeBlock(BlockPos, EnumFacing)}, also
     * switches to the obbySlot, and sneaks.
     *
     * @return <tt>false</tt> if packets are empty and nothing has been sent.
     */
    public boolean execute()
    {
        if (!packets.isEmpty())
        {
            boolean sneaking = sneak(packets);
            int lastSlot = this.lastSlot == -1
                            ? mc.player.inventory.currentItem
                            : this.lastSlot;

            // TODO: dont switch if we dont have to?
            cooldownBypass.getValue().switchTo(slot);

            if (!sneaking)
            {
                PingBypass.sendToActualServer(
                    new CPacketEntityAction(
                        mc.player, CPacketEntityAction.Action.START_SNEAKING));
            }

            packets.forEach(PingBypass::sendToActualServer);
            timer.reset(delay.getValue());

            if (placeSwing.getValue() == PlaceSwing.Once)
            {
                Swing.Packet.swing(InventoryUtil.getHand(slot));
            }

            if (!sneaking)
            {
                PingBypass.sendToActualServer(
                    new CPacketEntityAction(
                        mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
            }

            post.forEach(Runnable::run);
            packets.clear();
            post.clear();

            cooldownBypass.getValue().switchBack(lastSlot, slot);

            if (swing.getValue())
            {
                Swing.Client.swing(InventoryUtil.getHand(slot));
            }

            return true;
        }

        return false;
    }

    /**
     * @param packets the packets to check.
     * @return <tt>true</tt> if we shouldn't sneak.
     */
    protected boolean sneak(Collection<Packet<?>> packets)
    {
        return smartSneak.getValue()
                    && !(Managers.ACTION.isSneaking()
                        || packets.stream()
                                  .anyMatch(SpecialBlocks.PACKETCHECK));
    }

    /**
     * Method run onEnable.
     *
     * @return <tt>true</tt> if mc.world/player != null.
     */
    protected boolean checkNull()
    {
        packets.clear();
        blocksPlaced = 0;

        if (mc.player == null || mc.world == null)
        {
            this.disable();
            return false;
        }

        return true;
    }

    /**
     * A simple entity check. Checks if there are
     * any entities blocking the position we want to place on,
     * further if the entity is an entity player and
     *
     * @param pos the position to check.
     * @return <tt>true</tt> if no entities block.
     */
    public boolean entityCheck(BlockPos pos)
    {
        return entityCheckSimple(pos);
    }

    protected boolean entityCheckSimple(BlockPos pos)
    {
        for (Entity entity : mc.world
                .getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos)))
        {
            if (entity == null
                    || EntityUtil.isDead(entity)
                    || !entity.preventEntitySpawning
                    || (entity instanceof EntityPlayer
                        && !BlockUtil.isBlocking(pos,
                                                 (EntityPlayer) entity,
                                                 blockingType.getValue()))
                    || (entity instanceof EntityEnderCrystal
                        && blockingType.getValue() == BlockingType.Crystals))
            {
                continue;
            }

            return false;
        }

        return true;
    }

    public EntityPlayer getPlayerForRotations()
    {
        return mc.player;
    }

    public EntityPlayer getPlayer()
    {
        return mc.player;
    }

    public int getDelay()
    {
        return delay.getValue();
    }

}
