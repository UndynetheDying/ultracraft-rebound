package absolutelyaya.ultracraft.client.rendering.entity.other;

import absolutelyaya.ultracraft.Ultracraft;
import absolutelyaya.ultracraft.entity.other.InterruptableCharge;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;

public class InterruptableChargeRenderer extends EntityRenderer<InterruptableCharge>
{
	public InterruptableChargeRenderer(EntityRendererFactory.Context ctx)
	{
		super(ctx);
	}
	
	@Override
	public Identifier getTexture(InterruptableCharge entity)
	{
		return new Identifier(Ultracraft.MOD_ID, "textures/entity/interruptable_charge.png");
	}
	
	@Override
	public void render(InterruptableCharge entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light)
	{
		RenderLayer renderLayer = RenderLayer.getEntityTranslucent(getTexture(entity));
		VertexConsumer vertexConsumer = vertexConsumers.getBuffer(renderLayer);
		matrices.push();
		float f = entity.getScale() / 2f;
		matrices.scale(f, f, f);
		matrices.multiply(MinecraftClient.getInstance().gameRenderer.getCamera().getRotation());
		matrices.translate(0f, 0.25f, 0f);
		Matrix4f matrix = matrices.peek().getPositionMatrix();
		vertexConsumer.vertex(matrix, -0.5f, -0.5f, 0).color(1f, 1f, 1f, 1f).texture(0f, 0f).overlay(OverlayTexture.DEFAULT_UV).light(LightmapTextureManager.MAX_BLOCK_LIGHT_COORDINATE).normal(0f, 1f, 0f).next();
		vertexConsumer.vertex(matrix, -0.5f, 0.5f, 0).color(1f, 1f, 1f, 1f).texture(0f, 0.5f).overlay(OverlayTexture.DEFAULT_UV).light(LightmapTextureManager.MAX_BLOCK_LIGHT_COORDINATE).normal(0f, 1f, 0f).next();
		vertexConsumer.vertex(matrix, 0.5f, 0.5f, 0).color(1f, 1f, 1f, 1f).texture(0.5f, 0.5f).overlay(OverlayTexture.DEFAULT_UV).light(LightmapTextureManager.MAX_BLOCK_LIGHT_COORDINATE).normal(0f, 1f, 0f).next();
		vertexConsumer.vertex(matrix, 0.5f, -0.5f, 0).color(1f, 1f, 1f, 1f).texture(0.5f, 0f).overlay(OverlayTexture.DEFAULT_UV).light(LightmapTextureManager.MAX_BLOCK_LIGHT_COORDINATE).normal(0f, 1f, 0f).next();
		matrices.pop();
	}
	
	@Override
	public boolean shouldRender(InterruptableCharge entity, Frustum frustum, double x, double y, double z)
	{
		return true;
	}
}
