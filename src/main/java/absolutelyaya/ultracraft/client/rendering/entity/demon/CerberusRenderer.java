package absolutelyaya.ultracraft.client.rendering.entity.demon;

import absolutelyaya.ultracraft.Ultracraft;
import absolutelyaya.ultracraft.client.rendering.entity.feature.gecko.CerberusEmissiveLayer;
import absolutelyaya.ultracraft.entity.demon.CerberusEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import mod.azure.azurelib.renderer.GeoEntityRenderer;

public class CerberusRenderer extends GeoEntityRenderer<CerberusEntity>
{
	public CerberusRenderer(EntityRendererFactory.Context ctx)
	{
		super(ctx, new CerberusModel());
		addRenderLayer(new CerberusEmissiveLayer(this));
	}
	
	@Override
	public RenderLayer getRenderType(CerberusEntity animatable, Identifier texture, VertexConsumerProvider bufferSource, float partialTick)
	{
		return RenderLayer.getEntityTranslucent(texture);
	}
	
	@Override
	public void render(CerberusEntity entity, float entityYaw, float partialTick, MatrixStack poseStack, VertexConsumerProvider bufferSource, int packedLight)
	{
		poseStack.push();
		poseStack.scale(2, 2, 2);
		super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
		poseStack.pop();
	}
}
