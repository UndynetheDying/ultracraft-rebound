package absolutelyaya.ultracraft.entity.projectile;

import absolutelyaya.ultracraft.ExplosionHandler;
import absolutelyaya.ultracraft.ServerHitscanHandler;
import absolutelyaya.ultracraft.accessor.IParriable;
import absolutelyaya.ultracraft.accessor.LivingEntityAccessor;
import absolutelyaya.ultracraft.client.GunCooldownManager;
import absolutelyaya.ultracraft.client.UltracraftClient;
import absolutelyaya.ultracraft.components.UltraComponents;
import absolutelyaya.ultracraft.damage.DamageSources;
import absolutelyaya.ultracraft.registry.BlockRegistry;
import absolutelyaya.ultracraft.registry.EntityRegistry;
import absolutelyaya.ultracraft.registry.ItemRegistry;
import absolutelyaya.ultracraft.registry.SoundRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.ThrownEntity;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class JumpstartHookEntity extends ThrownEntity implements IIgnoreSharpshooter, IParriable
{
	protected static final TrackedData<Integer> VICTIM = DataTracker.registerData(JumpstartHookEntity.class, TrackedDataHandlerRegistry.INTEGER);
	protected static final TrackedData<Integer> CHARGE = DataTracker.registerData(JumpstartHookEntity.class, TrackedDataHandlerRegistry.INTEGER);
	protected static final TrackedData<Integer> STRESS = DataTracker.registerData(JumpstartHookEntity.class, TrackedDataHandlerRegistry.INTEGER);
	protected static final TrackedData<Integer> EXPLOSION_TICKS = DataTracker.registerData(JumpstartHookEntity.class, TrackedDataHandlerRegistry.INTEGER);
	
	final float maxDistance = 8f;
	
	public JumpstartHookEntity(EntityType<? extends ThrownEntity> entityType, World world)
	{
		super(entityType, world);
	}
	
	@Override
	protected void initDataTracker()
	{
		dataTracker.startTracking(VICTIM, -1);
		dataTracker.startTracking(CHARGE, 0);
		dataTracker.startTracking(STRESS, 0);
		dataTracker.startTracking(EXPLOSION_TICKS, 0);
	}
	
	public static JumpstartHookEntity spawn(LivingEntity owner, Vec3d pos, Vec3d vel)
	{
		JumpstartHookEntity hook = new JumpstartHookEntity(EntityRegistry.JUMPSTART_HOOK, owner.getWorld());
		hook.setOwner(owner);
		hook.setPosition(pos);
		hook.setVelocity(vel);
		return hook;
	}
	
	@Override
	protected void onBlockHit(BlockHitResult blockHitResult)
	{
		super.onBlockHit(blockHitResult);
		kill();
	}
	
	public Entity getVictim()
	{
		int id = dataTracker.get(VICTIM);
		if(id == -1)
			return null;
		return getWorld().getEntityById(id);
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
	}
	
	public boolean breakIfVacant()
	{
		if(getVictim() == null)
		{
			kill();
			return true;
		}
		else
			return false;
	}
	
	@Override
	public void tick()
	{
		boolean hasVictim = getVictim() != null;
		if(hasVictim)
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
		}
		if(getOwner() == null)
		{
			super.tick();
			return;
		}
		float distance = distanceTo(getOwner());
		if(hasVictim && !getWorld().isClient)
		{
			if(distance > maxDistance && dataTracker.get(EXPLOSION_TICKS) == 0)
			{
				int stress = dataTracker.get(STRESS);
				dataTracker.set(STRESS, stress + 1);
				if(stress + 1 >= 20)
					kill();
			}
			int charge = dataTracker.get(CHARGE);
			dataTracker.set(CHARGE, charge + 1);
			if(charge >= 100)
			{
				if(getVictim() instanceof LivingEntityAccessor living && living.getNails() > 0)
				{
					int explosionTicks = dataTracker.get(EXPLOSION_TICKS);
					if(explosionTicks == 0)
					{
						ExplosionHandler.explosion(getVictim(), getWorld(), getVictim().getPos(), DamageSources.get(getWorld(), DamageSources.EXPLOSION), 0.001f, 0f, 0.01f, false);
						getVictim().addVelocity(new Vec3d(0f, 1.5f, 0f));
						UltraComponents.WINGED.get(getOwner()).getGunCooldownManager().setCooldown(ItemRegistry.JUMPSTART_NAILGUN, 100, GunCooldownManager.SECONDARY);
					}
					else if(explosionTicks == 10)
					{
						getWorld().getOtherEntities(this,
								getBoundingBox().expand(10f), e -> e != getOwner() && (getOwner() != null && !getOwner().isTeammate(e))).forEach(e -> {
									ServerHitscanHandler.sendPacket((ServerWorld) getWorld(), getPos().addRandom(random, 0.1f),
											e.getPos().add(0f, e.getHeight() / 2f, 0f), ServerHitscanHandler.JUMPSTART_ARC);
									e.damage(DamageSources.get(getWorld(), DamageSources.JUMPSTART, getOwner()), 10);
								});
						kill();
					}
					dataTracker.set(EXPLOSION_TICKS, explosionTicks + 1);
				}
				else
				{
					getVictim().damage(DamageSources.get(getWorld(), DamageSources.JUMPSTART, getOwner()), 20);
					kill();
				}
			}
		}
		if(!hasVictim && distance >= maxDistance)
		{
			setPosition(getOwner().getPos().add(getPos().subtract(getOwner().getPos()).normalize().multiply(Math.max(maxDistance, 0f)))
								.subtract(0f, getGravity(), 0f));
			setVelocity(new Vec3d(0f, Math.max(getVelocity().y, 0f), 0f));
		}
		super.tick();
	}
	
	@Override
	public void onSpawnPacket(EntitySpawnS2CPacket packet)
	{
		super.onSpawnPacket(packet);
		if(getOwner() != null)
		{
			UltracraftClient.HITSCAN_HANDLER.addConnector(delta -> {
						if(getOwner() == null)
						{
							UltracraftClient.HITSCAN_HANDLER.removeMoving(getUuid());
							return getLeashPos(delta);
						}
						return getOwner().getLeashPos(delta);
					}, this::getLeashPos, getUuid(),
					new Vec2f(0.01f, 0.05f), 0.1f, 0x000000, 1);
		}
	}
	
	@Override
	public void onRemoved()
	{
		UltracraftClient.HITSCAN_HANDLER.removeMoving(getUuid());
		super.onRemoved();
		for (int i = 0; i < 6; i++)
		{
			Vec3d pos = getPos().addRandom(random, 0.2f);
			getWorld().addParticle(new BlockStateParticleEffect(ParticleTypes.BLOCK, BlockRegistry.SHEETMETAL.getDefaultState()), pos.x, pos.y, pos.z,
					0f, 0f, 0f);
		}
		playSound(SoundRegistry.NAILGUN_JUMPSTART_HOOK_BREAK, 1f, 1f);
	}
	
	@Override
	public void setParried(boolean val, PlayerEntity parrier)
	{
	
	}
	
	@Override
	public boolean isParried()
	{
		return false;
	}
	
	@Override
	public boolean isParriable()
	{
		return false;
	}
	
	@Override
	public PlayerEntity getParrier()
	{
		return null;
	}
	
	@Override
	public void setParrier(PlayerEntity p)
	{
	
	}
}
