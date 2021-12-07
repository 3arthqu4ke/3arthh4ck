package me.earth.earthhack.impl.modules.combat.autocrystal.util;

public class TimeStamp
{
    private final long timeStamp;
    private boolean valid = true;

    public TimeStamp()
    {
        this.timeStamp = System.currentTimeMillis();
    }

    public long getTimeStamp()
    {
        return timeStamp;
    }

    public boolean isValid()
    {
        return valid;
    }

    public void setValid(boolean valid)
    {
        this.valid = valid;
    }

}
