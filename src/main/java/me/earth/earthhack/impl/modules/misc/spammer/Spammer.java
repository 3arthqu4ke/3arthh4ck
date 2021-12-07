package me.earth.earthhack.impl.modules.misc.spammer;

import com.google.common.collect.Lists;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.misc.FileUtil;
import me.earth.earthhack.impl.util.text.ChatUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Spammer extends Module
{
    private static final String FILE = "earthhack/util/Spammer.txt";
    private static final String DEFAULT = "Good Fight!";
    private static final Random RND = new Random();

    protected final Setting<Integer> delay     =
            register(new NumberSetting<>("Delay", 5, 1, 60));
    protected final Setting<Boolean> random    =
            register(new BooleanSetting("Random", false));
    protected final Setting<Boolean> antiKick  =
            register(new BooleanSetting("AntiKick", false));
    protected final Setting<Boolean> greenText =
            register(new BooleanSetting("GreenText", false));
    protected final Setting<Boolean> refresh   =
            register(new BooleanSetting("Refresh", false));
    protected final Setting<Boolean> autoOff   =
            register(new BooleanSetting("AutoOff", false));

    protected final List<String> messages = new ArrayList<>();
    protected final StopWatch timer       = new StopWatch();
    protected int currentIndex = 0;

    public Spammer()
    {
        super("Spammer", Category.Misc);
        this.listeners.add(new ListenerUpdate(this));
        this.refresh.addObserver(event ->
        {
            ChatUtil.sendMessage(
                    "<"
                    + this.getDisplayName()
                    + "> Reloading File...");
            loadFile();
            currentIndex = 0;
            event.setCancelled(true);
        });
        this.setData(new SpammerData(this));
    }

    @Override
    protected void onLoad()
    {
        loadFile();
    }

    protected void onEnable()
    {
        currentIndex = 0;
    }

    private void loadFile()
    {
        messages.clear();

        for (String string :
                FileUtil.readFile(FILE, true,
                        Lists.newArrayList("Good Fight!")))
        {
            if (!string.replace("\\s", "").isEmpty())
            {
                messages.add(string);
            }
        }
    }

    protected String getSuffixedMessage()
    {
        return getMessage() + getSuffix();
    }

    protected String getSuffix()
    {
        if (antiKick.getValue())
        {
            return ChatUtil.generateRandomHexSuffix(2);
        }

        return "";
    }

    private String getMessage()
    {
        if (messages.isEmpty())
        {
            return DEFAULT;
        }

        String result;
        if (random.getValue())
        {
            result = messages.get(RND.nextInt(messages.size()));
            if (result != null)
            {
                return result;
            }
        }

        result = messages.get(currentIndex);
        currentIndex++;
        if (currentIndex >= messages.size()) currentIndex = 0;
        if (result != null)
        {
            return result;
        }

        return DEFAULT;
    }

}
