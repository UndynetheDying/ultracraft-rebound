package absolutelyaya.ultracraft.client.rendering;

import absolutelyaya.ultracraft.components.UltraComponents;
import absolutelyaya.ultracraft.components.player.IStyleComponent;
import absolutelyaya.ultracraft.components.player.IWingedPlayerComponent;
import absolutelyaya.ultracraft.cybergrind.CybergrindData;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Vector4i;

public class CybergrindArenaRenderer
{
	static final Vec3d[] colors = new Vec3d[] {
			new Vec3d(0.32, 0.46, 1),
			new Vec3d(0.31, 1, 0.12),
			new Vec3d(1, 0.90, 0),
			new Vec3d(1, 0.56, 0),
			new Vec3d(0.4, 0, 0),
			new Vec3d(0.65, 0, 0),
			new Vec3d(0.8, 0, 0),
			new Vec3d(1f, 1f, 1f),
	};
	static int lastRank;
	static float rankUpFlash, rankDownFlash, vOffset;
	
	public static void render(MatrixStack matrices, Camera cam, float delta)
	{
		PlayerEntity player = MinecraftClient.getInstance().player;
		IWingedPlayerComponent winged = UltraComponents.WINGED.get(player);
		IStyleComponent style = UltraComponents.STYLE.get(player);
		CybergrindData data = winged.getCybergrindData();
		if(data == null)
			return;
		
		float farplane = MinecraftClient.getInstance().gameRenderer.getFarPlaneDistance();
		float time = Util.getMeasuringTimeMs() / 1000f;
		BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
		RenderSystem.enableBlend();
		RenderSystem.enableDepthTest();
		RenderSystem.setShaderTexture(0, new Identifier("textures/misc/forcefield.png"));
		RenderSystem.setShader(GameRenderer::getPositionTexProgram);
		
		int rank = style.getRank();
		if(lastRank != rank)
		{
			if(rank > lastRank)
				rankUpFlash = 1f;
			else
				rankDownFlash = 1f;
			lastRank = rank;
		}
		Vec3d col = colors[rank];
		if(rank == 7)
		{
			col = new Vec3d((float)(Math.sin(time * 2) + 1f) / 2f, (float)(Math.sin(time * 2 + 1) + 1f) / 2f, (float)(Math.sin(time * 2 + 2) + 1f) / 2f);
			vOffset += delta / 30f;
		}
		if(rankUpFlash > 0f)
			col = col.lerp(new Vec3d(1f, 0f, 0f), rankUpFlash);
		if(rankDownFlash > 0f)
			col = col.lerp(Vec3d.ZERO, rankDownFlash);
		RenderSystem.setShaderColor((float)col.x, (float)col.y, (float)col.z, 0.9f + (float)Math.sin(time) * 0.1f);
		if(rankUpFlash > 0f)
		{
			vOffset += rankUpFlash / 10f;
			rankUpFlash = Math.max(0f, rankUpFlash - delta / 30f);
		}
		if(rankDownFlash > 0f)
			rankDownFlash = Math.max(0f, rankDownFlash - delta / 30f);
		
		//MatrixStack matrices = RenderSystem.getModelViewStack();
		matrices.push();
		Vec3d camPos = cam.getPos();
		matrices.translate(-camPos.x, -camPos.y, -camPos.z);
		//RenderSystem.applyModelViewMatrix();
		RenderSystem.disableCull();
		
		Vector4i bounds = data.getArenaBounds();
		bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
		Matrix4f matrix = matrices.peek().getPositionMatrix();
		float uvScale = 4f;
		//north
		bufferBuilder.vertex(matrix, bounds.x, -farplane, bounds.y - 0.01f)
				.texture((time % uvScale + bounds.z - bounds.x) / uvScale, -farplane / uvScale + vOffset).next();
		bufferBuilder.vertex(matrix, bounds.z, -farplane, bounds.y - 0.01f)
				.texture((time % uvScale) / uvScale, -farplane / uvScale + vOffset).next();
		bufferBuilder.vertex(matrix, bounds.z, farplane, bounds.y - 0.01f)
				.texture((time % uvScale) / uvScale, farplane / uvScale + vOffset).next();
		bufferBuilder.vertex(matrix, bounds.x, farplane, bounds.y - 0.01f)
				.texture((time % uvScale + bounds.z - bounds.x) / uvScale, farplane / uvScale + vOffset).next();
		//south
		bufferBuilder.vertex(matrix, bounds.z, farplane, bounds.w + 0.01f)
				.texture((time % uvScale + bounds.z - bounds.x) / uvScale, farplane / uvScale + vOffset).next();
		bufferBuilder.vertex(matrix, bounds.x, farplane, bounds.w + 0.01f)
				.texture((time % uvScale) / uvScale, farplane / uvScale + vOffset).next();
		bufferBuilder.vertex(matrix, bounds.x, -farplane, bounds.w + 0.01f)
				.texture((time % uvScale) / uvScale, -farplane / uvScale + vOffset).next();
		bufferBuilder.vertex(matrix, bounds.z, -farplane, bounds.w + 0.01f)
				.texture((time % uvScale + bounds.z - bounds.x) / uvScale, -farplane / uvScale + vOffset).next();
		//east
		bufferBuilder.vertex(matrix, bounds.x - 0.01f, -farplane, bounds.y)
				.texture((time % uvScale + bounds.y - bounds.w) / uvScale, -farplane / uvScale + vOffset).next();
		bufferBuilder.vertex(matrix, bounds.x - 0.01f, -farplane, bounds.w)
				.texture((time % uvScale) / uvScale, -farplane / uvScale + vOffset).next();
		bufferBuilder.vertex(matrix, bounds.x - 0.01f, farplane, bounds.w)
				.texture((time % uvScale) / uvScale, farplane / uvScale + vOffset).next();
		bufferBuilder.vertex(matrix, bounds.x - 0.01f, farplane, bounds.y)
				.texture((time % uvScale + bounds.y - bounds.w) / uvScale, farplane / uvScale + vOffset).next();
		//west
		bufferBuilder.vertex(matrix, bounds.z + 0.01f, farplane, bounds.w)
				.texture((time % uvScale + bounds.y - bounds.w) / uvScale, farplane / uvScale + vOffset).next();
		bufferBuilder.vertex(matrix, bounds.z + 0.01f, farplane, bounds.y)
				.texture((time % uvScale) / uvScale, farplane / uvScale + vOffset).next();
		bufferBuilder.vertex(matrix, bounds.z + 0.01f, -farplane, bounds.y)
				.texture((time % uvScale) / uvScale, -farplane / uvScale + vOffset).next();
		bufferBuilder.vertex(matrix, bounds.z + 0.01f, -farplane, bounds.w)
				.texture((time % uvScale + bounds.y - bounds.w) / uvScale, -farplane / uvScale + vOffset).next();
		
		BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
		RenderSystem.enableCull();
		matrices.pop();
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
	}
}
