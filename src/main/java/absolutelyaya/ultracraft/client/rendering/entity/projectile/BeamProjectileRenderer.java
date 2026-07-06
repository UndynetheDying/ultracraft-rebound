package absolutelyaya.ultracraft.client.rendering.entity.projectile;

import absolutelyaya.ultracraft.client.ClientHitscanHandler;
import absolutelyaya.ultracraft.client.rendering.HitscanRenderer;
import absolutelyaya.ultracraft.entity.projectile.BeamProjectileEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

import java.awt.*;

public class BeamProjectileRenderer extends EntityRenderer<BeamProjectileEntity>
{
	public BeamProjectileRenderer(EntityRendererFactory.Context ctx)
	{
		super(ctx);
	}
	
	@Override
	public Identifier getTexture(BeamProjectileEntity entity)
	{
		return null;
	}
	
	@Override
	public void render(BeamProjectileEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light)
	{
		matrices.push();
		Vec3d pos = entity.getLerpedPos(tickDelta);
		matrices.translate(-pos.x, -pos.y, -pos.z);
		ClientHitscanHandler.Hitscan.HitscanType type = ClientHitscanHandler.Hitscan.HitscanType.values()[entity.getHitscanType()];
		HitscanRenderer.renderRay(matrices, entity.getStartPos(), pos, Vec3d.ZERO, type.startGirth,
				new Color(type.color), RenderLayer.getLightning(), 2);
		matrices.pop();
	}
}
