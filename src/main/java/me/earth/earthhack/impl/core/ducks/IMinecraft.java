package me.earth.earthhack.impl.core.ducks;

import net.minecraft.client.resources.data.MetadataSerializer;
import net.minecraft.util.Timer;

/**
 * Duck interface for {@link net.minecraft.client.Minecraft}.
 */
public interface IMinecraft
{
    /**
     * Accessor for mc.rightClickDelayTimer.
     *
     * @return mc.rightClickDelayTimer.
     */
    int getRightClickDelay();

    /**
     * Accessor for mc.rightClickDelayTimer.
     *
     * @param delay the value to set the timer to.
     */
    void setRightClickDelay(int delay);

    /**
     * Accesses Minecraft's timer.
     *
     * @return minecraft's timer.
     */
    Timer getTimer();

    /**
     * Allows you to invoke
     * -rightClickMouse,
     * -clickMouse,
     * -middleClickMouse
     * based on the given ClickType.
     *
     * @param type the type of click.
     */
    void click(Click type);

    /** @return the current gameloop, will be incremented every gameloop. */
    int getGameLoop();

    /** @return <tt>true</tt> if 3arthh4ck is running. */
    boolean isEarthhackRunning();

    /** Polls all scheduled tasks and runs them. */
    void runScheduledTasks();

    /** @return Minecrafts MetadataSerializer. */
    MetadataSerializer getMetadataSerializer();

    /** Mouse Button to click with. */
    enum Click
    {
        RIGHT,
        LEFT,
        MIDDLE
    }

}
