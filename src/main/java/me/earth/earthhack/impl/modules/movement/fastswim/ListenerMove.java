package me.earth.earthhack.impl.modules.movement.fastswim;

import me.earth.earthhack.impl.event.events.movement.MoveEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.minecraft.MovementUtil;
import net.minecraft.block.material.Material;

final class ListenerMove extends ModuleListener<FastSwim, MoveEvent>
{
    public ListenerMove(FastSwim module)
    {
        super(module, MoveEvent.class);
    }

    @Override
    public void invoke(MoveEvent event)
    {
        if (module.strafe.getValue())
        {
            if (!module.accelerate.getValue() && Managers.NCP.passed(250))
            {
                if (!mc.player.onGround)
                {
                    if (mc.player.isInsideOfMaterial(Material.LAVA))
                    {
                        MovementUtil.strafe(event, module.hLava.getValue());
                        if (!module.fall.getValue())
                        {
                            if (mc.gameSettings.keyBindSneak.isKeyDown())
                            {
                                event.setY(-module.downLava.getValue());
                            }
                            else if (mc.gameSettings.keyBindJump.isKeyDown())
                            {
                                event.setY(module.hLava.getValue());
                            }
                            else
                            {
                                event.setY(0);
                            }
                        }
                    }
                    else if (mc.player.isInsideOfMaterial(Material.WATER))
                    {
                        MovementUtil.strafe(event, module.hLava.getValue());
                        if (!module.fall.getValue())
                        {
                            if (mc.gameSettings.keyBindSneak.isKeyDown())
                            {
                                event.setY(-module.downLava.getValue());
                            }
                            else if (mc.gameSettings.keyBindJump.isKeyDown())
                            {
                                event.setY(module.hLava.getValue());
                            }
                            else
                            {
                                event.setY(0);
                            }
                        }
                    }
                }
            }
            else if (module.accelerate.getValue())
            {
                if (!mc.player.onGround)
                {
                    if (Managers.NCP.passed(250))
                    {
                        if (mc.player.isInsideOfMaterial(Material.LAVA))
                        {
                            module.waterSpeed *= module.accelerateFactor.getValue();
                        }
                        else if (mc.player.isInsideOfMaterial(Material.WATER))
                        {
                            module.lavaSpeed *= module.accelerateFactor.getValue();
                        }
                    }
                    if (mc.player.isInsideOfMaterial(Material.LAVA))
                    {
                        MovementUtil.strafe(event, module.lavaSpeed);
                        if (!module.fall.getValue())
                        {
                            if (mc.gameSettings.keyBindSneak.isKeyDown())
                            {
                                event.setY(-module.downLava.getValue());
                            }
                            else if (mc.gameSettings.keyBindJump.isKeyDown())
                            {
                                event.setY(module.hLava.getValue());
                            }
                            else
                            {
                                event.setY(0);
                            }
                        }
                    }
                    else if (mc.player.isInsideOfMaterial(Material.WATER))
                    {
                        MovementUtil.strafe(event, module.waterSpeed);
                        if (!module.fall.getValue())
                        {
                            if (mc.gameSettings.keyBindSneak.isKeyDown())
                            {
                                event.setY(-module.downLava.getValue());
                            }
                            else if (mc.gameSettings.keyBindJump.isKeyDown())
                            {
                                event.setY(module.hLava.getValue());
                            }
                            else
                            {
                                event.setY(0);
                            }
                        }
                    }
                }
                else
                {
                    module.waterSpeed = module.hWater.getValue();
                    module.lavaSpeed = module.hLava.getValue();
                }
            }
        }
        else
        {
            if (Managers.NCP.passed(250) && !mc.player.onGround)
            {
                if (mc.player.isInsideOfMaterial(Material.LAVA))
                {
                    event.setX(event.getX() * module.hLava.getValue());
                    event.setY(event.getY() * module.vLava.getValue());
                    event.setZ(event.getZ() * module.hLava.getValue());
                }
                else if (mc.player.isInsideOfMaterial(Material.WATER))
                {
                    event.setX(event.getX() * module.hWater.getValue());
                    event.setY(event.getY() * module.vWater.getValue());
                    event.setZ(event.getZ() * module.hWater.getValue());
                }
            }
        }
    }

}
