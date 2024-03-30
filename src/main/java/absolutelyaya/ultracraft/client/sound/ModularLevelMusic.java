package absolutelyaya.ultracraft.client.sound;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class ModularLevelMusic
{
	final RegistryEntry<SoundEvent> calm, combat;
	
	public ModularLevelMusic(Identifier calm, Identifier combat)
	{
		Registry<SoundEvent> registry = Registries.SOUND_EVENT;
		this.calm = registry.getEntry(registry.get(calm));
		this.combat = registry.getEntry(registry.get(combat));
	}
	
	public ModularLevelMusic(RegistryEntry<SoundEvent> calm, RegistryEntry<SoundEvent> combat)
	{
		this.calm = calm;
		this.combat = combat;
	}
	
	public RegistryEntry<SoundEvent> getCalmSound()
	{
		return calm;
	}
	
	public RegistryEntry<SoundEvent> getCombatSound()
	{
		return combat;
	}
}
