package absolutelyaya.ultracraft.client.rendering.entity.feature.gecko;

import absolutelyaya.ultracraft.Ultracraft;
import absolutelyaya.ultracraft.entity.machine.V2Entity;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import mod.azure.azurelib.cache.object.BakedGeoModel;
import mod.azure.azurelib.renderer.GeoRenderer;
import mod.azure.azurelib.renderer.layer.GeoRenderLayer;

public class V2EmissiveLayer extends GeoRenderLayer<V2Entity>
{
	static final Identifier YELLOW = Ultracraft.texIdentifier("textures/entity/v2/yellow_e");
	static final Identifier BLUE = Ultracraft.texIdentifier("textures/entity/v2/blue_e");
	static final Identifier RED = Ultracraft.texIdentifier("textures/entity/v2/red_e");
	static final Identifier GREEN = Ultracraft.texIdentifier("textures/entity/v2/green_e");
	
	public V2EmissiveLayer(GeoRenderer<V2Entity> entityRendererIn)
	{
		super(entityRendererIn);
	}
	
	@Override
	public void render(MatrixStack poseStack, V2Entity animatable, BakedGeoModel bakedModel, RenderLayer renderType, VertexConsumerProvider bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay)
	{
		RenderLayer renderLayer;
		Identifier tex = switch((animatable.getMovementMode()) % 4)
		{
			default -> YELLOW;
			case 1 -> BLUE;
			case 2 -> RED;
			case 3 -> GREEN;
		};
		renderLayer = RenderLayer.getEntityCutout(tex);
		getRenderer().reRender(getDefaultBakedModel(animatable), poseStack, bufferSource, animatable, renderLayer,
				bufferSource.getBuffer(renderLayer), partialTick, LightmapTextureManager.MAX_BLOCK_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV,
				1, 1, 1, 1);
	}
}
