package absolutelyaya.ultracraft;

import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public enum Layer
{
	OVERWORLD(World.OVERWORLD, null),
	LIMBO(RegistryKey.of(RegistryKeys.WORLD, new Identifier(Ultracraft.MOD_ID, "limbo")), new BlockPos(0, 38, 0));
	public final RegistryKey<World> worldKey;
	public final BlockPos arrivalPos;
	
	Layer(RegistryKey<World> worldKey, BlockPos arrivalPos)
	{
		this.worldKey = worldKey;
		this.arrivalPos = arrivalPos;
	}
	
	public static Layer fromRegistryKey(RegistryKey<World> registryKey)
	{
		for (Layer l : values())
		{
			if(l.worldKey.equals(registryKey))
				return l;
		}
		return null;
	}
}