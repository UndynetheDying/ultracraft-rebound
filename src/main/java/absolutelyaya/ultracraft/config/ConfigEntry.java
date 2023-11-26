package absolutelyaya.ultracraft.config;

public abstract class ConfigEntry<T>
{
	protected final String id;
	protected T value = null, defaultValue;
	
	public ConfigEntry(String id, T defaultValue)
	{
		this.id = id;
		this.defaultValue = defaultValue;
	}
	
	public String getId()
	{
		return id;
	}
	
	public T getValue()
	{
		if(value == null)
			return defaultValue;
		return value;
	}
	
	public String serialize()
	{
		return String.format("%s:%s", id, getValue());
	}
	
	public abstract void deserialize(String value);
	
	public boolean isValid(T v)
	{
		return true;
	}
}
