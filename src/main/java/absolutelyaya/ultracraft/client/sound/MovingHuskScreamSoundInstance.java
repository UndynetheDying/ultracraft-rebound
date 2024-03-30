package absolutelyaya.ultracraft.client.sound;

import absolutelyaya.ultracraft.entity.husk.AbstractHuskEntity;
import absolutelyaya.ultracraft.registry.SoundRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;

public class MovingHuskScreamSoundInstance extends MovingPlayerSoundInstance
{
	public MovingHuskScreamSoundInstance(AbstractHuskEntity owner)
	{
		super(SoundRegistry.HUSK_SCREAM_LOOP, owner);
		volume = 0f;
		pitch = 0.9f;
	}
	
	@Override
	public void tick()
	{
		if(owner.isRemoved())
			setDone();
		x = owner.getX();
		y = owner.getY();
		z = owner.getZ();
		
		if(((AbstractHuskEntity)owner).shouldScream())
			volume = (float)Math.min(volume + 0.1, 0.5f);
		else
			volume = 0f;
		PlayerEntity player = MinecraftClient.getInstance().player;
		if(player != null)
			pitch = Math.min(1f - Math.max((player.distanceTo(owner) - 8) / 24f, 0f), 1f) * 0.9f;
	}
}
