package absolutelyaya.ultracraft.client.sound;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class ModularLevelMusic
{
	final RegistryEntry<SoundEvent> calm, fight;
	
	public ModularLevelMusic(Identifier calm, Identifier fight)
	{
		Registry<SoundEvent> registry = Registries.SOUND_EVENT;
		this.calm = registry.getEntry(registry.get(calm));
		this.fight = registry.getEntry(registry.get(fight));
	}
	
	public ModularLevelMusic(RegistryEntry<SoundEvent> calm, RegistryEntry<SoundEvent> fight)
	{
		this.calm = calm;
		this.fight = fight;
	}
	
	public RegistryEntry<SoundEvent> getCalmSound()
	{
		return calm;
	}
	
	public RegistryEntry<SoundEvent> getFightSound()
	{
		return fight;
	}
}
