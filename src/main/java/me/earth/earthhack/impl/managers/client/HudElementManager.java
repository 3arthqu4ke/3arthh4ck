package me.earth.earthhack.impl.managers.client;

import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.api.hud.HudElement;
import me.earth.earthhack.api.register.IterationRegister;
import me.earth.earthhack.api.register.Registrable;
import me.earth.earthhack.api.register.exception.CantUnregisterException;
import me.earth.earthhack.impl.Earthhack;
import me.earth.earthhack.impl.hud.watermark.Watermark;
import me.earth.earthhack.impl.hud.welcomer.Welcomer;

import java.util.Comparator;

public class HudElementManager extends IterationRegister<HudElement> {

    private int currentZ = 0;

    public void init()
    {
        Earthhack.getLogger().info("Initializing Hud Elements.");
        this.forceRegister(new Watermark());
        this.forceRegister(new Welcomer());
    }

    public void load()
    {
        for (HudElement element : getRegistered())
        {
            Earthhack.getLogger().info(element.getName());
            element.load();
        }
        registered.sort(Comparator.comparing(HudElement::getZ));
    }

    @Override
    public void unregister(HudElement element) throws CantUnregisterException
    {
        super.unregister(element);
        Bus.EVENT_BUS.unsubscribe(element);
    }

    private void forceRegister(HudElement element)
    {
        registered.add(element);
        if (element instanceof Registrable)
        {
            ((Registrable) element).onRegister();
        }
        element.setZ(currentZ);
        currentZ++;
    }

}
