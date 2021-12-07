package me.earth.earthhack.impl.modules.misc.announcer;

import com.google.common.collect.Lists;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.modules.misc.announcer.util.Announcement;
import me.earth.earthhack.impl.modules.misc.announcer.util.AnnouncementType;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.misc.FileUtil;
import me.earth.earthhack.impl.util.text.ChatUtil;
import net.minecraft.entity.player.EntityPlayer;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Announcer extends Module
{
    /** A random used to get a random Message. */
    private static final Random RANDOM = new Random();

    protected final Setting<Double> delay    =
            register(new NumberSetting<>("Delay", 5.0, 0.0, 60.0));
    protected final Setting<Boolean> distance =
            register(new BooleanSetting("Distance", true));
    protected final Setting<Boolean> mine     =
            register(new BooleanSetting("Mine", true));
    protected final Setting<Boolean> place    =
            register(new BooleanSetting("Place", true));
    protected final Setting<Boolean> eat      =
            register(new BooleanSetting("Eat", true));
    protected final Setting<Boolean> join     =
            register(new BooleanSetting("Join", true));
    protected final Setting<Boolean> leave    =
            register(new BooleanSetting("Leave", true));
    protected final Setting<Boolean> totems   =
            register(new BooleanSetting("Totems", true));
    protected final Setting<Boolean> autoEZ   =
            register(new BooleanSetting("AutoEZ", true));
    protected final Setting<Boolean> miss   =
            register(new BooleanSetting("ArrowMiss", false));
    protected final Setting<Boolean> friends   =
            register(new BooleanSetting("Friends", false));
    protected final Setting<Boolean> antiKick =
            register(new BooleanSetting("AntiKick", false));
    protected final Setting<Boolean> green    =
            register(new BooleanSetting("GreenText", false));
    protected final Setting<Boolean> refresh  =
            register(new BooleanSetting("Refresh", false));
    protected final Setting<Boolean> random   =
            register(new BooleanSetting("Random", false));
    protected final Setting<Double> minDist   =
            register(new NumberSetting<>("MinDistance", 10.0, 1.0, 100.0));

    /** Handles the Announcements. */
    protected final Map<AnnouncementType, Announcement> announcements =
            new ConcurrentHashMap<>();
    /** Contains Messages loaded from the files. */
    protected final Map<AnnouncementType, List<String>> messages =
            new ConcurrentHashMap<>();
    /** Handles previously announced types. */
    protected final Set<AnnouncementType> types = new HashSet<>();
    /** KA and AC targets. */
    protected final Set<EntityPlayer> targets = new HashSet<>();
    /** Timer to handle delay. */
    protected final StopWatch timer = new StopWatch();
    /** Handles distance we travelled. */
    double travelled;
    /** Tracks arrows and their targets to announce misses */
    protected final Map<Integer, EntityPlayer> arrowMap = new ConcurrentHashMap<>();

    public Announcer()
    {
        super("Announcer", Category.Misc);
        this.listeners.add(new ListenerDigging(this));
        this.listeners.add(new ListenerDeath(this));
        this.listeners.add(new ListenerJoin(this));
        this.listeners.add(new ListenerLeave(this));
        this.listeners.add(new ListenerPlace(this));
        this.listeners.add(new ListenerTotems(this));
        this.listeners.add(new ListenerMotion(this));
        this.listeners.add(new ListenerDisconnect(this));
        this.listeners.add(new ListenerWorldClient(this));
        this.listeners.add(new ListenerEat(this));
        this.listeners.add(new ListenerSpawn(this));
        this.setData(new AnnouncerData(this));
    }

    @Override
    protected void onEnable()
    {
        reset();
    }

    @Override
    protected void onLoad()
    {
        loadFiles();
    }

    public void reset()
    {
        travelled = 0.0;
        announcements.clear();
        types.clear();
        targets.clear();
    }

    public void loadFiles()
    {
        reset();
        messages.clear();
        for (AnnouncementType type : AnnouncementType.values())
        {
            List<String> list =
                    FileUtil.readFile(
                                type.getFile(),
                                true,
                                Lists.newArrayList(type.getDefaultMessage()));

            messages.put(type, list);
        }
    }

    String getNextMessage()
    {
        for (Map.Entry<AnnouncementType, Announcement> entry
                : announcements.entrySet())
        {
            if (entry == null
                    || entry.getValue() == null
                    || entry.getKey() == null
                    || entry.getKey() == AnnouncementType.Distance
                    || types.contains(entry.getKey())
                    || !shouldAnnounce(entry.getKey()))
            {
                continue;
            }

            Announcement announcement = entry.getValue();
            types.add(entry.getKey());
            announcements.remove(entry.getKey());
            return convert(entry.getKey(), announcement);
        }

        if (!types.isEmpty())
        {
            types.clear();
            return getNextMessage();
        }

        if (distance.getValue())
        {
            int dist = (int) travelled;
            if (dist > minDist.getValue())
            {
                travelled = 0.0;
                return convert(AnnouncementType.Distance,
                               new Announcement("Block", dist));
            }
        }

        return null;
    }

    Announcement addWordAndIncrement(AnnouncementType type, String word)
    {
        Announcement announcement = announcements.get(type);
        if (announcement != null && announcement.getName().equals(word))
        {
            announcement.setAmount(announcement.getAmount() + 1);
            return announcement;
        }

        announcement = new Announcement(word, 1);
        announcements.put(type, announcement);
        return announcement;
    }

    private String convert(AnnouncementType type, Announcement announcement)
    {
        List<String> list = messages.get(type);
        String text = null;
        if (list != null && !list.isEmpty())
        {
            if (random.getValue())
            {
                text = list.get(RANDOM.nextInt(list.size()));
            }
            else
            {
                text = list.get(0);
            }
        }

        if (text == null)
        {
            text = type.getDefaultMessage();
        }

        return (green.getValue() ? ">" : "")
                + text
                    .replace("<NUMBER>",
                            Integer.toString(announcement.getAmount()))
                    .replace("<NAME>", announcement.getName())
                + (antiKick.getValue()
                    ? " " + ChatUtil.generateRandomHexSuffix(2)
                    : "");
    }

    private boolean shouldAnnounce(AnnouncementType type)
    {
        switch (type)
        {
            case Distance:
                return distance.getValue();
            case Mine:
                return mine.getValue();
            case Place:
                return place.getValue();
            case Eat:
                return eat.getValue();
            case Join:
                return join.getValue();
            case Leave:
                return leave.getValue();
            case Totems:
                return totems.getValue();
            case Death:
                return autoEZ.getValue();
            case Miss:
                return miss.getValue();
        }

        return false;
    }

}
