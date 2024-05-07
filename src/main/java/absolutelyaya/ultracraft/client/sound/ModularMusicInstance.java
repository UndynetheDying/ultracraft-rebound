package absolutelyaya.ultracraft.client.sound;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.TickableSoundInstance;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;

public class ModularMusicInstance extends PositionedSoundInstance implements TickableSoundInstance, INonPausingSoundInstance
{
	boolean fadingIn = true, fadingOut, wasGamePaused;
	float fadeInVolume = 0.01f, normalPitch = 1f, normalVolume = 1f, pauseMultiplier = -1f;
	
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
		MinecraftClient client = MinecraftClient.getInstance();
		if(fadingOut && fadingIn)
			fadingIn = false;
		if(fadingOut && (volume -= 0.05f) <= 0)
			client.getSoundManager().stop(this);
		if(fadingIn && fadeInVolume < 1f)
			fadeInVolume = Math.min(fadeInVolume + 0.1f, 1f);
		else
			fadingIn = false;
		boolean death = client.currentScreen instanceof DeathScreen;
		if(shouldLowerPitchWhenPaused())
		{
			if(client.isPaused() || death)
			{
				if(!wasGamePaused)
				{
					normalPitch = pitch;
					normalVolume = volume;
					if(pauseMultiplier == -1f)
						pauseMultiplier = 0.99f;
				}
				else if(pauseMultiplier > 0.7f)
					pauseMultiplier = Math.max(pauseMultiplier - 0.1f / (death ? 100f : 5f), 0.7f);
			}
			else if(pauseMultiplier >= 0f && pauseMultiplier < 1f)
				pauseMultiplier = Math.min(pauseMultiplier + 0.2f / 5f, 1f);
			if(pauseMultiplier >= 0f)
			{
				pitch = normalPitch * pauseMultiplier;
				setVolume(normalVolume - (0.75f * ((1f - pauseMultiplier) / 0.3f)));
				if(pauseMultiplier == 1f)
					pauseMultiplier = -1f;
			}
		}
		wasGamePaused = client.isPaused() || death;
	}
	
	public void startFadeout()
	{
		fadingIn = false;
		fadingOut = true;
	}
	
	@Override
	public boolean shouldLowerPitchWhenPaused()
	{
		return !fadingOut;
	}
	
	@Override
	public boolean shouldAlwaysPlay()
	{
		return true;
	}
}
