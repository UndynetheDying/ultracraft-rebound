package absolutelyaya.ultracraft.mixin.client.render;

import absolutelyaya.ultracraft.client.rendering.entity.feature.EnragedFeature;
import absolutelyaya.ultracraft.components.UltraComponents;
import absolutelyaya.ultracraft.components.entity.ILivingComponent;
import mod.azure.azurelib.cache.object.BakedGeoModel;
import mod.azure.azurelib.core.animatable.GeoAnimatable;
import mod.azure.azurelib.core.object.Color;
import mod.azure.azurelib.model.GeoModel;
import mod.azure.azurelib.renderer.GeoEntityRenderer;
import mod.azure.azurelib.renderer.GeoRenderer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GeoEntityRenderer.class)
public abstract class GeoEntityRendererMixin<T extends Entity & GeoAnimatable> implements GeoRenderer<T>
{
	@Shadow public abstract T getAnimatable();
	
	@Unique
	EnragedFeature<LivingEntity> rage;
	
	@Inject(method = "<init>", at = @At("TAIL"))
	private void onInit(EntityRendererFactory.Context renderManager, GeoModel<T> model, CallbackInfo ci)
	{
		rage = new EnragedFeature<>(renderManager.getModelLoader());
	}
	
	@Override
	public Color getRenderColor(T animatable, float partialTick, int packedLight)
	{
		if (!(animatable instanceof LivingEntity livingEntity))
			return Color.WHITE;
			
		ILivingComponent living = UltraComponents.LIVING.get(livingEntity);
		if (living.isEnraged())
			return new Color(0xffff4444);
		if (living.isCancerous())
			return new Color(0xff33ff4d);
		return Color.WHITE;
	}
	
	@Inject(method = "applyRenderLayers(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/entity/Entity;Lmod/azure/azurelib/cache/object/BakedGeoModel;Lnet/minecraft/client/render/RenderLayer;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/client/render/VertexConsumer;FII)V", at = @At(value = "TAIL"))
	void afterRenderLayers(MatrixStack matrices, T animatable, BakedGeoModel model, RenderLayer renderType, VertexConsumerProvider bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay, CallbackInfo ci)
	{
		if(animatable instanceof LivingEntity entity && !entity.isSpectator() && rage != null)
			rage.render(matrices, bufferSource, packedLight, entity, entity.limbAnimator.getPos(), entity.limbAnimator.getSpeed(), partialTick, 0f, entity.headYaw, entity.getPitch());
	}
}
