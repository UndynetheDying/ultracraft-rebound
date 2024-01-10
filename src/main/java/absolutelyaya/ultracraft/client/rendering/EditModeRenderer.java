package absolutelyaya.ultracraft.client.rendering;

import absolutelyaya.ultracraft.UltraComponents;
import absolutelyaya.ultracraft.Ultracraft;
import absolutelyaya.ultracraft.block.mapping.AbstractMappingBlockEntity;
import absolutelyaya.ultracraft.block.mapping.RoomBlockEntity;
import absolutelyaya.ultracraft.components.player.IEditorComponent;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.*;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

public class EditModeRenderer
{
	public static EditModeRenderer Instance;
	
	public List<BlockPos> newRoomBlocks, newOrphans, resolvedOrphans = new ArrayList<>();
	private List<BlockPos> roomBlocks = new ArrayList<>(), orphans = new ArrayList<>();
	float pingTime, pulseTime;
	
	public EditModeRenderer()
	{
		Instance = this;
	}
	
	public void render(MatrixStack matrices, Camera cam, float delta)
	{
		PlayerEntity player = MinecraftClient.getInstance().player;
		IEditorComponent editor = UltraComponents.EDITOR.get(player);
		if(!editor.isActive())
			return;
		pulseTime = (pulseTime + delta / 10f) % 4f;
		float pulse = Math.max((float)Math.sin(pulseTime), 0f);
		if(newRoomBlocks != null)
		{
			roomBlocks = newRoomBlocks;
			newRoomBlocks = null;
			pingTime = 10f;
		}
		if(newOrphans != null)
		{
			orphans = newOrphans;
			newOrphans = null;
			pingTime = 10f;
		}
		if(resolvedOrphans.size() > 0)
		{
			orphans.removeAll(resolvedOrphans);
			resolvedOrphans.clear();
			pingTime = 1f;
		}
		VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEffectVertexConsumers();
		VertexConsumerProvider.Immediate textImmediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
		VertexConsumer lines = immediate.getBuffer(RenderLayer.LINES);
		TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
		RenderSystem.disableDepthTest();
		
		List<BlockPos> blocks = new ArrayList<>(roomBlocks);
		BlockPos focusRoom = editor.getEditFocus("room");
		if(focusRoom != null && (player.getWorld().getBlockEntity(focusRoom) instanceof RoomBlockEntity room))
			blocks.addAll(room.getChildren());
		
		Vec3d camPos = cam.getPos();
		for (BlockPos pos : blocks)
		{
			Vec3d targetPos = pos.toCenterPos();
			if(!(player.getWorld().getBlockEntity(pos) instanceof AbstractMappingBlockEntity blockEntity))
				continue;
			matrices.push();
			matrices.translate(targetPos.x, targetPos.y, targetPos.z);
			matrices.translate(-camPos.x, -camPos.y, -camPos.z);
			Vector4f col = new Vector4f(blockEntity.getColor());
			if(pingTime > col.w)
				col.set(Math.min(pingTime, 1f));
			boolean focused = pos.equals(editor.getEditFocus(blockEntity.getFocusKey()));
			if(focused || blockEntity.alwaysShowArea())
			{
				if(blockEntity.getMin() != null && blockEntity.getMax() != null)
				{
					Box box = blockEntity.getAreaBox().offset(-pos.getX() - 0.5, -pos.getY() - 0.5, -pos.getZ() - 0.5);
					Vector4f areaColor = blockEntity.getAreaColor();
					WorldRenderer.drawBox(matrices, lines, box, areaColor.x, areaColor.y, areaColor.z, areaColor.w);
					if(focused)
						drawLinesBetweenBoxes(lines, matrices, new Box(new BlockPos(0, 0, 0)).offset(-0.5f, -0.5f, -0.5f), box,
								new Vector4f(areaColor).mul(1f, 1f, 1f, 0.5f));
					drawFloatingText(textRenderer, matrices, textImmediate, box.getCenter().toVector3f().add(0f, 0.25f, 0f), blockEntity.getAreaLabelSize(),
							blockEntity.getAreaLabel(), 0xffffffff, cam);
				}
			}
			if(focused)
				col = col.lerp(new Vector4f(0f, 1f, 0f, 1f), pulse);
			WorldRenderer.drawBox(matrices, lines, new Box(new BlockPos(0, 0, 0)).expand(-0.01).offset(-0.5, -0.5, -0.5),
					col.x, col.y, col.z, col.w);
			matrices.push();
			matrices.translate(0f, 1f, 0f);
			drawFloatingText(textRenderer, matrices, textImmediate, new Vector3f(), 1f, blockEntity.getAreaLabel(), 0xffffffff, cam);
			matrices.pop();
			drawSprite(matrices, new Vector3f(0, 0, 0), textImmediate, blockEntity.getTexture(), cam);
			matrices.pop();
			if(blockEntity.showCamLine())
				drawLineToCam(lines, matrices, targetPos.toVector3f(), cam, col);
		}
		
		for (BlockPos pos : orphans)
		{
			Vec3d targetPos = pos.toCenterPos();
			if(!(player.getWorld().getBlockEntity(pos) instanceof AbstractMappingBlockEntity blockEntity))
				continue;
			if(blockEntity.getParent() != null && player.getWorld().getBlockEntity(blockEntity.getParent()) instanceof RoomBlockEntity)
			{
				resolvedOrphans.add(pos);
				continue;
			}
			matrices.push();
			matrices.translate(targetPos.x, targetPos.y, targetPos.z);
			matrices.translate(-camPos.x, -camPos.y, -camPos.z);
			float alpha = Math.max(Math.min(pingTime, 1f), 0.5f);
			Vector4f col = new Vector4f(0.2f, 0f, 0f, alpha);
			col = col.lerp(new Vector4f(1f, 0f, 0f, alpha), (float)Math.sin(pulseTime) * 0.5f + 0.5f);
			WorldRenderer.drawBox(matrices, lines, new Box(new BlockPos(0, 0, 0)).expand(-0.01).offset(-0.5, -0.5, -0.5),
					col.x, col.y, col.z, col.w);
			matrices.push();
			matrices.translate(0f, 1f, 0f);
			drawFloatingText(textRenderer, matrices, textImmediate, new Vector3f(), 1f, blockEntity.getAreaLabel(), 0xffffffff, cam);
			matrices.pop();
			drawSprite(matrices, new Vector3f(0, 0, 0), textImmediate, blockEntity.getTexture(), cam);
			matrices.pop();
			drawLineToCam(lines, matrices, targetPos.toVector3f(), cam, col);
		}
		
		if(pingTime > 0f)
			pingTime -= delta / 20f;
		RenderSystem.enableDepthTest();
	}
	
	void drawFloatingText(TextRenderer renderer, MatrixStack matrices, VertexConsumerProvider.Immediate immediate, Vector3f tPos, float size, Text text,
						  int col, Camera cam)
	{
		matrices.push();
		matrices.translate(tPos.x, tPos.y, tPos.z);
		matrices.multiply(cam.getRotation());
		matrices.scale(-size / 50f, -size / 50f, size / 50f);
		renderer.draw(text, -renderer.getWidth(text) / 2f, 0f, col, false, matrices.peek().getPositionMatrix(),
				immediate, TextRenderer.TextLayerType.NORMAL, 0x44000000, LightmapTextureManager.MAX_LIGHT_COORDINATE);
		matrices.pop();
	}
	
	void drawSprite(MatrixStack matrices, Vector3f tPos, VertexConsumerProvider consumerProvider, String texture, Camera cam)
	{
		matrices.push();
		matrices.translate(tPos.x, tPos.y, tPos.z);
		matrices.multiply(cam.getRotation());
		matrices.scale(1f, 1f, -1f);
		//POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL
		Matrix4f matrix = matrices.peek().getPositionMatrix();
		VertexConsumer consumer = consumerProvider.getBuffer(RenderLayer.getEntityCutout(new Identifier(Ultracraft.MOD_ID, "textures/item/editor/" + texture + ".png")));
		consumer.vertex(matrix, -0.5f, -0.5f, 0f).color(0xffffffff).texture(1f, 1f).overlay(OverlayTexture.DEFAULT_UV)
				.light(LightmapTextureManager.MAX_LIGHT_COORDINATE).normal(0f, 1f, 0f).next();
		consumer.vertex(matrix, -0.5f, 0.5f, 0f).color(0xffffffff).texture(1f, 0f).overlay(OverlayTexture.DEFAULT_UV)
				.light(LightmapTextureManager.MAX_LIGHT_COORDINATE).normal(0f, 1f, 0f).next();
		consumer.vertex(matrix, 0.5f, 0.5f, 0f).color(0xffffffff).texture(0f, 0f).overlay(OverlayTexture.DEFAULT_UV)
				.light(LightmapTextureManager.MAX_LIGHT_COORDINATE).normal(0f, 1f, 0f).next();
		consumer.vertex(matrix, 0.5f, -0.5f, 0f).color(0xffffffff).texture(0f, 1f).overlay(OverlayTexture.DEFAULT_UV)
				.light(LightmapTextureManager.MAX_LIGHT_COORDINATE).normal(0f, 1f, 0f).next();
		matrices.pop();
	}
	
	void drawLineToCam(VertexConsumer lines, MatrixStack matrices, Vector3f targetPos, Camera cam, Vector4f col)
	{
		Vector3f camPos = cam.getPos().toVector3f();
		Vector3f p = new Vector3f(0, 0, 0.25f).rotateX((float)Math.toRadians(cam.getPitch())).rotateY(-(float)Math.toRadians(cam.getYaw()));
		drawLineBetween(lines, matrices, p, targetPos.sub(camPos), col);
	}
	
	void drawLinesBetweenBoxes(VertexConsumer lines, MatrixStack matrices, Box start, Box end, Vector4f col)
	{
		drawLineBetween(lines, matrices, new Vec3d(start.minX, start.minY, start.minZ).toVector3f(),
										new Vec3d(end.minX, end.minY, end.minZ).toVector3f(), col);
		drawLineBetween(lines, matrices, new Vec3d(start.maxX, start.minY, start.minZ).toVector3f(),
										new Vec3d(end.maxX, end.minY, end.minZ).toVector3f(), col);
		drawLineBetween(lines, matrices, new Vec3d(start.minX, start.maxY, start.minZ).toVector3f(),
										new Vec3d(end.minX, end.maxY, end.minZ).toVector3f(), col);
		drawLineBetween(lines, matrices, new Vec3d(start.maxX, start.maxY, start.minZ).toVector3f(),
										new Vec3d(end.maxX, end.maxY, end.minZ).toVector3f(), col);
		
		drawLineBetween(lines, matrices, new Vec3d(start.minX, start.minY, start.maxZ).toVector3f(),
										new Vec3d(end.minX, end.minY, end.maxZ).toVector3f(), col);
		drawLineBetween(lines, matrices, new Vec3d(start.maxX, start.minY, start.maxZ).toVector3f(),
										new Vec3d(end.maxX, end.minY, end.maxZ).toVector3f(), col);
		drawLineBetween(lines, matrices, new Vec3d(start.minX, start.maxY, start.maxZ).toVector3f(),
										new Vec3d(end.minX, end.maxY, end.maxZ).toVector3f(), col);
		drawLineBetween(lines, matrices, new Vec3d(start.maxX, start.maxY, start.maxZ).toVector3f(),
										new Vec3d(end.maxX, end.maxY, end.maxZ).toVector3f(), col);
	}
	
	void drawLineBetween(VertexConsumer lines, MatrixStack matrices, Vector3f startPos, Vector3f targetPos, Vector4f col)
	{
		Matrix4f matrix = matrices.peek().getPositionMatrix();
		Matrix3f normal = matrices.peek().getNormalMatrix();
		lines.vertex(matrix, startPos.x, startPos.y, startPos.z)
				.color(col.x, col.y, col.z, col.w).normal(normal, 1f, 0f, 0f).next();
		lines.vertex(matrix, targetPos.x, targetPos.y, targetPos.z)
				.color(col.x, col.y, col.z, col.w).normal(normal, 1f, 0f, 0f).next();
		lines.vertex(matrix, startPos.x, startPos.y, startPos.z)
				.color(col.x, col.y, col.z, col.w).normal(normal, 0f, 1f, 0f).next();
		lines.vertex(matrix, targetPos.x, targetPos.y, targetPos.z)
				.color(col.x, col.y, col.z, col.w).normal(normal, 0f, 1f, 0f).next();
		lines.vertex(matrix, startPos.x, startPos.y, startPos.z)
				.color(col.x, col.y, col.z, col.w).normal(normal, 0f, 0.0f, 1f).next();
		lines.vertex(matrix, targetPos.x, targetPos.y, targetPos.z)
				.color(col.x, col.y, col.z, col.w).normal(normal, 0f, 0.0f, 1f).next();
	}
}
