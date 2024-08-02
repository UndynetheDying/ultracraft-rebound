package absolutelyaya.ultracraft.client.sound;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.sound.SoundEvent;

public class MovingWeaponSoundInstance extends MovingEntitySoundInstance
{
	final boolean warmUp;
	
	public MovingWeaponSoundInstance(SoundEvent event, Entity owner, boolean warmUp, float volume)
	{
		super(event, owner);
		this.warmUp = warmUp;
		if(warmUp)
			pitch = 0.5f;
		this.volume = volume;
		MinecraftClient.getInstance().getSoundManager().play(this);
	}
	
	@Override
	public void tick()
	{
		if(owner == null || owner.isRemoved())
		{
			setDone();
			return;
		}
		if(warmUp && pitch < 1f)
			pitch = Math.min(pitch + 1f / 10f, 1f);
		
		x = owner.getX();
		y = owner.getY();
		z = owner.getZ();
	}
}
