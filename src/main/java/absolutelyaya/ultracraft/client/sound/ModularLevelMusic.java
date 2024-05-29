package absolutelyaya.ultracraft.client.sound;

import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class ModularLevelMusic
{
	final Identifier calmID, combatID;
	final int combatThreshold;
	final boolean noCalmdown;
	
	public ModularLevelMusic(Identifier calmID, Identifier combatID, int combatThreshold, boolean noCalmdown)
	{
		this.calmID = calmID;
		this.combatID = combatID;
		this.combatThreshold = combatThreshold;
		this.noCalmdown = noCalmdown;
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
	
	public int getCombatThreshold()
	{
		return combatThreshold;
	}
	
	public boolean isNoCalmdown()
	{
		return noCalmdown;
	}
}
