package absolutelyaya.ultracraft.mixin.client.render;

import absolutelyaya.ultracraft.components.UltraComponents;
import absolutelyaya.ultracraft.components.entity.ILivingComponent;
import com.mojang.blaze3d.systems.RenderSystem;
import mod.azure.azurelib.core.animatable.GeoAnimatable;
import mod.azure.azurelib.core.object.Color;
import mod.azure.azurelib.renderer.GeoEntityRenderer;
import mod.azure.azurelib.renderer.GeoRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GeoEntityRenderer.class)
public abstract class GeoEntityRendererMixin<T extends Entity & GeoAnimatable> implements GeoRenderer<T>
{
	@Override
	public Color getRenderColor(T animatable, float partialTick, int packedLight)
	{
		if (!(animatable instanceof LivingEntity livingEntity))
			return Color.WHITE;
			
		ILivingComponent living = UltraComponents.LIVING.get(livingEntity);
		if (living.isCancerous())
			return new Color(0xff33ff4d);
		return Color.WHITE;
	}
}
