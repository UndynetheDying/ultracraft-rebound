package absolutelyaya.ultracraft.compat;

import absolutelyaya.ultracraft.Ultracraft;
import absolutelyaya.ultracraft.block.TerminalBlockEntity;
import absolutelyaya.ultracraft.item.TerminalItem;
import absolutelyaya.ultracraft.registry.BlockRegistry;
import absolutelyaya.ultracraft.registry.ItemRegistry;
import me.shedaniel.rei.api.client.entry.filtering.base.BasicFilteringRule;
import me.shedaniel.rei.api.client.registry.entry.CollapsibleEntryRegistry;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class REIClientPlugin implements me.shedaniel.rei.api.client.plugins.REIClientPlugin
{
	@Override
	public void registerBasicEntryFiltering(BasicFilteringRule<?> rule)
	{
		rule.hide(List.of(
				EntryStacks.of(ItemRegistry.COIN.getDefaultStack()),
				EntryStacks.of(ItemRegistry.COIN),
				EntryStacks.of(ItemRegistry.FAKE_BANNER),
				EntryStacks.of(ItemRegistry.FAKE_SHIELD),
				EntryStacks.of(ItemRegistry.FAKE_CHEST),
				EntryStacks.of(ItemRegistry.FAKE_ENDER_CHEST),
				EntryStacks.of(ItemRegistry.FAKE_TERMINAL),
				EntryStacks.of(ItemRegistry.FAKE_HELL_SPAWNER),
				EntryStacks.of(ItemRegistry.KILLERFISH),
				EntryStacks.of(ItemRegistry.LUMPFISH),
				EntryStacks.of(ItemRegistry.BLOOD_RAY),
				EntryStacks.of(ItemRegistry.EJECTED_CORE),
				EntryStacks.of(ItemRegistry.NAIL),
				EntryStacks.of(ItemRegistry.MINCED_MEAT),
				EntryStacks.of(ItemRegistry.KNUCKLEBLASTER),
				EntryStacks.of(ItemRegistry.PLACEHOLDER),
				EntryStacks.of(ItemRegistry.FLORP),
				EntryStacks.of(ItemRegistry.PITR_POIN)));
	}
	
	@Override
	public void registerCollapsibleEntries(CollapsibleEntryRegistry registry)
	{
		List<EntryStack<?>> entries = new ArrayList<>();
		for (TerminalBlockEntity.Base base : TerminalBlockEntity.Base.values())
			entries.add(EntryStacks.of(TerminalItem.getStack(base)));
		registry.group(new Identifier(Ultracraft.MOD_ID, "terminal-variants"), Text.translatable("rei-group.ultracraft.terminals"), entries);
		entries = List.of(
				EntryStacks.of(BlockRegistry.MAP_ROOM),
				EntryStacks.of(BlockRegistry.MAP_TRIGGER),
				EntryStacks.of(BlockRegistry.MAP_ENEMY_TRIGGER),
				EntryStacks.of(BlockRegistry.MAP_CHECKPOINT),
				EntryStacks.of(BlockRegistry.MAP_REDSTONE),
				EntryStacks.of(BlockRegistry.MAP_RECEIVER),
				EntryStacks.of(BlockRegistry.MAP_DOOR),
				EntryStacks.of(BlockRegistry.MAP_SPAWNER),
				EntryStacks.of(BlockRegistry.MAP_EXPLOSION),
				EntryStacks.of(BlockRegistry.MAP_SOUND),
				EntryStacks.of(BlockRegistry.MAP_PROGRESSION),
				EntryStacks.of(BlockRegistry.MAP_LEVEL),
				EntryStacks.of(BlockRegistry.MAP_TIMER),
				EntryStacks.of(BlockRegistry.MAP_TRAVEL),
				EntryStacks.of(BlockRegistry.MAP_TITLE));
		registry.group(new Identifier(Ultracraft.MOD_ID, "mapping"), Text.translatable("rei-group.ultracraft.mapping"), entries);
	}
}
