package absolutelyaya.ultracraft;

import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public enum Layer
{
	OVERWORLD(World.OVERWORLD, null, new Identifier(Ultracraft.MOD_ID, "dimension.overworld")),
	LIMBO(RegistryKey.of(RegistryKeys.WORLD, new Identifier(Ultracraft.MOD_ID, "limbo")), new BlockPos(0, 36, 24),
			new Identifier(Ultracraft.MOD_ID, "dimension.limbo"));
	public final RegistryKey<World> worldKey;
	public final BlockPos arrivalPos;
	public final Identifier progression;
	
	Layer(RegistryKey<World> worldKey, BlockPos arrivalPos, Identifier progression)
	{
		this.worldKey = worldKey;
		this.arrivalPos = arrivalPos;
		this.progression = progression;
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