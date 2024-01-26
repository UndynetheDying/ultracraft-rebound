package absolutelyaya.ultracraft.client.sound;

import absolutelyaya.ultracraft.entity.projectile.IHomingProjectile;
import absolutelyaya.ultracraft.registry.SoundRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;

public class MovingHomingProjectileSoundInstance extends MovingPlayerSoundInstance
{
	public MovingHomingProjectileSoundInstance(IHomingProjectile owner)
	{
		super(SoundRegistry.SPIN, (ProjectileEntity)owner);
		volume = 1.75f;
		pitch = 1.2f;
	}
	
	@Override
	public void tick()
	{
		if (owner.isRemoved())
			setDone();
		x = owner.getX();
		y = owner.getY();
		z = owner.getZ();
		
		PlayerEntity player = MinecraftClient.getInstance().player;
		if (player != null)
			pitch = Math.min(1f - Math.max((player.distanceTo(owner) - 16) / 16f, 0f), 1f) * 1.1f;
	}
}
