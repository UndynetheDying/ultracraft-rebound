package absolutelyaya.ultracraft.structure;

import absolutelyaya.ultracraft.Ultracraft;
import absolutelyaya.ultracraft.registry.StructureRegistry;
import com.mojang.serialization.Codec;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.structure.StructureContext;
import net.minecraft.structure.StructurePiecesCollector;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.gen.structure.StructureType;

import java.util.Optional;

public class AbandonedFacilityStructure extends Structure
{
	static final Identifier ID = new Identifier(Ultracraft.MOD_ID, "abandoned_facility1");
	public static final Codec<AbandonedFacilityStructure> CODEC = AbandonedFacilityStructure.createCodec(AbandonedFacilityStructure::new);
	
	protected AbandonedFacilityStructure(Config config)
	{
		super(config);
	}
	
	@Override
	protected Optional<StructurePosition> getStructurePosition(Context context)
	{
		return AbandonedFacilityStructure.getStructurePosition(context, Heightmap.Type.WORLD_SURFACE_WG, collector -> addPieces(collector, context));
	}
	
	private void addPieces(StructurePiecesCollector collector, Structure.Context context)
	{
		getStructurePosition(context).ifPresent(pos -> {
			BlockRotation blockRotation = BlockRotation.random(context.random());
			collector.addPiece(new Piece(context.structureTemplateManager(), ID, pos.position().up(100), blockRotation));
		});
	}
	
	@Override
	public StructureType<?> getType()
	{
		return StructureRegistry.ABANDONED_FACILITY;
	}
	
	public static class Piece extends GenericRotatedPiece
	{
		public Piece(StructureTemplateManager structureTemplateManager, Identifier id, BlockPos pos, BlockRotation rot)
		{
			super(StructureRegistry.ABANDONED_FACILITY_PIECE, structureTemplateManager, id, pos.up(100), rot);
		}
		
		public Piece(StructureContext structureContext, NbtCompound nbt)
		{
			super(StructureRegistry.ABANDONED_FACILITY_PIECE, structureContext, nbt);
		}
	}
}
