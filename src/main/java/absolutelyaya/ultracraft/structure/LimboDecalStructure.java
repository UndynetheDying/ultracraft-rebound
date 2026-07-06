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
import absolutelyaya.ultracraft.util.WeightedList;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.gen.structure.StructureType;

import java.util.Optional;

public class LimboDecalStructure extends Structure
{
	static final WeightedList<Identifier[]> PIECES = new WeightedList<>() {
		{
			add(getIdArray("mini_ruin", 3), 3);
			add(getIdArray("plant_pillar", 2), 3);
			add(getIdArray("wide_pillar", 2), 2);
			add(getIdArray("path", 1), 2);
			add(getIdArray("ring_smol", 2), 2);
			add(getIdArray("ring_tall", 1), 1);
			add(getIdArray("misc", 4), 3);
			add(getIdArray("gateway", 2), 1);
			add(getIdArray("plant_wall", 3), 2);
			add(getIdArray("fountain", 2), 2);
		}
	};
	public static final Codec<LimboDecalStructure> CODEC = LimboDecalStructure.createCodec(LimboDecalStructure::new);
	
	protected LimboDecalStructure(Config config)
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
			Identifier[] list = PIECES.getRandomItem();
			collector.addPiece(new Piece(context.structureTemplateManager(), list[context.random().nextInt(list.length)], pos.position().up(), blockRotation));
		});
	}
	
	@Override
	public StructureType<?> getType()
	{
		return StructureRegistry.LIMBO_DECAL;
	}
	
	static Identifier[] getIdArray(String id, int count)
	{
		Identifier[] ids = new Identifier[count];
		for (int i = 0; i < count; i++)
		{
			ids[i] = Ultracraft.identifier("limbo/" + id + (i + 1));
		}
		return ids;
	}
	
	public static class Piece extends GenericRotatedPiece
	{
		public Piece(StructureTemplateManager structureTemplateManager, Identifier id, BlockPos pos, BlockRotation rot)
		{
			super(StructureRegistry.LIMBO_DECAL_PIECE, structureTemplateManager, id, pos, rot);
		}
		
		public Piece(StructureContext structureContext, NbtCompound nbt)
		{
			super(StructureRegistry.LIMBO_DECAL_PIECE, structureContext, nbt);
		}
		
		@Override
		public void generate(StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox chunkBox, ChunkPos chunkPos, BlockPos pivot)
		{
			pos = startPos.down();
			super.generate(world, structureAccessor, chunkGenerator, random, chunkBox, chunkPos, pivot.down());
		}
	}
}
