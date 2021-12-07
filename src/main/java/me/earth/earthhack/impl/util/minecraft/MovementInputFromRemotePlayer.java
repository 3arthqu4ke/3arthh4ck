package me.earth.earthhack.impl.util.minecraft;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovementInput;

public class MovementInputFromRemotePlayer extends MovementInput
{
    private final EntityPlayer player;

    public MovementInputFromRemotePlayer(EntityPlayer player)
    {
        this.player = player;
    }

    public void updatePlayerMoveState()
    {
        MovementInput input = MovementUtil.inverse(player, 0.2783);
        this.moveForward = input.moveForward;
        this.moveStrafe = input.moveStrafe;

        if (moveForward == 0 && moveStrafe == 0)
        {
            this.forwardKeyDown = false;
            this.backKeyDown = false;
            this.leftKeyDown = false;
            this.rightKeyDown = false;
        }

        if (moveForward < 0)
        {
            backKeyDown = true;
        }
        else if (moveForward > 0)
        {
            forwardKeyDown = true;
        }

        if (moveStrafe < 0)
        {
            rightKeyDown = true;
        }
        else if (moveStrafe > 0)
        {
            leftKeyDown = true;
        }

        this.jump = false; // TODO: detect strafing
        this.sneak = input.sneak;
        if (this.sneak)
        {
            this.moveForward *= 0.3d;
            this.moveStrafe *= 0.3d;
        }
    }
}
