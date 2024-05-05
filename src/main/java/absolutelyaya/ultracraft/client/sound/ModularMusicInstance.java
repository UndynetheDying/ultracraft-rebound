package absolutelyaya.ultracraft.client.sound;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.TickableSoundInstance;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;

public class ModularMusicInstance extends PositionedSoundInstance implements TickableSoundInstance
{
	boolean fadingIn = true, fadingOut;
	float fadeInVolume = 0.01f;
	
	public ModularMusicInstance(SoundEvent sound)
	{
		super(sound.getId(), SoundCategory.MUSIC, 0.01f, 1f, SoundInstance.createRandom(), true, 0,
				AttenuationType.NONE, 0.0, 0.0, 0.0, true);
	}
	
	public void setVolume(float volume)
	{
		this.volume = Math.min(volume, fadingIn ? fadeInVolume : Float.MAX_VALUE);
	}
	
	@Override
	public boolean isDone()
	{
		return false;
	}
	
	@Override
	public void tick()
	{
		if(fadingOut && fadingIn)
			fadingIn = false;
		if(fadingOut && (volume -= 0.05f) <= 0)
			MinecraftClient.getInstance().getSoundManager().stop(this);
		if(fadingIn && fadeInVolume < 1f)
			fadeInVolume = Math.min(fadeInVolume + 0.1f, 1f);
		else
			fadingIn = false;
	}
	
	public void startFadeout()
	{
		fadingIn = false;
		fadingOut = true;
	}
}
