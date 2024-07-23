package absolutelyaya.ultracraft.client.sound;

import absolutelyaya.ultracraft.registry.SoundRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

public class MovingChainsawSoundInstance extends MovingEntitySoundInstance
{
	public MovingChainsawSoundInstance(Entity owner)
	{
		super(SoundRegistry.SHOTGUN_SAW_ACTIVE, owner);
		volume = 0.6f;
	}
	
	@Override
	public void tick()
	{
		if(owner == null || owner.isRemoved())
		{
			setDone();
			return;
		}
		x = owner.getX();
		y = owner.getY();
		z = owner.getZ();
		
		PlayerEntity player = MinecraftClient.getInstance().player;
		if(player != null)
			pitch = Math.min(1f - Math.max((player.distanceTo(owner) - 4) / 16f, 0f), 1f);
	}
}
