package absolutelyaya.ultracraft;

import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public enum Layer
{
	OVERWORLD(Ultracraft.identifier("prelude"), null, Ultracraft.identifier("dimension.overworld")),
	LIMBO(Ultracraft.identifier("limbo"), new BlockPos(0, 36, 24), Ultracraft.identifier("dimension.limbo"));
	public final BlockPos arrivalPos;
	public final Identifier progression;
	public final Identifier id;
	
	Layer(Identifier id, BlockPos arrivalPos, Identifier progression)
	{
		this.id = id;
		this.arrivalPos = arrivalPos;
		this.progression = progression;
	}
	
	public static Layer fromIdentifier(Identifier id)
	{
		for (Layer l : values())
		{
			if(l.id.equals(id))
				return l;
		}
		return null;
	}
	
	public RegistryKey<World> getWorldKey()
	{
		if(id.equals(Ultracraft.identifier("prelude")))
			return World.OVERWORLD;
		return RegistryKey.of(RegistryKeys.WORLD, id);
	}
}