package absolutelyaya.ultracraft.client.rendering.entity.feature.gecko;

import absolutelyaya.ultracraft.Ultracraft;
import absolutelyaya.ultracraft.block.CerberusBlock;
import absolutelyaya.ultracraft.block.CerberusBlockEntity;
import absolutelyaya.ultracraft.entity.demon.CerberusEntity;
import mod.azure.azurelib.cache.object.BakedGeoModel;
import mod.azure.azurelib.renderer.GeoRenderer;
import mod.azure.azurelib.renderer.layer.GeoRenderLayer;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class CerberusBlockEmissiveLayer extends GeoRenderLayer<CerberusBlockEntity>
{
	private static final Identifier TEXTURE = Ultracraft.identifier("textures/entity/cerberus_e.png");
	
	public CerberusBlockEmissiveLayer(GeoRenderer<CerberusBlockEntity> entityRendererIn)
	{
		super(entityRendererIn);
	}
	
	@Override
	public void render(MatrixStack poseStack, CerberusBlockEntity animatable, BakedGeoModel bakedModel, RenderLayer renderType, VertexConsumerProvider bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay)
	{
		RenderLayer layer = RenderLayer.getEntityTranslucentEmissive(TEXTURE);
		
		getRenderer().reRender(getDefaultBakedModel(animatable), poseStack, bufferSource, animatable, layer,
				bufferSource.getBuffer(layer), partialTick, packedLight, OverlayTexture.DEFAULT_UV,
				1, 1, 1, 1);
	}
}
