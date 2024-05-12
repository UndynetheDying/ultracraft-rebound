package absolutelyaya.ultracraft.client.rendering.entity.feature.gecko;

import absolutelyaya.ultracraft.Ultracraft;
import absolutelyaya.ultracraft.components.UltraComponents;
import absolutelyaya.ultracraft.entity.demon.HideousMassEntity;
import mod.azure.azurelib.cache.object.BakedGeoModel;
import mod.azure.azurelib.renderer.GeoRenderer;
import mod.azure.azurelib.renderer.layer.GeoRenderLayer;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class HideousMassEmissiveLayer extends GeoRenderLayer<HideousMassEntity>
{
	private static final Identifier TEXTURE = new Identifier(Ultracraft.MOD_ID, "textures/entity/hideous_mass_emissive.png");
	
	public HideousMassEmissiveLayer(GeoRenderer<HideousMassEntity> entityRendererIn)
	{
		super(entityRendererIn);
	}
	
	@Override
	public void render(MatrixStack poseStack, HideousMassEntity animatable, BakedGeoModel bakedModel, RenderLayer renderType, VertexConsumerProvider bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay)
	{
		if(!(animatable.isDying() || UltraComponents.LIVING.get(animatable).isEnraged()))
			return;
		RenderLayer layer = RenderLayer.getEntityTranslucentEmissive(TEXTURE);
		
		getRenderer().reRender(getDefaultBakedModel(animatable), poseStack, bufferSource, animatable, layer,
				bufferSource.getBuffer(layer), partialTick, packedLight, OverlayTexture.DEFAULT_UV,
				1, 1, 1, 1);
	}
}
