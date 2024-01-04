package absolutelyaya.ultracraft.components.world;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;

public class DimensionDataComponent implements IDimensionDataComponent
{
	final World provider;
	
	Map<String, Integer> flags = new HashMap<>();
	boolean fixedStructuresPlaced;
	
	public DimensionDataComponent(World provider)
	{
		this.provider = provider;
	}
	
	@Override
	public boolean isFixedStructuresPlaced()
	{
		return fixedStructuresPlaced;
	}
	
	@Override
	public void setFixedStructuresPlaced(boolean b)
	{
		fixedStructuresPlaced = b;
	}
	
	@Override
	public Map<String, Integer> getAllFlags()
	{
		return flags;
	}
	
	@Override
	public int getFlag(String id)
	{
		return flags.getOrDefault(id, -1);
	}
	
	@Override
	public void setFlag(String id, int value) //TODO: add command to set and query flags
	{
		flags.put(id, value);
	}
	
	@Override
	public void readFromNbt(NbtCompound tag)
	{
		if(tag.contains("fixedStructuresPlaced", NbtElement.BYTE_TYPE))
			fixedStructuresPlaced = tag.getBoolean("fixedStructuresPlaced");
		if(tag.contains("flags", NbtElement.COMPOUND_TYPE))
		{
			NbtCompound flagCompound = tag.getCompound("flags");
			for (String id : flagCompound.getKeys())
			{
				if(flagCompound.contains(id, NbtElement.INT_TYPE))
					flags.put(id, flagCompound.getInt(id));
			}
		}
	}
	
	@Override
	public void writeToNbt(NbtCompound tag)
	{
		tag.putBoolean("fixedStructuresPlaced", fixedStructuresPlaced);
		NbtCompound flagCompound = new NbtCompound();
		for (String id : flags.keySet())
			flagCompound.putInt(id, flags.get(id));
		tag.put("flags", flagCompound);
	}
}
