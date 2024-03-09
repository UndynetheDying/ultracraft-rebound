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
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;

public class LimboManager extends DimensionManager
{
	public static final Identifier ID = new Identifier(Ultracraft.MOD_ID, "limbo");
	public static final RegistryKey<World> WORLD_KEY = RegistryKey.of(RegistryKeys.WORLD, ID);
	final ServerWorld world;
	
	final String FLAG_SLAB1 = "slab1";
	final String FLAG_SLAB2 = "slab2";
	final String FLAG_SLAB3 = "slab3";
	final String FLAG_SLAB4 = "slab4";
	final String FLAG_SLAB_CHAMBER_OPEN = "slab_open";
	
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
		StructureTemplateManager templateManager = world.getStructureTemplateManager();
		Ultracraft.LOGGER.info("Placing limbo/spawn");
		templateManager.getTemplate(new Identifier(Ultracraft.MOD_ID, "limbo/spawn"))
				.ifPresent(i -> {
					BlockPos pos = new BlockPos(0, 0, 0);
					int y = world.getWorldChunk(pos).sampleHeightmap(Heightmap.Type.WORLD_SURFACE_WG, pos.getX(), pos.getZ());
					i.place(world, pos.add(-26, y - 9, -26), new BlockPos(0, 0, 0), new StructurePlacementData(), world.getRandom(), 2);
				});
		Ultracraft.LOGGER.info("Placing limbo/destiny-chapel");
		templateManager.getTemplate(new Identifier(Ultracraft.MOD_ID, "limbo/destiny-chapel"))
				.ifPresent(i -> {
					BlockPos pos = new BlockPos(-1000, 0, 0);
					int y = world.getWorldChunk(pos).sampleHeightmap(Heightmap.Type.WORLD_SURFACE_WG, pos.getX(), pos.getZ());
					i.place(world, pos.add(0, y - 2, -13), new BlockPos(0, 0, 0), new StructurePlacementData(), world.getRandom(), 2);
				});
		Ultracraft.LOGGER.info("Placing limbo/rodent");
		templateManager.getTemplate(new Identifier(Ultracraft.MOD_ID, "limbo/rodent"))
				.ifPresent(i -> {
					BlockPos pos = new BlockPos(0, 0, -1000);
					int y = world.getWorldChunk(pos).sampleHeightmap(Heightmap.Type.WORLD_SURFACE_WG, pos.getX(), pos.getZ());
					i.place(world, pos.add(-18, y - 15, 0), new BlockPos(0, 0, 0), new StructurePlacementData(), world.getRandom(), 2);
				});
		Ultracraft.LOGGER.info("Placing limbo/cybergrind");
		templateManager.getTemplate(new Identifier(Ultracraft.MOD_ID, "limbo/cybergrind"))
				.ifPresent(i -> {
					BlockPos pos = new BlockPos(1000, 0, 0);
					i.place(world, pos.add(0, 0, -45), new BlockPos(0, 0, 0), new StructurePlacementData(), world.getRandom(), 2);
				});
		Ultracraft.LOGGER.info("Limbo fixed Structure Placement complete!");
		UltraComponents.DIMENSION_DATA.get(world).setFixedStructuresPlaced(true);
	}
}
