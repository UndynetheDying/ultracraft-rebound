package absolutelyaya.ultracraft.dimension;

import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public final class LevelData
{
	private final Text title;
	private final Text Description;
	private final Text author;
	private final String authorLink;
	private final long parTime;
	private final Identifier thumbnail;
	private final Identifier structure;
	private final BlockPos spawnOffset;
	private final String parTimeString;
	
	public LevelData(Text title, Text description, Text author, String authorLink, long parTime, String parTimeString, Identifier thumbnail, Identifier structure, BlockPos spawnOffset)
	{
		this.title = title;
		this.Description = description;
		this.author = author;
		this.authorLink = authorLink;
		this.parTime = parTime;
		this.thumbnail = thumbnail;
		this.structure = structure;
		this.spawnOffset = spawnOffset;
		this.parTimeString = parTimeString;
	}
	
	public Text title()
	{
		return title;
	}
	
	public Text Description()
	{
		return Description;
	}
	
	public Text author()
	{
		return author;
	}
	
	public String authorLink()
	{
		return authorLink;
	}
	
	public long parTime()
	{
		return parTime;
	}
	
	public Identifier thumbnail()
	{
		return thumbnail;
	}
	
	public Identifier structure()
	{
		return structure;
	}
	
	public BlockPos spawnOffset()
	{
		return spawnOffset;
	}
	
	public String parTimeString()
	{
		return parTimeString;
	}
	
	@Override
	public String toString()
	{
		return "LevelData[" +
					   "title=" + title + ", " +
					   "Description=" + Description + ", " +
					   "author=" + author + ", " +
					   "authorLink=" + authorLink + ", " +
					   "parTime=" + parTime + ", " +
					   "parTimeString=" + parTimeString + ", " +
					   "thumbnail=" + thumbnail + ", " +
					   "structure=" + structure + ", " +
					   "spawnOffset=" + spawnOffset + ']';
	}
}
