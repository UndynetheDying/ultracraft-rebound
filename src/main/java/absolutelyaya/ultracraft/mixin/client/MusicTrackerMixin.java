package absolutelyaya.ultracraft.mixin.client;

import absolutelyaya.ultracraft.client.UltracraftClient;
import absolutelyaya.ultracraft.client.gui.LevelHUD;
import absolutelyaya.ultracraft.client.sound.FadingMusicInstance;
import absolutelyaya.ultracraft.client.sound.ModularLevelMusic;
import absolutelyaya.ultracraft.client.sound.ModularMusicInstance;
import absolutelyaya.ultracraft.components.UltraComponents;
import absolutelyaya.ultracraft.components.player.ILevelStatsComponent;
import absolutelyaya.ultracraft.data.LevelDataManager;
import absolutelyaya.ultracraft.registry.SoundRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.*;
import net.minecraft.sound.MusicSound;
import net.minecraft.sound.SoundCategory;
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
	ModularMusicInstance calm, combat;
	FadingMusicInstance cybergrind;
	PositionedSoundInstance intro;
	float action, minAction;
	int introTicks = -1;
	
	@Shadow @Final private MinecraftClient client;
	
	@Shadow private @Nullable SoundInstance current;
	
	@Shadow public abstract void stop();
	
	@Inject(method = "tick", at = @At("HEAD"), cancellable = true)
	void onTick(CallbackInfo ci)
	{
		MusicSound musicType = client.getMusicType();
		if (musicType != null)
		{
			if(curLevelMusic != null)
				stopModular(false);
			if(musicType.equals(SoundRegistry.CYBERGRIND_MUSIC))
			{
				if(current != null && !musicType.getSound().value().getId().equals(current.getId()))
					client.getSoundManager().stop(current);
				if(!client.getSoundManager().isPlaying(current))
				{
					FadingMusicInstance instance = new FadingMusicInstance(musicType.getSound().value(), 1f);
					if (instance.getSound() != SoundManager.MISSING_SOUND)
						client.getSoundManager().play(instance);
					current = instance;
				}
			}
			else if(current instanceof FadingMusicInstance fading && current.getId().equals(SoundRegistry.CYBERGRIND_MUSIC.getSound().value().getId()))
			{
				fading.startFadeout();
				current = null;
			}
			return;
		}
		ci.cancel();
		if (client.player == null)
		{
			if(curLevelMusic != null)
				stopModular(false);
			return;
		}
		ILevelStatsComponent levelStats = UltraComponents.LEVEL_STATS.get(client.player);
		Identifier level = levelStats.getCurrentLevel();
		ModularLevelMusic music = null;
		if(levelStats.shouldLevelMusicFade())
		{
			stopModular(true);
			levelStats.setShouldMusicFade(false);
			LevelHUD.clearLastPlayedMusicId();
			minAction = 0f;
		}
		String trackID = levelStats.getCurLevelSoundTrackKey();
		if(level != null && trackID != null)
			music = LevelDataManager.getLevelData(level).getMusic(trackID);
		if(music == null)
		{
			if(curLevelMusic != null)
				stopModular(false);
			return;
		}
		if (current != null) //stop any vanilla music
			stop();
		if((curLevelMusic == null || !curLevelMusic.equals(level)))
		{
			if(music.isHasIntro() && introTicks == -1)
			{
				intro = new PositionedSoundInstance(music.getIntroSound().getId(), SoundCategory.MUSIC, 1f, 1f, SoundInstance.createRandom(), false,
						0, SoundInstance.AttenuationType.NONE, 0.0, 0.0, 0.0, true);
				client.getSoundManager().play(intro);
  				introTicks = music.getIntroLength();
			}
			if(!music.isHasIntro() || (music.isHasIntro() && introTicks == 0))
			{
				if(music.getCalmSound() != null)
					calm = playModular(music.getCalmSound());
				if(music.getCombatSound() != null)
					combat = playModular(music.getCombatSound());
				if(calm == null && combat != null)
					combat.setVolume(1f);
				action = minAction = 0f;
				curLevelMusic = level;
				if(introTicks == 0)
				{
					if(calm != null)
						calm.skipFadein();
					if(combat != null)
						combat.skipFadein();
					introTicks = -1;
				}
			}
			else if(introTicks > 0)
				introTicks--;
		}
		else if(calm != null && combat != null)
		{
			action = MathHelper.clamp(action + (levelStats.isInCombat() ? 0.05f : -0.05f) * UltracraftClient.getDeltaTime() *
													   UltracraftClient.getConfig().musicTransitionSpeed, 0f, 2.5f);
			if(music.isNoCalmdown())
				minAction = action = Math.max(action, minAction);
			calm.setVolume(1f - Math.min(action, 1f));
			combat.setVolume(Math.min(action, 1f));
		}
		if(calm != null && !client.getSoundManager().isPlaying(calm))
			client.getSoundManager().play(calm);
		if(combat != null && !client.getSoundManager().isPlaying(combat))
			client.getSoundManager().play(combat);
	}
	
	ModularMusicInstance playModular(SoundEvent sound)
	{
		if(sound == null)
			return null;
		ModularMusicInstance instance = new ModularMusicInstance(sound);
		if (instance.getSound() != SoundManager.MISSING_SOUND)
			client.getSoundManager().play(instance);
		return instance;
	}
	
	void stopModular(boolean fade)
	{
		if(calm != null)
		{
			if(!fade)
				client.getSoundManager().stop(calm);
			else
				calm.startFadeout();
			calm = null;
		}
		if(combat != null)
		{
			if(!fade)
				client.getSoundManager().stop(combat);
			else
				combat.startFadeout();
			combat = null;
		}
		curLevelMusic = null;
	}
}
