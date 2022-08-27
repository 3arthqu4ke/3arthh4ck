package me.earth.earthhack.impl.modules.misc.autoregear;

import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BindSetting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.api.util.bind.Bind;
import me.earth.earthhack.impl.util.helpers.addable.setting.SimpleRemovingSetting;
import me.earth.earthhack.impl.util.helpers.blocks.BlockPlacingModule;
import me.earth.earthhack.impl.util.helpers.command.CustomCommandModule;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockUtil;
import me.earth.earthhack.impl.util.text.ChatUtil;
import me.earth.earthhack.impl.util.text.TextColor;
import net.minecraft.block.Block;
import net.minecraft.block.BlockShulkerBox;
import net.minecraft.init.Items;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemShulkerBox;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class AutoRegear extends BlockPlacingModule
        implements CustomCommandModule
{
    // TODO: yes pls use, args.length = 1 is used to display settings in the chat
    private static final String[] ARGS = new String[]{"SAVE", "RESET"};

    protected final Setting<Integer> delay =
            register(new NumberSetting<>("Delay", 50, 0, 500));
    protected final Setting<Float> range =
            register(new NumberSetting<>("Range", 6.0f, 1.0f, 8.0f));
    protected final Setting<Bind> regear =
            register(new BindSetting("Regear", Bind.none()));
    protected final Setting<Boolean> grabShulker =
            register(new BooleanSetting("GrabShulker", false));
    protected final Setting<Boolean> placeShulker =
            register(new BooleanSetting("PlaceShulker", false));
    protected final Setting<Boolean> placeEchest =
            register(new BooleanSetting("PlaceEchest", false));
    protected final Setting<Boolean> steal =
            register(new BooleanSetting("Steal", false));

    public AutoRegear()
    {
        super("AutoRegear", Category.Misc);
        this.listeners.add(new ListenerTick(this));
        this.listeners.add(new ListenerKeyPress(this));
        this.listeners.add(new ListenerMotion(this));
    }

    protected StopWatch delayTimer = new StopWatch();
    protected boolean shouldRegear;

    protected void onEnable()
    {
        shouldRegear = false;
    }

    public void unregisterAll()
    {
        for (int i = 0; i < 28; i++)
        {
            if (getSettingFromSlot(i) != null)
            {
                unregister(getSettingFromSlot(i));
            }
        }
    }

    public void registerInventory()
    {
        for (int i = 9; i < 45; i++)
        {
            int id = Item.REGISTRY.getIDForObject(mc.player.inventoryContainer.getInventory().get(i).getItem());
            register(new SimpleRemovingSetting((i - 9) + ":" + id));
        }
    }

    @Override
    public boolean execute(String[] args)
    {
        if (args.length == 1)
        {
            unregisterAll();
            registerInventory();
            ChatUtil.sendMessage(TextColor.GREEN + "Kit saved!");
            return true;
        }

        return false;
    }

    @Override
    public Setting<?> getSettingConfig(String name)
    {
        if (getSetting(name) == null)
        {
            Setting<?> newSetting = new SimpleRemovingSetting(name);
            register(newSetting);
            return newSetting;
        }
        else
        {
            return getSetting(name);
        }
    }

    public Setting<?> getSettingFromSlot(int slot) {
        for (Setting<?> setting : getSettings()) {
            if (setting.getName().startsWith(Integer.toString(slot))) return setting;
        }
        return null;
    }

    private int safetyFactor(BlockPos pos)
    {
        return safety(pos) + safety(pos.up());
    }

    private int safety(BlockPos pos)
    {
        int safety = 0;
        for (EnumFacing facing : EnumFacing.values()) {
            if (!mc.world.getBlockState(pos.offset(facing)).getMaterial().isReplaceable()) {
                safety++;
            }
        }
        return safety;
    }

    public BlockPos getBlock(Block type) {
        AtomicReference<BlockPos> block = new AtomicReference<>();
        BlockUtil.sphere(range.getValue(), (pos ->
        {
            if (mc.world.getBlockState(pos).getBlock() == type)
            {
                block.set(pos);
            }
            return false;
        }));
        return block.get();
    }

    public BlockPos getShulkerBox() {
        AtomicReference<BlockPos> block = new AtomicReference<>();
        BlockUtil.sphere(range.getValue(), (pos ->
        {
            if (mc.world.getBlockState(pos).getBlock() instanceof BlockShulkerBox)
            {
                block.set(pos);
            }
            return false;
        }));
        return block.get();
    }

    public BlockPos getOptimalPlacePos(boolean shulkerCheck) {
        Set<BlockPos> positions = new HashSet<>();
        BlockUtil.sphere(range.getValue(), (pos ->
        {
            if (mc.world.getBlockState(pos).getBlock().isReplaceable(mc.world, pos)
                    && entityCheck(pos)
                    && mc.world.getBlockState(pos.up()).getBlock().isReplaceable(mc.world, pos)
                    && (!shulkerCheck || mc.world.getBlockState(pos.down()).getBlock().isReplaceable(mc.world, pos)))
            {
                positions.add(pos);
            }
            return false;
        }));
        return positions.stream().sorted(Comparator.comparingInt(pos -> safety(pos) * -1)).collect(Collectors.toList()).get(0);
    }

    public boolean hasKit()
    {
        for (int i = 9; i < 45; i++)
        {
            boolean foundExp = false;
            boolean foundCrystals = false;
            boolean foundGapples = false;
            ItemStack stack = mc.player.inventoryContainer.getInventory().get(i);
            if (stack.getItem() instanceof ItemShulkerBox)
            {
                NBTTagCompound tagCompound = stack.getTagCompound();
                if (tagCompound != null && tagCompound.hasKey("BlockEntityTag", 10))
                {
                    NBTTagCompound blockEntityTag =
                            tagCompound.getCompoundTag("BlockEntityTag");

                    if (blockEntityTag.hasKey("Items", 9))
                    {
                        NonNullList<ItemStack> nonNullList =
                                NonNullList.withSize(27, ItemStack.EMPTY);

                        ItemStackHelper.loadAllItems(blockEntityTag, nonNullList);
                        for (ItemStack stack1 : nonNullList)
                        {
                            if (stack1.getItem() == Items.GOLDEN_APPLE)
                            {
                                foundGapples = true;
                            }
                            else if (stack1.getItem() == Items.EXPERIENCE_BOTTLE)
                            {
                                foundExp = true;
                            }
                            else if (stack1.getItem() == Items.END_CRYSTAL)
                            {
                                foundCrystals = true;
                            }
                        }
                        if (foundExp
                                && foundGapples
                                && foundCrystals)
                        {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

}
