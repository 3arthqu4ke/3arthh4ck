package me.earth.earthhack.api.command;

import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.util.EnumHelper;

public class Completer
{
    private static String lastCompleted;

    private final String initial;
    private final String[] args;

    private String result;

    private boolean mc_complete;

    public Completer(String initial, String[] args)
    {
        this.initial = initial;
        this.args = args;
    }

    public Completer setResult(String result)
    {
        lastCompleted = result;
        this.result = result;
        return this;
    }

    public String getResult()
    {
        return result == null ? initial : result;
    }

    public Completer setMcComplete(boolean complete)
    {
        this.mc_complete = complete;
        return this;
    }

    public boolean shouldMcComplete()
    {
        return mc_complete;
    }

    public String[] getArgs()
    {
        return args;
    }

    public String getInitial()
    {
        return initial;
    }

    public static String nextValueInSetting(Setting<?> settingIn,
                                            String previous)
    {
        if (settingIn instanceof EnumSetting)
        {
            Enum<?> next = EnumHelper.next(
                    EnumHelper.fromString((Enum<?>) settingIn.getInitial(),
                            previous));
            return next.name();
        }
        else if (settingIn instanceof BooleanSetting)
        {
            return String.valueOf(!Boolean.parseBoolean(previous));
        }

        return previous;
    }

    public String getLastCompleted()
    {
        return lastCompleted;
    }

    public String getLast()
    {
        return args[args.length - 1];
    }

    public void setLastCompleted(String lastCompletedIn)
    {
        lastCompleted = lastCompletedIn;
    }

    public boolean isSame()
    {
        return initial.equalsIgnoreCase(lastCompleted);
    }

}
