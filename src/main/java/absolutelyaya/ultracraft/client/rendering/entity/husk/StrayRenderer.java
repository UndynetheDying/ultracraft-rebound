package absolutelyaya.ultracraft.client.rendering.entity.husk;

import absolutelyaya.ultracraft.entity.husk.StrayEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import mod.azure.azurelib.renderer.GeoEntityRenderer;

public class StrayRenderer extends GeoEntityRenderer<StrayEntity>
{
	public StrayRenderer(EntityRendererFactory.Context ctx)
	{
		super(ctx, new StrayModel());
	}
	
	@Override
	public RenderLayer getRenderType(StrayEntity animatable, Identifier texture, VertexConsumerProvider bufferSource, float partialTick)
	{
		return RenderLayer.getEntityTranslucent(texture);
	}
	
	@Override
	public void render(StrayEntity entity, float entityYaw, float partialTick, MatrixStack poseStack, VertexConsumerProvider bufferSource, int packedLight)
	{
		super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
	}
}
