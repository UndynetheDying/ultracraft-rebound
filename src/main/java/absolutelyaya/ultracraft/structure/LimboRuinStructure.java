package absolutelyaya.ultracraft.structure;

import absolutelyaya.ultracraft.Ultracraft;
import absolutelyaya.ultracraft.registry.StructureRegistry;
import com.mojang.serialization.Codec;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.structure.*;
import net.minecraft.structure.processor.BlockIgnoreStructureProcessor;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Heightmap;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.gen.structure.StructureType;

import java.util.Optional;

public class LimboRuinStructure extends Structure
{
	static final BlockPos DEFAULT_POSITION = new BlockPos(4, 0, 15);
	static final Identifier CHAPEL = new Identifier(Ultracraft.MOD_ID, "limbo/ruin1");
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
		ChunkPos chunkPos = context.chunkPos();
		BlockPos blockPos = new BlockPos(chunkPos.getStartX(), 40, chunkPos.getStartZ());
		BlockRotation blockRotation = BlockRotation.random(context.random());
		collector.addPiece(new Piece(context.structureTemplateManager(), CHAPEL, blockPos, blockRotation));
	}
	
	@Override
	public StructureType<?> getType()
	{
		return StructureRegistry.LIMBO_RUIN;
	}
	
	public static class Piece extends SimpleStructurePiece
	{
		public Piece(StructureTemplateManager structureTemplateManager, Identifier id, BlockPos pos, BlockRotation rot)
		{
			super(StructureRegistry.LIMBO_RUIN_PIECE, 0, structureTemplateManager, id, id.toString(), createPlacementData(rot), pos);
		}
		
		public Piece(StructureContext structureContext, NbtCompound nbt)
		{
			super(StructureRegistry.LIMBO_RUIN_PIECE, nbt, structureContext.structureTemplateManager(),
					identifier -> createPlacementData(BlockRotation.valueOf(nbt.getString("Rot"))));
		}
		
		private static StructurePlacementData createPlacementData(BlockRotation rotation)
		{
			return new StructurePlacementData().setRotation(rotation).setMirror(BlockMirror.NONE)
						   .setPosition(DEFAULT_POSITION).addProcessor(BlockIgnoreStructureProcessor.IGNORE_STRUCTURE_BLOCKS);
		}
		
		@Override
		protected void handleMetadata(String metadata, BlockPos pos, ServerWorldAccess world, Random random, BlockBox boundingBox) {
		}
		
		@Override
		public void generate(StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox chunkBox, ChunkPos chunkPos, BlockPos pivot)
		{
			pos = new BlockPos(pos.getX(), world.getTopY(Heightmap.Type.WORLD_SURFACE_WG, pos.getX(), pos.getZ()), pos.getZ());
			super.generate(world, structureAccessor, chunkGenerator, random, chunkBox, chunkPos, pivot);
		}
		
		@Override
		protected void writeNbt(StructureContext context, NbtCompound nbt)
		{
			super.writeNbt(context, nbt);
			nbt.putString("Rot", placementData.getRotation().name());
		}
	}
}
