package absolutelyaya.ultracraft.client.rendering.entity.projectile;

import absolutelyaya.ultracraft.entity.projectile.ChainsawEntity;
import mod.azure.azurelib.model.GeoModel;
import mod.azure.azurelib.renderer.GeoEntityRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;

public class ChainsawEntityRenderer extends GeoEntityRenderer<ChainsawEntity>
{
	public ChainsawEntityRenderer(EntityRendererFactory.Context renderManager)
	{
		super(renderManager, new ChainsawEntityModel());
	}
	
	@Override
	public void render(ChainsawEntity saw, float yaw, float delta, MatrixStack matrices, VertexConsumerProvider vertexConsumerProvider, int i)
	{
		matrices.push();
		matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(saw.getYaw(delta)));
		matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-saw.getPitch(delta)));
		matrices.translate(0f, 0.1f, -0.2f);
		super.render(saw, yaw, delta, matrices, vertexConsumerProvider, i);
		matrices.pop();
	}
}
