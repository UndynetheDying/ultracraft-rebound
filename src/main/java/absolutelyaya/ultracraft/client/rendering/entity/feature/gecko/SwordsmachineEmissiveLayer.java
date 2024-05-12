package absolutelyaya.ultracraft.client.rendering.entity.feature.gecko;

import absolutelyaya.ultracraft.Ultracraft;
import absolutelyaya.ultracraft.components.UltraComponents;
import absolutelyaya.ultracraft.entity.machine.DestinyBondSwordsmachineEntity;
import absolutelyaya.ultracraft.entity.machine.SwordsmachineEntity;
import absolutelyaya.ultracraft.registry.StatusEffectRegistry;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import mod.azure.azurelib.cache.object.BakedGeoModel;
import mod.azure.azurelib.renderer.GeoRenderer;
import mod.azure.azurelib.renderer.layer.GeoRenderLayer;

public class SwordsmachineEmissiveLayer extends GeoRenderLayer<SwordsmachineEntity>
{
	private static final Identifier TEXTURE = new Identifier(Ultracraft.MOD_ID, "textures/entity/swordsmachine_emissive.png");
	private static final Identifier TEXTURE_RAGE = new Identifier(Ultracraft.MOD_ID, "textures/entity/swordsmachine_emissive_rage.png");
	private static final Identifier TEXTURE_TUNDRA = new Identifier(Ultracraft.MOD_ID, "textures/entity/swordsmachine_emissive_tundra.png");
	private static final Identifier TEXTURE_AGONY = new Identifier(Ultracraft.MOD_ID, "textures/entity/swordsmachine_emissive_agony.png");
	
	public SwordsmachineEmissiveLayer(GeoRenderer<SwordsmachineEntity> entityRendererIn)
	{
		super(entityRendererIn);
	}
	
	@Override
	public void render(MatrixStack poseStack, SwordsmachineEntity animatable, BakedGeoModel bakedModel, RenderLayer renderType, VertexConsumerProvider bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay)
	{
		RenderLayer emissiveLayer;
		if(animatable instanceof DestinyBondSwordsmachineEntity destinySM)
			emissiveLayer = RenderLayer.getEntityTranslucentEmissive(destinySM.getVariant() == 0 ? TEXTURE_TUNDRA : TEXTURE_AGONY);
		else
			emissiveLayer = RenderLayer.getEntityTranslucentEmissive(UltraComponents.LIVING.get(animatable).isEnraged() ? TEXTURE_RAGE : TEXTURE);
		
		float[] col = RenderSystem.getShaderColor();
		getRenderer().reRender(getDefaultBakedModel(animatable), poseStack, bufferSource, animatable, emissiveLayer,
				bufferSource.getBuffer(emissiveLayer), partialTick, packedLight, OverlayTexture.DEFAULT_UV,
				col[0], col[1], col[2], col[3]);
	}
}
