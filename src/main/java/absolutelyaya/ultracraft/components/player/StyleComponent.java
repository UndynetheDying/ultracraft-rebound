package absolutelyaya.ultracraft.components.player;

import absolutelyaya.ultracraft.UltraComponents;
import absolutelyaya.ultracraft.item.AbstractWeaponItem;
import absolutelyaya.ultracraft.registry.PacketRegistry;
import absolutelyaya.ultracraft.data.StyleBonus;
import absolutelyaya.ultracraft.registry.ScoreboardCriteria;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
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
	final PlayerEntity provider;
	Map<Identifier, Integer> stalenessMap = new HashMap<>();
	Queue<Pair<String, Long>> bonusQueue = new ArrayDeque<>();
	String[] recentBonuses = new String[]{};
	int style;
	float chain, movementMultiplier = 1f;
	boolean dirty;
	
	public StyleComponent(PlayerEntity provider)
	{
		this.provider = provider;
	}
	
	@Override
	public void styleBonusGet(StyleBonus bonus)
	{
		bonusQueue.add(new Pair<>(bonus.getTranslationKey(), provider.getWorld().getTime()));
		float score = bonus.getScore();
		if(bonus.isUseStaleness())
		{
			ItemStack stack = provider.getMainHandStack();
			if(stack == null)
				return;
			Identifier id = Registries.ITEM.getId(stack.getItem());
			score *= getStalenessMod(id);
			if(stack.getItem() instanceof AbstractWeaponItem)
				stalenessMap.put(id, MathHelper.clamp(getStaleness(id) + 5, 0, 150));
			for (Identifier key : stalenessMap.keySet())
				if(!key.equals(id) && getStaleness(id) > 0)
					stalenessMap.put(key, MathHelper.clamp(getStaleness(key) - 10, 0, 150));
		}
		if(!provider.getWorld().isClient)
		{
			style += score;
			chain += score;
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
	public void clientStyleBonusGet(String key)
	{
		bonusQueue.add(new Pair<>(key, provider.getWorld().getTime()));
		updateRecentBonuses();
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
	
	void updateRecentBonuses()
	{
		Queue<Pair<String, Long>> q = new ArrayDeque<>(bonusQueue);
		recentBonuses = new String[Math.min(6, q.size())];
		for (int i = 0; i < Math.min(6, q.size()); i++)
			recentBonuses[i] = q.remove().getLeft();
	}
	
	@Override
	public String[] getRecentBonuses()
	{
		return recentBonuses;
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
	
	@Override
	public void readFromNbt(NbtCompound tag)
	{
		if(tag.contains("style", NbtElement.INT_TYPE))
			style = tag.getInt("style");
		if(tag.contains("chain", NbtElement.FLOAT_TYPE))
			chain = tag.getFloat("chain");
		if(tag.contains("staleness", NbtElement.LIST_TYPE))
		{
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
		if(bonusQueue.size() > 0 && provider.getWorld().getTime() - bonusQueue.peek().getRight() > 60)
		{
			bonusQueue.remove();
			updateRecentBonuses();
		}
		if(chain > 0)
			chain = Math.max(chain - getChainDecay() / 2f, 0);
		if(dirty)
		{
			UltraComponents.STYLE.sync(provider);
			dirty = false;
		}
		if(UltraComponents.WING_DATA.get(provider).isActive() && (!provider.isOnGround()||provider.isSprinting()))
			movementMultiplier = MathHelper.clamp(movementMultiplier + 0.126f, 1f, 3f);
		else if(movementMultiplier > 0)
			movementMultiplier = MathHelper.clamp(movementMultiplier - 0.126f, 1f, 3f);
	}
}
