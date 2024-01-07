package absolutelyaya.ultracraft.client.rendering;

import absolutelyaya.ultracraft.UltraComponents;
import absolutelyaya.ultracraft.block.mapping.AbstractMappingBlockEntity;
import absolutelyaya.ultracraft.components.player.IEditorComponent;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
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
	
	public List<BlockPos> newLevelBlocks;
	private List<BlockPos> levelBlocks = new ArrayList<>();
	float levelAlpha;
	
	public EditModeRenderer()
	{
		Instance = this;
	}
	
	public void render(MatrixStack matrices, Camera cam, float delta)
	{
		IEditorComponent editor = UltraComponents.EDITOR.get(MinecraftClient.getInstance().player);
		if(!editor.isActive())
			return;
		if(newLevelBlocks != null)
		{
			levelBlocks = newLevelBlocks;
			newLevelBlocks = null;
			levelAlpha = 10f;
		}
		VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEffectVertexConsumers();
		VertexConsumerProvider.Immediate textImmediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
		VertexConsumer lines = immediate.getBuffer(RenderLayer.LINES);
		TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
		RenderSystem.disableDepthTest();
		
		for (BlockPos pos : levelBlocks)
		{
			Vec3d camPos = cam.getPos();
			Vec3d targetPos = pos.toCenterPos();
			if(!(MinecraftClient.getInstance().player.getWorld().getBlockEntity(pos) instanceof AbstractMappingBlockEntity blockEntity))
				continue;
			matrices.push();
			matrices.translate(targetPos.x, targetPos.y, targetPos.z);
			matrices.translate(-camPos.x, -camPos.y, -camPos.z);
			Vector4f col = new Vector4f(1f, 1f, 1f, MathHelper.clamp(levelAlpha, 0.2f, 1f));
			if(pos.equals(editor.getEditFocus(blockEntity.getFocusKey())) || blockEntity.alwaysShowArea())
			{
				if(blockEntity.getMin(pos) != null && blockEntity.getMax(pos) != null)
				{
					Box box = new Box(blockEntity.getMin(pos), blockEntity.getMax(pos)).expand(0.5f);
					WorldRenderer.drawBox(matrices, lines, box, 0.3f, 0.3f, 0.3f, 0.75f);
					drawFloatingText(textRenderer, matrices, textImmediate, box.getCenter().toVector3f().add(0f, 0.25f, 0f), 4f,
							blockEntity.getAreaLabel(), 0xffffffff, cam);
				}
				col = new Vector4f(0f, 0.75f, 0f, 1f);
			}
			WorldRenderer.drawBox(matrices, lines, new Box(new BlockPos(0, 0, 0)).expand(-0.01).offset(-0.5, -0.5, -0.5),
					col.x, col.y, col.z, col.w);
			matrices.push();
			matrices.translate(0f, 1f, 0f);
			drawFloatingText(textRenderer, matrices, textImmediate, new Vector3f(), 1f, blockEntity.getAreaLabel(), 0xffffffff, cam);
			matrices.pop();
			matrices.pop();
			drawLineToCam(lines, matrices, targetPos.toVector3f(), cam, col);
		}
		if(levelAlpha > 0f)
			levelAlpha -= delta / 20;
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
	
	void drawLineToCam(VertexConsumer lines, MatrixStack matrices, Vector3f targetPos, Camera cam, Vector4f col)
	{
		Matrix4f matrix = matrices.peek().getPositionMatrix();
		Matrix3f normal = matrices.peek().getNormalMatrix();
		Vector3f camPos = cam.getPos().toVector3f();
		Vec3d p = new Vec3d(0, 0, 0.25).rotateX(-(float)Math.toRadians(cam.getPitch())).rotateY(-(float)Math.toRadians(cam.getYaw()));
		lines.vertex(matrix, (float)p.x, (float)p.y, (float)p.z)
				.color(col.x, col.y, col.z, col.w).normal(normal, 1f, 0f, 0f).next();
		lines.vertex(matrix, (targetPos.x - camPos.x), (targetPos.y - camPos.y), (targetPos.z - camPos.z))
				.color(col.x, col.y, col.z, col.w).normal(normal, 1f, 0f, 0f).next();
		lines.vertex(matrix, (float)p.x, (float)p.y, (float)p.z)
				.color(col.x, col.y, col.z, col.w).normal(normal, 0f, 1f, 0f).next();
		lines.vertex(matrix, (targetPos.x - camPos.x), (targetPos.y - camPos.y), (targetPos.z - camPos.z))
				.color(col.x, col.y, col.z, col.w).normal(normal, 0f, 1f, 0f).next();
		lines.vertex(matrix, (float)p.x, (float)p.y, (float)p.z)
				.color(col.x, col.y, col.z, col.w).normal(normal, 0f, 0.0f, 1f).next();
		lines.vertex(matrix, (targetPos.x - camPos.x), (targetPos.y - camPos.y), (targetPos.z - camPos.z))
				.color(col.x, col.y, col.z, col.w).normal(normal, 0f, 0.0f, 1f).next();
	}
}
