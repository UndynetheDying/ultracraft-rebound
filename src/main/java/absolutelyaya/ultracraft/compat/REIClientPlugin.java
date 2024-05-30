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
				EntryStacks.of(BlockRegistry.MAP_TITLE),
				EntryStacks.of(BlockRegistry.MAP_TITLE_LISTENER),
				EntryStacks.of(BlockRegistry.MAP_DAMAGE),
				EntryStacks.of(BlockRegistry.MAP_CYBERGRIND),
				EntryStacks.of(BlockRegistry.MAP_LIGHT),
				EntryStacks.of(BlockRegistry.MAP_MUSIC),
				EntryStacks.of(BlockRegistry.MAP_MUSIC_LISTENER),
				EntryStacks.of(BlockRegistry.MAP_GLOBAL_REDSTONE),
				EntryStacks.of(BlockRegistry.MAP_GLOBAL_RECEIVER),
				EntryStacks.of(BlockRegistry.MAP_GLOBAL_TITLE));
		registry.group(new Identifier(Ultracraft.MOD_ID, "mapping"), Text.translatable("rei-group.ultracraft.mapping"), entries);
		entries = List.of(
				EntryStacks.of(BlockRegistry.RED_CARPET),
				EntryStacks.of(BlockRegistry.ORANGE_CARPET),
				EntryStacks.of(BlockRegistry.YELLOW_CARPET),
				EntryStacks.of(BlockRegistry.LIME_CARPET),
				EntryStacks.of(BlockRegistry.GREEN_CARPET),
				EntryStacks.of(BlockRegistry.CYAN_CARPET),
				EntryStacks.of(BlockRegistry.LIGHT_BLUE_CARPET),
				EntryStacks.of(BlockRegistry.BLUE_CARPET),
				EntryStacks.of(BlockRegistry.PURPLE_CARPET),
				EntryStacks.of(BlockRegistry.MAGENTA_CARPET),
				EntryStacks.of(BlockRegistry.PINK_CARPET),
				EntryStacks.of(BlockRegistry.BROWN_CARPET),
				EntryStacks.of(BlockRegistry.BLACK_CARPET),
				EntryStacks.of(BlockRegistry.GRAY_CARPET),
				EntryStacks.of(BlockRegistry.LIGHT_GRAY_CARPET),
				EntryStacks.of(BlockRegistry.WHITE_CARPET));
		registry.group(new Identifier(Ultracraft.MOD_ID, "carpet"), Text.translatable("rei-group.ultracraft.carpet"), entries);
		entries = List.of(
				EntryStacks.of(ItemRegistry.PRELUDE1_DISK),
				EntryStacks.of(ItemRegistry.PRELUDE1_CALM_DISK),
				EntryStacks.of(ItemRegistry.CERBERUS_DISK),
				EntryStacks.of(ItemRegistry.CERBERUS_CALM_DISK),
				EntryStacks.of(ItemRegistry.LIMBO2_DISK),
				EntryStacks.of(ItemRegistry.LIMBO2_CALM_DISK),
				EntryStacks.of(ItemRegistry.CYBERGRIND_DISK),
				EntryStacks.of(ItemRegistry.CLAIR_DE_LUNE_DISK));
		registry.group(new Identifier(Ultracraft.MOD_ID, "discs"), Text.translatable("rei-group.ultracraft.discs"), entries);
	}
}
