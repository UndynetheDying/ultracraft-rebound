package absolutelyaya.ultracraft.dimension;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public record LevelData(Identifier structure, BlockPos pos, BlockPos spawnOffset)
{
	public BlockPos getSpawnPos()
	{
		return pos.add(spawnOffset);
	}
}
