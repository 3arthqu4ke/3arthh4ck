package me.earth.earthhack.impl.event.events.misc;

public class UpdateEvent
{
    private final boolean isPingBypass;

    public UpdateEvent(boolean isPingBypass) {
        this.isPingBypass = isPingBypass;
    }

    public UpdateEvent() {
        this(false);
    }

    public boolean isPingBypass() {
        return isPingBypass;
    }

}
