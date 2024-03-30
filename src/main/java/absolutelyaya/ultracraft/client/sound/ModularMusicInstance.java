package absolutelyaya.ultracraft.client.sound;

import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.TickableSoundInstance;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.random.Random;

public class ModularMusicInstance extends PositionedSoundInstance implements TickableSoundInstance
{
	public ModularMusicInstance(SoundEvent sound, boolean battle)
	{
		super(sound.getId(), SoundCategory.MUSIC, battle ? 1f : 0f, 1f, SoundInstance.createRandom(), true, 0,
				AttenuationType.NONE, 0.0, 0.0, 0.0, true);
	}
	
	public void setVolume(float volume)
	{
		this.volume = volume;
	}
	
	@Override
	public boolean isDone()
	{
		return false;
	}
	
	@Override
	public void tick()
	{
	
	}
}
