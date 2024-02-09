package absolutelyaya.ultracraft.config;

import absolutelyaya.ultracraft.Layer;
import absolutelyaya.ultracraft.Ultracraft;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.registry.Registries;
import net.minecraft.server.MinecraftServer;

import java.util.HashMap;
import java.util.Map;

public class CybergrindConfig extends Config
{
	public static CybergrindConfig INSTANCE;
	static boolean frozen;
	
	public final IntegerEntry baseBudget = new IntegerEntry("BaseBudget", 5);
	public final IntegerEntry minBudgetPerWave = new IntegerEntry("MinBudgetPerWave", 5);
	public final IntegerEntry maxBudgetPerWave = new IntegerEntry("MaxBudgetPerWave", 8);
	public final IntegerEntry difficultyBudgetBonus = new IntegerEntry("MinimumBudget", 2);
	public final IntegerEntry wavesPerDifficultyLow = new IntegerEntry("MinWaves", 2);
	public final IntegerEntry wavesPerDifficultyHigh = new IntegerEntry("MaxWaves", 4);
	public final IntegerEntry wavesPerDifficultyBonus = new IntegerEntry("DifficultyBonusWaves", 0);
	public final BooleanEntry stopUponDeath = new BooleanEntry("StopOnDeath", true);
	public final FloatEntry startChance = new FloatEntry("StartChance", 0.2f);
	public final IntegerEntry cooldown = new IntegerEntry("Cooldown", 5);
	static final Map<Layer, Map<EntityType<? extends HostileEntity>, IntegerEntry>> costs = new HashMap<>();
	
	public CybergrindConfig(MinecraftServer server)
	{
		super(server, "cybergrind");
		initEntries();
		INSTANCE = this;
	}
	
	public void registerCost(EntityType<? extends HostileEntity> type, int cost, Layer minLayer)
	{
		String id = Registries.ENTITY_TYPE.getId(type).toString();
		if(frozen)
		{
			Ultracraft.LOGGER.error("Tried registering spawn cost for entity " + id + " too late. Please use the entrypoint 'cybergrind'");
			return;
		}
		IntegerEntry entry = new IntegerEntry(id, cost);
		if(!costs.containsKey(minLayer))
			costs.put(minLayer, new HashMap<>());
		costs.get(minLayer).put(type, entry);
		entries.add(entry);
	}
	
	/**
	 * Not how this usually works; but the entries of spawn costs could change upon /reload.
	 * If this behavior isn't necessary, I'd recommend just adding the entries in the constructor, like other config classes do it.
	 */
	void initEntries()
	{
		entries.clear();
		entries.add(new Comment(" ## ################################# ##  #"));
		entries.add(new Comment("     Welcome to Cybergrind Config"));
		entries.add(new Comment(" ## ################################# ##  #"));
		entries.add(new Comment(" At the Start of a Round, a Budget is set; every enemy spawn is basically bought using that budget, until there is none left."));
		entries.add(new Comment(" budget = base + repeat(wave, random(min, max)) + difficultyBonus * difficulty"));
		entries.add(baseBudget);
		entries.add(minBudgetPerWave);
		entries.add(maxBudgetPerWave);
		entries.add(difficultyBudgetBonus);
		entries.add(new Comment(" Waves = repeat(difficulty, rand(min, max)) + difficulty * bonus"));
		entries.add(wavesPerDifficultyLow);
		entries.add(wavesPerDifficultyHigh);
		entries.add(wavesPerDifficultyBonus);
		entries.add(new Comment(" Stop when all participants have died"));
		entries.add(stopUponDeath);
		entries.add(new Comment(" Chance to start a Cybergrind each Night; after a Cybergrind has ended, for [cooldown] nights, the chance will be 0."));
		entries.add(startChance);
		entries.add(cooldown);
		entries.add(new Comment(" ## ################################# ##  #"));
		entries.add(new Comment("              Spawn Costs"));
		entries.add(new Comment(" ## ################################# ##  #"));
		for (Layer layer : costs.keySet())
			entries.addAll(costs.get(layer).values());
	}
	
	public static void clearCosts()
	{
		frozen = false;
		costs.clear();
	}
	
	@Override
	public void load(MinecraftServer server)
	{
		INSTANCE.initEntries();
		super.load(server);
	}
	
	public static void freeze()
	{
		frozen = true;
		Ultracraft.LOGGER.info("Froze Cybergrind Data with " + costs.size() + " registered enemy spawn Costs.");
	}
	
	@Override
	protected String getExportPath()
	{
		return "ultracraft/";
	}
	
	@Override
	protected String getFileName()
	{
		return "cybergrind.properties";
	}
	
	public Map<EntityType<? extends HostileEntity>, IntegerEntry> getCosts(Layer minLayer)
	{
		Map<EntityType<? extends HostileEntity>, IntegerEntry> map = new HashMap<>();
		int min = minLayer.ordinal();
		for (int i = 0; i < min; i++)
		{
			Layer layer = Layer.values()[i];
			if(costs.containsKey(layer))
				map.putAll(costs.get(layer));
		}
		return map;
	}
}
