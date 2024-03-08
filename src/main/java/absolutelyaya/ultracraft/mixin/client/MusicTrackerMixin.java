package absolutelyaya.ultracraft.mixin.client;

import absolutelyaya.ultracraft.client.UltracraftClient;
import absolutelyaya.ultracraft.client.sound.ModularLevelMusic;
import absolutelyaya.ultracraft.client.sound.ModularMusicInstance;
import absolutelyaya.ultracraft.components.UltraComponents;
import absolutelyaya.ultracraft.components.player.IWingedPlayerComponent;
import absolutelyaya.ultracraft.data.LevelDataManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.*;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MusicTracker.class)
public abstract class MusicTrackerMixin
{
	Identifier curLevelMusic;
	ModularMusicInstance calm, fight;
	float action;
	
	@Shadow @Final private MinecraftClient client;
	
	@Shadow private @Nullable SoundInstance current;
	
	@Shadow public abstract void stop();
	
	@Inject(method = "tick", at = @At("HEAD"), cancellable = true)
	void onTick(CallbackInfo ci)
	{
		if (client.getMusicType() != null)
		{
			if(curLevelMusic != null)
				stopModular();
			return;
		}
		ci.cancel();
		if (client.player == null)
		{
			if(curLevelMusic != null)
				stopModular();
			return;
		}
		IWingedPlayerComponent winged =  UltraComponents.WINGED.get(client.player);
		Identifier level = winged.getCurrentLevel();
		ModularLevelMusic music = null;
		if(level != null)
			music = LevelDataManager.getLevelData(level).getMusic();
		if(music == null)
		{
			if(curLevelMusic != null)
				stopModular();
			return;
		}
		if (current != null)
			stop();
		if(curLevelMusic == null || !curLevelMusic.equals(level))
		{
			if(music.getCalmSound() != null)
				calm = playModular(music.getCalmSound(), false);
			if(music.getCombatSound() != null)
				fight = playModular(music.getCombatSound(), true);
			if(calm == null && fight != null)
				fight.setVolume(1f);
			curLevelMusic = level;
		}
		else if(calm != null && fight != null)
		{
			action = MathHelper.clamp(action + (winged.isInFight() ? 0.05f : -0.05f) * client.getTickDelta() *
													   UltracraftClient.getConfig().musicTransitionSpeed, 0f, 1f);
			calm.setVolume(1f - action);
			fight.setVolume(action);
		}
		if(calm != null && !client.getSoundManager().isPlaying(calm))
			client.getSoundManager().play(calm);
		if(fight != null && !client.getSoundManager().isPlaying(fight))
			client.getSoundManager().play(fight);
	}
	
	ModularMusicInstance playModular(RegistryEntry<SoundEvent> sound, boolean fight)
	{
		if(sound.value() == null)
			return null;
		ModularMusicInstance instance = new ModularMusicInstance(sound.value(), fight);
		if (instance.getSound() != SoundManager.MISSING_SOUND)
			client.getSoundManager().play(instance);
		return instance;
	}
	
	void stopModular()
	{
		if(calm != null)
		{
			client.getSoundManager().stop(calm);
			calm = null;
		}
		if(fight != null)
		{
			client.getSoundManager().stop(fight);
			fight = null;
		}
		curLevelMusic = null;
	}
}
