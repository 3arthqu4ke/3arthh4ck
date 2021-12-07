package me.earth.earthhack.impl.modules.render.rechams;

import me.earth.earthhack.impl.event.events.render.RenderCrystalCubeEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;

public class ListenerRenderCrystalCube extends ModuleListener<Chams, RenderCrystalCubeEvent>
{

	public ListenerRenderCrystalCube(Chams module)
	{
		super(module, RenderCrystalCubeEvent.class);
	}

	@Override
	public void invoke(RenderCrystalCubeEvent event)
	{
		module.crystalMode.getValue().renderCrystalCube(event, module); // if this is somehow needed in another mode, this needs to change!
	}

}
