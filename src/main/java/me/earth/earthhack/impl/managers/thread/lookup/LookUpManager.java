package me.earth.earthhack.impl.managers.thread.lookup;

import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.Earthhack;
import me.earth.earthhack.impl.util.thread.LookUpUtil;
import me.earth.earthhack.impl.util.thread.ThreadUtil;
import me.earth.earthhack.tweaker.launch.Argument;
import me.earth.earthhack.tweaker.launch.DevArguments;

import java.util.UUID;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * A Manager that can LookUp, UUID, Name and NameHistory
 * for a given UUID/Name.
 */
public class LookUpManager implements Globals
{
    /** The Cooldown between internet connections */
    private static final long CONNECTION_COOLDOWN;

    static
    {
        Argument<Long> a = DevArguments.getInstance().getArgument("connection");
        Earthhack.getLogger().info("Connection Timeout: " + a.getValue());
        CONNECTION_COOLDOWN = a.getValue();
    }

    private volatile ScheduledExecutorService service;
    private final AtomicLong last = new AtomicLong();

    /**
     * Attempts to fill in the missing parts
     * of the given LookUp based on its type.
     * If no data can be found an internet connection
     * will be made from the lookUp thread.
     *
     * If you want to lookUp a Name from a uuid create a new LookUp
     * for the Parameters
     * {@link LookUp.Type#NAME}
     * and the uuid.
     *
     * If you want to lookUp a UUID from a name create a new LookUp
     * for the Parameters
     * {@link LookUp.Type#UUID}
     * and the uuid.
     *
     * If you want to lookUp the NameHistory, create a new LookUp
     * for the Parameters
     * {@link LookUp.Type#HISTORY}
     * and the players name.
     *
     * @param lookUp the lookUp to fill in.
     * @return a future representing an internet lookup, or null
     *         if no internet connection was needed.
     */
    public Future<?> doLookUp(LookUp lookUp)
    {
        switch (lookUp.type)
        {
            case NAME:
                if (lookUp.uuid == null)
                {
                    lookUp.onFailure();
                    break;
                }

                String name = LookUpUtil.getNameSimple(lookUp.uuid);
                if (name != null)
                {
                    lookUp.name = name;
                    lookUp.onSuccess();
                    break;
                }

                return scheduleLookUp(lookUp);
            case UUID:
                if (lookUp.name == null)
                {
                    lookUp.onFailure();
                    break;
                }

                UUID uuid = LookUpUtil.getUUIDSimple(lookUp.name);
                if (uuid != null)
                {
                    lookUp.uuid = uuid;
                    lookUp.onSuccess();
                    break;
                }

                return scheduleLookUp(lookUp);
            case HISTORY:
                if (lookUp.name == null)
                {
                    lookUp.onFailure();
                    break;
                }

                UUID id = LookUpUtil.getUUIDSimple(lookUp.name);
                if (id != null)
                {
                    lookUp.uuid = id;
                }

                return scheduleLookUp(lookUp);
            default:
        }

        return null;
    }

    /**
     * If the name/uuid, isn't cached or
     * easily gettable, we need to do an internet lookup.
     *
     * @param lookUp the lookup.
     */
    private void doBigLookUp(LookUp lookUp)
    {
        switch (lookUp.type)
        {
            case NAME:
                String name = LookUpUtil.getName(lookUp.uuid);
                if (name != null)
                {
                    lookUp.name = name;
                    lookUp.onSuccess();
                }
                else
                {
                    lookUp.onFailure();
                }
                break;
            case UUID:
                UUID uuid = LookUpUtil.getUUID(lookUp.name);
                if (uuid != null)
                {
                    lookUp.uuid = uuid;
                    lookUp.onSuccess();
                }
                else
                {
                    lookUp.onFailure();
                }
                break;
            case HISTORY:
                UUID id = lookUp.uuid;
                if (id == null)
                {
                    id = LookUpUtil.getUUID(lookUp.name);
                }

                if (id != null) //double lookup idk
                {
                    lookUp.names = LookUpUtil.getNameHistory(id);
                    lookUp.onSuccess();
                }
                else
                {
                    lookUp.onFailure();
                }
                break;
            default:
        }
    }


    /**
     * Adds a new lookUp to the queue and
     * notifies the lookUpThread.
     *
     * @param lookUp the lookUp to lookUp.
     */
    private Future<?> scheduleLookUp(LookUp lookUp)
    {
        if (service == null)
        {
            synchronized (this)
            {
                if (service == null)
                {
                    service = ThreadUtil.newDaemonScheduledExecutor("LookUp");
                }
            }
        }

        long t = Math.max(0, CONNECTION_COOLDOWN
                - System.currentTimeMillis()
                + last.getAndSet(System.currentTimeMillis()));

        return service
                .schedule(() -> doBigLookUp(lookUp), t, TimeUnit.MILLISECONDS);
    }

}
