package absolutelyaya.ultracraft.dimension;

import absolutelyaya.ultracraft.UltraComponents;
import absolutelyaya.ultracraft.Ultracraft;
import absolutelyaya.ultracraft.block.SlabBlock;
import absolutelyaya.ultracraft.components.world.IDimensionDataComponent;
import absolutelyaya.ultracraft.config.ServerConfig;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;

public class LimboManager implements DimensionManager
{
	public static final Identifier ID = new Identifier(Ultracraft.MOD_ID, "limbo");
	public static final RegistryKey<World> WORLD_KEY = RegistryKey.of(RegistryKeys.WORLD, ID);
	final ServerWorld world;
	final Box spawnBounds = new Box(-26, 0, -26, 26, 100, 31);
	
	final String FLAG_SPAWN_Y = "spawnStructureHeight";
	final String FLAG_SLAB1 = "slab1";
	final String FLAG_SLAB2 = "slab2";
	final String FLAG_SLAB3 = "slab3";
	final String FLAG_SLAB4 = "slab4";
	final String FLAG_SLAB_CHAMBER_OPEN = "slabChamberOpened";
	
	public LimboManager(ServerWorld world)
	{
		this.world = world;
	}
	
	public void tick()
	{
		IDimensionDataComponent data = UltraComponents.DIMENSION_DATA.get(world);
		if(!ServerConfig.INSTANCE.disableFixedStructures.getValue() && !data.isFixedStructuresPlaced() &&
				   world.isChunkLoaded(world.getRandomAlivePlayer().getBlockPos()))
			prePlaceStructures();
	}
	
	public ActionResult onBlockInteract(PlayerEntity player, World world, Hand hand, BlockHitResult hit)
	{
		IDimensionDataComponent data = UltraComponents.DIMENSION_DATA.get(world);
		if(!data.isFixedStructuresPlaced())
			return ActionResult.PASS;
		BlockPos pos = hit.getBlockPos();
		//Spawn Slab Blocks
		int spawnY = data.getFlag(FLAG_SPAWN_Y);
		if(pos.equals(new BlockPos(-6, spawnY + 2,0)) || pos.equals(new BlockPos(6, spawnY + 2,0)) ||
				   pos.equals(new BlockPos(0, spawnY + 2,-6)))
		{
			player.sendMessage(Text.translatable("limbo.slab_press.fail.spawn"), true);
			return ActionResult.FAIL;
		}
		if(pos.equals(new BlockPos(0, spawnY + 1,6)) && world.getBlockState(pos).get(SlabBlock.ACTIVE))
		{
			player.sendMessage(Text.translatable("limbo.slab_press.fail.active"), true);
			return ActionResult.FAIL;
		}
		if(spawnBounds.contains(pos.toCenterPos()) && player.getStackInHand(hand).getItem() instanceof BlockItem)
		{
			player.sendMessage(Text.translatable("limbo.structure.modify-fail"), true);
			return ActionResult.FAIL;
		}
		return ActionResult.PASS;
	}
	
	@Override
	public ActionResult onAttackBlock(PlayerEntity player, World world, Hand hand, BlockPos pos, Direction direction)
	{
		IDimensionDataComponent data = UltraComponents.DIMENSION_DATA.get(world);
		if(!data.isFixedStructuresPlaced())
			return ActionResult.PASS;
		if(spawnBounds.contains(pos.toCenterPos()))
		{
			player.sendMessage(Text.translatable("limbo.structure.modify-fail"), true);
			return ActionResult.FAIL;
		}
		return ActionResult.PASS;
	}
	
	@Override
	public ServerWorld getWorld()
	{
		return world;
	}
	
	@Override
	public void onWorldLoad()
	{
	}
	
	void prePlaceStructures()
	{
		Ultracraft.LOGGER.info("Placing fixed Limbo Structures...");
		StructureTemplateManager templateManager = world.getStructureTemplateManager();
		Ultracraft.LOGGER.info("Placing limbo/spawn");
		templateManager.getTemplate(new Identifier(Ultracraft.MOD_ID, "limbo/spawn"))
				.ifPresent(i -> {
					BlockPos pos = new BlockPos(0, 0, 0);
					int y = world.getTopY(Heightmap.Type.WORLD_SURFACE_WG, pos.getX(), pos.getZ());
					i.place(world, pos.add(-26, y - 10, -26), new BlockPos(0, 0, 0), new StructurePlacementData(), world.getRandom(), 2);
					UltraComponents.DIMENSION_DATA.get(world).setFlag(FLAG_SPAWN_Y, y);
				});
		Ultracraft.LOGGER.info("Limbo fixed Structure Placement complete!");
		UltraComponents.DIMENSION_DATA.get(world).setFixedStructuresPlaced(true);
	}
}
