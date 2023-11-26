package absolutelyaya.ultracraft.config;

public class EnumEntry<E extends Enum<E>> extends ConfigEntry<Enum<E>>
{
	protected final Class<E> valueClass;
	protected E[] options;
	
	public EnumEntry(String id, E defaultValue)
	{
		super(id, defaultValue);
		valueClass = defaultValue.getDeclaringClass();
	}
	
	@Override
	public void deserialize(String value)
	{
		E v = E.valueOf(valueClass, value);
		this.value = isValid(v) ? v : defaultValue;
	}
	
	public EnumEntry<E> setValidOptions(E[] options)
	{
		this.options = options;
		return this;
	}
	
	public E[] getValidOptions()
	{
		return options;
	}
	
	@Override
	public boolean isValid(Enum<E> v)
	{
		if(options == null)
			return true;
		for (E option : options)
			if(v.equals(option))
				return true;
		return false;
	}
}
