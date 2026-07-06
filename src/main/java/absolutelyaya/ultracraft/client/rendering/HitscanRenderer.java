package absolutelyaya.ultracraft.client.rendering;

import absolutelyaya.ultracraft.client.ClientHitscanHandler;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

import java.awt.*;

public class HitscanRenderer
{
	public static void render(ClientHitscanHandler.Hitscan hitscan, MatrixStack matrices, Camera camera, float delta)
	{
		Color col = hitscan.getColor();
		Vec3d camPos = camera.getPos();
		Vec3d from = hitscan.getFrom(delta);
		Vec3d to = hitscan.getTo(delta);
		float girth = Math.max(hitscan.getGirth(), 0f);
		
		if(hitscan.isElectic())
			renderElectricArc(matrices, from, to, camPos, girth, col, RenderLayer.getLightning(), hitscan.getLayers(), (int)(from.getX() * 100f + from.getZ() * 100f));
		else if(hitscan instanceof ClientHitscanHandler.Connector connector)
			renderConnector(matrices, connector, camPos, RenderLayer.getGui(), delta);
		else
			renderRay(matrices, from, to, camPos, girth, col, RenderLayer.getLightning(), hitscan.getLayers());
	}
	
	public static void renderRay(MatrixStack matrices, Vec3d from, Vec3d to, Vec3d camPos, float girth, Color col, RenderLayer layer, int steps)
	{
		RenderSystem.setShader(GameRenderer::getPositionColorProgram);
		RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
		Vec3d dir = to.subtract(from).normalize();
		float dist = (float)from.distanceTo(to);
		float rot = (float)(-Math.atan2(dir.z, dir.x) - Math.toRadians(90));
		matrices.push();
		matrices.translate(from.x, from.y, from.z);
		matrices.translate(-camPos.x, -camPos.y, -camPos.z);
		
		//matrixStack.multiply(Quaternion.fromEulerXyz((float)(Math.atan2(-dir.y, Math.abs(dir.z))), rot, 0f));
		double dx = dir.x;
		double dy = dir.y;
		double dz = dir.z;
		float f = MathHelper.sqrt((float)(dx * dx + dz * dz));
		matrices.multiply(RotationAxis.POSITIVE_Y.rotation(rot));
		matrices.multiply(RotationAxis.POSITIVE_X.rotation((float)(-Math.atan2(f, dy) - Math.toRadians(90))));
		VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEffectVertexConsumers();
		VertexConsumer consumer = immediate.getBuffer(layer);
		
		Matrix4f matrix = matrices.peek().getPositionMatrix();
		for (int i = 1; i <= steps; i++)
		{
			float g = girth / i;
			consumer.vertex(matrix, -g / 2, -g / 2, dist).color(col.getRed(), col.getGreen(), col.getBlue(), 255).next();
			consumer.vertex(matrix, -g / 2, g / 2, dist).color(col.getRed(), col.getGreen(), col.getBlue(), 255).next();
			consumer.vertex(matrix, -g / 2, g / 2, -0.0f).color(col.getRed(), col.getGreen(), col.getBlue(), 255).next();
			consumer.vertex(matrix, -g / 2, -g / 2, -0.0f).color(col.getRed(), col.getGreen(), col.getBlue(), 255).next();
			
			consumer.vertex(matrix, g / 2, -g / 2, -0.0f).color(col.getRed(), col.getGreen(), col.getBlue(), 255).next();
			consumer.vertex(matrix, g / 2, -g / 2, dist).color(col.getRed(), col.getGreen(), col.getBlue(), 255).next();
			consumer.vertex(matrix, -g / 2, -g / 2, dist).color(col.getRed(), col.getGreen(), col.getBlue(), 255).next();
			consumer.vertex(matrix, -g / 2, -g / 2, -0.0f).color(col.getRed(), col.getGreen(), col.getBlue(), 255).next();
			
			consumer.vertex(matrix, g / 2, -g / 2, -0.0f).color(col.getRed(), col.getGreen(), col.getBlue(), 255).next();
			consumer.vertex(matrix, g / 2, g / 2, -0.0f).color(col.getRed(), col.getGreen(), col.getBlue(), 255).next();
			consumer.vertex(matrix, g / 2, g / 2, dist).color(col.getRed(), col.getGreen(), col.getBlue(), 255).next();
			consumer.vertex(matrix, g / 2, -g / 2, dist).color(col.getRed(), col.getGreen(), col.getBlue(), 255).next();
			
			consumer.vertex(matrix, -g / 2, g / 2, -0.0f).color(col.getRed(), col.getGreen(), col.getBlue(), 255).next();
			consumer.vertex(matrix, -g / 2, g / 2, dist).color(col.getRed(), col.getGreen(), col.getBlue(), 255).next();
			consumer.vertex(matrix, g / 2, g / 2, dist).color(col.getRed(), col.getGreen(), col.getBlue(), 255).next();
			consumer.vertex(matrix, g / 2, g / 2, -0.0f).color(col.getRed(), col.getGreen(), col.getBlue(), 255).next();
		}
		matrices.pop();
	}
	
	public static void renderElectricArc(MatrixStack matrices, Vec3d from, Vec3d to, Vec3d camPos, float girth, Color col, RenderLayer layer, int steps, int seed)
	{
		Random rand = Random.create(seed);
		float distance = (float)from.distanceTo(to);
		Vec3d dir = to.subtract(from).normalize();
		Vec3d lastOffset = Vec3d.ZERO;
		for (float i = 0; i < distance / 0.5f; i += 0.5f)
		{
			Vec3d segmentFrom = from.lerp(to, i / distance), segmentTo = from.lerp(to, Math.min(i + 0.5, distance) / distance);
			Vec3d newOffset = Vec3d.ZERO.addRandom(rand, 0.2f);
			renderRay(matrices, segmentFrom.add(lastOffset), segmentTo.add(newOffset), camPos, girth, col, layer, steps);
			while(rand.nextFloat() < 0.2f)
			{
				Vec3d branchTo = segmentFrom.add(getBranchTo(rand, dir, 1.21f));
				renderRay(matrices, segmentFrom.add(lastOffset), branchTo, camPos, girth / (1 + rand.nextFloat()), col, layer, steps);
				Vec3d branchDir = branchTo.subtract(segmentFrom.add(lastOffset)).normalize();
				while(rand.nextFloat() < 0.3f)
					renderRay(matrices, branchTo, branchTo.add(getBranchTo(rand, branchDir, 0.8f)), camPos, girth / (1.5f + rand.nextFloat()), col, layer, steps);
			}
			lastOffset = newOffset;
		}
	}
	
	static Vec3d getBranchTo(Random rand, Vec3d dir, float diversion)
	{
		Vec3d branchTo = new Vec3d(0f, 0f, -(0.75f + rand.nextFloat()));
		branchTo = branchTo.rotateX(-(float)(Math.atan2(dir.y, Math.sqrt(1 - dir.y * dir.y))) + (rand.nextFloat() * 0.7f - 0.35f) * diversion);
		branchTo = branchTo.rotateY((float)(-Math.atan2(dir.z, dir.x) - Math.toRadians(90f)) + (rand.nextFloat() * 0.7f - 0.35f) * diversion);
		return branchTo;
	}
	
	public static void renderConnector(MatrixStack matrices, ClientHitscanHandler.Connector connector, Vec3d camPos, RenderLayer layer, float delta)
	{
		Color col = connector.getColor();
		Vec3d from = connector.getFrom(delta);
		Vec3d to = connector.getTo(delta);
		float girth = Math.max(connector.getGirth(), 0f);
		int steps = connector.getLayers();
		
		renderRay(matrices, from, to, camPos, girth, col, layer, steps);
		
		if(connector.getSpark() != null && connector.getSparkPos().get() != -1f)
		{
			float fullSparkPos = connector.getSparkPos().get() + delta * 0.05f;
			float partialSparkPos = fullSparkPos % 1f;
			Vec3d globalSparkPos = from.lerp(to, partialSparkPos).subtract(camPos);
			
			VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEffectVertexConsumers();
			VertexConsumer consumer = immediate.getBuffer(RenderLayer.getEntityTranslucent(connector.getSpark()));
			matrices.push();
			matrices.translate(globalSparkPos.x, globalSparkPos.y, globalSparkPos.z);
			Quaternionf camRot = new Quaternionf(MinecraftClient.getInstance().gameRenderer.getCamera().getRotation());
			matrices.multiply(camRot.rotateY((float)Math.toRadians(180f)));
			float rot = (float)Math.floor(fullSparkPos) * 90f;
			matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(rot));
			Matrix4f matrix = matrices.peek().getPositionMatrix();
			Matrix3f normalMatrix = matrices.peek().getNormalMatrix();
			float sin = -(float)Math.sin(Math.toRadians(rot + 90f)), cos = -(float)Math.cos(Math.toRadians(rot + 90f));
			consumer.vertex(matrix, -0.33f, -0.33f, 0f).color(255, 255, 255, 255).texture(0f, 1f)
					.overlay(OverlayTexture.DEFAULT_UV).light(LightmapTextureManager.MAX_LIGHT_COORDINATE).normal(normalMatrix, sin, cos, 0f).next();
			consumer.vertex(matrix, 0.33f, -0.33f, 0f).color(255, 255, 255, 255).texture(1f, 1f)
					.overlay(OverlayTexture.DEFAULT_UV).light(LightmapTextureManager.MAX_LIGHT_COORDINATE).normal(normalMatrix, sin, cos, 0f).next();
			consumer.vertex(matrix, 0.33f, 0.33f, 0f).color(255, 255, 255, 255).texture(1f, 0f)
					.overlay(OverlayTexture.DEFAULT_UV).light(LightmapTextureManager.MAX_LIGHT_COORDINATE).normal(normalMatrix, sin, cos, 0f).next();
			consumer.vertex(matrix, -0.33f, 0.33f, 0f).color(255, 255, 255, 255).texture(0f, 0f)
					.overlay(OverlayTexture.DEFAULT_UV).light(LightmapTextureManager.MAX_LIGHT_COORDINATE).normal(normalMatrix, sin, cos, 0f).next();
			matrices.pop();
		}
	}
}
