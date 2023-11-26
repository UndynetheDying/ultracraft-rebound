package absolutelyaya.ultracraft.config;

public class Comment extends ConfigEntry<String>
{
	
	public Comment(String text)
	{
		super("#", text);
	}
	
	@Override
	public void deserialize(String value)
	{
	
	}
	
	@Override
	public String serialize()
	{
		return id + " " + defaultValue;
	}
}
