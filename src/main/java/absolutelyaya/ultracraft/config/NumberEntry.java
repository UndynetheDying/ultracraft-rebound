package absolutelyaya.ultracraft.config;

public abstract class NumberEntry <T extends Number> extends ConfigEntry<T>
{
	protected T min, max;
	
	public NumberEntry(String id, T defaultValue)
	{
		super(id, defaultValue);
	}
	
	@Override
	public boolean isValid(T v)
	{
		return v.floatValue() >= min.floatValue() && v.floatValue() <= max.floatValue();
	}
	
	/**
	 * Min and max are both inclusive
	 */
	public NumberEntry<T> setRange(T min, T max)
	{
		this.min = min;
		this.max = max;
		return this;
	}
	
	public T getMin()
	{
		return min;
	}
	
	public T getMax()
	{
		return max;
	}
}
