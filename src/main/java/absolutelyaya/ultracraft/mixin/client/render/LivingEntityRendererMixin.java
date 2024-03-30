package absolutelyaya.ultracraft.mixin.client.render;

import absolutelyaya.ultracraft.components.UltraComponents;
import absolutelyaya.ultracraft.components.entity.ILivingComponent;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin<T extends LivingEntity>
{
	@ModifyArgs(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/EntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;IIFFFF)V"))
	void modifyRenderColor(Args args, T livingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i)
	{
		//another thing making specifically GeoEntities green is in GeoEntityRendererMixin
		ILivingComponent living = UltraComponents.LIVING.get(livingEntity);
		if(living.isCancerous())
		{
			args.set(4, 0.2f);
			args.set(5, 1f);
			args.set(6, 0.3f);
		}
	}
}
