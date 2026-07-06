package absolutelyaya.ultracraft.config;

public class BooleanEntry extends ConfigEntry<Boolean>
{
	public BooleanEntry(String id, boolean defaultValue)
	{
		super(id, defaultValue);
	}
	
	@Override
	public void deserialize(String value)
	{
		this.value = Boolean.parseBoolean(value);
	}
}
