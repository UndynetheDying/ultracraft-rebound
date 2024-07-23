package absolutelyaya.ultracraft.client.sound;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class SoundInstanceManager
{
	static final Map<Integer, Map<String, MovingEntitySoundInstance>> movingSounds = new HashMap<>();
	
	public static void attachWeaponSoundInstance(String id, Identifier sound, PlayerEntity target, boolean warmUp, float volume)
	{
		if(!movingSounds.containsKey(target.getId()))
			movingSounds.put(target.getId(), new HashMap<>());
		Map<String, MovingEntitySoundInstance> entry = movingSounds.get(target.getId());
		if(entry.containsKey(id))
			removeSoundInstance(id, target);
		MovingEntitySoundInstance instance = new MovingWeaponSoundInstance(SoundEvent.of(sound), target, warmUp, volume);
		entry.put(id, instance);
	}
	
	public static void attachMovementSounds(PlayerEntity target)
	{
		SoundManager sound = MinecraftClient.getInstance().getSoundManager();
		sound.play(new MovingSlideSoundInstance(target));
		sound.play(new MovingWindSoundInstance(target));
	}
	
	public static void removeSoundInstance(String id, PlayerEntity target)
	{
		if(!movingSounds.containsKey(target.getId()))
			return;
		Map<String, MovingEntitySoundInstance> entry = movingSounds.get(target.getId());
		MovingEntitySoundInstance sound = entry.remove(id);
		if(sound != null)
			sound.setFinished();
	}
}
