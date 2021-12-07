package me.earth.earthhack.impl.modules.render.rechams;

import me.earth.earthhack.impl.event.events.render.RenderArmorEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.render.rechams.mode.ChamsMode;

public class ListenerRenderArmor extends ModuleListener<Chams, RenderArmorEvent>
{

	public ListenerRenderArmor(Chams module)
	{
		super(module, RenderArmorEvent.class);
	}

	@Override
	public void invoke(RenderArmorEvent event)
	{
		module.getModeFromEntity(event.getEntity()).renderArmor(event, module);
	}

}
