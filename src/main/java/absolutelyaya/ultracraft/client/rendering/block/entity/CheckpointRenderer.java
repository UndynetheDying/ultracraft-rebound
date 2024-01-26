package absolutelyaya.ultracraft.client.rendering.block.entity;

import absolutelyaya.ultracraft.components.UltraComponents;
import absolutelyaya.ultracraft.Ultracraft;
import absolutelyaya.ultracraft.block.mapping.CheckpointBlockEntity;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class CheckpointRenderer implements BlockEntityRenderer<CheckpointBlockEntity>
{
	final Identifier TEXTURE = new Identifier(Ultracraft.MOD_ID, "textures/block/checkpoint.png");
	
	@Override
	public void render(CheckpointBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay)
	{
		PlayerEntity player = MinecraftClient.getInstance().player;
		BlockPos lastCheckpoint = UltraComponents.WINGED.get(player).getLastCheckpoint();
		if(lastCheckpoint != null && lastCheckpoint.equals(entity.getPos()))
			return;
		entity.progressTime(MinecraftClient.getInstance().getLastFrameDuration() / 120f);
		Box box = entity.getAreaBox().offset(entity.getPos().multiply(-1));
		Vec3d center = box.getCenter();
		Vector3f min, max;
		float xSize = (float)Math.abs(box.minX - box.maxX), zSize = (float)Math.abs(box.minZ - box.maxZ), uSpan;
		if(xSize > zSize)
		{
			min = new Vector3f((float)box.minX, (float)(center.y - 0.5), (float)center.z);
			max = new Vector3f((float)box.maxX, (float)(center.y + 0.5), (float)center.z);
			uSpan = xSize / 6f;
		}
		else
		{
			min = new Vector3f((float)center.x, (float)(center.y - 0.5), (float)box.minZ);
			max = new Vector3f((float)center.x, (float)(center.y + 0.5), (float)box.maxZ);
			uSpan = zSize / 6f;
		}
		VertexConsumer consumer = vertexConsumers.getBuffer(RenderLayer.getEntityTranslucentCull(TEXTURE));
		Matrix4f matrix = matrices.peek().getPositionMatrix();
		Matrix3f normal = matrices.peek().getNormalMatrix();
		//POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL
		float minU = (float)entity.getTime(), maxU = minU + uSpan;
		RenderSystem.setShaderTexture(0, TEXTURE);
		consumer.vertex(matrix, min.x, min.y, min.z).color(0xffffffff).texture(maxU, 1).overlay(OverlayTexture.DEFAULT_UV)
				.light(LightmapTextureManager.MAX_LIGHT_COORDINATE).normal(normal, 0, 1, 0).next();
		consumer.vertex(matrix, min.x, max.y, min.z).color(0xffffffff).texture(maxU, 0).overlay(OverlayTexture.DEFAULT_UV)
				.light(LightmapTextureManager.MAX_LIGHT_COORDINATE).normal(normal, 0, 1, 0).next();
		consumer.vertex(matrix, max.x, max.y, max.z).color(0xffffffff).texture(minU, 0).overlay(OverlayTexture.DEFAULT_UV)
				.light(LightmapTextureManager.MAX_LIGHT_COORDINATE).normal(normal, 0, 1, 0).next();
		consumer.vertex(matrix, max.x, min.y, max.z).color(0xffffffff).texture(minU, 1).overlay(OverlayTexture.DEFAULT_UV)
				.light(LightmapTextureManager.MAX_LIGHT_COORDINATE).normal(normal, 0, 1, 0).next();
		
		consumer.vertex(matrix, max.x, min.y, max.z).color(0xffffffff).texture(maxU, 1).overlay(OverlayTexture.DEFAULT_UV)
				.light(LightmapTextureManager.MAX_LIGHT_COORDINATE).normal(normal, 0, 1, 0).next();
		consumer.vertex(matrix, max.x, max.y, max.z).color(0xffffffff).texture(maxU, 0).overlay(OverlayTexture.DEFAULT_UV)
				.light(LightmapTextureManager.MAX_LIGHT_COORDINATE).normal(normal, 0, 1, 0).next();
		consumer.vertex(matrix, min.x, max.y, min.z).color(0xffffffff).texture(minU, 0).overlay(OverlayTexture.DEFAULT_UV)
				.light(LightmapTextureManager.MAX_LIGHT_COORDINATE).normal(normal, 0, 1, 0).next();
		consumer.vertex(matrix, min.x, min.y, min.z).color(0xffffffff).texture(minU, 1).overlay(OverlayTexture.DEFAULT_UV)
				.light(LightmapTextureManager.MAX_LIGHT_COORDINATE).normal(normal, 0, 1, 0).next();
	}
	
	@Override
	public boolean rendersOutsideBoundingBox(CheckpointBlockEntity blockEntity)
	{
		return true;
	}
}
