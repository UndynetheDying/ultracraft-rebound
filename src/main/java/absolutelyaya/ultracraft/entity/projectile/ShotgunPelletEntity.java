package absolutelyaya.ultracraft.entity.projectile;

import absolutelyaya.ultracraft.ExplosionHandler;
import absolutelyaya.ultracraft.components.UltraComponents;
import absolutelyaya.ultracraft.Ultracraft;
import absolutelyaya.ultracraft.accessor.ProjectileEntityAccessor;
import absolutelyaya.ultracraft.client.UltracraftClient;
import absolutelyaya.ultracraft.config.ServerConfig;
import absolutelyaya.ultracraft.damage.DamageSources;
import absolutelyaya.ultracraft.data.StyleBonusManager;
import absolutelyaya.ultracraft.registry.EntityRegistry;
import absolutelyaya.ultracraft.registry.ItemRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class ShotgunPelletEntity extends HellBulletEntity implements ProjectileEntityAccessor
{
	BlockPos hitPos = null;
	boolean chosenOne;
	float damage = 0.5f;
	
	public ShotgunPelletEntity(EntityType<? extends ThrownItemEntity> entityType, World world)
	{
		super(entityType, world);
	}
	
	protected ShotgunPelletEntity(LivingEntity owner, World world)
	{
		super(EntityRegistry.SHOTGUN_PELLET, owner, world);
	}
	
	public static ShotgunPelletEntity spawn(LivingEntity owner, World world, boolean chosen)
	{
		ShotgunPelletEntity pellet = new ShotgunPelletEntity(owner, world);
		pellet.chosenOne = chosen;
		return pellet;
	}
	
	@Override
	protected ItemStack getItem()
	{
		return ItemRegistry.CERBERUS_BALL.getDefaultStack();
	}
	
	@Override
	public void tick()
	{
		super.tick();
		if(age == 1 && !isRemoved())
			if(getWorld().isClient)
				UltracraftClient.TRAIL_RENDERER.createTrail(uuid, this::getPoint, new Vector4f(1f, 1f, 0f, 0.4f), 5);
		if(age == 5 && chosenOne) //after projboost time window, set speed to normal pellets
			setVelocity(getVelocity().normalize().multiply(1.5f));
	}
	
	Pair<Vector3f, Vector3f> getPoint()
	{
		float yVel = (float)getVelocity().normalize().y;
		float xAngle = (float)Math.toRadians(yVel * 90);
		float yAngle = (float)Math.toRadians(Math.abs(yVel) * MinecraftClient.getInstance().gameRenderer.getCamera().getYaw());
		Vector3f left =	getPos().toVector3f().add(new Vector3f(0f, 0.1f, 0f).rotateX(xAngle).rotateY(yAngle));
		Vector3f right = getPos().toVector3f().add(new Vector3f(0f, -0.1f, 0f).rotateX(xAngle).rotateY(yAngle));
		return new Pair<>(left, right);
	}
	
	@Override
	protected int getMaxAge()
	{
		return 200;
	}
	
	public void increaseDamage(float val)
	{
		damage += val;
	}
	
	@Override
	protected void onEntityHit(EntityHitResult entityHitResult)
	{
		Entity entity = entityHitResult.getEntity();
		boolean parried = ((ProjectileEntityAccessor)this).isParried();
		if(!entity.getClass().equals(ignore) && !isOwner(entity))
			entity.damage(DamageSources.get(getWorld(), DamageSources.SHOTGUN, getOwner()),
					damage * ServerConfig.INSTANCE.shotgunDamage.getValue());
		if(parried)
			onParriedCollision(entityHitResult);
	}
	
	@Override
	protected void onBlockHit(BlockHitResult blockHitResult)
	{
		super.onBlockHit(blockHitResult);
		hitPos = blockHitResult.getBlockPos();
	}
	
	public void handleStatus(byte status)
	{
		if (status == 3)
		{
			if(hitPos != null)
				getWorld().addParticle(new BlockStateParticleEffect(ParticleTypes.BLOCK, getWorld().getBlockState(hitPos)), true,
						getX(), getY(), getZ(), 0, 0, 0);
		}
	}
	
	@Override
	public void onParriedCollision(HitResult hitResult)
	{
		Vec3d pos = hitResult.getPos();
		ExplosionHandler.explosion(null, getWorld(), pos, DamageSources.get(getWorld(), DamageSources.PROJBOOST, parrier), 7f, 4.6f, 3f, true);
		if(hitResult.getType().equals(HitResult.Type.ENTITY) && isParried())
			UltraComponents.STYLE.get(parrier).styleBonusGet(StyleBonusManager.getBonuses().get(Ultracraft.identifier("projboost")));
	}
	
	@Override
	public void onRemoved()
	{
		super.onRemoved();
		if(getWorld().isClient)
			UltracraftClient.TRAIL_RENDERER.removeTrail(uuid);
	}
	
	@Override
	protected void onCollision(HitResult hitResult)
	{
		if(isRemoved())
			return;
		if(getWorld().isClient)
			UltracraftClient.TRAIL_RENDERER.removeTrail(uuid);
		super.onCollision(hitResult);
	}
	
	@Override
	public boolean isBoostable()
	{
		return super.isBoostable() && chosenOne;
	}
	
	@Override
	public void onKnockedBackbyExplosion(Entity exploder)
	{
	
	}
}
