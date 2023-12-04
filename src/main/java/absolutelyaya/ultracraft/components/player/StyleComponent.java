package absolutelyaya.ultracraft.components.player;

import absolutelyaya.ultracraft.UltraComponents;
import absolutelyaya.ultracraft.item.AbstractWeaponItem;
import absolutelyaya.ultracraft.registry.PacketRegistry;
import absolutelyaya.ultracraft.style.StyleBonus;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

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
	float chain;
	
	public StyleComponent(PlayerEntity provider)
	{
		this.provider = provider;
	}
	
	@Override
	public void styleBonusGet(StyleBonus bonus)
	{
		bonusQueue.add(new Pair<>(bonus.getTranslationKey(), provider.getWorld().getTime()));
		updateRecentBonuses();
		if(bonus.isUseStaleness())
		{
			ItemStack stack = provider.getMainHandStack();
			if(stack == null)
				return;
			Identifier id = Registries.ITEM.getId(stack.getItem());
			if(stack.getItem() instanceof AbstractWeaponItem)
				stalenessMap.put(id, getStaleness(id) + 5);
			for (Identifier key : stalenessMap.keySet())
				if(!key.equals(id) && getStaleness(id) > 0)
					stalenessMap.put(key, Math.max(getStaleness(key) - 10, 0));
		}
		if(!provider.getWorld().isClient)
		{
			style += bonus.getScore();
			chain += bonus.getScore();
			sync();
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
	
	void updateRecentBonuses()
	{
		Queue<Pair<String, Long>> q = new ArrayDeque<>(bonusQueue);
		recentBonuses = new String[Math.min(7, q.size())];
		for (int i = 0; i < Math.min(7, q.size()); i++)
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
		sync();
	}
	
	@Override
	public void takeDamage(float damage)
	{
		style = (int)Math.max(style - damage * 1.5f, 0);
		chain = (int)Math.max(chain - damage * 1.5f, 0);
		sync();
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
	public void sync()
	{
		UltraComponents.STYLE.sync(provider);
	}
	
	@Override
	public void readFromNbt(NbtCompound tag)
	{
		if(tag.contains("style", NbtElement.INT_TYPE))
			style = tag.getInt("style");
		if(tag.contains("chain", NbtElement.FLOAT_TYPE))
			chain = tag.getFloat("chain");
	}
	
	@Override
	public void writeToNbt(NbtCompound tag)
	{
		tag.putInt("style", style);
		tag.putFloat("chain", chain);
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
	}
}
