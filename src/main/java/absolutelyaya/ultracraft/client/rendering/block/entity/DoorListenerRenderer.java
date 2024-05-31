package absolutelyaya.ultracraft.client.rendering.block.entity;

import absolutelyaya.ultracraft.Ultracraft;
import absolutelyaya.ultracraft.block.mapping.DoorListenerBlockEntity;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class DoorListenerRenderer implements BlockEntityRenderer<DoorListenerBlockEntity>
{
	static final Identifier TEXTURE = Ultracraft.identifier("textures/block/door_skull.png");
	
	@Override
	public void render(DoorListenerBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay)
	{
		if(!entity.shouldRenderSkull())
			return;
		Box box = entity.getAreaBox().offset(entity.getPos().multiply(-1)).expand(0.01f);
		Vector3f center = box.getCenter().toVector3f();
		VertexConsumer consumer = vertexConsumers.getBuffer(RenderLayer.getEntityTranslucentCull(TEXTURE));
		renderFace(consumer, matrices, new Vector3f(center.x + 0.5f, center.y - 1f, (float)box.maxZ),
				new Vector3f(center.x - 0.5f, center.y + 1f, (float)box.maxZ));
		renderFace(consumer, matrices, new Vector3f(center.x - 0.5f, center.y - 1f, (float)box.minZ),
				new Vector3f(center.x + 0.5f, center.y + 1f, (float)box.minZ));
		
		renderFace(consumer, matrices, new Vector3f((float)box.maxX, center.y - 1f, center.z - 0.5f),
				new Vector3f((float)box.maxX, center.y + 1f, center.z + 0.5f));
		renderFace(consumer, matrices, new Vector3f((float)box.minX, center.y - 1f, center.z + 0.5f),
				new Vector3f((float)box.minX, center.y + 1f, center.z - 0.5f));
	}
	
	void renderFace(VertexConsumer consumer, MatrixStack matrices, Vector3f min, Vector3f max)
	{
		Matrix4f matrix = matrices.peek().getPositionMatrix();
		Matrix3f normal = matrices.peek().getNormalMatrix();
		//POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL
		RenderSystem.setShaderTexture(0, TEXTURE);
		consumer.vertex(matrix, min.x, min.y, min.z).color(0xffffffff).texture(1, 1).overlay(OverlayTexture.DEFAULT_UV)
				.light(LightmapTextureManager.MAX_LIGHT_COORDINATE).normal(normal, 0, 1, 0).next();
		consumer.vertex(matrix, min.x, max.y, min.z).color(0xffffffff).texture(1, 0).overlay(OverlayTexture.DEFAULT_UV)
				.light(LightmapTextureManager.MAX_LIGHT_COORDINATE).normal(normal, 0, 1, 0).next();
		consumer.vertex(matrix, max.x, max.y, max.z).color(0xffffffff).texture(0, 0).overlay(OverlayTexture.DEFAULT_UV)
				.light(LightmapTextureManager.MAX_LIGHT_COORDINATE).normal(normal, 0, 1, 0).next();
		consumer.vertex(matrix, max.x, min.y, max.z).color(0xffffffff).texture(0, 1).overlay(OverlayTexture.DEFAULT_UV)
				.light(LightmapTextureManager.MAX_LIGHT_COORDINATE).normal(normal, 0, 1, 0).next();
	}
	
	@Override
	public boolean rendersOutsideBoundingBox(DoorListenerBlockEntity blockEntity)
	{
		return true;
	}
}
