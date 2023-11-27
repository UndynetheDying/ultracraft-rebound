package absolutelyaya.ultracraft.config;

public abstract class ConfigEntry<T>
{
	protected final String id;
	protected T value = null, defaultValue;
	protected String translationKey;
	
	public ConfigEntry(String id, T defaultValue)
	{
		this.id = id;
		this.defaultValue = defaultValue;
		translationKey = "config.ultracraft." + id;
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
	
	public void setValue(T value)
	{
		this.value = value;
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
	
	public String getTranslationKey()
	{
		return translationKey;
	}
	
	public ConfigEntry<T> setTranslationKey(String key)
	{
		this.translationKey = key;
		return this;
	}
}
