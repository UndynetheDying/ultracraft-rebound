package absolutelyaya.ultracraft.client.rendering;

import absolutelyaya.ultracraft.components.UltraComponents;
import absolutelyaya.ultracraft.components.player.IStyleComponent;
import absolutelyaya.ultracraft.components.player.IWingedPlayerComponent;
import absolutelyaya.ultracraft.cybergrind.CybergrindData;
import absolutelyaya.ultracraft.util.ColorUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Vector3f;
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
		if(rankUpFlash > 0f)
			col = col.lerp(new Vec3d(1f, 0f, 0f), rankUpFlash);
		if(rankDownFlash > 0f)
			col = col.lerp(Vec3d.ZERO, rankDownFlash);
		//RenderSystem.setShaderColor((float)col.x, (float)col.y, (float)col.z, );
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
		Matrix4f matrix = matrices.peek().getPositionMatrix();
		float uvScale = 4f;
		for (int i = -32; i < 64; i++)
		{
			bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
			float alpha;
			if(rank == 7)
			{
				col = new Vec3d(ColorUtil.hsv2rgb(new Vector3f(-time / 2f + i / 10f, 1f, 1f)));
				alpha = 0.75f + (float)Math.sin(time * 3f) * 0.25f;
			}
			else
				alpha = 0.6f + (float)Math.sin((time - i * 2f) / 5f + time * 2f) * 0.4f;
			if(rankUpFlash > 0f)
				col = col.lerp(new Vec3d(1f, 0f, 0f), rankUpFlash);
			RenderSystem.setShaderColor((float)col.x, (float)col.y, (float)col.z, alpha);
			float segmentHeight = 4f;
			float bottom = segmentHeight * (i + 15), top = bottom + segmentHeight;
			//north
			bufferBuilder.vertex(matrix, bounds.x, bottom, bounds.y - 0.01f)
					.texture((time % uvScale + bounds.z - bounds.x) / uvScale, bottom / uvScale + vOffset).next();
			bufferBuilder.vertex(matrix, bounds.z, bottom, bounds.y - 0.01f)
					.texture((time % uvScale) / uvScale, bottom / uvScale + vOffset).next();
			bufferBuilder.vertex(matrix, bounds.z, top, bounds.y - 0.01f)
					.texture((time % uvScale) / uvScale, top / uvScale + vOffset).next();
			bufferBuilder.vertex(matrix, bounds.x, top, bounds.y - 0.01f)
					.texture((time % uvScale + bounds.z - bounds.x) / uvScale, top / uvScale + vOffset).next();
			//south
			bufferBuilder.vertex(matrix, bounds.z, top, bounds.w + 0.01f)
					.texture((time % uvScale + bounds.z - bounds.x) / uvScale, top / uvScale + vOffset).next();
			bufferBuilder.vertex(matrix, bounds.x, top, bounds.w + 0.01f)
					.texture((time % uvScale) / uvScale, top / uvScale + vOffset).next();
			bufferBuilder.vertex(matrix, bounds.x, bottom, bounds.w + 0.01f)
					.texture((time % uvScale) / uvScale, bottom / uvScale + vOffset).next();
			bufferBuilder.vertex(matrix, bounds.z, bottom, bounds.w + 0.01f)
					.texture((time % uvScale + bounds.z - bounds.x) / uvScale, bottom / uvScale + vOffset).next();
			//east
			bufferBuilder.vertex(matrix, bounds.x - 0.01f, bottom, bounds.y)
					.texture((time % uvScale + bounds.y - bounds.w) / uvScale, bottom / uvScale + vOffset).next();
			bufferBuilder.vertex(matrix, bounds.x - 0.01f, bottom, bounds.w)
					.texture((time % uvScale) / uvScale, bottom / uvScale + vOffset).next();
			bufferBuilder.vertex(matrix, bounds.x - 0.01f, top, bounds.w)
					.texture((time % uvScale) / uvScale, top / uvScale + vOffset).next();
			bufferBuilder.vertex(matrix, bounds.x - 0.01f, top, bounds.y)
					.texture((time % uvScale + bounds.y - bounds.w) / uvScale, top / uvScale + vOffset).next();
			//west
			bufferBuilder.vertex(matrix, bounds.z + 0.01f, top, bounds.w)
					.texture((time % uvScale + bounds.y - bounds.w) / uvScale, top / uvScale + vOffset).next();
			bufferBuilder.vertex(matrix, bounds.z + 0.01f, top, bounds.y)
					.texture((time % uvScale) / uvScale, top / uvScale + vOffset).next();
			bufferBuilder.vertex(matrix, bounds.z + 0.01f, bottom, bounds.y)
					.texture((time % uvScale) / uvScale, bottom / uvScale + vOffset).next();
			bufferBuilder.vertex(matrix, bounds.z + 0.01f, bottom, bounds.w)
					.texture((time % uvScale + bounds.y - bounds.w) / uvScale, bottom / uvScale + vOffset).next();
			BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
		}
		
		RenderSystem.enableCull();
		matrices.pop();
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
	}
}
