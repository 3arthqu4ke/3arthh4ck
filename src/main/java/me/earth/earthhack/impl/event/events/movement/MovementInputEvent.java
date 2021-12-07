package me.earth.earthhack.impl.event.events.movement;

import me.earth.earthhack.api.event.events.Event;
import net.minecraft.util.MovementInput;

public class MovementInputEvent extends Event
{
    private final MovementInput input;

    public MovementInputEvent(MovementInput input)
    {
        this.input = input;
    }

    public MovementInput getInput()
    {
        return input;
    }

}
