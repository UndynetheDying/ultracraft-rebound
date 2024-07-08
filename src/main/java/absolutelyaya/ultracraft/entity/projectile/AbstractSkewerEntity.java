package absolutelyaya.ultracraft.entity.projectile;

import absolutelyaya.ultracraft.accessor.ProjectileEntityAccessor;
import absolutelyaya.ultracraft.damage.DamageSources;
import absolutelyaya.ultracraft.damage.DamageTypeTags;
import absolutelyaya.ultracraft.registry.SoundRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public abstract class AbstractSkewerEntity extends PersistentProjectileEntity
{
	protected static final TrackedData<Float> GROUND_TIME = DataTracker.registerData(AbstractSkewerEntity.class, TrackedDataHandlerRegistry.FLOAT);
	protected static final TrackedData<Float> IMPACT_YAW = DataTracker.registerData(AbstractSkewerEntity.class, TrackedDataHandlerRegistry.FLOAT);
	protected static final TrackedData<Float> IMPACT_PITCH = DataTracker.registerData(AbstractSkewerEntity.class, TrackedDataHandlerRegistry.FLOAT);
	protected static final TrackedData<Integer> HEALTH = DataTracker.registerData(AbstractSkewerEntity.class, TrackedDataHandlerRegistry.INTEGER);
	protected static final TrackedData<Integer> SHAKE = DataTracker.registerData(AbstractSkewerEntity.class, TrackedDataHandlerRegistry.INTEGER);
	protected static final TrackedData<Integer> VICTIM = DataTracker.registerData(AbstractSkewerEntity.class, TrackedDataHandlerRegistry.INTEGER);
	
	protected int unmovingTicks;
	
	protected AbstractSkewerEntity(EntityType<? extends PersistentProjectileEntity> entityType, World world)
	{
		super(entityType, world);
		if(this instanceof ProjectileEntityAccessor proj)
			proj.setIsParriable(() -> !(isInGround() || getVictim() != null));
	}
	
	@Override
	protected void initDataTracker()
	{
		super.initDataTracker();
		dataTracker.startTracking(GROUND_TIME, 0f);
		dataTracker.startTracking(IMPACT_YAW, 0f);
		dataTracker.startTracking(IMPACT_PITCH, 0f);
		dataTracker.startTracking(HEALTH, 2);
		dataTracker.startTracking(SHAKE, 0);
		dataTracker.startTracking(VICTIM, -1);
	}
	
	@Override
	public void tick()
	{
		super.tick();
		if(dataTracker.get(GROUND_TIME) > 240)
			despawn();
		if(getVelocity().equals(Vec3d.ZERO) && !inGround && getVictim() == null)
			unmovingTicks++;
		else if(unmovingTicks > 0)
			unmovingTicks = 0;
		if(unmovingTicks > 20)
			despawn();
		if(getVictim() != null)
		{
			if(!getVictim().isAlive())
			{
				dataTracker.set(VICTIM, -1);
				return;
			}
			setVelocity(Vec3d.ZERO);
			lastRenderX = prevX = getX();
			lastRenderY = prevY = getY();
			lastRenderZ = prevZ = getZ();
			setPosition(getVictim().getPos().add(0f, getVictim().getHeight() / 2, 0f));
			setYaw(prevYaw = dataTracker.get(IMPACT_YAW));
			setPitch(prevPitch = dataTracker.get(IMPACT_PITCH));
		}
		if(isRemoved() || (!isInGround() && getVictim() == null))
			return;
		dataTracker.set(GROUND_TIME, dataTracker.get(GROUND_TIME) + 1f);
		if(dataTracker.get(SHAKE) > 0)
			dataTracker.set(SHAKE, dataTracker.get(SHAKE) - 1);
	}
	
	protected void despawn()
	{
		getWorld().sendEntityStatus(this, EntityStatuses.PLAY_DEATH_SOUND_OR_ADD_PROJECTILE_HIT_PARTICLES);
		if(!getWorld().isClient())
			discard();
	}
	
	public boolean isInGround()
	{
		return inGround;
	}
	
	@Override
	public float getPitch(float tickDelta)
	{
		if(dataTracker.get(IMPACT_PITCH) != 0)
			return dataTracker.get(IMPACT_PITCH);
		return super.getPitch(tickDelta);
	}
	
	@Override
	public float getYaw()
	{
		if(dataTracker.get(IMPACT_YAW) != 0)
			return dataTracker.get(IMPACT_YAW);
		return super.getYaw();
	}
	
	public Entity getVictim()
	{
		int id = dataTracker.get(VICTIM);
		if(id == -1)
			return null;
		return getWorld().getEntityById(id);
	}
	
	@Override
	public void handleStatus(byte status)
	{
		if (status != EntityStatuses.PLAY_DEATH_SOUND_OR_ADD_PROJECTILE_HIT_PARTICLES)
			return;
		for (int i = 0; i < 16; i++)
		{
			Vec3d pos = getPos().addRandom(random, 0.1f);
			Vec3d vel = Vec3d.ZERO.addRandom(random, 0.25f);
			getWorld().addParticle(new ItemStackParticleEffect(ParticleTypes.ITEM, asItemStack()), pos.x, pos.y, pos.z, vel.x, vel.y, vel.z);
		}
		playSound(SoundRegistry.SKEWER_BREAK, 1f, 0.9f + random.nextFloat() * 0.2f);
	}
	
	@Override
	protected SoundEvent getHitSound()
	{
		return SoundRegistry.SKEWER_HIT_GROUND;
	}
	
	@Override
	protected void onEntityHit(EntityHitResult entityHitResult)
	{
		if(getVictim() != null)
			return;
		Entity entity = entityHitResult.getEntity();
		if(entity.isPartOf(owner))
			return;
		dataTracker.set(VICTIM, entity.getId());
		dataTracker.set(IMPACT_YAW, getYaw());
		dataTracker.set(IMPACT_PITCH, getPitch());
		if(this instanceof ProjectileEntityAccessor proj && proj.isParried())
			proj.onParriedCollision(entityHitResult);
	}
	
	@Override
	protected void onBlockHit(BlockHitResult blockHitResult)
	{
		if(getVictim() != null)
			return;
		super.onBlockHit(blockHitResult);
	}
	
	@Override
	public boolean damage(DamageSource source, float amount)
	{
		float mult = source.isOf(DamageSources.KNUCKLE_PUNCH) ? 2f : 1f;
		dataTracker.set(SHAKE, dataTracker.get(SHAKE) + (int)(10 * mult));
		if((isInGround() || getVictim() != null) && (source.isIn(DamageTypeTags.MELEE) || source.isIn(DamageTypeTags.PUNCH)))
		{
			dataTracker.set(HEALTH, dataTracker.get(HEALTH) - (int)(1 * mult));
			if(dataTracker.get(HEALTH) <= 0)
				onPunchBroken();
			return true;
		}
		return super.damage(source, amount);
	}
	
	abstract void onPunchBroken();
	
	@Override
	public float getTargetingMargin()
	{
		return 0.3f;
	}
	
	public int getShaking()
	{
		return dataTracker.get(SHAKE);
	}
}
