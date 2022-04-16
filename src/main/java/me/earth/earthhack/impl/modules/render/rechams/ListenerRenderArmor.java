package me.earth.earthhack.impl.modules.render.rechams;

import me.earth.earthhack.impl.event.events.render.RenderArmorEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;

public class ListenerRenderArmor extends ModuleListener<ReChams, RenderArmorEvent>
{

	public ListenerRenderArmor(ReChams module)
	{
		super(module, RenderArmorEvent.class);
	}

	@Override
	public void invoke(RenderArmorEvent event)
	{
		module.getModeFromEntity(event.getEntity()).renderArmor(event, module);
	}

}
