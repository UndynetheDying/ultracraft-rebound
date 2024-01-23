package absolutelyaya.ultracraft.dimension;

import absolutelyaya.ultracraft.Ultracraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class LevelManager implements DimensionManager
{
	public static LevelManager Instance;
	public static final Identifier ID = new Identifier(Ultracraft.MOD_ID, "levels");
	public static final RegistryKey<World> WORLD_KEY = RegistryKey.of(RegistryKeys.WORLD, ID);
	final ServerWorld world;
	public static final Map<Identifier, LevelData> levels = new HashMap<>();
	static final List<Identifier> instantiated = new ArrayList<>();
	
	public LevelManager(ServerWorld world)
	{
		Instance = this;
		this.world = world;
	}
	
	public static void registerLevel(Identifier id, LevelData level)
	{
		levels.put(id, level);
	}
	
	public boolean instantiateLevel(Identifier id)
	{
		if(!levels.containsKey(id))
			return false;
		LevelData data = levels.get(id);
		StructureTemplateManager templateManager = world.getStructureTemplateManager();
		Identifier structure = data.structure();
		Ultracraft.LOGGER.info("Placing " + structure);
		AtomicBoolean success = new AtomicBoolean(false);
		templateManager.getTemplate(structure)
				.ifPresent(i -> {
					i.place(world, data.pos(), new BlockPos(0, 0, 0), new StructurePlacementData(), world.getRandom(), 2);
					instantiated.add(id);
					success.set(true);
				});
		if(!success.get())
			Ultracraft.LOGGER.warn("Structure " + structure + " placement failed; won't teleport Player.");
		return success.get();
	}
	
	@Override
	public void tick()
	{
	
	}
	
	@Override
	public ActionResult onBlockInteract(PlayerEntity player, World world, Hand hand, BlockHitResult hit)
	{
		return ActionResult.PASS;
	}
	
	@Override
	public ActionResult onAttackBlock(PlayerEntity player, World world, Hand hand, BlockPos pos, Direction direction)
	{
		return player.isCreative() ? ActionResult.PASS : ActionResult.FAIL;
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
	
	static {
		registerLevel(new Identifier(Ultracraft.MOD_ID, "level.1-1"), new LevelData(/*new Identifier(Ultracraft.MOD_ID, "level/1-1")*/
				new Identifier(Ultracraft.MOD_ID, "limbo/ruin1"), new BlockPos(0, 64, 0), new BlockPos(0, 0, 0)));
	}
}
