package absolutelyaya.ultracraft.structure;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.structure.*;
import net.minecraft.structure.processor.BlockIgnoreStructureProcessor;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.ServerWorldAccess;

public class GenericRotatedPiece extends SimpleStructurePiece
{
	protected BlockPos startPos;
	
	public GenericRotatedPiece(StructurePieceType type, StructureTemplateManager structureTemplateManager, Identifier id, BlockPos pos, BlockRotation rot)
	{
		super(type, 0, structureTemplateManager, id, id.toString(), createPlacementData(rot), pos);
		startPos = this.pos;
	}
	
	public GenericRotatedPiece(StructurePieceType type, StructureContext structureContext, NbtCompound nbt)
	{
		super(type, nbt, structureContext.structureTemplateManager(),
				identifier -> createPlacementData(BlockRotation.valueOf(nbt.getString("Rot"))));
		startPos = this.pos;
	}
	
	private static StructurePlacementData createPlacementData(BlockRotation rotation)
	{
		return new StructurePlacementData().setRotation(rotation).setMirror(BlockMirror.NONE)
					   .addProcessor(BlockIgnoreStructureProcessor.IGNORE_STRUCTURE_BLOCKS);
	}
	
	@Override
	protected void handleMetadata(String metadata, BlockPos pos, ServerWorldAccess world, Random random, BlockBox boundingBox) {
	}
	
	@Override
	protected void writeNbt(StructureContext context, NbtCompound nbt)
	{
		super.writeNbt(context, nbt);
		nbt.putString("Rot", placementData.getRotation().name());
	}
}
