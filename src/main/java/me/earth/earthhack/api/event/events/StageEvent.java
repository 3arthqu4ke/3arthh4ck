package me.earth.earthhack.api.event.events;

/**
 * An Event that has different {@link Stage}s.
 * <p>
 * Having 2 Events should be better if you listen
 * to only one of the stages sometimes, but otherwise
 * this Event is convenient.
 */
public class StageEvent extends Event
{
    private final Stage stage;

    public StageEvent(Stage stage)
    {
        this.stage = stage;
    }

    public Stage getStage()
    {
        return stage;
    }

}
