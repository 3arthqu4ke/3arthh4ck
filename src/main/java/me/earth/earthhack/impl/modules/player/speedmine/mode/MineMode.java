package me.earth.earthhack.impl.modules.player.speedmine.mode;

public enum MineMode
{
    Reset(false),
    Packet(false),
    Smart(false),
    Fast(true),
    Instant(true),
    Civ(true),
    Damage(false);

    /**
     * True if this mode can break the same block multiple times.
     */
    public final boolean isMultiBreaking;

    MineMode(boolean isMultiBreaking) {
        this.isMultiBreaking = isMultiBreaking;
    }

}
