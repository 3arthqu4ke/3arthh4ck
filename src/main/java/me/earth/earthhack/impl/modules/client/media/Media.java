package me.earth.earthhack.impl.modules.client.media;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.StringSetting;
import me.earth.earthhack.api.util.TextUtil;
import me.earth.earthhack.impl.commands.util.CommandUtil;
import me.earth.earthhack.impl.core.mixins.gui.MixinGuiNewChat;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.accountspoof.AccountSpoof;
import me.earth.earthhack.impl.modules.client.autoconfig.RemovingString;
import me.earth.earthhack.impl.modules.client.pingbypass.PingBypassModule;
import me.earth.earthhack.impl.util.helpers.addable.RegisteringModule;
import me.earth.earthhack.impl.util.text.ChatUtil;
import me.earth.earthhack.impl.util.text.TextColor;
import me.earth.earthhack.impl.util.thread.LookUpUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * Allows you to protect alt names in chat.
 * {@link MixinGuiNewChat}.
 */
public class Media extends RegisteringModule<String, RemovingString>
{
    protected static final ModuleCache<PingBypassModule> PING_BYPASS =
            Caches.getModule(PingBypassModule.class);
    protected static final ModuleCache<AccountSpoof> ACCOUNT_SPOOF =
        Caches.getModule(AccountSpoof.class);

    protected final Setting<String> replacement =
            register(new StringSetting("Replacement", "3arthqu4ke"));
    protected final Setting<Boolean> replaceCustom =
            register(new BooleanSetting("Custom", false));
    protected final Setting<Boolean> dontShowInCommands =
            register(new BooleanSetting("DontCompleteCommands", true));

    /** Cache for already matched strings. */
    protected final Map<String, String> cache =
            new ConcurrentHashMap<>();
    /** Custom Replacements. */
    protected final Map<Setting<String>, Pattern> custom =
            new ConcurrentHashMap<>();

    /** The current Pattern. */
    protected Pattern pattern;
    /** Pattern for the PingBypass. */
    protected Pattern pingBypass;
    /** If PingBypass is enabled. */
    protected boolean pingBypassEnabled;
    /** If a Name has been requested from PingBypass. */
    protected boolean send;

    /** Constructs a new Media Module. */
    public Media()
    {
        super("Media", Category.Client,
                "Add_Media",
                "name> <replace",
                s -> new RemovingString(s, s),
                s -> "Replaces on " + s.getName() + ".");

        this.listeners.add(new ListenerClearChat(this));
        this.listeners.add(new ListenerTick(this));

        this.pattern = compile(mc.getSession().getUsername());
        this.replacement.addObserver(event -> cache.clear());
        this.replaceCustom.addObserver(event -> cache.clear());
        register(new BooleanSetting("Reload", false))
            .addObserver(event -> reload());
        this.setData(new MediaData(this));
    }

    @Override
    public void onEnable()
    {
        pingBypassEnabled = false;
        send = false;
    }

    @Override
    public void add(String[] args)
    {
        if (args.length < 4)
        {
            ChatUtil.sendMessage(TextColor.RED
                    + "Please specify a Replacement!");
            return;
        }

        RemovingString setting = addSetting(args[2]);
        if (setting == null)
        {
            ChatUtil.sendMessage(TextColor.RED
                    + "A Replacement for "
                    + TextColor.WHITE
                    + args[2]
                    + TextColor.RED
                    + " already exists!");
            return;
        }

        setting.fromString(CommandUtil.concatenate(args, 3));
    }

    @Override
    protected RemovingString addSetting(String string)
    {
        RemovingString setting = super.addSetting(string);
        if (setting != null)
        {
            custom.put(setting, compile(setting.getName()));
        }

        return setting;
    }

    @Override
    public Setting<?> unregister(Setting<?> setting)
    {
        Setting<?> s = super.unregister(setting);
        if (s != null)
        {
            custom.remove(s);
        }

        return s;
    }

    @Override
    public void del(String string)
    {
        Setting<?> setting = this.getSetting(string);
        if (setting != null)
        {
            custom.remove(setting);
        }

        super.del(string);
        cache.clear();
    }

    @Override
    public String getInput(String input, boolean add)
    {
        if (!add)
        {
            return super.getInput(input, false);
        }

        String player = LookUpUtil.findNextPlayerName(input);
        if (player != null)
        {
            return TextUtil.substring(player, input.length());
        }

        return "";
    }

    @Override
    protected String formatString(String string)
    {
        return string;
    }

    public boolean isHidingInCommands(String name)
    {
        return isEnabled()
            && dontShowInCommands.getValue()
            && (custom.keySet().stream().anyMatch(
                s -> s.getName().equalsIgnoreCase(name))
            || pattern.matcher(name).matches()
            || pingBypass != null && pingBypass.matcher(name).matches());
    }

    public void reload()
    {
        cache.clear();
        // TODO: reload media properly -> management thingy
        this.pattern = compile(mc.getSession().getUsername());
    }

    /**
     * Matches the text for the Pattern and
     * replaces it with the Replacement Setting.
     *
     * @param text the text to match.
     * @return the text with the username replaced..
     */
    public String convert(String text)
    {
        if (!this.isEnabled() || text == null || pattern == null)
        {
            return text;
        }

        return cache.computeIfAbsent(text, v ->
        {
            String toAdd = text;
            if (replaceCustom.getValue())
            {
                for (Map.Entry<Setting<String>, Pattern> entry
                        : custom.entrySet())
                {
                    if (getSetting(entry.getKey().getName()) != null && getSettings().contains(entry.getKey()))
                    {
                        toAdd = entry.getValue()
                                     .matcher(toAdd)
                                     .replaceAll(entry.getKey().getValue());
                    }
                }
            }

            if (PING_BYPASS.isEnabled() && pingBypass != null)
            {
                toAdd = pingBypass.matcher(toAdd)
                                  .replaceAll(replacement.getValue());
            }

            if (ACCOUNT_SPOOF.isEnabled())
            {
                toAdd = ACCOUNT_SPOOF.get().pattern
                    .matcher(toAdd)
                    .replaceAll(replacement.getValue());
            }

            return pattern.matcher(toAdd).replaceAll(replacement.getValue());
        });
    }

    private Pattern compile(String name)
    {
        cache.clear();
        return compileWithColorCodes(name);
    }

    /**
     * Creates a regex that matches the name but with all possible
     * colorcodes between the letters. This is important for some
     * chat messages and servers with multiColored names.
     *
     * @param name the name to get a regex from
     * @return a pattern for the name.
     */
    public static Pattern compileWithColorCodes(String name)
    {
        if (name == null)
        {
            return null;
        }

        StringBuilder regex = new StringBuilder("(?<!")
                                            .append(TextColor.SECTIONSIGN)
                                            .append(")(");
        char[] array = name.toCharArray();
        for (int i = 0; i < array.length; i++)
        {
            char c = array[i];
            regex.append(c);
            if (i != array.length - 1)
            {
                for (TextColor textColor : TextColor.values())
                {
                    if (textColor == TextColor.None)
                    {
                        continue;
                    }

                    String color = textColor.getColor();
                    regex.append("[").append(color).append("]").append("*");
                }
            }
        }

        return Pattern.compile(regex.append(")").toString());
    }

    public void setPingBypassName(String name)
    {
        pingBypass = compile(name);
    }

}
