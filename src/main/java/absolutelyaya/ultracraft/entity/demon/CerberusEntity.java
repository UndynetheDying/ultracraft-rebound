package absolutelyaya.ultracraft.entity.demon;

import absolutelyaya.ultracraft.block.mapping.RoomBlockEntity;
import absolutelyaya.ultracraft.components.UltraComponents;
import absolutelyaya.ultracraft.Ultracraft;
import absolutelyaya.ultracraft.accessor.Enrageable;
import absolutelyaya.ultracraft.accessor.IAnimatedEnemy;
import absolutelyaya.ultracraft.accessor.LivingEntityAccessor;
import absolutelyaya.ultracraft.config.ServerConfig;
import absolutelyaya.ultracraft.damage.DamageSources;
import absolutelyaya.ultracraft.data.StyleBonusManager;
import absolutelyaya.ultracraft.entity.AbstractUltraHostileEntity;
import absolutelyaya.ultracraft.entity.IFlagger;
import absolutelyaya.ultracraft.entity.goal.TimedAttackGoal;
import absolutelyaya.ultracraft.entity.other.ShockwaveEntity;
import absolutelyaya.ultracraft.entity.projectile.CerberusBallEntity;
import absolutelyaya.ultracraft.registry.EntityRegistry;
import absolutelyaya.ultracraft.registry.SoundRegistry;
import absolutelyaya.ultracraft.registry.StatusEffectRegistry;
import mod.azure.azurelib.animatable.GeoEntity;
import mod.azure.azurelib.util.AzureLibUtil;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import mod.azure.azurelib.core.animatable.instance.AnimatableInstanceCache;
import mod.azure.azurelib.core.animation.AnimatableManager;
import mod.azure.azurelib.core.animation.AnimationController;
import mod.azure.azurelib.core.animation.AnimationState;
import mod.azure.azurelib.core.animation.RawAnimation;
import mod.azure.azurelib.core.object.PlayState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class CerberusEntity extends AbstractUltraHostileEntity implements GeoEntity, IAnimatedEnemy, Enrageable, IFlagger
{
	protected static final float BOSS_HEALTH = 160f, REGULAR_HEALTH = 44f;
	protected static final TrackedData<Integer> ATTACK_COOLDOWN = DataTracker.registerData(CerberusEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("idle");
	private static final RawAnimation WALK_ANIM = RawAnimation.begin().thenLoop("walk");
	private static final RawAnimation THROW_ANIM = RawAnimation.begin().thenLoop("throw");
	private static final RawAnimation RAM_ANIM = RawAnimation.begin().thenLoop("ram");
	private static final RawAnimation STOMP_ANIM = RawAnimation.begin().thenLoop("stomp");
	private final AnimatableInstanceCache cache = AzureLibUtil.createInstanceCache(this);
	protected static final TrackedData<Boolean> ENRAGED = DataTracker.registerData(CerberusEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	protected static final TrackedData<Boolean> DROP_APPLE = DataTracker.registerData(CerberusEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final byte ANIMATION_IDLE = 0;
	private static final byte ANIMATION_THROW = 1;
	private static final byte ANIMATION_RAM = 2;
	private static final byte ANIMATION_STOMP = 3;
	private final List<BlockPos> listenerRooms = new ArrayList<>();
	
	public CerberusEntity(EntityType<? extends AbstractUltraHostileEntity> entityType, World world)
	{
		super(entityType, world);
		setStepHeight(1f);
		((LivingEntityAccessor)this).setTakePunchKnockbackSupplier(() -> false); //disable knockback
	}
	
	public static DefaultAttributeContainer.Builder getDefaultAttributes()
	{
		return AbstractUltraHostileEntity.createMobAttributes()
					   .add(EntityAttributes.GENERIC_MAX_HEALTH, BOSS_HEALTH)
					   .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3d)
					   .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 6.0d)
					   .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 64.0d)
					   .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 1.0d);
	}
	
	@Override
	protected void initGoals()
	{
		targetSelector.add(0, new StepOnMeUwUGoal(this));
		targetSelector.add(0, new RamAttackGoal(this));
		targetSelector.add(0, new ThrowAttackGoal(this));
		targetSelector.add(1, new ApproachTargetGoal(this));
		
		targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
	}
	
	@Override
	protected void initDataTracker()
	{
		super.initDataTracker();
		dataTracker.startTracking(ATTACK_COOLDOWN, 10);
		dataTracker.startTracking(ENRAGED, false);
		dataTracker.startTracking(DROP_APPLE, false);
	}
	
	@Override
	public void onTrackedDataSet(TrackedData<?> data)
	{
		if(data.equals(BOSS))
		{
			float health = getTrueMaxHealth();
			if(getHealth() != health)
				setHealth(health);
		}
	}
	
	@Override
	public @Nullable EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason,
										   @Nullable EntityData entityData, @Nullable NbtCompound entityNbt)
	{
		onTrackedDataSet(BOSS);
		return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
	}
	
	public static CerberusEntity spawnAsBoss(World world, Vec3d pos, boolean halfHealth)
	{
		CerberusEntity cerb = new CerberusEntity(EntityRegistry.CERBERUS, world);
		cerb.setPosition(pos);
		cerb.dataTracker.set(BOSS, true);
		if(halfHealth)
			cerb.setHealth(cerb.getTrueMaxHealth() / 2f);
		world.spawnEntity(cerb);
		return cerb;
	}
	
	private <E extends GeoEntity> PlayState predicate(AnimationState<E> event)
	{
		byte anim = dataTracker.get(ANIMATION);
		AnimationController<?> controller = event.getController();
		
		controller.setAnimationSpeed(getAnimSpeedMult());
		switch (anim)
		{
			case ANIMATION_IDLE -> controller.setAnimation(event.isMoving() ? WALK_ANIM : IDLE_ANIM);
			case ANIMATION_THROW -> controller.setAnimation(THROW_ANIM);
			case ANIMATION_RAM -> controller.setAnimation(RAM_ANIM);
			case ANIMATION_STOMP -> controller.setAnimation(STOMP_ANIM);
		}
		return PlayState.CONTINUE;
	}
	
	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar)
	{
		controllerRegistrar.add( new AnimationController<>(this, "controller", 2, this::predicate));
	}
	
	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache()
	{
		return cache;
	}
	
	@Override
	public void setAnimation(byte id)
	{
		dataTracker.set(ANIMATION, id);
	}
	
	@Override
	protected double getTeleportParticleSize()
	{
		return 2.5;
	}
	
	public void enrage()
	{
		dataTracker.set(ENRAGED, true);
		playSound(SoundRegistry.GENERIC_ENRAGE, 1.5f, 0.9f);
		getWorld().getEntitiesByType(TypeFilter.instanceOf(PlayerEntity.class), getBoundingBox().expand(32), i -> true)
				.forEach(p -> UltraComponents.STYLE.get(p).styleBonusGet(StyleBonusManager.getBonuses().get(Ultracraft.identifier("enrage"))));
	}
	
	public boolean isEnraged()
	{
		return hasStatusEffect(StatusEffectRegistry.ENRAGED);
	}
	
	public boolean shouldBeEnraged()
	{
		return dataTracker.get(ENRAGED);
	}
	
	@Override
	public Vec3d getEnrageFeatureSize()
	{
		return new Vec3d(2.2f, 2.2f, 2.2f);
	}
	
	@Override
	public Vec3d getEnragedFeatureOffset()
	{
		return new Vec3d(0f, 1.8f, 0f);
	}
	
	@Override
	public int getAnimSpeedMult()
	{
		return isEnraged() ? 2 : 1;
	}
	
	@Override
	public void setCooldown(int cooldown)
	{
		dataTracker.set(ATTACK_COOLDOWN, cooldown);
	}
	
	@Override
	public int getCooldown()
	{
		return dataTracker.get(ATTACK_COOLDOWN);
	}
	
	@Override
	public boolean isHeadFixed()
	{
		return getAnimation() != ANIMATION_IDLE;
	}
	
	@Override
	public int getMaxHeadRotation()
	{
		return isHeadFixed() ? 0 : 75;
	}
	
	int getTargetDistance()
	{
		double dist = getBoundingBox().getCenter().distanceTo(getTarget().getPos());
		if(dist > 6)
			return 3;
		else if(dist > 3)
			return 2;
		else
			return 1;
	}
	
	@Override
	public boolean isFireImmune()
	{
		return true;
	}
	
	private void throwBullet(LivingEntity target)
	{
		CerberusBallEntity bullet = CerberusBallEntity.spawn(this, getWorld());
		double d = target.getEyeY() - target.getHeight() / 2.0;
		double e = target.getX() - getX();
		double f = d - bullet.getY();
		double g = target.getZ() - getZ();
		bullet.setVelocity(e, f, g, 2.5f, 0.0f);
		bullet.setNoGravity(true);
		playSound(SoundRegistry.GENERIC_FIRE, 1.0f, 0.2f / (getRandom().nextFloat() * 0.2f + 0.4f));
		getWorld().spawnEntity(bullet);
	}
	
	public void setRotation(float rot)
	{
		setBodyYaw(rot);
		setHeadYaw(rot);
	}
	
	@Override
	public void tick()
	{
		super.tick();
		if(dataTracker.get(ATTACK_COOLDOWN) > 0)
			dataTracker.set(ATTACK_COOLDOWN, dataTracker.get(ATTACK_COOLDOWN) - 1);
		if(shouldBeEnraged())
			addStatusEffect(new StatusEffectInstance(StatusEffectRegistry.ENRAGED, 1, 0, true, false));
	}
	
	@Override
	public void tickMovement()
	{
		super.tickMovement();
		if(isHeadFixed())
			bodyYaw = headYaw;
	}
	
	@Override
	public void move(MovementType movementType, Vec3d movement)
	{
		BlockHitResult hit = getWorld().raycast(new RaycastContext(getPos(), getPos().subtract(0, 2, 0),
				RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, this));
		if(ServerConfig.INSTANCE.smSafeLedges.getValue() && !hit.getType().equals(HitResult.Type.MISS) && !isEnraged())
		{
			for (int x = -1; x <= 1; x++)
			{
				for (int z = -1; z <= 1; z++)
				{
					if(x == 0 && z == 0)
						continue;
					if(getWorld().isSpaceEmpty(getBoundingBox().expand(0.1).offset(movement).offset(x * 0.4, -2, z * 0.4)))
					{
						movement = movement.multiply(1f - Math.abs(x), 1f, 1f - Math.abs(z));
						setVelocity(getVelocity().multiply(1f - Math.abs(x), 1f, 1f - Math.abs(z)));
					}
				}
			}
		}
		super.move(movementType, movement);
	}
	
	@Override
	public void writeCustomDataToNbt(NbtCompound nbt)
	{
		super.writeCustomDataToNbt(nbt);
		nbt.putBoolean("enraged", isEnraged());
	}
	
	@Override
	public void readCustomDataFromNbt(NbtCompound nbt)
	{
		super.readCustomDataFromNbt(nbt);
		if(nbt.contains("enraged"))
			dataTracker.set(ENRAGED, nbt.getBoolean("enraged"));
	}
	
	public float getTrueMaxHealth()
	{
		return isBoss() ? BOSS_HEALTH : REGULAR_HEALTH;
	}
	
	public float getHealthPercent()
	{
		return getHealth() / getTrueMaxHealth();
	}
	
	public boolean isCracked()
	{
		return getHealthPercent() < (isBoss() ? 0.33 : 0.5);
	}
	
	@Override
	public void onDeath(DamageSource damageSource)
	{
		super.onDeath(damageSource);
		getWorld().getEntitiesByType(TypeFilter.instanceOf(CerberusEntity.class), getBoundingBox().expand(64),
						e -> !e.isEnraged() && e != this).forEach(CerberusEntity::enrage);
	}
	
	@Override
	public boolean damage(DamageSource source, float amount)
	{
		if(source.isOf(DamageSources.PARRY) && source.getSource() instanceof CerberusBallEntity ball && this.equals(ball.getOwner()) && ball.isParried())
			dataTracker.set(DROP_APPLE, true);
		float halfOfMax = getMaxHealth() / 2;
		if(isBoss() && getHealth() > halfOfMax && getHealth() - amount <= halfOfMax)
			setRoomFlag("cerbWave2", true);
		return super.damage(source, amount);
	}
	
	@Override
	protected Identifier getLootTableId()
	{
		return dataTracker.get(DROP_APPLE) ? Ultracraft.identifier("cerberus_guaranteed_apple") : super.getLootTableId();
	}
	
	@Override
	public List<BlockPos> getListenerRooms()
	{
		return listenerRooms;
	}
	
	@Override
	public void bindListenerRoom(BlockPos pos)
	{
		listenerRooms.add(pos);
	}
	
	@SuppressWarnings("SameParameterValue")
	void setRoomFlag(String flag, boolean state)
	{
		List<BlockPos> removalList = new ArrayList<>();
		for (BlockPos pos : listenerRooms)
		{
			if(!(getWorld().getBlockEntity(pos) instanceof RoomBlockEntity room))
			{
				removalList.add(pos);
				continue;
			}
			room.setFlag(flag, state);
		}
		listenerRooms.removeAll(removalList);
	}
	
	static class ApproachTargetGoal extends Goal
	{
		final CerberusEntity cerb;
		
		public ApproachTargetGoal(CerberusEntity cerb)
		{
			this.cerb = cerb;
			
		}
		
		@Override
		public boolean canStart()
		{
			LivingEntity target = cerb.getTarget();
			return cerb.getAnimation() == ANIMATION_IDLE && target != null && cerb.squaredDistanceTo(target) > 5 * 5;
		}
		
		@Override
		public void start()
		{
			cerb.getNavigation().startMovingTo(cerb.getTarget(), 1f);
		}
		
		@Override
		public boolean shouldContinue()
		{
			return false;
		}
	}
	
	static class ThrowAttackGoal extends TimedAttackGoal<CerberusEntity>
	{
		public ThrowAttackGoal(CerberusEntity cerb)
		{
			super(cerb, ANIMATION_IDLE, ANIMATION_THROW, 34);
		}
		
		@Override
		public boolean canStart()
		{
			if(super.canStart())
				return mob.getRandom().nextInt(likelyhoodPerDistance()) == 0;
			return false;
		}
		
		int likelyhoodPerDistance()
		{
			return switch (mob.getTargetDistance())
			{
				case 1 -> 16;
				case 2 -> 5;
				case 3 -> 2;
				default -> 10;
			};
		}
		
		@Override
		public void start()
		{
			super.start();
			mob.getNavigation().stop();
		}
		
		@Override
		protected void process()
		{
			super.process();
			if(timer == 19)
				mob.throwBullet(target);
			mob.getLookControl().lookAt(target.getX(), target.getEyeY(), target.getZ());
		}
	}
	
	static class RamAttackGoal extends TimedAttackGoal<CerberusEntity>
	{
		Vec3d ramDir;
		final List<PlayerEntity> hits = new ArrayList<>();
		
		public RamAttackGoal(CerberusEntity cerb)
		{
			super(cerb, ANIMATION_IDLE, ANIMATION_RAM, 40);
			setControls(EnumSet.of(Control.LOOK));
		}
		
		@Override
		public boolean canStart()
		{
			if(super.canStart())
				return mob.getRandom().nextInt(likelyhoodPerDistance()) == 0;
			return false;
		}
		
		int likelyhoodPerDistance()
		{
			return switch (mob.getTargetDistance())
			{
				case 1 -> 4;
				case 2 -> 2;
				case 3 -> 8;
				default -> 10;
			};
		}
		
		@Override
		public void start()
		{
			super.start();
			mob.getNavigation().stop();
			hits.clear();
		}
		
		@Override
		protected void process()
		{
			super.process();
			if(timer > 15 && timer < 22)
				mob.setVelocity(ramDir);
			else if (timer == 14)
				ramDir = target.getPos().subtract(mob.getPos()).multiply(1, 0, 1).normalize();
			else if(timer < 15)
				mob.getLookControl().lookAt(target.getX(), target.getEyeY(), target.getZ(), 90, 90);
			
			if(timer > 15 && timer < 30)
			{
				mob.getWorld().getEntitiesByType(TypeFilter.instanceOf(PlayerEntity.class), mob.getBoundingBox().expand(0.2), p -> true)
						.forEach(p -> {
							if(!hits.contains(p))
							{
								p.damage(mob.getDamageSources().mobAttack(mob), 5f);
								p.setVelocity(ramDir.multiply(3).add(0.0, 0.5, 0.0));
								hits.add(p);
							}
						});
				Vec3d lookPos = mob.getPos().add(ramDir.multiply(10));
				mob.getLookControl().lookAt(lookPos.x, mob.getEyeY(), lookPos.z, 90, 90);
			}
		}
		
		@Override
		public void stop()
		{
			super.stop();
			hits.clear();
		}
	}
	
	static class StepOnMeUwUGoal extends TimedAttackGoal<CerberusEntity>
	{
		public StepOnMeUwUGoal(CerberusEntity cerb)
		{
			super(cerb, ANIMATION_IDLE, ANIMATION_STOMP, 26);
			baseCooldown = 50;
		}
		
		@Override
		public boolean canStart()
		{
			if(super.canStart())
				return mob.getRandom().nextInt(likelyhoodPerDistance()) == 0;
			return false;
		}
		
		int likelyhoodPerDistance()
		{
			return switch (mob.getTargetDistance())
			{
				case 1 -> 2;
				case 2 -> 8;
				case 3 -> 18;
				default -> 10;
			};
		}
		
		@Override
		public void start()
		{
			super.start();
			mob.getNavigation().stop();
		}
		
		@Override
		protected void process()
		{
			super.process();
			mob.getLookControl().lookAt(target.getX(), target.getEyeY(), target.getZ());
			
			if(!mob.getWorld().isClient && timer == 15)
			{
				ShockwaveEntity shockwave = EntityRegistry.SHOCKWAVE.spawn((ServerWorld)mob.getWorld(), mob.getBlockPos(), SpawnReason.EVENT);
				if(shockwave != null)
				{
					shockwave.setAffectOnly(PlayerEntity.class);
					shockwave.setDamage(5f);
					shockwave.setGrowRate(0.35f);
					shockwave.setOwner(mob);
				}
			}
		}
	}
}
