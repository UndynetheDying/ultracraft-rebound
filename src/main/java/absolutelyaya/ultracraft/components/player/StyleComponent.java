package absolutelyaya.ultracraft.components.player;

import absolutelyaya.ultracraft.Ultracraft;
import absolutelyaya.ultracraft.api.HeavyEntities;
import absolutelyaya.ultracraft.components.UltraComponents;
import absolutelyaya.ultracraft.accessor.WingedPlayerEntity;
import absolutelyaya.ultracraft.damage.DamageSources;
import absolutelyaya.ultracraft.data.StyleBonusManager;
import absolutelyaya.ultracraft.entity.AbstractUltraHostileEntity;
import absolutelyaya.ultracraft.item.weapons.AbstractWeaponItem;
import absolutelyaya.ultracraft.registry.PacketRegistry;
import absolutelyaya.ultracraft.data.StyleBonus;
import absolutelyaya.ultracraft.registry.ScoreboardCriteria;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

public class StyleComponent implements IStyleComponent
{
	final Identifier KILL_ID = Ultracraft.identifier("kill");
	final Identifier OVERKILL_ID = Ultracraft.identifier("overkill");
	final Identifier BIG_KILL_ID = Ultracraft.identifier("big_kill");
	final Identifier MULTI_KILL_ID = Ultracraft.identifier("multikill");
	final Identifier FINISHED_ID = Ultracraft.identifier("finished");
	final Identifier AIR_KILL_ID = Ultracraft.identifier("air_kill");
	final Identifier FIREWORKS_ID = Ultracraft.identifier("fireworks");
	final Identifier AIR_SLAM_ID = Ultracraft.identifier("air_slam");
	final Identifier FRIENDLY_FIRE_ID = Ultracraft.identifier("friendly_fire");
	final Identifier BIG_FIST_ID = Ultracraft.identifier("big_fist");
	final PlayerEntity provider;
	Map<Identifier, Integer> stalenessMap = new HashMap<>();
	Queue<Pair<String, Long>> bonusQueue = new ArrayDeque<>();
	long killStreakTimer;
	int style, killStreak;
	float chain, movementMultiplier = 1f;
	boolean dirty;
	
	public StyleComponent(PlayerEntity provider)
	{
		this.provider = provider;
	}
	
	@Override
	public void styleBonusGet(StyleBonus bonus)
	{
		if(bonus == null)
			return;
		bonusQueue.add(new Pair<>(bonus.getTranslationKey(), provider.getWorld().getTime()));
		float score = bonus.getScore();
		boolean shouldApplyStaleness = shouldApplyStaleness();
		if(bonus.isUseStaleness())
		{
			ItemStack stack = provider.getMainHandStack();
			if(stack == null)
				return;
			Identifier id = Registries.ITEM.getId(stack.getItem());
			if(!shouldApplyStaleness)
				stalenessMap.remove(id);
			else
			{
				score *= getStalenessMod(id);
				if(stack.getItem() instanceof AbstractWeaponItem)
					stalenessMap.put(id, MathHelper.clamp(getStaleness(id) + 5, 0, 150));
				for (Identifier key : stalenessMap.keySet())
					if(!key.equals(id) && getStaleness(id) > 0)
						stalenessMap.put(key, MathHelper.clamp(getStaleness(key) - 10, 0, 150));
			}
		}
		if(!provider.getWorld().isClient)
		{
			style += score;
			chain += score;
			ILevelStatsComponent levelStats = UltraComponents.LEVEL_STATS.get(provider);
			if(levelStats.getCurrentLevelInstance() != null)
				levelStats.onStyle(score);
			markDirty();
			if(provider instanceof ServerPlayerEntity serverPlayer)
			{
				PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
				buf.writeString(bonus.getTranslationKey());
				ServerPlayNetworking.send(serverPlayer, PacketRegistry.STYLE_BONUS_PACKET_ID, buf);
			}
		}
	}
	
	@Override
	public void onKill(LivingEntity entity, DamageSource damage)
	{
		boolean heavy = HeavyEntities.isHeavy(entity.getType());
		styleBonusGet(StyleBonusManager.getBonuses().get(heavy ? BIG_KILL_ID : KILL_ID));
		if(killStreakTimer > 0 || killStreak == 0)
			killStreak++;
		killStreakTimer = 15;
		if(killStreak > 1)
			styleBonusGet(StyleBonusManager.getBonuses().get(MULTI_KILL_ID.withSuffixedPath(String.valueOf(Math.min(killStreak, 4)))));
		if(killStreak > 4)
		{
			style += 100 * killStreak;
			chain += 100 * killStreak;
			markDirty();
		}
		if(damage.isOf(DamageSources.SHOTGUN) && !heavy && entity.distanceTo(provider) < 1.5f)
			styleBonusGet(StyleBonusManager.getBonuses().get(OVERKILL_ID));
		if((provider.equals(damage.getSource()) || provider.equals(damage.getAttacker())) && entity.isOnFire())
			styleBonusGet(StyleBonusManager.getBonuses().get(FINISHED_ID));
		if(!entity.isOnGround())
		{
			if(damage.isIn(DamageTypeTags.IS_EXPLOSION))
				styleBonusGet(StyleBonusManager.getBonuses().get(FIREWORKS_ID));
			else if(damage.isOf(DamageSources.SLAM))
				styleBonusGet(StyleBonusManager.getBonuses().get(AIR_SLAM_ID));
			else if(damage.isOf(DamageSources.GUN))
				styleBonusGet(StyleBonusManager.getBonuses().get(AIR_KILL_ID));
		}
		if(damage.getSource() instanceof ProjectileEntity projectile && projectile.getOwner() instanceof AbstractUltraHostileEntity)
			styleBonusGet(StyleBonusManager.getBonuses().get(FRIENDLY_FIRE_ID));
		if(heavy && damage.isIn(absolutelyaya.ultracraft.damage.DamageTypeTags.PUNCH))
			styleBonusGet(StyleBonusManager.getBonuses().get(BIG_FIST_ID));
	}
	
	@Override
	public void clientStyleBonusGet(String key)
	{
		bonusQueue.add(new Pair<>(key, provider.getWorld().getTime()));
	}
	
	@Override
	public int getStaleness(Identifier id)
	{
		return stalenessMap.getOrDefault(id, 0);
	}
	
	public float getStalenessMod(Identifier id)
	{
		int staleness = getStaleness(id);
		if(staleness < 50)
			return 1.5f;
		else if(staleness < 100)
			return 1.0f;
		if(staleness < 150)
			return 0.5f;
		else
			return 0f;
	}
	
	@Override
	public Queue<Pair<String, Long>> getBonusQueue()
	{
		return bonusQueue;
	}
	
	@Override
	public int getScore()
	{
		return style;
	}
	
	@Override
	public void resetScore()
	{
		style = 0;
		chain = 0;
		markDirty();
	}
	
	@Override
	public void takeDamage(float damage)
	{
		style = (int)Math.max(style - damage * 1.5f, 0);
		chain = (int)Math.max(chain - damage * 1.5f, 0);
		ILevelStatsComponent levelStats = UltraComponents.LEVEL_STATS.get(provider);
		if(levelStats.getCurrentLevelInstance() != null)
			levelStats.onStyle(-damage * 1.5f);
		markDirty();
	}
	
	@Override
	public int getRank()
	{
		if(chain > getStyleForRank(7))
			return 7;
		else if(chain > getStyleForRank(6))
			return 6;
		else if(chain > getStyleForRank(5))
			return 5;
		else if(chain > getStyleForRank(4))
			return 4;
		else if(chain > getStyleForRank(3))
			return 3;
		else if(chain > getStyleForRank(2))
			return 2;
		else if(chain > getStyleForRank(1))
			return 1;
		return 0;
	}
	
	int getStyleForRank(int rank)
	{
		return switch(rank)
		{
			default -> 0;
			case 1 -> 200;
			case 2 -> 400;
			case 3 -> 500;
			case 4 -> 700;
			case 5 -> 850;
			case 6 -> 1000;
			case 7 -> 1500;
		};
	}
	
	float getChainDecay()
	{
		return switch(getRank())
		{
			default -> 1f;
			case 1 -> 1.25f;
			case 2 -> 1.5f;
			case 3 -> 2f;
			case 4 -> 3f;
			case 5 -> 4f;
			case 6 -> 6f;
			case 7 -> 8f;
		};
	}
	
	@Override
	public float getRankProgress()
	{
		if(getRank() == 7)
			return 1f;
		int sub = getStyleForRank(getRank());
		return (chain - sub) / (getStyleForRank(getRank() + 1) - sub);
	}
	
	@Override
	public float getChain()
	{
		return chain;
	}
	
	@Override
	public void markDirty()
	{
		dirty = true;
		provider.getScoreboard().forEachScore(ScoreboardCriteria.STYLE, provider.getEntityName(), i -> i.setScore(style));
	}
	
	@Override
	public float getMovementMultiplier()
	{
		return movementMultiplier;
	}
	
	boolean shouldApplyStaleness()
	{
		return UltraComponents.LOADOUT.get(provider).isMoreThanOneWeaponHeld();
	}
	
	@Override
	public void readFromNbt(NbtCompound tag)
	{
		if(tag.contains("style", NbtElement.INT_TYPE))
			style = tag.getInt("style");
		if(tag.contains("chain", NbtElement.FLOAT_TYPE))
			chain = tag.getFloat("chain");
		if(tag.contains("staleness", NbtElement.LIST_TYPE))
		{
			stalenessMap.clear();
			tag.getList("staleness", NbtElement.COMPOUND_TYPE).forEach(i -> {
				if(i instanceof NbtCompound compound)
				{
					Identifier id = Identifier.tryParse(compound.getString("id"));
					int score = compound.getInt("score");
					stalenessMap.put(id, score);
				}
			});
		}
	}
	
	@Override
	public void writeToNbt(NbtCompound tag)
	{
		tag.putInt("style", style);
		tag.putFloat("chain", chain);
		NbtList list = new NbtList();
		stalenessMap.forEach((id, i) -> {
			NbtCompound element = new NbtCompound();
			element.putString("id", id.toString());
			element.putInt("score", i);
			list.add(element);
		});
		tag.put("staleness", list);
	}
	
	@Override
	public void tick()
	{
		if(Ultracraft.isTimeFrozen())
			return;
		if(!bonusQueue.isEmpty() && provider.getWorld().getTime() - bonusQueue.peek().getRight() > 60)
			bonusQueue.remove();
		if(chain > 0)
			chain = Math.max(chain - getChainDecay() / 2f, 0);
		if(dirty)
		{
			UltraComponents.STYLE.sync(provider);
			dirty = false;
		}
		if(UltraComponents.WING_DATA.get(provider).isActive() && (!provider.isOnGround() || (UltraComponents.HIVEL.get(provider).isSliding())))
			movementMultiplier = MathHelper.clamp(movementMultiplier + 0.126f, 1f, 3f);
		else if(movementMultiplier > 0)
			movementMultiplier = MathHelper.clamp(movementMultiplier - 0.126f, 1f, 3f);
		if(!Ultracraft.isTimeFrozen() && killStreakTimer > 0)
		{
			killStreakTimer--;
			if(killStreakTimer == 0)
				killStreak = 0;
		}
	}
}
