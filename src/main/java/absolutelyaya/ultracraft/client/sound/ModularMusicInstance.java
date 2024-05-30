package absolutelyaya.ultracraft.client.sound;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.client.sound.TickableSoundInstance;
import net.minecraft.sound.SoundEvent;

public class ModularMusicInstance extends FadingMusicInstance implements TickableSoundInstance, INonPausingSoundInstance
{
	boolean wasGamePaused;
	float normalPitch = 1f, normalVolume = 1f, pauseMultiplier = -1f;
	
	public ModularMusicInstance(SoundEvent sound)
	{
		super(sound, 0.01f);
	}
	
	@Override
	public void tick()
	{
		super.tick();
		MinecraftClient client = MinecraftClient.getInstance();
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
