package me.earth.earthhack.impl.core.mixins.render.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderEnderCrystal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RenderEnderCrystal.class)
public interface IRenderEnderCrystal
{

	@Accessor("modelEnderCrystal")
	ModelBase getModelEnderCrystal();

	@Accessor("modelEnderCrystalNoBase")
	ModelBase getModelEnderCrystalNoBase();

}
