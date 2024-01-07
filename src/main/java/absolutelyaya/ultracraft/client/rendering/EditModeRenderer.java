package absolutelyaya.ultracraft.client.rendering;

import absolutelyaya.ultracraft.client.UltracraftClient;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

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
		if(!UltracraftClient.isEditMode())
			return;
		if(newLevelBlocks != null)
		{
			levelBlocks = newLevelBlocks;
			newLevelBlocks = null;
			levelAlpha = 10f;
		}
		VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEffectVertexConsumers();
		VertexConsumer lines = immediate.getBuffer(RenderLayer.LINES);
		RenderSystem.disableDepthTest();
		
		for (BlockPos pos : levelBlocks)
		{
			matrices.push();
			Vec3d camPos = cam.getPos();
			Vec3d targetPos = pos.toCenterPos();
			matrices.translate(targetPos.x, targetPos.y, targetPos.z);
			matrices.translate(-camPos.x, -camPos.y, -camPos.z);
			Matrix3f normal = matrices.peek().getNormalMatrix();
			WorldRenderer.drawBox(matrices, lines, new Box(new BlockPos(0, 0, 0)).expand(-0.01).offset(-0.5, -0.5, -0.5),
					1f, 1f, 1f, MathHelper.clamp(levelAlpha, 0.2f, 1f));
			matrices.pop();
			Matrix4f matrix = matrices.peek().getPositionMatrix();
			Vec3d p = new Vec3d(0, 0, 0.25).rotateX(-(float)Math.toRadians(cam.getPitch())).rotateY(-(float)Math.toRadians(cam.getYaw()));
			lines.vertex(matrix, (float)p.x, (float)p.y, (float)p.z)
					.color(1f, 1f, 1f, MathHelper.clamp(levelAlpha, 0.2f, 1f)).normal(normal, 0.0f, 1.0f, 0.0f).next();
			lines.vertex(matrix, (float)(targetPos.x - camPos.x), (float)(targetPos.y - camPos.y), (float)(targetPos.z - camPos.z))
					.color(1f, 1f, 1f, MathHelper.clamp(levelAlpha, 0.2f, 1f)).normal(normal, 0.0f, 1.0f, 0.0f).next();
		}
		if(levelAlpha > 0f)
			levelAlpha -= delta / 20;
		RenderSystem.enableDepthTest();
	}
}
