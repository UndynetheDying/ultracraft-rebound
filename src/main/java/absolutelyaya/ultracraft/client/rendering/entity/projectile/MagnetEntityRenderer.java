package absolutelyaya.ultracraft.client.rendering.entity.projectile;

import absolutelyaya.ultracraft.Ultracraft;
import absolutelyaya.ultracraft.client.rendering.entity.feature.gecko.MagnetEmissiveLayer;
import absolutelyaya.ultracraft.entity.projectile.MagnetEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import org.joml.AxisAngle4f;
import org.joml.Quaternionf;
import mod.azure.azurelib.renderer.GeoEntityRenderer;

public class MagnetEntityRenderer extends GeoEntityRenderer<MagnetEntity>
{
	static final Identifier GREEN = Ultracraft.identifier("textures/entity/magnet.png");
	static final Identifier YELLOW = Ultracraft.identifier("textures/entity/magnet1.png");
	static final Identifier RED = Ultracraft.identifier("textures/entity/magnet2.png");
	
	final Random random;
	
	public MagnetEntityRenderer(EntityRendererFactory.Context context)
	{
		super(context, new MagnetEntityModel());
		addRenderLayer(new MagnetEmissiveLayer(this));
		random = Random.create();
	}
	
	@Override
	public Identifier getTexture(MagnetEntity entity)
	{
		float strain = entity.getStrain();
		if(strain > 1f)
			return RED;
		else if(strain > 0.5f)
			return YELLOW;
		return GREEN;
	}
	
	@Override
	public void render(MagnetEntity magnet, float yaw, float delta, MatrixStack matrices, VertexConsumerProvider vertexConsumerProvider, int i)
	{
		matrices.push();
		matrices.multiply(new Quaternionf(new AxisAngle4f((magnet.getYaw(delta) + 182.5f) * MathHelper.RADIANS_PER_DEGREE, 0f, 1f, 0f)));
		matrices.multiply(new Quaternionf(new AxisAngle4f((magnet.getPitch(delta) + 90f) * MathHelper.RADIANS_PER_DEGREE, 1f, 0f, 0f)));
		float f = magnet.getShaking() / 20f;
		Vec3d v = Vec3d.ZERO.addRandom(random, f);
		matrices.translate(v.x, v.y, v.z);
		super.render(magnet, yaw, delta, matrices, vertexConsumerProvider, i);
		matrices.pop();
		magnet.setFlash(Math.max(magnet.getFlash() - delta / 10f, 0));
	}
}
