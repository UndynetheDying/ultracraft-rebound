package absolutelyaya.ultracraft.client.sound;

import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class ModularLevelMusic
{
	final Identifier calmID, combatID, introID;
	final int combatThreshold;
	final boolean noCalmdown;
	
	public ModularLevelMusic(Identifier calmID, Identifier combatID, int combatThreshold, boolean noCalmdown, Identifier intoID)
	{
		this.calmID = calmID;
		this.combatID = combatID;
		this.combatThreshold = combatThreshold;
		this.noCalmdown = noCalmdown;
		this.introID = intoID;
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
	
	public SoundEvent getIntroSound()
	{
		if(introID == null)
			return null;
		return SoundEvent.of(introID);
	}
	
	public boolean isHasIntro()
	{
		return introID != null;
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
