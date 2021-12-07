package me.earth.earthhack.impl.modules.render.search;

import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.managers.thread.scheduler.Scheduler;
import me.earth.earthhack.impl.util.helpers.addable.ListType;
import me.earth.earthhack.impl.util.helpers.addable.RemovingItemAddingModule;
import me.earth.earthhack.impl.util.helpers.addable.setting.SimpleRemovingSetting;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.misc.intintmap.IntIntMap;
import me.earth.earthhack.impl.util.misc.intintmap.IntIntMapImpl;
import me.earth.earthhack.impl.util.render.ColorUtil;
import me.earth.earthhack.impl.util.render.WorldRenderUtil;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Search extends RemovingItemAddingModule
{
    protected final Setting<Boolean> lines      =
            register(new BooleanSetting("Lines", true));
    protected final Setting<Boolean> fill       =
            register(new BooleanSetting("Fill", false));
    protected final Setting<Boolean> tracers    =
            register(new BooleanSetting("Tracers", false));
    protected final Setting<Boolean> softReload =
            register(new BooleanSetting("SoftReload", false));
    protected final Setting<Integer> maxBlocks =
            register(new NumberSetting<>("Max-Blocks", 10000, 0, 10000));
    protected final Setting<Double> range =
            register(new NumberSetting<>("Range", 256.0, 0.0, 512.0));
    protected final Setting<Boolean> countInRange =
            register(new BooleanSetting("Count-Range", false));
    protected final Setting<Boolean> coloredTracers =
            register(new BooleanSetting("Colored-Tracers", false));
    protected final Setting<Boolean> noUnloaded =
            register(new BooleanSetting("NoUnloaded", false));
    protected final Setting<Boolean> remove =
            register(new BooleanSetting("Remove", false));

    protected final Map<BlockPos, SearchResult> toRender = new ConcurrentHashMap<>();
    protected final IntIntMap colors = new IntIntMapImpl(35, 0.75f);
    protected final StopWatch timer = new StopWatch();

    protected int found;

    /** Constructs a new Search Module. */
    public Search()
    {
        super("Search",
                Category.Render,
                s -> "Searches for " + s.getName() + ".");
        super.listType.setValue(ListType.WhiteList);
        super.listType.addObserver(e -> e.setValue(ListType.WhiteList));
        unregister(listType);
        this.listeners.add(new ListenerBlockRender(this));
        this.listeners.add(new ListenerRender(this));
        this.listeners.add(new ListenerWorldClient(this));
        this.listeners.add(new ListenerUnloadChunk(this));
        this.listeners.add(new ListenerBlockChange(this));
        this.listeners.add(new ListenerMultiBlockChange(this));

        colors.put(5,   0xa58a2a55); // Wooden Planks
        colors.put(10,  0xff9307ff); // Lava
        colors.put(11,  0xff930755); // Lava
        colors.put(14,  0x90900055); // Gold Ore
        colors.put(15,  0x80705055); // Iron Ore
        colors.put(16,  0x20202055); // Coal Ore
        colors.put(17,  0xa58a2a55); // Wood
        colors.put(21,  0x306055);   // Lapis Lazuli Ore
        colors.put(26,  0xff000055); // Bed
        colors.put(41,  0x90900055); // Block of Gold
        colors.put(42,  0x80705055); // Block of Iron
        colors.put(49,  0x6521d122); // Obsidian
        colors.put(52,  0x7ad955);   // Monster Spawner
        colors.put(56,  0x90aa55);   // Diamond Ore
        colors.put(57,  0x90aa55);   // Block of Diamond
        colors.put(73,  0x60000055); // Redstone Ore
        colors.put(74,  0x60000055); // Redstone Ore
        colors.put(90,  0x6521d144); // Portal
        colors.put(98,  0x90aa55);   // Stone Bricks
        colors.put(112, 0xff431e);   // Nether Brick
        colors.put(129, 0x802055);   // Emerald Ore
        colors.put(162, 0xa58a2a55); // Wood
        colors.put(354, 0x90aa55);   // Air

        this.setData(new SearchData(this));
    }

    @Override
    protected void onEnable()
    {
        toRender.clear();
        // We need to schedule this or we aren't subscribed when this happens
        Scheduler.getInstance().schedule(this::reloadRenders);
    }

    @Override
    public String getDisplayInfo()
    {
        return found + "";
    }

    @Override
    protected SimpleRemovingSetting addSetting(String string)
    {
        SimpleRemovingSetting s = super.addSetting(string);
        if (s != null)
        {
            reloadRenders();
        }

        return s;
    }

    @Override
    public Setting<?> unregister(Setting<?> setting)
    {
        Setting<?> s = super.unregister(setting);
        if (s != null)
        {
            toRender.clear();
            reloadRenders();
        }

        return s;
    }

    public void reloadRenders()
    {
        if (mc.world != null && mc.renderGlobal != null && mc.player != null)
        {
            WorldRenderUtil.reload(softReload.getValue());
        }
    }

    /**
     * @param state the state to get a color for.
     * @return a color for the state.
     */
    public int getColor(IBlockState state)
    {
        int id = Block.getIdFromBlock(state.getBlock());
        int color = colors.get(id);
        if (color != 0)
        {
            return color;
        }

        int blue = state.getMaterial().getMaterialMapColor().colorValue;
        int red = blue >> 16 & 255;
        int green = blue >> 8 & 255;
        blue &= 255;

        return ColorUtil.toARGB(red, green, blue, 100);
    }

}
