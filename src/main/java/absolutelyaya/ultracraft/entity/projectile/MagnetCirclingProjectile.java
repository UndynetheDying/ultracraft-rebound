package absolutelyaya.ultracraft.entity.projectile;

import absolutelyaya.ultracraft.Ultracraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

public abstract class MagnetCirclingProjectile extends ProjectileEntity
{
	public MagnetCirclingProjectile(EntityType<? extends ProjectileEntity> entityType, World world)
	{
		super(entityType, world);
		setNoGravity(true);
	}
	
	@Override
	public void tick()
	{
		super.tick();
		if(isRemoved() || (getWorld().isClient && Ultracraft.isTimeFrozen()))
			return;
		HitResult hitResult = ProjectileUtil.getCollision(this, this::canHit);
		if (hitResult.getType() != HitResult.Type.MISS)
			onCollision(hitResult);
		checkBlockCollision();
		
		Vec3d vel = getVelocity();
		List<Entity> magnets = getWorld().getOtherEntities(this, getBoundingBox().expand(8f), i -> i instanceof MagnetEntity);
		if(!magnets.isEmpty() && shouldCircleMagnet())
		{
			MagnetEntity nearest = null;
			float nearestDistance = Float.MAX_VALUE;
			for (Entity e : magnets)
			{
				float dist = (float)e.getPos().distanceTo(getPos());
				if(e instanceof MagnetEntity magnet && dist < nearestDistance)
				{
					nearest = magnet;
					nearestDistance = dist;
				}
			}
			if(nearest != null)
			{
				Vec3d dir = nearest.getPos().add(nearest.getAttractOffset()).subtract(getPos()).normalize();
				setVelocity(vel = dir.multiply(vel.length()).rotateY((float)Math.toRadians(80f)));
				setYaw((float)Math.toDegrees(MathHelper.atan2(vel.x, vel.z)));
				setPitch(0f);
			}
		}
		
		double x = getX() + vel.x;
		double y = getY() + vel.y;
		double z = getZ() + vel.z;
		setPosition(x, y, z);
	}
	
	protected boolean shouldCircleMagnet()
	{
		return true;
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
}
