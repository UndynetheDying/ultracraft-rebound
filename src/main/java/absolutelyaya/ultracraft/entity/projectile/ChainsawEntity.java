package absolutelyaya.ultracraft.entity.projectile;

import absolutelyaya.ultracraft.Ultracraft;
import absolutelyaya.ultracraft.accessor.ProjectileEntityAccessor;
import absolutelyaya.ultracraft.client.UltracraftClient;
import absolutelyaya.ultracraft.damage.DamageSources;
import absolutelyaya.ultracraft.registry.EntityRegistry;
import mod.azure.azurelib.animatable.GeoEntity;
import mod.azure.azurelib.core.animatable.instance.AnimatableInstanceCache;
import mod.azure.azurelib.core.animatable.instance.InstancedAnimatableInstanceCache;
import mod.azure.azurelib.core.animation.AnimatableManager;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.util.Pair;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Vector4f;

public class ChainsawEntity extends ProjectileEntity implements GeoEntity, ProjectileEntityAccessor, IIgnoreSharpshooter
{
	protected final AnimatableInstanceCache cache = new InstancedAnimatableInstanceCache(this);
	protected static final TrackedData<Boolean> CONNECTED = DataTracker.registerData(ChainsawEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	protected static final TrackedData<Boolean> AWAITING_PARRY = DataTracker.registerData(ChainsawEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	protected static final TrackedData<Integer> WAITING_TICKS = DataTracker.registerData(ChainsawEntity.class, TrackedDataHandlerRegistry.INTEGER);
	
	public ChainsawEntity(EntityType<? extends ProjectileEntity> entityType, World world)
	{
		super(entityType, world);
		setNoGravity(true);
	}
	
	public static ChainsawEntity spawn(LivingEntity user, World world)
	{
		ChainsawEntity saw = new ChainsawEntity(EntityRegistry.CHAINSAW, world);
		saw.setOwner(user);
		saw.setPosition(user.getEyePos());
		return saw;
	}
	
	@Override
	protected void initDataTracker()
	{
		dataTracker.startTracking(CONNECTED, true);
		dataTracker.startTracking(AWAITING_PARRY, false);
		dataTracker.startTracking(WAITING_TICKS, 0);
	}
	
	@Override
	public void onTrackedDataSet(TrackedData<?> data)
	{
		super.onTrackedDataSet(data);
		if(data.equals(CONNECTED))
		{
			if(!dataTracker.get(CONNECTED))
				UltracraftClient.HITSCAN_HANDLER.removeMoving(getUuid());
		}
	}
	
	@Override
	public void tick()
	{
		super.tick();
		if(isRemoved() || (getWorld().isClient && Ultracraft.isTimeFrozen()))
			return;
		if(dataTracker.get(AWAITING_PARRY))
		{
			if(dataTracker.get(WAITING_TICKS) <= 0 || owner == null)
			{
				if(!getWorld().isClient)
					kill();
			}
			else
			{
				setPosition(owner.getEyePos());
				//setInvisible(true);
				dataTracker.set(WAITING_TICKS, dataTracker.get(WAITING_TICKS) - 1);
			}
			return;
		}
		HitResult hitResult = ProjectileUtil.getCollision(this, this::canHit);
		if (hitResult.getType() != HitResult.Type.MISS)
			onCollision(hitResult);
		checkBlockCollision();
		Vec3d vel = getVelocity();
		if(dataTracker.get(CONNECTED))
		{
			if(owner != null)
				setVelocity(vel.lerp((owner.getEyePos().subtract(getPos())).normalize(), Math.max(age / 10f, 1f) * 0.125f));
			else
				dataTracker.set(CONNECTED, false);
		}
		double x = getX() + vel.x;
		double y = getY() + vel.y;
		double z = getZ() + vel.z;
		setPosition(x, y, z);
	}
	
	@Override
	protected void onEntityHit(EntityHitResult entityHitResult)
	{
		super.onEntityHit(entityHitResult);
		if(entityHitResult.getEntity().equals(owner))
		{
			if(dataTracker.get(CONNECTED) && !dataTracker.get(AWAITING_PARRY))
			{
				dataTracker.set(WAITING_TICKS, 5);
				dataTracker.set(AWAITING_PARRY, true);
			}
		}
		else
			entityHitResult.getEntity().damage(DamageSources.get(getWorld(), DamageSources.SAW, this, owner), 6);
	}
	
	@Override
	protected void onBlockHit(BlockHitResult blockHitResult)
	{
		super.onBlockHit(blockHitResult);
		Vec3d hitNormal = new Vec3d(blockHitResult.getSide().getUnitVector());
		setVelocity(getVelocity().subtract(hitNormal.multiply(2 * getVelocity().dotProduct(hitNormal))).normalize());
		Vec3d dir = getVelocity();
		setRotation((float)-Math.toDegrees(Math.atan2(dir.z, dir.x)) + 90, (float)Math.toDegrees(Math.atan2(dir.y, Math.sqrt(1 - dir.y * dir.y))));
	}
	
	@Override
	public void setParried(boolean val, PlayerEntity parrier)
	{
		setVelocity(parrier.getRotationVector());
		setRotation(-parrier.getYaw(), -parrier.getPitch());
		dataTracker.set(AWAITING_PARRY, false);
		age = 0;
		//setInvisible(false);
	}
	
	public void onKnucklePunch(PlayerEntity player)
	{
		Vec3d vel = player.getRotationVector();
		setVelocity(vel);
		setVelocityClient(vel.x, vel.y, vel.z);
		setRotation(-player.getYaw(), -player.getPitch());
		scheduleVelocityUpdate();
		dataTracker.set(AWAITING_PARRY, false);
		age = 0;
		//setInvisible(false);
		setOwner(getParrier());
		if(dataTracker.get(CONNECTED))
			dataTracker.set(CONNECTED, false);
	}
	
	@Override
	public void onParriedCollision(HitResult hitResult)
	{
	
	}
	
	@Override
	public boolean isHitscanHittable(byte type)
	{
		return false;
	}
	
	@Override
	public boolean isBoostable()
	{
		return false;
	}
	
	@Override
	public boolean isParried()
	{
		return false;
	}
	
	@Override
	public boolean isParriable()
	{
		return true;
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
	
	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar)
	{
	
	}
	
	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache()
	{
		return cache;
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
					new Vec2f(0.01f, 0.05f), 0.1f, () -> 0x000000, 1);
		}
		UltracraftClient.TRAIL_RENDERER.createTrail(uuid,
				() -> new Pair<>(getPos().subtract(0f, 0.1f, 0f).toVector3f(), getPos().add(0f, 0.1f, 0f).toVector3f()),
				new Vector4f(1f, 0.2f, 0.2f, 0.4f), 15);
	}
	
	@Override
	public void onRemoved()
	{
		if(dataTracker.get(CONNECTED))
			UltracraftClient.HITSCAN_HANDLER.removeMoving(getUuid());
		if(getWorld().isClient)
			UltracraftClient.TRAIL_RENDERER.removeTrail(uuid);
		super.onRemoved();
	}
}
