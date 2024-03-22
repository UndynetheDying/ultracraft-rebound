package absolutelyaya.ultracraft.structure;

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

public class GenericRotatedPiece extends SimpleStructurePiece
{
	public GenericRotatedPiece(StructurePieceType type, StructureTemplateManager structureTemplateManager, Identifier id, BlockPos pos, BlockRotation rot)
	{
		super(type, 0, structureTemplateManager, id, id.toString(), createPlacementData(rot), pos);
	}
	
	public GenericRotatedPiece(StructurePieceType type, StructureContext structureContext, NbtCompound nbt)
	{
		super(type, nbt, structureContext.structureTemplateManager(),
				identifier -> createPlacementData(BlockRotation.valueOf(nbt.getString("Rot"))));
	}
	
	private static StructurePlacementData createPlacementData(BlockRotation rotation)
	{
		return new StructurePlacementData().setRotation(rotation).setMirror(BlockMirror.NONE).setPosition(new BlockPos(0, -1, 0))
					   .addProcessor(BlockIgnoreStructureProcessor.IGNORE_STRUCTURE_BLOCKS);
	}
	
	@Override
	protected void handleMetadata(String metadata, BlockPos pos, ServerWorldAccess world, Random random, BlockBox boundingBox) {
	}
	
	@Override
	public void generate(StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox chunkBox, ChunkPos chunkPos, BlockPos pivot)
	{
		pos = new BlockPos(pos.getX(), world.getTopY(Heightmap.Type.WORLD_SURFACE_WG, pos.getX(), pos.getZ()), pos.getZ()).down();
		super.generate(world, structureAccessor, chunkGenerator, random, chunkBox, chunkPos, pivot);
	}
	
	@Override
	protected void writeNbt(StructureContext context, NbtCompound nbt)
	{
		super.writeNbt(context, nbt);
		nbt.putString("Rot", placementData.getRotation().name());
	}
}
