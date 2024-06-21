package absolutelyaya.ultracraft.mixin.client;

import absolutelyaya.ultracraft.client.sound.INonPausingSoundInstance;
import net.minecraft.client.sound.*;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Map;

@Mixin(SoundSystem.class)
public abstract class SoundSystemMixin
{
	@Shadow @Final private List<TickableSoundInstance> tickingSounds;
	
	@Shadow public abstract void stop(SoundInstance sound);
	
	@Shadow @Final private Map<SoundInstance, Channel.SourceManager> sources;
	
	@Shadow protected abstract float getAdjustedVolume(SoundInstance sound);
	
	@Shadow protected abstract float getAdjustedPitch(SoundInstance sound);
	
	@Shadow @Final private Channel channel;
	
	@Inject(method = "tick(Z)V", at = @At("HEAD"))
	void onTick(boolean paused, CallbackInfo ci)
	{
		if(paused)
		{
			tickingSounds.forEach(i -> {
				if(i instanceof INonPausingSoundInstance)
				{
					i.tick();
					if(i.isDone())
						stop(i);
					else
					{
						float volume = getAdjustedVolume(i);
						float pitch = getAdjustedPitch(i);
						Channel.SourceManager manager = sources.get(i);
						//since the game is paused, updating the sources position shouldn't be necessary.
						if(manager != null)
						{
							manager.run(source -> {
								source.setVolume(volume);
								source.setPitch(pitch);
							});
						}
					}
				}
			});
		}
	}
	
	@Inject(method = "pauseAll", at = @At("TAIL"))
	void onPauseAll(CallbackInfo ci)
	{
		tickingSounds.forEach(i -> {
			if(i instanceof INonPausingSoundInstance && sources.get(i) != null)
				sources.get(i).run(Source::resume);
		});
	}
}
