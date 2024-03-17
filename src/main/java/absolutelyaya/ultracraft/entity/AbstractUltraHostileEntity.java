package absolutelyaya.ultracraft.entity;

import absolutelyaya.ultracraft.api.HeavyEntities;
import absolutelyaya.ultracraft.components.UltraComponents;
import absolutelyaya.ultracraft.components.player.ILevelStatsComponent;
import absolutelyaya.ultracraft.cybergrind.CybergrindGame;
import absolutelyaya.ultracraft.cybergrind.CybergrindManager;
import absolutelyaya.ultracraft.dimension.LevelManager;
import absolutelyaya.ultracraft.particle.ParryIndicatorParticleEffect;
import absolutelyaya.ultracraft.particle.TeleportParticleEffect;
import absolutelyaya.ultracraft.registry.SoundRegistry;
import com.chocohead.mm.api.ClassTinkerers;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.*;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector4i;

public abstract class AbstractUltraHostileEntity extends HostileEntity
{
	protected static final TrackedData<Byte> ANIMATION = DataTracker.registerData(AbstractUltraHostileEntity.class, TrackedDataHandlerRegistry.BYTE);
	protected static final TrackedData<Boolean> BOSS = DataTracker.registerData(AbstractUltraHostileEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	protected static final TrackedData<Boolean> CYBERGRIND = DataTracker.registerData(AbstractUltraHostileEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	
	protected ServerBossBar bossBar;
	boolean wasBossbarVisible;
	
	protected AbstractUltraHostileEntity(EntityType<? extends HostileEntity> entityType, World world)
	{
		super(entityType, world);
	}
	
	public byte getAnimation()
	{
		return dataTracker.get(ANIMATION);
	}
	
	@Override
	protected void initDataTracker()
	{
		super.initDataTracker();
		dataTracker.startTracking(ANIMATION, (byte)0);
		dataTracker.startTracking(BOSS, getBossDefault());
		dataTracker.startTracking(CYBERGRIND, false);
	}
	
	@Nullable
	@Override
	public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt)
	{
		return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
	}
	
	@Override
	public void readCustomDataFromNbt(NbtCompound nbt)
	{
		super.readCustomDataFromNbt(nbt);
		if(nbt.contains("boss", NbtElement.BYTE_TYPE))
			dataTracker.set(BOSS, nbt.getBoolean("boss"));
		if(nbt.contains("cybergrind", NbtElement.BYTE_TYPE))
			dataTracker.set(CYBERGRIND, nbt.getBoolean("cybergrind"));
	}
	
	@Override
	public void writeCustomDataToNbt(NbtCompound nbt)
	{
		super.writeCustomDataToNbt(nbt);
		nbt.putBoolean("boss", dataTracker.get(BOSS));
		nbt.putBoolean("cybergrind", dataTracker.get(CYBERGRIND));
	}
	
	@Override
	public void onSpawnPacket(EntitySpawnS2CPacket packet)
	{
		super.onSpawnPacket(packet);
		getWorld().addParticle(new TeleportParticleEffect(getTeleportParticleSize()), packet.getX(), packet.getY(), packet.getZ(), 0f, 0f, 0f);
		float pitch = HeavyEntities.isHeavy(getType()) ? 0.65f : 1.3f;
		getWorld().playSound(getX(), getY(), getZ(), SoundRegistry.GENERIC_SPAWN, SoundCategory.HOSTILE, 0.75f,  pitch+ random.nextFloat() * 0.15f, false);
	}
	
	protected ServerBossBar initBossBar()
	{
		return new ServerBossBar(getDisplayName(), BossBar.Color.RED, ClassTinkerers.getEnum(BossBar.Style.class, "ULTRA"));
	}
	
	protected double getTeleportParticleSize()
	{
		return 1.0;
	}
	
	public void addParryIndicatorParticle(Vec3d offset, boolean useYaw, boolean unparriable)
	{
		if(useYaw)
			offset = offset.rotateY(-(float)Math.toRadians(getYaw() + 180));
		if(!getWorld().isClient)
		{
			((ServerWorld)getWorld()).spawnParticles(new ParryIndicatorParticleEffect(unparriable),
					getX() + offset.x, getY() + offset.y, getZ() + offset.z, 1, 0f, 0f, 0f, 0f);
		}
	}
	
	@Override
	protected boolean shouldSwimInFluids()
	{
		return !getWorld().getFluidState(getBlockPos()).isIn(FluidTags.WATER);
	}
	
	@Override
	public int getAir()
	{
		return getMaxAir();
	}
	
	@Override
	protected int computeFallDamage(float fallDistance, float damageMultiplier)
	{
		return super.computeFallDamage(fallDistance - 3, damageMultiplier);
	}
	
	public boolean shouldScream()
	{
		if(isOnGround())
			return false;
		BlockHitResult hit = getWorld().raycast(new RaycastContext(getPos(), getPos().add(0, -32, 0),
				RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, this));
		boolean miss = hit.getType().equals(HitResult.Type.MISS);
		return isAlive() && miss ||
					   (!miss && computeFallDamage((float)(fallDistance + Math.abs(getPos().getY() - (float)hit.getPos().y)), 1f) - 2f >= getHealth());
	}
	
	@Override
	public void setCustomName(@Nullable Text name)
	{
		super.setCustomName(name);
		if(isBoss() && isBossBarVisible())
			bossBar.setName(getDisplayName());
	}
	
	@Override
	public void onStartedTrackingBy(ServerPlayerEntity player)
	{
		super.onStartedTrackingBy(player);
		if(isBoss() && isBossBarVisible())
			bossBar.addPlayer(player);
	}
	
	@Override
	public void onStoppedTrackingBy(ServerPlayerEntity player)
	{
		super.onStoppedTrackingBy(player);
		if(isBoss() && isBossBarVisible())
			bossBar.removePlayer(player);
	}
	
	@Override
	protected void mobTick()
	{
		super.mobTick();
		if(isBoss() && isBossBarVisible())
			bossBar.setPercent(getHealth() / getMaxHealth());
		if(getWorld().isClient || !isBoss())
			return;
		if(wasBossbarVisible && !isBossBarVisible())
			bossBar.clearPlayers();
		else if(!wasBossbarVisible && isBossBarVisible())
		{
			getWorld().getPlayers(TargetPredicate.DEFAULT.ignoreVisibility(), this, getBoundingBox().expand(64))
					.forEach(p -> bossBar.addPlayer((ServerPlayerEntity)p));
		}
		wasBossbarVisible = isBossBarVisible();
	}
	
	@Override
	public void tickMovement()
	{
		super.tickMovement();
		
		if(!getWorld().isClient)
		{
			CybergrindGame cybergrind = CybergrindManager.Instance.getActiveGame();
			if(cybergrind != null && cybergrind.getCenter() != null)
			{
				Vector4i bounds = cybergrind.getArenaBounds();
				if(getX() < bounds.x || getX() > bounds.z || getZ() < bounds.y || getZ() > bounds.w)
				{
					BlockPos pos = cybergrind.getCenter();
					navigation.startMovingTo(pos.getX(), pos.getY(), pos.getZ(), 1f);
				}
			}
			return;
		}
		if(!isOnGround() && getVelocity().y < 0f)
			fallDistance -= getVelocity().y;
		else
			fallDistance = 0f;
	}
	
	@Override
	public boolean damage(DamageSource source, float amount)
	{
		boolean b = super.damage(source, amount);
		if(isBoss() && isBossBarVisible())
			bossBar.setPercent(getHealth() / getMaxHealth());
		return b;
	}
	
	protected boolean getBossDefault()
	{
		return false;
	}
	
	public boolean isBoss()
	{
		boolean boss = dataTracker.get(BOSS);
		if(boss && bossBar == null)
			bossBar = initBossBar();
		return boss;
	}
	
	public boolean isBossBarVisible()
	{
		return bossBar != null;
	}
	
	protected EnemySoundType getSoundType()
	{
		return EnemySoundType.GENERIC;
	}
	
	@Override
	protected SoundEvent getHurtSound(DamageSource source)
	{
		return switch(getSoundType()) {
			case HUSK -> SoundRegistry.HUSK_DAMAGE;
			case MACHINE -> SoundRegistry.MACHINE_DAMAGE;
			case GENERIC -> SoundEvents.ENTITY_GENERIC_HURT;
		};
	}
	
	@Override
	protected SoundEvent getDeathSound()
	{
		return switch(getSoundType()) {
			case HUSK -> SoundRegistry.HUSK_DEATH;
			case MACHINE -> SoundRegistry.MACHINE_DEATH;
			case GENERIC -> SoundEvents.ENTITY_GENERIC_DEATH;
		};
	}
	
	protected void playFireSound()
	{
		playSound(SoundRegistry.GENERIC_FIRE, 1, 1);
	}
	
	public void markCybergrind()
	{
		dataTracker.set(BOSS, false);
		dataTracker.set(CYBERGRIND, true);
		if(bossBar != null)
			bossBar.clearPlayers();
	}
	
	public boolean isCybergrind()
	{
		return dataTracker.get(CYBERGRIND);
	}
	
	@Override
	protected Identifier getLootTableId()
	{
		if(isCybergrind())
			return super.getLootTableId().withPrefixedPath("_cg");
		return super.getLootTableId();
	}
	
	@Override
	public void onDeath(DamageSource damageSource)
	{
		super.onDeath(damageSource);
		if(getWorld().getRegistryKey().equals(LevelManager.WORLD_KEY) && !getWorld().isClient)
		{
			getWorld().getEntitiesByType(TypeFilter.instanceOf(PlayerEntity.class), getBoundingBox().expand(192f), p -> true).forEach(p -> {
				ILevelStatsComponent levelStats = UltraComponents.LEVEL_STATS.get(p);
				if(levelStats.getCurrentLevelInstance() != null)
					LevelManager.Instance.getInstance(levelStats.getCurrentLevelInstance()).onKill();
			});
		}
	}
}
