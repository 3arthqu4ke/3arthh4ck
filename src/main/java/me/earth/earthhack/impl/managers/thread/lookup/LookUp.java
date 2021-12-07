package me.earth.earthhack.impl.managers.thread.lookup;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

/**
 * Callback for a LoopUp of name, uuid or name history.
 */
public abstract class LookUp
{
    protected String name;
    protected UUID uuid;
    protected Map<Date, String> names;
    protected Type type;

    public LookUp(Type type, String name)
    {
        this.type = type;
        this.name = name;
    }

    public LookUp(Type type, UUID uuid)
    {
        this.type = type;
        this.uuid = uuid;
    }

    /**
     * Might be called asynchronously
     */
    public abstract void onSuccess();

    /**
     * Will almost always be called asynchronously.
     */
    public abstract void onFailure();

    /**
     * The Type of LookUp.
     */
    public enum Type
    {
        NAME,
        UUID,
        HISTORY
    }

}
