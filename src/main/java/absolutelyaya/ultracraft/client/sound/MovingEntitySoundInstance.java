package absolutelyaya.ultracraft.client.sound;

import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.entity.Entity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;

public abstract class MovingEntitySoundInstance extends MovingSoundInstance
{
	protected final Entity owner;
	
	public MovingEntitySoundInstance(SoundEvent event, Entity owner)
	{
		super(event, SoundCategory.PLAYERS, SoundInstance.createRandom());
		this.owner = owner;
		repeat = true;
		repeatDelay = 0;
		volume = 0;
		pitch = 1f;
		x = owner.getX();
		y = owner.getY();
		z = owner.getZ();
	}
	
	public MovingEntitySoundInstance(SoundEvent event, SoundCategory category, Entity owner)
	{
		super(event, category, SoundInstance.createRandom());
		this.owner = owner;
		repeat = true;
		repeatDelay = 0;
		volume = 0;
		pitch = 1f;
		x = owner.getX();
		y = owner.getY();
		z = owner.getZ();
	}
	
	@Override
	public boolean shouldAlwaysPlay()
	{
		return true;
	}
	
	public void setFinished()
	{
		setDone();
	}
}
