package absolutelyaya.ultracraft.components.player;

import absolutelyaya.ultracraft.UltraComponents;
import absolutelyaya.ultracraft.item.AbstractWeaponItem;
import absolutelyaya.ultracraft.style.StyleBonus;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
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
	
	public StyleComponent(PlayerEntity provider)
	{
		this.provider = provider;
	}
	
	@Override
	public void styleBonusGet(StyleBonus bonus)
	{
		bonusQueue.add(new Pair<>(bonus.getTranslationKey(), provider.getWorld().getTime()));
		if(bonusQueue.size() <= 8)
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
		style += bonus.getScore();
		sync();
	}
	
	@Override
	public int getStaleness(Identifier id)
	{
		return stalenessMap.getOrDefault(id, 0);
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
		sync();
	}
	
	@Override
	public void takeDamage(float damage)
	{
		style = (int)Math.max(style - damage * 1.5f, 0);
		sync();
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
		if(tag.contains("bonusQueue", NbtElement.LIST_TYPE))
		{
			Queue<Pair<String, Long>> q = new ArrayDeque<>();
			NbtList list = tag.getList("bonusQueue", NbtElement.COMPOUND_TYPE);
			for (NbtElement nbtElement : list)
			{
				NbtCompound element = (NbtCompound) nbtElement;
				q.add(new Pair<>(element.getString("translation"), element.getLong("time")));
			}
			bonusQueue = q;
		}
		updateRecentBonuses();
	}
	
	@Override
	public void writeToNbt(NbtCompound tag)
	{
		tag.putInt("style", style);
		NbtList queue = new NbtList();
		Queue<Pair<String, Long>> q = new ArrayDeque<>(bonusQueue);
		for (Pair<String, Long> p : q)
		{
			NbtCompound element = new NbtCompound();
			element.putString("translation", p.getLeft());
			element.putLong("time", p.getRight());
			queue.add(element);
		}
		tag.put("bonusQueue", queue);
	}
	
	@Override
	public void tick()
	{
		if(bonusQueue.size() > 0 && provider.getWorld().getTime() - bonusQueue.peek().getRight() > 60)
		{
			bonusQueue.remove();
			updateRecentBonuses();
		}
	}
}
