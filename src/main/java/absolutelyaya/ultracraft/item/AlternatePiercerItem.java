package absolutelyaya.ultracraft.item;

public class AlternatePiercerItem extends PierceRevolverItem
{
	public AlternatePiercerItem(Settings settings)
	{
		super(settings);
	}
	
	@Override
	protected boolean isAlternate()
	{
		return true;
	}
}
