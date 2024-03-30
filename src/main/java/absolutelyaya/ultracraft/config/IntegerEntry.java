package absolutelyaya.ultracraft.config;

public class IntegerEntry extends NumberEntry<Integer>
{
	
	public IntegerEntry(String id, Integer defaultValue)
	{
		super(id, defaultValue);
	}
	
	@Override
	public void deserialize(String value)
	{
		this.value = Integer.parseInt(value);
	}
}
