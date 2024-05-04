package absolutelyaya.ultracraft.client.sound;

import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class ModularLevelMusic
{
	final String author;
	final Identifier calmID, combatID;
	
	public ModularLevelMusic(String author, Identifier calmID, Identifier combatID)
	{
		this.author = author;
		this.calmID = calmID;
		this.combatID = combatID;
	}
	
	public String getAuthor()
	{
		return author;
	}
	
	public SoundEvent getCalmSound()
	{
		if(calmID == null)
			return null;
		return SoundEvent.of(calmID);
	}
	
	public SoundEvent getCombatSound()
	{
		if(combatID == null)
			return null;
		return SoundEvent.of(combatID);
	}
}
