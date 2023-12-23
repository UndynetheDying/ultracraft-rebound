package absolutelyaya.ultracraft.item;

public class AlternateSharpshooterItem extends SharpshooterRevolverItem
{
	public AlternateSharpshooterItem(Settings settings)
	{
		super(settings);
	}
	
	@Override
	protected boolean isAlternate()
	{
		return true;
	}
}
