package absolutelyaya.ultracraft.client.rendering.entity.demon;

import absolutelyaya.ultracraft.client.UltracraftClient;
import absolutelyaya.ultracraft.entity.demon.HideousMassEntity;
import mod.azure.azurelib.cache.object.BakedGeoModel;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.random.Random;
import mod.azure.azurelib.renderer.GeoEntityRenderer;

public class HideousMassRenderer extends GeoEntityRenderer<HideousMassEntity>
{
	public HideousMassRenderer(EntityRendererFactory.Context renderManager)
	{
		super(renderManager, new HideousMassModel());
	}
	
	@Override
	public void actuallyRender(MatrixStack matrices, HideousMassEntity entity, BakedGeoModel model, RenderLayer renderType, VertexConsumerProvider bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha)
	{
		matrices.push();
		if(entity != null && (entity.isDying() || entity.isDead()))
		{
			Random r = entity.getRandom();
			float f = UltracraftClient.getConfig().safeVFX ? 0.1f : 0.5f;
			matrices.translate(r.nextFloat() * f, r.nextFloat() * f, r.nextFloat() * f);
		}
		super.actuallyRender(matrices, entity, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
		matrices.pop();
	}
}
