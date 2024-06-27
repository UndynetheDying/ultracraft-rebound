package absolutelyaya.ultracraft.particle;

import net.minecraft.client.particle.*;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.Random;

public class ButterflyParticle extends SpriteBillboardParticle
{
	Vector3f targetMoveDir;
	int ageOffset;
	
	public ButterflyParticle(ClientWorld clientWorld, double d, double e, double f)
	{
		super(clientWorld, d, e, f);
		ageOffset = random.nextInt(20);
	}
	
	@Override
	public void buildGeometry(VertexConsumer vertexConsumer, Camera camera, float tickDelta)
	{
		Vec3d camPos = camera.getPos();
		float x = (float) (MathHelper.lerp(tickDelta, this.prevPosX, this.x) - camPos.x);
		float y = (float) (MathHelper.lerp(tickDelta, this.prevPosY, this.y) - camPos.y);
		float z = (float) (MathHelper.lerp(tickDelta, this.prevPosZ, this.z) - camPos.z);
		Vector3f dir = new Vector3f((float)velocityX, (float)velocityY, (float)velocityZ).normalize();
		
		for (int i = 0; i < 2; i++)
		{
			Vector3f[] verts = new Vector3f[]{
					new Vector3f(-2f, 0f, -2f),
					new Vector3f(-2f, 0f, 2f),
					new Vector3f(0f, 0f, 2f),
					new Vector3f(0f, 0f, -2f)
			};
			for (Vector3f vert : verts)
			{
				vert.add(i * 2, 0, 0).div(2f)
						.rotate(RotationAxis.POSITIVE_Z.rotationDegrees((float)(Math.sin((age + ageOffset + tickDelta) / (onGround ? 5f : 1f)) * (onGround ? 10f : 35f) - (onGround ? 12.5 : 0)) * (i == 0 ? 1f : -1f)));
				if(!onGround)
					vert.rotate(RotationAxis.POSITIVE_X.rotation((float)(Math.atan2(dir.y, Math.sqrt(1 - dir.y * dir.y)))));
				vert.rotate(RotationAxis.POSITIVE_Y.rotation((float)(-Math.atan2(dir.z, dir.x) - Math.toRadians(90f))));
				vert.mul(scale).add(x, y, z);
			}
			float half = (getMaxU() - getMinU()) / 2f;
			Vector2f[] uvs = new Vector2f[]{
					new Vector2f(getMinU() + half, getMaxV()),
					new Vector2f(getMinU(), getMinV())
			};
			for (Vector2f uv : uvs) uv.add(half * i, 0);
			drawPlane(vertexConsumer, verts, uvs, red, green, blue, alpha, getBrightness(tickDelta));
		}
		
		if(age <= 10)
			alpha = Math.min((age + tickDelta) / 10f, 1);
		else if(maxAge - age <= 22)
			alpha = MathHelper.clamp((maxAge - age - 2f + tickDelta) / 20f, 0f, 1f);
	}
	
	@Override
	public void tick()
	{
		super.tick();
		if(age % 50 == 0 && random.nextFloat() <= 0.66f && !onGround)
			targetMoveDir = new Vector3f(random.nextFloat() - 0.5f, random.nextFloat() - 0.5f, random.nextFloat() - 0.5f).mul(0.2f);
		Vector3f vel = new Vector3f((float)velocityX, (float)velocityY, (float)velocityZ).lerp(targetMoveDir, 0.02f);
		velocityX = vel.x;
		velocityY = vel.y;
		velocityZ = vel.z;
	}
	
	void drawPlane(VertexConsumer vertexConsumer, Vector3f[] verts, Vector2f[] uvs, float r, float g, float b, float a, int brightness)
	{
		vertexConsumer.vertex(verts[0].x, verts[0].y, verts[0].z).texture(uvs[1].x, uvs[1].y).color(r, g, b, a).light(brightness).next();
		vertexConsumer.vertex(verts[1].x, verts[1].y, verts[1].z).texture(uvs[1].x, uvs[0].y).color(r, g, b, a).light(brightness).next();
		vertexConsumer.vertex(verts[2].x, verts[2].y, verts[2].z).texture(uvs[0].x, uvs[0].y).color(r, g, b, a).light(brightness).next();
		vertexConsumer.vertex(verts[3].x, verts[3].y, verts[3].z).texture(uvs[0].x, uvs[1].y).color(r, g, b, a).light(brightness).next();
		vertexConsumer.vertex(verts[3].x, verts[3].y, verts[3].z).texture(uvs[0].x, uvs[1].y).color(r, g, b, a).light(brightness).next();
		vertexConsumer.vertex(verts[2].x, verts[2].y, verts[2].z).texture(uvs[0].x, uvs[0].y).color(r, g, b, a).light(brightness).next();
		vertexConsumer.vertex(verts[1].x, verts[1].y, verts[1].z).texture(uvs[1].x, uvs[0].y).color(r, g, b, a).light(brightness).next();
		vertexConsumer.vertex(verts[0].x, verts[0].y, verts[0].z).texture(uvs[1].x, uvs[1].y).color(r, g, b, a).light(brightness).next();
	}
	
	@Override
	public ParticleTextureSheet getType()
	{
		return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
	}
	
	public static class Factory implements ParticleFactory<DefaultParticleType>
	{
		protected final SpriteProvider spriteProvider;
		
		public Factory(SpriteProvider spriteProvider)
		{
			this.spriteProvider = spriteProvider;
		}
		
		@Nullable
		@Override
		public Particle createParticle(DefaultParticleType parameters, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ)
		{
			Random r = new Random();
			ButterflyParticle butterfly = new ButterflyParticle(world, x, y, z);
			butterfly.setSprite(spriteProvider);
			butterfly.setVelocity((r.nextFloat() - 0.5f) * 0.1f, (r.nextFloat() - 0.5f) * 0.1f, (r.nextFloat() - 0.5f) * 0.1f);
			butterfly.targetMoveDir = new Vector3f((r.nextFloat() - 0.5f) * 0.1f, (r.nextFloat() - 0.5f) * 0.1f, (r.nextFloat() - 0.5f) * 0.1f);
			butterfly.setMaxAge(400);
			butterfly.scale = 0.2f + r.nextFloat() * 0.1f;
			butterfly.alpha = 0f;
			return butterfly;
		}
	}
}
