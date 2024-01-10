package absolutelyaya.ultracraft.registry;

import absolutelyaya.ultracraft.Ultracraft;
import absolutelyaya.ultracraft.block.*;
import absolutelyaya.ultracraft.block.CarpetBlock;
import absolutelyaya.ultracraft.block.FlowerbedBlock;
import absolutelyaya.ultracraft.block.SlabBlock;
import absolutelyaya.ultracraft.block.mapping.*;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.minecraft.block.*;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.item.BlockItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

public class BlockRegistry
{
	public static final Block ELEVATOR = register("elevator",
			new ElevatorBlock(AbstractBlock.Settings.copy(Blocks.IRON_BLOCK).mapColor(MapColor.DULL_RED).requiresTool()),
			true);
	public static final Block ELEVATOR_WALL = register("elevator_wall",
			new ElevatorBlock(AbstractBlock.Settings.copy(Blocks.IRON_BLOCK).mapColor(MapColor.DULL_RED).requiresTool()),
			true);
	public static final Block ELEVATOR_FLOOR = register("elevator_floor",
			new ElevatorFloorBlock(AbstractBlock.Settings.copy(Blocks.IRON_BLOCK).mapColor(MapColor.DULL_RED).requiresTool()),
			true);
	public static final Block SECRET_ELEVATOR = register("secret_elevator",
			new ElevatorBlock(AbstractBlock.Settings.copy(Blocks.IRON_BLOCK).mapColor(MapColor.TERRACOTTA_GRAY).requiresTool()),
			true);
	public static final Block SECRET_ELEVATOR_WALL = register("secret_elevator_wall",
			new ElevatorBlock(AbstractBlock.Settings.copy(Blocks.IRON_BLOCK).mapColor(MapColor.TERRACOTTA_GRAY).requiresTool()),
			true);
	public static final Block SECRET_ELEVATOR_FLOOR = register("secret_elevator_floor",
			new ElevatorFloorBlock(AbstractBlock.Settings.copy(Blocks.IRON_BLOCK).mapColor(MapColor.TERRACOTTA_GRAY).requiresTool()),
			true);
	public static final Block PEDESTAL = register("pedestal",
			new PedestalBlock(AbstractBlock.Settings.copy(Blocks.COBBLESTONE).mapColor(MapColor.GRAY).nonOpaque().requiresTool()),
			true);
	public static final Block CERBERUS = register("cerberus_block",
			new CerberusBlock(AbstractBlock.Settings.copy(Blocks.COBBLESTONE).mapColor(MapColor.DEEPSLATE_GRAY).nonOpaque()
									  .requiresTool().strength(5f, 6f).luminance(b -> 6)), true);
	public static final Block BLOOD = register("blood", new FluidBlock(FluidRegistry.STILL_BLOOD,
			FabricBlockSettings.copyOf(Blocks.WATER).replaceable().pistonBehavior(PistonBehavior.DESTROY)), false);
	public static final Block FLESH = register("flesh",
			new FleshBlock(AbstractBlock.Settings.copy(Blocks.NETHERRACK).sounds(BlockSoundGroup.MUD).mapColor(MapColor.DARK_RED).nonOpaque()), true);
	public static final Block RUSTY_PIPE = register("rusty_pipe",
			new PipeBlock(AbstractBlock.Settings.copy(Blocks.IRON_BLOCK).mapColor(MapColor.TERRACOTTA_BROWN)), true);
	public static final Block RUSTY_MESH = register("rusty_mesh",
			new Block(AbstractBlock.Settings.copy(Blocks.IRON_BLOCK).mapColor(MapColor.TERRACOTTA_BROWN)), true);
	public static final Block CRACKED_STONE = register("cracked_stone",
			new Block(AbstractBlock.Settings.copy(Blocks.STONE).mapColor(MapColor.GRAY)), true);
	public static final Block TERMINAL = register("terminal",
			new TerminalBlock(AbstractBlock.Settings.copy(Blocks.IRON_BLOCK).mapColor(MapColor.GRAY).nonOpaque()), false);
	public static final Block TERMINAL_DISPLAY = register("terminal_display",
			new TerminalDisplayBlock(AbstractBlock.Settings.copy(BlockRegistry.TERMINAL).luminance((state) -> state.get(TerminalDisplayBlock.GLOWS) ? 8 : 0)), false);
	public static final Block VENT_COVER = register("vent_cover",
			new VentCoverBlock(AbstractBlock.Settings.copy(Blocks.IRON_BARS).mapColor(MapColor.DEEPSLATE_GRAY).requiresTool().strength(3f, 4f).nonOpaque()),
			true);
	public static final Block HELL_OBSERVER = register("hell_observer",
			new HellObserverBlock(AbstractBlock.Settings.copy(Blocks.NETHERRACK).sounds(BlockSoundGroup.MUD).mapColor(MapColor.DARK_CRIMSON).nonOpaque()), true);
	public static final Block MAUERWERK1 = register("mauerwerk1",
			new Block(AbstractBlock.Settings.copy(Blocks.STONE).sounds(BlockSoundGroup.STONE).mapColor(MapColor.GRAY).requiresTool()), true);
	public static final Block MAUERWERK2 = register("mauerwerk2",
			new Block(AbstractBlock.Settings.copy(Blocks.STONE).sounds(BlockSoundGroup.STONE).mapColor(MapColor.GRAY).requiresTool()), true);
	public static final Block ORNATE_WAINSCOT = register("ornate_wainscot",
			new Block(AbstractBlock.Settings.copy(Blocks.CALCITE).sounds(BlockSoundGroup.CALCITE).mapColor(MapColor.GRAY).requiresTool()), true);
	public static final Block ADORNED_RAILING = register("adorned_railing",
			new AdornedRailingBlock(AbstractBlock.Settings.copy(Blocks.IRON_BARS).sounds(BlockSoundGroup.METAL).mapColor(MapColor.GRAY).requiresTool()), true);
	public static final Block HELL_SPAWNER = register("hell_spawner",
			new HellSpawnerBlock(AbstractBlock.Settings.copy(Blocks.NETHERRACK).sounds(BlockSoundGroup.MUD).mapColor(MapColor.DARK_CRIMSON).nonOpaque()), false);
	public static final Block SKY_BLOCK = register("sky_block",
			new SkyBlock(AbstractBlock.Settings.copy(Blocks.BEDROCK).sounds(BlockSoundGroup.GLASS).mapColor(MapColor.BLACK).pistonBehavior(PistonBehavior.BLOCK)
								 .allowsSpawning((state, blockView, blockPos, entityType) -> false)), false);
	public static final Block SLAB_BLOCK = register("slab_block",
			new SlabBlock(AbstractBlock.Settings.copy(Blocks.SMOOTH_STONE).mapColor(MapColor.LIGHT_GRAY).luminance(b -> b.get(SlabBlock.ACTIVE) ? 5 : 0)), true);
	public static final Block CARPET = register("carpet",
			new CarpetBlock(AbstractBlock.Settings.copy(Blocks.BLUE_CARPET).mapColor(MapColor.BLUE)), true);
	public static final Block FLOWERBED = register("flowerbed",
			new FlowerbedBlock(AbstractBlock.Settings.copy(Blocks.OAK_LEAVES).mapColor(MapColor.DARK_GREEN)), true);
	public static final Block PORTAL = register("portal",
			new PortalBlock(AbstractBlock.Settings.copy(Blocks.IRON_BLOCK).mapColor(MapColor.TERRACOTTA_PURPLE)), true);
	
	public static final Block MAP_ROOM = register("map_room", new RoomBlock(AbstractBlock.Settings.copy(Blocks.COMMAND_BLOCK)), true);
	public static final Block MAP_TRIGGER = register("map_trigger", new TriggerBlock(AbstractBlock.Settings.copy(Blocks.COMMAND_BLOCK)), true);
	public static final Block MAP_REDSTONE = register("map_redstone", new RedstoneListenerBlock(AbstractBlock.Settings.copy(Blocks.COMMAND_BLOCK)), true);
	public static final Block MAP_DOOR = register("map_door", new DoorListenerBlock(AbstractBlock.Settings.copy(Blocks.COMMAND_BLOCK)), true);
	public static final Block MAP_SPAWNER = register("map_spawner", new SpawnerListenerBlock(AbstractBlock.Settings.copy(Blocks.COMMAND_BLOCK)), true);
	public static final Block MAP_ENEMY_TRIGGER = register("map_enemy_trigger", new EnemyTriggerBlock(AbstractBlock.Settings.copy(Blocks.COMMAND_BLOCK)), true);
	
	@SuppressWarnings("SameParameterValue")
	private static Block register(String name, Block block, boolean item, int burn, int spread)
	{
		FlammableBlockRegistry.getDefaultInstance().add(block, burn, spread);
		if(item)
			registerItem(name, block);
		return Registry.register(Registries.BLOCK, new Identifier(Ultracraft.MOD_ID, name), block);
	}
	
	private static Block register(String name, Block block, boolean item)
	{
		if(item)
			registerItem(name, block);
		return Registry.register(Registries.BLOCK, new Identifier(Ultracraft.MOD_ID, name), block);
	}
	
	private static void registerItem(String name, Block block)
	{
		Registry.register(Registries.ITEM, new Identifier(Ultracraft.MOD_ID, name),
				new BlockItem(block, new FabricItemSettings()));
	}
	
	public static void registerBlocks()
	{
		FlammableBlockRegistry registry = FlammableBlockRegistry.getDefaultInstance();
		registry.add(FLOWERBED, 20, 10);
		registry.add(CARPET, 20, 5);
	}
}
