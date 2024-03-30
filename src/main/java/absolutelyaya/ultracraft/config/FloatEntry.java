package absolutelyaya.ultracraft.config;

public class FloatEntry extends NumberEntry<Float>
{
	public FloatEntry(String id, Float defaultValue)
	{
		super(id, defaultValue);
	}
	
	@Override
	public void deserialize(String value)
	{
		this.value = Float.valueOf(value);
	}
}
