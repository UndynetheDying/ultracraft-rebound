package absolutelyaya.ultracraft.registry;

import absolutelyaya.ultracraft.Ultracraft;
import net.minecraft.block.Block;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class TagRegistry
{
	public static final TagKey<Block> FRAGILE = TagKey.of(RegistryKeys.BLOCK, Ultracraft.identifier("fragile"));
	public static final TagKey<Block> EXPLOSION_BREAKABLE = TagKey.of(RegistryKeys.BLOCK, Ultracraft.identifier("explosion_breakable"));
	public static final TagKey<Block> PUNCH_BREAKABLE = TagKey.of(RegistryKeys.BLOCK, Ultracraft.identifier("punch_breakable"));
	public static final TagKey<Block> KNUCKLE_BREAKABLE = TagKey.of(RegistryKeys.BLOCK, Ultracraft.identifier("knuckle_breakable"));
	public static final TagKey<Block> KNUCKLE_BLAST_BREAKABLE = TagKey.of(RegistryKeys.BLOCK, Ultracraft.identifier("knuckle_blast_breakable"));
	public static final TagKey<Block> SLAM_BREAKABLE = TagKey.of(RegistryKeys.BLOCK, Ultracraft.identifier("slam_breakable"));
	public static final TagKey<Block> CANNOT_CONNECT = TagKey.of(RegistryKeys.BLOCK, Ultracraft.identifier("cannot_connect"));
	
	public static final TagKey<Fluid> UNSKIMMABLE_FLUIDS = TagKey.of(RegistryKeys.FLUID, Ultracraft.identifier("unskimmable"));
	public static final TagKey<Fluid> BLOOD_FLUID = TagKey.of(RegistryKeys.FLUID, Ultracraft.identifier("blood"));
	
	public static final TagKey<Item> PUNCH_FLAMES = TagKey.of(RegistryKeys.ITEM, Ultracraft.identifier("punch_flames"));
	
	public static void register()
	{
	
	}
}
