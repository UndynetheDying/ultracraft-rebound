package absolutelyaya.ultracraft.registry;

import absolutelyaya.ultracraft.Ultracraft;
import absolutelyaya.ultracraft.structure.LimboDecalStructure;
import absolutelyaya.ultracraft.structure.LimboRuinStructure;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.structure.StructureType;

public class StructureRegistry
{
	public static final StructureType<LimboRuinStructure> LIMBO_RUIN = Registry.register(Registries.STRUCTURE_TYPE,
			new Identifier(Ultracraft.MOD_ID, "limbo_ruin"), () -> LimboRuinStructure.CODEC);
	public static final StructureType<LimboDecalStructure> LIMBO_DECAL = Registry.register(Registries.STRUCTURE_TYPE,
			new Identifier(Ultracraft.MOD_ID, "limbo_decal"), () -> LimboDecalStructure.CODEC);
	
	public static final StructurePieceType LIMBO_RUIN_PIECE = Registry.register(Registries.STRUCTURE_PIECE,
			new Identifier(Ultracraft.MOD_ID, "limbo_ruin"), LimboRuinStructure.Piece::new);
	public static final StructurePieceType LIMBO_DECAL_PIECE = Registry.register(Registries.STRUCTURE_PIECE,
			new Identifier(Ultracraft.MOD_ID, "limbo_decal"), LimboDecalStructure.Piece::new);
	
	public static void register()
	{
	
	}
}
