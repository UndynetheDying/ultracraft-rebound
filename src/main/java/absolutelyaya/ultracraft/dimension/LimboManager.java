package absolutelyaya.ultracraft.dimension;

import absolutelyaya.ultracraft.components.UltraComponents;
import absolutelyaya.ultracraft.Ultracraft;
import absolutelyaya.ultracraft.components.world.IDimensionDataComponent;
import absolutelyaya.ultracraft.config.ServerConfig;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;

public class LimboManager extends DimensionManager
{
	public static final Identifier ID = Ultracraft.identifier("limbo");
	public static final RegistryKey<World> WORLD_KEY = RegistryKey.of(RegistryKeys.WORLD, ID);
	final ServerWorld world;
	
	public LimboManager(ServerWorld world)
	{
		this.world = world;
	}
	
	public void tick()
	{
		IDimensionDataComponent data = UltraComponents.DIMENSION_DATA.get(world);
		if(!ServerConfig.INSTANCE.disableFixedStructures.getValue() && !data.isFixedStructuresPlaced())
			prePlaceStructures();
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
	
	@Override
	Text getModifyFailText()
	{
		return Text.translatable("message.limbo.structure.modify-fail");
	}
	
	void prePlaceStructures()
	{
		Ultracraft.LOGGER.info("Placing fixed Limbo Structures...");
		boolean tileDrops = world.getGameRules().getBoolean(GameRules.DO_TILE_DROPS);
		world.getGameRules().get(GameRules.DO_TILE_DROPS).set(false, world.getServer());
		StructureTemplateManager templateManager = world.getStructureTemplateManager();
		Ultracraft.LOGGER.info("Placing limbo/spawn");
		templateManager.getTemplate(Ultracraft.identifier("limbo/spawn"))
				.ifPresent(i -> {
					BlockPos pos = new BlockPos(0, 0, 0);
					int y = world.getWorldChunk(pos).sampleHeightmap(Heightmap.Type.WORLD_SURFACE_WG, pos.getX(), pos.getZ());
					i.place(world, pos.add(-26, y - 9, -26), new BlockPos(0, 0, 0), new StructurePlacementData(), world.getRandom(), 2);
				});
		Ultracraft.LOGGER.info("Placing limbo/destiny-chapel");
		templateManager.getTemplate(Ultracraft.identifier("limbo/destiny-chapel"))
				.ifPresent(i -> {
					BlockPos pos = new BlockPos(-1000, 0, 0);
					int y = world.getWorldChunk(pos).sampleHeightmap(Heightmap.Type.WORLD_SURFACE_WG, pos.getX(), pos.getZ());
					i.place(world, pos.add(0, y - 2, -13), new BlockPos(0, 0, 0), new StructurePlacementData(), world.getRandom(), 2);
				});
		Ultracraft.LOGGER.info("Placing limbo/rodent");
		templateManager.getTemplate(Ultracraft.identifier("limbo/rodent"))
				.ifPresent(i -> {
					BlockPos pos = new BlockPos(0, 0, -1000);
					int y = world.getWorldChunk(pos).sampleHeightmap(Heightmap.Type.WORLD_SURFACE_WG, pos.getX(), pos.getZ());
					i.place(world, pos.add(-18, y - 15, 0), new BlockPos(0, 0, 0), new StructurePlacementData(), world.getRandom(), 2);
				});
		Ultracraft.LOGGER.info("Placing limbo/cybergrind");
		templateManager.getTemplate(Ultracraft.identifier("limbo/cybergrind"))
				.ifPresent(i -> {
					BlockPos pos = new BlockPos(1000, 0, 0);
					i.place(world, pos.add(0, 0, -45), new BlockPos(0, 0, 0), new StructurePlacementData(), world.getRandom(), 2);
				});
		Ultracraft.LOGGER.info("Limbo fixed Structure Placement complete!");
		world.getGameRules().get(GameRules.DO_TILE_DROPS).set(tileDrops, world.getServer());
		UltraComponents.DIMENSION_DATA.get(world).setFixedStructuresPlaced(true);
	}
}
