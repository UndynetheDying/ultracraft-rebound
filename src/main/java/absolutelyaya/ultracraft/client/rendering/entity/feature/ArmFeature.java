package absolutelyaya.ultracraft.client.rendering.entity.feature;

import absolutelyaya.ultracraft.components.UltraComponents;
import absolutelyaya.ultracraft.Ultracraft;
import absolutelyaya.ultracraft.components.player.IArmComponent;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;

public class ArmFeature<T extends PlayerEntity, M extends PlayerEntityModel<T>> extends FeatureRenderer<T, M>
{
	static final Identifier FEEDBACKER_SLIM = Ultracraft.identifier("textures/entity/arms/feedbacker_slim.png");
	static final Identifier KNUCKLEBLASTER_SLIM = Ultracraft.identifier("textures/entity/arms/knuckleblaster_slim.png");
	static final Identifier FEEDBACKER = Ultracraft.identifier("textures/entity/arms/feedbacker.png");
	static final Identifier KNUCKLEBLASTER = Ultracraft.identifier("textures/entity/arms/knuckleblaster.png");
	
	public ArmFeature(FeatureRendererContext<T, M> context)
	{
		super(context);
	}
	
	@Override
	public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch)
	{
		if(entity.isInvisible())
			return;
		IArmComponent arms = UltraComponents.ARMS.get(entity);
		byte activeArm = arms.getActiveArm();
		boolean noneEquipped = activeArm == -1;
		if(noneEquipped || !arms.isVisible())
			return;
		PlayerEntityModel<T> model = getContextModel();
		boolean slim = entity instanceof AbstractClientPlayerEntity clientPlayer && clientPlayer.getModel().equals("slim");
		VertexConsumer consumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(getTexture(activeArm, slim)));
		int overlay = LivingEntityRenderer.getOverlay(entity, 0f);
		if(entity.getMainArm().equals(Arm.RIGHT))
		{
			boolean arm = model.leftArm.visible, sleeve = model.leftSleeve.visible;
			model.leftArm.visible = model.leftSleeve.visible = true;
			
			model.leftArm.render(matrices, consumer, light, overlay);
			model.leftSleeve.render(matrices, consumer, light, overlay);
			
			model.leftArm.visible = arm;
			model.leftSleeve.visible = sleeve;
		}
		else
		{
			boolean arm = model.rightArm.visible, sleeve = model.rightSleeve.visible;
			model.rightArm.visible = model.rightSleeve.visible = true;
			
			model.rightArm.render(matrices, consumer, light, overlay);
			model.rightSleeve.render(matrices, consumer, light, overlay);
			
			model.rightArm.visible = arm;
			model.rightSleeve.visible = sleeve;
		}
	}
	
	public static Identifier getTexture(byte arm, boolean slim)
	{
		if(arm == 1)
			return slim ? KNUCKLEBLASTER_SLIM : KNUCKLEBLASTER;
		else
			return slim ? FEEDBACKER_SLIM : FEEDBACKER;
	}
}
