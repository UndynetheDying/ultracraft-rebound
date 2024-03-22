package absolutelyaya.ultracraft.structure;

import absolutelyaya.ultracraft.Ultracraft;
import absolutelyaya.ultracraft.registry.StructureRegistry;
import com.mojang.serialization.Codec;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.structure.*;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.gen.structure.StructureType;

import java.util.Optional;

public class LimboRuinStructure extends Structure
{
	static final Identifier CHAPEL = new Identifier(Ultracraft.MOD_ID, "limbo/ruin1");
	static final Identifier CASTLE = new Identifier(Ultracraft.MOD_ID, "limbo/ruin2");
	static final Identifier[] PIECES = new Identifier[] { CHAPEL, CASTLE };
	public static final Codec<LimboRuinStructure> CODEC = LimboRuinStructure.createCodec(LimboRuinStructure::new);
	
	protected LimboRuinStructure(Config config)
	{
		super(config);
	}
	
	@Override
	protected Optional<StructurePosition> getStructurePosition(Context context)
	{
		return LimboRuinStructure.getStructurePosition(context, Heightmap.Type.WORLD_SURFACE_WG, collector -> addPieces(collector, context));
	}
	
	private void addPieces(StructurePiecesCollector collector, Structure.Context context)
	{
		getStructurePosition(context).ifPresent(pos -> {
			BlockRotation blockRotation = BlockRotation.random(context.random());
			collector.addPiece(new Piece(context.structureTemplateManager(), PIECES[context.random().nextInt(PIECES.length)], pos.position().up(), blockRotation));
		});
	}
	
	@Override
	public StructureType<?> getType()
	{
		return StructureRegistry.LIMBO_RUIN;
	}
	
	public static class Piece extends GenericRotatedPiece
	{
		public Piece(StructureTemplateManager structureTemplateManager, Identifier id, BlockPos pos, BlockRotation rot)
		{
			super(StructureRegistry.LIMBO_RUIN_PIECE, structureTemplateManager, id, pos, rot);
		}
		
		public Piece(StructureContext structureContext, NbtCompound nbt)
		{
			super(StructureRegistry.LIMBO_RUIN_PIECE, structureContext, nbt);
		}
	}
}
