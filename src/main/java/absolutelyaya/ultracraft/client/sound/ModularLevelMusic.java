package absolutelyaya.ultracraft.client.sound;

import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class ModularLevelMusic
{
	final String author, name;
	final Identifier calmID, combatID;
	final int color;
	
	public ModularLevelMusic(String author, String name, int color, Identifier calmID, Identifier combatID)
	{
		this.author = author;
		this.name = name;
		this.color = color;
		this.calmID = calmID;
		this.combatID = combatID;
	}
	
	public String getAuthor()
	{
		return author;
	}
	
	public String getTrackName()
	{
		return name;
	}
	
	public int getColor()
	{
		return color;
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
	
	public boolean shouldShowPopup()
	{
		return author != null && !author.isEmpty();
	}
}
