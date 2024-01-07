package absolutelyaya.ultracraft.block.mapping;

public class LevelBlock extends AbstractMappingBlock
{
	static int i = 0;
	
	public LevelBlock(Settings settings)
	{
		super(settings);
		id = "level-" + i++;
	}
}
