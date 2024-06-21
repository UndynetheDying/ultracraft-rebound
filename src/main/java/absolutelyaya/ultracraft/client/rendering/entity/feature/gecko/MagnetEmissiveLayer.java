package absolutelyaya.ultracraft.client.rendering.entity.feature.gecko;

import absolutelyaya.ultracraft.Ultracraft;
import absolutelyaya.ultracraft.entity.projectile.MagnetEntity;
import mod.azure.azurelib.cache.object.BakedGeoModel;
import mod.azure.azurelib.renderer.GeoRenderer;
import mod.azure.azurelib.renderer.layer.GeoRenderLayer;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class MagnetEmissiveLayer extends GeoRenderLayer<MagnetEntity>
{
	static final Identifier GREEN = Ultracraft.identifier("textures/entity/magnet_e.png");
	static final Identifier YELLOW = Ultracraft.identifier("textures/entity/magnet1_e.png");
	static final Identifier RED = Ultracraft.identifier("textures/entity/magnet2_e.png");
	
	public MagnetEmissiveLayer(GeoRenderer<MagnetEntity> entityRendererIn)
	{
		super(entityRendererIn);
	}
	
	@Override
	public void render(MatrixStack poseStack, MagnetEntity animatable, BakedGeoModel bakedModel, RenderLayer renderType, VertexConsumerProvider bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay)
	{
		RenderLayer armorRenderType;
		Identifier tex = getTexture(animatable);
		armorRenderType = RenderLayer.getEntityTranslucentEmissive(tex);
		getRenderer().reRender(getDefaultBakedModel(animatable), poseStack, bufferSource, animatable, armorRenderType,
				bufferSource.getBuffer(armorRenderType), partialTick, packedLight, OverlayTexture.DEFAULT_UV, 1, 1, 1, 1);
	}
	
	Identifier getTexture(MagnetEntity entity)
	{
		float strain = entity.getStrain();
		if(strain > 1f)
			return RED;
		else if(strain > 0.5f)
			return YELLOW;
		return GREEN;
	}
}
