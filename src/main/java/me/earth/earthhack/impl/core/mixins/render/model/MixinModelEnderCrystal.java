package me.earth.earthhack.impl.core.mixins.render.model;

import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.impl.event.events.render.RenderCrystalCubeEvent;
import net.minecraft.client.model.ModelEnderCrystal;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ModelEnderCrystal.class)
public abstract class MixinModelEnderCrystal
{

	@Shadow private ModelRenderer base;

	@Shadow @Final private ModelRenderer glass;

	@Shadow @Final private ModelRenderer cube;

	@Inject(
			method = "render",
			at = @At(
					"HEAD"
			),
			cancellable = true
	)
	public void renderHook(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale, CallbackInfo ci)
	{
		/*ci.cancel();
		GlStateManager.pushMatrix();
		GlStateManager.scale(2.0F, 2.0F, 2.0F);
		GlStateManager.translate(0.0F, -0.5F, 0.0F);

		if (base != null)
		{
			final RenderCrystalCubeEvent preBaseEvent = new RenderCrystalCubeEvent(scale, base, RenderCrystalCubeEvent.Model.BASE, Stage.PRE);
			Bus.EVENT_BUS.post(preBaseEvent);
			if (!preBaseEvent.isCancelled()) base.render(scale);
			final RenderCrystalCubeEvent postBaseEvent = new RenderCrystalCubeEvent(scale, base, RenderCrystalCubeEvent.Model.BASE, Stage.POST);
			Bus.EVENT_BUS.post(postBaseEvent);
		}

		GlStateManager.translate(0.0F, 0.8F + ageInTicks, 0.0F);

		// render cubic center
		GlStateManager.pushMatrix();
		GlStateManager.rotate(limbSwingAmount, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotate(60.0F, 0.7071F, 0.0F, 0.7071F);
		GlStateManager.scale(0.875F, 0.875F, 0.875F);
		GlStateManager.rotate(60.0F, 0.7071F, 0.0F, 0.7071F);
		GlStateManager.rotate(limbSwingAmount, 0.0F, 1.0F, 0.0F);
		GlStateManager.scale(0.875F, 0.875F, 0.875F);
		GlStateManager.rotate(60.0F, 0.7071F, 0.0F, 0.7071F);
		GlStateManager.rotate(limbSwingAmount, 0.0F, 1.0F, 0.0F);
		final RenderCrystalCubeEvent preBaseEvent = new RenderCrystalCubeEvent(scale, cube, RenderCrystalCubeEvent.Model.GLASS_1, Stage.PRE);
		Bus.EVENT_BUS.post(preBaseEvent);
		if (!preBaseEvent.isCancelled()) cube.render(scale);
		final RenderCrystalCubeEvent postBaseEvent = new RenderCrystalCubeEvent(scale, cube, RenderCrystalCubeEvent.Model.GLASS_1, Stage.POST);
		Bus.EVENT_BUS.post(postBaseEvent);
		GlStateManager.popMatrix();

		// render first (smaller) glass "cage"
		GlStateManager.pushMatrix();
		GlStateManager.rotate(limbSwingAmount, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotate(60.0F, 0.7071F, 0.0F, 0.7071F);
		GlStateManager.scale(0.875F, 0.875F, 0.875F);
		GlStateManager.rotate(60.0F, 0.7071F, 0.0F, 0.7071F);
		GlStateManager.rotate(limbSwingAmount, 0.0F, 1.0F, 0.0F);
		final RenderCrystalCubeEvent preGlassTwoEvent = new RenderCrystalCubeEvent(scale, glass, RenderCrystalCubeEvent.Model.GLASS_2, Stage.PRE);
		Bus.EVENT_BUS.post(preGlassTwoEvent);
		if (!preBaseEvent.isCancelled()) glass.render(scale);
		final RenderCrystalCubeEvent postGlassTwoEvent = new RenderCrystalCubeEvent(scale, glass, RenderCrystalCubeEvent.Model.GLASS_2, Stage.POST);
		Bus.EVENT_BUS.post(postGlassTwoEvent);
		GlStateManager.popMatrix();

		// render second glass cage
		GlStateManager.pushMatrix();
		GlStateManager.rotate(limbSwingAmount, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotate(60.0F, 0.7071F, 0.0F, 0.7071F);
		final RenderCrystalCubeEvent preGlassEvent = new RenderCrystalCubeEvent(scale, glass, RenderCrystalCubeEvent.Model.CUBE, Stage.PRE);
		Bus.EVENT_BUS.post(preGlassEvent);
		if (!preBaseEvent.isCancelled()) glass.render(scale);
		final RenderCrystalCubeEvent postGlassEvent = new RenderCrystalCubeEvent(scale, glass, RenderCrystalCubeEvent.Model.CUBE, Stage.POST);
		Bus.EVENT_BUS.post(postGlassEvent);
		GlStateManager.popMatrix();

		GlStateManager.popMatrix();*/
	}

	@Redirect(
			method = "Lnet/minecraft/client/model/ModelEnderCrystal;render(Lnet/minecraft/entity/Entity;FFFFFF)V",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/model/ModelRenderer;render(F)V",
					ordinal = 0
			)
	)
	public void renderBaseHook(ModelRenderer model, float scale)
	{
		final RenderCrystalCubeEvent preBaseEvent = new RenderCrystalCubeEvent(scale, model, RenderCrystalCubeEvent.Model.BASE, Stage.PRE);
		Bus.EVENT_BUS.post(preBaseEvent);
		if (!preBaseEvent.isCancelled()) model.render(preBaseEvent.getScale());
		final RenderCrystalCubeEvent postBaseEvent = new RenderCrystalCubeEvent(scale, model, RenderCrystalCubeEvent.Model.BASE, Stage.POST);
		Bus.EVENT_BUS.post(postBaseEvent);
	}

	@Redirect(
			method = "Lnet/minecraft/client/model/ModelEnderCrystal;render(Lnet/minecraft/entity/Entity;FFFFFF)V",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/model/ModelRenderer;render(F)V",
					ordinal = 1
			)
	)
	public void renderGlassHook(ModelRenderer model, float scale)
	{
		final RenderCrystalCubeEvent preBaseEvent = new RenderCrystalCubeEvent(scale, model, RenderCrystalCubeEvent.Model.GLASS_1, Stage.PRE);
		Bus.EVENT_BUS.post(preBaseEvent);
		if (!preBaseEvent.isCancelled()) model.render(preBaseEvent.getScale());
		final RenderCrystalCubeEvent postBaseEvent = new RenderCrystalCubeEvent(scale, model, RenderCrystalCubeEvent.Model.GLASS_1, Stage.POST);
		Bus.EVENT_BUS.post(postBaseEvent);
	}

	@Redirect(
			method = "Lnet/minecraft/client/model/ModelEnderCrystal;render(Lnet/minecraft/entity/Entity;FFFFFF)V",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/model/ModelRenderer;render(F)V",
					ordinal = 2
			)
	)
	public void renderGlassHook2(ModelRenderer model, float scale)
	{
		final RenderCrystalCubeEvent preBaseEvent = new RenderCrystalCubeEvent(scale, model, RenderCrystalCubeEvent.Model.GLASS_2, Stage.PRE);
		Bus.EVENT_BUS.post(preBaseEvent);
		if (!preBaseEvent.isCancelled()) model.render(preBaseEvent.getScale());
		final RenderCrystalCubeEvent postBaseEvent = new RenderCrystalCubeEvent(scale, model, RenderCrystalCubeEvent.Model.GLASS_2, Stage.POST);
		Bus.EVENT_BUS.post(postBaseEvent);
	}

	@Redirect(
			method = "Lnet/minecraft/client/model/ModelEnderCrystal;render(Lnet/minecraft/entity/Entity;FFFFFF)V",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/model/ModelRenderer;render(F)V",
					ordinal = 3
			)
	)
	public void renderCubeHook(ModelRenderer model, float scale)
	{
		final RenderCrystalCubeEvent preBaseEvent = new RenderCrystalCubeEvent(scale, model, RenderCrystalCubeEvent.Model.CUBE, Stage.PRE);
		Bus.EVENT_BUS.post(preBaseEvent);
		if (!preBaseEvent.isCancelled()) model.render(preBaseEvent.getScale());
		final RenderCrystalCubeEvent postBaseEvent = new RenderCrystalCubeEvent(scale, model, RenderCrystalCubeEvent.Model.CUBE, Stage.POST);
		Bus.EVENT_BUS.post(postBaseEvent);
	}

}
