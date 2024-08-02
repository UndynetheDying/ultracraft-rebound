package absolutelyaya.ultracraft.mixin.client.render;

import absolutelyaya.ultracraft.client.UltracraftClient;
import absolutelyaya.ultracraft.item.BlahajItem;
import absolutelyaya.ultracraft.item.SwordsmachinePlushieItem;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.ModelWithArms;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(HeldItemFeatureRenderer.class)
public abstract class HeldItemFeatureRendererMixin<T extends LivingEntity, M extends EntityModel<T> & ModelWithArms> extends FeatureRenderer<T, M>
{
	@Shadow @Final private HeldItemRenderer heldItemRenderer;
	
	public HeldItemFeatureRendererMixin(FeatureRendererContext<T, M> context)
	{
		super(context);
	}
	
	@WrapOperation(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/feature/HeldItemFeatureRenderer;renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;Lnet/minecraft/util/Arm;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"))
	void applyPlushieHugOffset(HeldItemFeatureRenderer<T, M> instance, LivingEntity entity, ItemStack stack, ModelTransformationMode transformationMode, Arm arm, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, Operation<Void> original)
	{
		if((!entity.getMainArm().equals(arm) && (stack.getItem() instanceof SwordsmachinePlushieItem || stack.getItem() instanceof BlahajItem) &&
				   !(entity.getMainArm().equals(Arm.LEFT) && !entity.getStackInHand(Hand.MAIN_HAND).isEmpty())) &&
				   (entity.getPose().equals(EntityPose.CROUCHING) || entity.getPose().equals(EntityPose.STANDING)))
		{
			if(stack.getItem() instanceof BlahajItem)
			{
				matrices.push();
				matrices.translate(-0.1f, 0.3f, -0.075f);
				matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90f));
				matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(5f));
				matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(15f));
				matrices.scale(1.1f, 1.1f, 1.1f);
				renderItem(entity, stack, matrices, vertexConsumers, light);
				matrices.pop();
				return;
			}
			matrices.push();
			matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(20f));
			matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-80f));
			matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(215f));
			Vec3d offset = new Vec3d(0.15 - (MinecraftClient.getInstance().player.isSneaking() ? 0.2 : 0), -0.4 - (entity.isSneaking() ? 0.2 : 0), -0.1);
			matrices.translate(offset.x, offset.y, offset.z);
			matrices.scale(1.75f, 1.75f, 1.75f);
			renderItem(entity, stack, matrices, vertexConsumers, light);
			matrices.pop();
		}
		else
			original.call(instance, entity, stack, transformationMode, arm, matrices, vertexConsumers, light);
	}
	
	protected void renderItem(LivingEntity entity, ItemStack stack, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light)
	{
		if (!stack.isEmpty())
		{
			matrices.push();
			matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-90.0F));
			matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90.0F));
			heldItemRenderer.renderItem(entity, stack, ModelTransformationMode.THIRD_PERSON_LEFT_HAND, false, matrices, vertexConsumers, light);
			matrices.pop();
		}
	}
}
