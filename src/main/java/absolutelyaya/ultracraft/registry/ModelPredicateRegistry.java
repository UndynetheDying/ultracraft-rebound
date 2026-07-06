package absolutelyaya.ultracraft.registry;

import absolutelyaya.ultracraft.Ultracraft;
import absolutelyaya.ultracraft.block.AbstractPedestalBlock;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;

public class ModelPredicateRegistry
{
	public static void registerModels()
	{
		ModelPredicateProviderRegistry.register(BlockRegistry.PEDESTAL.asItem(), Ultracraft.identifier("type"),
				(stack, world, entity, seed) -> {
					if(!stack.hasNbt())
						return 0;
					NbtCompound state = null;
					if(stack.getNbt().contains("BlockStateTag", NbtElement.COMPOUND_TYPE))
						state = stack.getNbt().getCompound("BlockStateTag");
					else if(stack.getNbt().contains("BlockEntityTag", NbtElement.COMPOUND_TYPE))
						state = stack.getNbt().getCompound("BlockEntityTag");
					if(state == null || !state.contains("type", NbtElement.STRING_TYPE))
						return 0;
					AbstractPedestalBlock.Type type = AbstractPedestalBlock.Type.valueOf(state.getString("type").toUpperCase());
					for (int i = 0; i < AbstractPedestalBlock.Type.values().length; i++)
					{
						if(type.equals(AbstractPedestalBlock.Type.values()[i]))
							return i / 10f;
					}
					return 0;
				});
		ModelPredicateProviderRegistry.register(BlockRegistry.SLAB_BLOCK.asItem(), Ultracraft.identifier("number"),
				(stack, world, entity, seed) -> {
					if(!stack.hasNbt())
						return 0;
					NbtCompound state = null;
					if(stack.getNbt().contains("BlockStateTag", NbtElement.COMPOUND_TYPE))
						state = stack.getNbt().getCompound("BlockStateTag");
					if(state == null || !state.contains("number", NbtElement.STRING_TYPE))
						return 0;
					try
					{
						return Integer.parseInt(state.getString("number")) / 10f;
					}
					catch (NumberFormatException e)
					{
						return 0;
					}
				});
	}
}
