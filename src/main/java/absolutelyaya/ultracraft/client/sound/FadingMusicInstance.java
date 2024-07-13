package absolutelyaya.ultracraft.client.sound;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.TickableSoundInstance;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;

public class FadingMusicInstance extends PositionedSoundInstance implements TickableSoundInstance
{
	boolean fadingIn = true, fadingOut;
	float fadeInVolume = 0.01f;
	
	public FadingMusicInstance(SoundEvent sound, float volume)
	{
		super(sound.getId(), SoundCategory.MUSIC, volume, 1f, SoundInstance.createRandom(), true, 0,
				AttenuationType.NONE, 0.0, 0.0, 0.0, true);
	}
	
	public void setVolume(float volume)
	{
		this.volume = volume;
	}
	
	@Override
	public float getVolume()
	{
		return Math.min(volume, fadingIn ? fadeInVolume : Float.MAX_VALUE);
	}
	
	@Override
	public boolean isDone()
	{
		return false;
	}
	
	@Override
	public void tick()
	{
		MinecraftClient client = MinecraftClient.getInstance();
		if(fadingOut && fadingIn)
			fadingIn = false;
		if(fadingOut && (volume -= 0.05f) <= 0)
			client.getSoundManager().stop(this);
		if(fadingIn && fadeInVolume < 1f)
			fadeInVolume = Math.min(fadeInVolume + 0.05f, 1f);
		else
			fadingIn = false;
	}
	
	public void skipFadein()
	{
		fadingIn = false;
		fadeInVolume = 1f;
	}
	
	public void startFadeout()
	{
		fadingIn = false;
		fadingOut = true;
	}
}
