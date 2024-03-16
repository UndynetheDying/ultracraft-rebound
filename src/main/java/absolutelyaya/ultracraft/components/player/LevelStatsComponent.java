package absolutelyaya.ultracraft.components.player;

import absolutelyaya.ultracraft.client.gui.LevelHUD;
import absolutelyaya.ultracraft.components.UltraComponents;
import absolutelyaya.ultracraft.dimension.LevelManager;
import absolutelyaya.ultracraft.entity.AbstractUltraHostileEntity;
import absolutelyaya.ultracraft.util.TimeUtil;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

import java.util.HashMap;
import java.util.Map;

import static absolutelyaya.ultracraft.data.LevelDataManager.getLevelData;

public class LevelStatsComponent implements ILevelStatsComponent, AutoSyncedComponent
{
	PlayerEntity provider;
	HashMap<Identifier, Pair<Long, Long>> bestTimes = new HashMap<>();
	HashMap<Identifier, Integer> lastPlayedVersion = new HashMap<>();
	HashMap<Identifier, Integer> bestRanks = new HashMap<>();
	Identifier currentLevel;
	String currentLevelInstance;
	boolean fighting, undamaged;
	int fightCheckCooldown, deaths;
	float style;
	long timerStart = -1, lastStoppedTimer = -1;
	
	public LevelStatsComponent(PlayerEntity provider)
	{
		this.provider = provider;
	}
	
	@Override
	public void enterLevel(Identifier levelId, String instance)
	{
		deaths = 0;
		style = 0;
		undamaged = true;
		stopTimer(true);
		String lastInstance = currentLevelInstance; //prevents infinite loop when rescuing from null instance
		currentLevelInstance = instance;
		if(!provider.getWorld().isClient && lastInstance != null)
			LevelManager.Instance.leaveInstance((ServerPlayerEntity)provider, lastInstance);
		currentLevel = levelId;
		if(levelId != null)
			lastPlayedVersion.put(levelId, getLevelData(levelId).getVersion());
		UltraComponents.LEVEL_STATS.sync(provider);
	}
	
	@Override
	public Identifier getCurrentLevel()
	{
		return currentLevel;
	}
	
	@Override
	public String getCurrentLevelInstance()
	{
		return currentLevelInstance;
	}
	
	@Override
	public boolean isInFight()
	{
		if(fightCheckCooldown-- > 0)
			return fighting;
		fighting = provider.getWorld().getOtherEntities(provider, provider.getBoundingBox().expand(32f),
				e -> e instanceof AbstractUltraHostileEntity && e.isAlive()).size() > 0;
		fightCheckCooldown = 10;
		return fighting;
	}
	
	@Override
	public boolean isTimerRunning()
	{
		return timerStart > -1;
	}
	
	@Override
	public void startTimer()
	{
		if(currentLevel == null)
			return;
		timerStart = System.currentTimeMillis();
		if(provider.getWorld().isClient)
			LevelHUD.Instance.initTimer(bestTimes.getOrDefault(currentLevel, new Pair<>(-1L, -1L)), getLevelData(currentLevel).getTimeRanks());
	}
	
	@Override
	public void stopTimer(boolean interruption)
	{
		UltraComponents.LEVEL_STATS.sync(provider);
		if(!interruption && currentLevel != null)
		{
			long elapsedTime = getElapsedTimer();
			Pair<Long, Long> pb = bestTimes.getOrDefault(currentLevel, new Pair<>(Long.MAX_VALUE, Long.MAX_VALUE));
			long best = pb.getLeft(), perfectBest = pb.getRight();
			if(best > elapsedTime)
				best = elapsedTime;
			if(isPerfect() && perfectBest > elapsedTime)
				perfectBest = elapsedTime;
			if(best != pb.getLeft() || (perfectBest != -1 && perfectBest != pb.getRight()))
			{
				bestTimes.put(currentLevel, new Pair<>(best, perfectBest));
				if(provider.getWorld().isClient)
				{
					Text levelName = getLevelData(currentLevel).getTitleText();
					provider.sendMessage(Text.translatable("message.ultracraft.level.new-pb-time", levelName, TimeUtil.milliToString(elapsedTime)));
				}
			}
			if(provider.getWorld().isClient)
				LevelHUD.Instance.stopTimer();
			lastStoppedTimer = elapsedTime;
		}
		timerStart = -1;
	}
	
	@Override
	public long getElapsedTimer()
	{
		if(!isTimerRunning())
			return -1;
		return System.currentTimeMillis() - timerStart;
	}
	
	@Override
	public long getLastStoppedTimer()
	{
		return lastStoppedTimer;
	}
	
	@Override
	public boolean isPerfect()
	{
		return deaths == 0;
	}
	
	@Override
	public void onDeath()
	{
		deaths++;
		UltraComponents.LEVEL_STATS.sync(provider);
	}
	
	@Override
	public int getDeaths()
	{
		return deaths;
	}
	
	@Override
	public int getKills()
	{
		LevelManager.LevelInstance inst = LevelManager.Instance.getInstance(currentLevelInstance);
		if(inst == null)
			return 0;
		return inst.getKills();
	}
	
	@Override
	public int getStyle()
	{
		return (int)style;
	}
	
	@Override
	public void onStyle(float score)
	{
		style += score;
		UltraComponents.LEVEL_STATS.sync(provider);
	}
	
	@Override
	public void onDamage()
	{
		undamaged = false;
		UltraComponents.LEVEL_STATS.sync(provider);
	}
	
	@Override
	public boolean isUndamaged()
	{
		return undamaged;
	}
	
	@Override
	public long getBestTime(Identifier id, boolean perfect)
	{
		Pair<Long, Long> pb = bestTimes.getOrDefault(id, null);
		if(pb == null)
			return -1L;
		return perfect ? pb.getRight() : pb.getLeft();
	}
	
	@Override
	public int getLastPlayedLevelVersion(Identifier id)
	{
		return lastPlayedVersion.getOrDefault(id, -1);
	}
	
	@Override
	public int getBestRank(Identifier levelId)
	{
		return bestRanks.getOrDefault(levelId, -1);
	}
	
	@Override
	public void readFromNbt(NbtCompound tag)
	{
		if(tag.contains("level", NbtElement.STRING_TYPE))
			currentLevel = new Identifier(tag.getString("level"));
		if(tag.contains("timerStart", NbtElement.LONG_TYPE))
			timerStart = tag.getLong("timerStart");
		if(tag.contains("lastStoppedTime", NbtElement.LONG_TYPE))
			lastStoppedTimer = tag.getLong("lastStoppedTime");
		if(tag.contains("style", NbtElement.FLOAT_TYPE))
			style = tag.getLong("style");
		if(tag.contains("deaths", NbtElement.INT_TYPE))
			deaths = tag.getInt("deaths");
		if(tag.contains("bestTimes", NbtElement.COMPOUND_TYPE))
		{
			NbtCompound records = tag.getCompound("bestTimes");
			for(String key : records.getKeys())
			{
				NbtCompound pb = records.getCompound(key);
				bestTimes.put(Identifier.tryParse(key), new Pair<>(pb.getLong("imperfect"), pb.getLong("perfect")));
			}
		}
		if(tag.contains("bestRanks", NbtElement.COMPOUND_TYPE))
		{
			NbtCompound records = tag.getCompound("bestRanks");
			for(String key : records.getKeys())
				bestRanks.put(Identifier.tryParse(key), records.getInt(key));
		}
		if(tag.contains("versions", NbtElement.COMPOUND_TYPE))
		{
			NbtCompound versions = tag.getCompound("versions");
			for(String key : versions.getKeys())
				lastPlayedVersion.put(Identifier.tryParse(key), versions.getInt(key));
		}
		if(tag.contains("currentLevelInstance", NbtElement.STRING_TYPE))
			currentLevelInstance = tag.getString("currentLevelInstance");
	}
	
	@Override
	public void writeToNbt(NbtCompound tag)
	{
		if(timerStart != -1)
			tag.putLong("timerStart", timerStart);
		if(lastStoppedTimer != -1)
			tag.putLong("lastStoppedTime", lastStoppedTimer);
		if(style > 0)
			tag.putFloat("style", style);
		if(bestTimes.size() > 0)
		{
			NbtCompound records = new NbtCompound();
			for (Map.Entry<Identifier, Pair<Long, Long>> e : bestTimes.entrySet())
			{
				NbtCompound pb = new NbtCompound();
				pb.putLong("imperfect", e.getValue().getLeft());
				pb.putLong("perfect", e.getValue().getRight());
				records.put(e.getKey().toString(), pb);
			}
			tag.put("bestTimes", records);
		}
		if(bestRanks.size() > 0)
		{
			NbtCompound records = new NbtCompound();
			for (Map.Entry<Identifier, Integer> e : bestRanks.entrySet())
				records.putInt(e.getKey().toString(), e.getValue());
			tag.put("bestRanks", records);
		}
		if(lastPlayedVersion.size() > 0)
		{
			NbtCompound records = new NbtCompound();
			for (Map.Entry<Identifier, Integer> e : lastPlayedVersion.entrySet())
				records.putInt(e.getKey().toString(), e.getValue());
			tag.put("versions", records);
		}
		if(getCurrentLevel() != null)
		{
			tag.putString("level", getCurrentLevel().toString());
			tag.putInt("deaths", deaths);
		}
		if(currentLevelInstance != null && !currentLevelInstance.isEmpty())
			tag.putString("currentLevelInstance", currentLevelInstance);
	}
}
