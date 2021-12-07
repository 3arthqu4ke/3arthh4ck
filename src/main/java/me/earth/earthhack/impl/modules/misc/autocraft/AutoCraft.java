package me.earth.earthhack.impl.modules.misc.autocraft;

import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.util.helpers.blocks.BlockPlacingModule;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockUtil;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiCrafting;
import net.minecraft.init.Blocks;
import net.minecraft.item.crafting.*;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class AutoCraft extends BlockPlacingModule
{
    protected final Setting<Integer> delay =
            register(new NumberSetting<>("Delay", 50, 0, 500));
    protected final Setting<Integer> clickDelay =
            register(new NumberSetting<>("ClickDelay", 50, 0, 500));
    protected final Setting<Boolean> placeTable =
            register(new BooleanSetting("PlaceTable", false));
    protected final Setting<Boolean> craftTable =
            register(new BooleanSetting("CraftTable", false));
    protected final Setting<Boolean> moveTable =
            register(new BooleanSetting("MoveTable", false));
    protected final Setting<Float> tableRange =
            register(new NumberSetting<>("TableRange", 6.0f, 1.0f, 8.0f));

    public AutoCraft()
    {
        super("AutoCraft", Category.Misc);
        this.listeners.add(new ListenerTick(this));
        this.listeners.add(new ListenerMotion(this));
    }

    private final Queue<CraftTask> taskQueue = new ConcurrentLinkedDeque<>();
    protected CraftTask currentTask;
    protected CraftTask lastTask;
    protected final StopWatch delayTimer = new StopWatch();
    protected final StopWatch clickDelayTimer = new StopWatch();
    protected boolean shouldTable = false;

    protected void onEnable()
    {
        delayTimer.reset();
        clickDelayTimer.reset();
        // submit(new CraftTask("crafting_table", 1));
        submit(new CraftTask("furnace", 1));
    }

    public BlockPos getCraftingTable() {
        AtomicReference<BlockPos> craftingTable = new AtomicReference<>();
        BlockUtil.sphere(tableRange.getValue(), (pos ->
        {
            if (mc.world.getBlockState(pos).getBlock() == Blocks.CRAFTING_TABLE)
            {
                craftingTable.set(pos);
            }
            return false;
        }));
        return craftingTable.get();
    }

    public BlockPos getCraftingTableBlock() {
        Set<BlockPos> positions = new HashSet<>();
        BlockUtil.sphere(tableRange.getValue(), (pos ->
        {
            if (mc.world.getBlockState(pos).getBlock().isReplaceable(mc.world, pos)
                    && entityCheck(pos)) {
                positions.add(pos);
            }
            return false;
        }));
        return positions.stream().sorted(Comparator.comparingInt(pos -> safety(pos) * -1)).collect(Collectors.toList()).get(0);
    }

    public void submit(CraftTask task)
    {
        taskQueue.add(task);
    }

    public CraftTask dequeue()
    {
        return taskQueue.poll();
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

    public static class SlotEntry
    {
        private final int guiSlot;
        private final int inventorySlot;

        public SlotEntry(int guiSlot, int inventorySlot)
        {
            this.guiSlot = guiSlot;
            this.inventorySlot = inventorySlot;
        }

        public int getGuiSlot()
        {
            return guiSlot;
        }

        public int getInventorySlot()
        {
            return inventorySlot;
        }
    }

    public static class CraftTask
    {
        private final IRecipe recipe;
        private List<SlotEntry> entryList;
        private final boolean inTable;
        protected int runs;
        protected int step = 0;

        public CraftTask(String recipeName, int runs)
        {
            this(Objects.requireNonNull(CraftingManager.getRecipe(new ResourceLocation(recipeName))), runs);
        }

        public CraftTask(IRecipe recipe, int runs)
        {
            this.recipe = recipe;
            entryList = new ArrayList<>();
            inTable = !recipe.canFit(2, 2);
            this.runs = runs;

            int i = 1;
            for (Ingredient stack : recipe.getIngredients())
            {
                if (recipe instanceof ShapedRecipes)
                {
                    if (stack != Ingredient.EMPTY)
                    {
                        int inventorySlot = InventoryUtil.findInInventory(
                                itemStack ->
                                        Arrays.stream(
                                                stack.getMatchingStacks())
                                                .anyMatch(itemStack1 -> itemStack1.getItem() == itemStack.getItem()),
                                    false);
                        entryList.add(new SlotEntry(i, inventorySlot));
                    }
                    i++;
                }
                else if (recipe instanceof ShapelessRecipes) // TODO: shapeless support
                {

                }
            }
        }

        public void updateSlots() {
            int i = 1;
            List<SlotEntry> entries = new ArrayList<>();
            for (Ingredient stack : recipe.getIngredients())
            {
                if (recipe instanceof ShapedRecipes)
                {
                    if (stack != Ingredient.EMPTY)
                    {
                        int inventorySlot;
                        if (mc.currentScreen instanceof GuiCrafting)
                        {
                            inventorySlot = InventoryUtil.findInCraftingTable(((GuiContainer) mc.currentScreen).inventorySlots,
                                    itemStack ->
                                        Arrays.stream(
                                            stack.getMatchingStacks())
                                            .anyMatch(itemStack1 -> itemStack1.getItem() == itemStack.getItem()));
                        }
                        else
                        {
                            inventorySlot = InventoryUtil.findInInventory(
                                    itemStack ->
                                            Arrays.stream(
                                                    stack.getMatchingStacks())
                                                    .anyMatch(itemStack1 -> itemStack1.getItem() == itemStack.getItem()),
                                    false);
                        }
                        if (inventorySlot != -1)
                        {
                            entries.add(new SlotEntry(i, inventorySlot));
                        }
                    }
                    i++;
                }
                else if (recipe instanceof ShapelessRecipes) // TODO: shapeless support
                {

                }
            }
            entryList = entries;
        }

        public IRecipe getRecipe()
        {
            return recipe;
        }

        public List<SlotEntry> getSlotToSlotMap()
        {
            return entryList;
        }

        public boolean isInTable()
        {
            return inTable;
        }

        public int getStep()
        {
            return step;
        }

        public void setStep(int step)
        {
            this.step = step;
        }

        public int getRuns()
        {
            return runs;
        }

        public void setRuns(int runs)
        {
            this.runs = runs;
        }
    }

}
