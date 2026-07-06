package absolutelyaya.ultracraft.components.player;

import absolutelyaya.ultracraft.components.UltraComponents;
import absolutelyaya.ultracraft.Ultracraft;
import absolutelyaya.ultracraft.accessor.WingedPlayerEntity;
import absolutelyaya.ultracraft.client.gui.terminal.WeaponsTab;
import absolutelyaya.ultracraft.data.UltraRecipeManager;
import absolutelyaya.ultracraft.registry.GameruleRegistry;
import absolutelyaya.ultracraft.registry.ItemRegistry;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ProgressionComponent implements IProgressionComponent, AutoSyncedComponent
{
	public static final Identifier FEEDBACKER = Ultracraft.identifier("feedbacker");
	public static final Identifier KNUCKLEBLASTER = Ultracraft.identifier("knuckleblaster");
	public static final Identifier SLAB = Ultracraft.identifier("slab");
	public static final Identifier HIVEL = Ultracraft.identifier("hivel");
	public static final Identifier BLOODHEAL = Ultracraft.identifier("bloodheal");
	public static final Identifier ULTRAHUD = Ultracraft.identifier("ultrahud");
	
	static final List<Identifier> ENTRIES = new ArrayList<>() {
		{
			add(Registries.ITEM.getId(ItemRegistry.PIERCE_REVOLVER));
			add(Registries.ITEM.getId(ItemRegistry.MARKSMAN_REVOLVER));
			add(Registries.ITEM.getId(ItemRegistry.SHARPSHOOTER_REVOLVER));
			add(Registries.ITEM.getId(ItemRegistry.CORE_SHOTGUN));
			add(Registries.ITEM.getId(ItemRegistry.PUMP_SHOTGUN));
			add(Registries.ITEM.getId(ItemRegistry.SAW_SHOTGUN));
			add(Registries.ITEM.getId(ItemRegistry.ATTRACTOR_NAILGUN));
			add(Registries.ITEM.getId(ItemRegistry.OVERHEAT_NAILGUN));
			add(Registries.ITEM.getId(ItemRegistry.JUMPSTART_NAILGUN));
			add(FEEDBACKER);
			add(KNUCKLEBLASTER);
			add(SLAB);
			add(HIVEL);
			add(BLOODHEAL);
			add(ULTRAHUD);
		}
	};
	
	PlayerEntity provider;
	List<Identifier> unlocked = new ArrayList<>();
	List<Identifier> owned = new ArrayList<>();
	
	public ProgressionComponent(PlayerEntity provider)
	{
		this.provider = provider;
		reset();
	}
	
	@Override
	public void lock(Identifier id)
	{
		unlocked.remove(id);
	}
	
	@Override
	public void lockAll()
	{
		unlocked.clear();
	}
	
	@Override
	public void unlock(Identifier id)
	{
		if(!unlocked.contains(id))
			unlocked.add(id);
	}
	
	@Override
	public void unlockAll()
	{
		for (Identifier id : ENTRIES)
			unlock(id);
	}
	
	@Override
	public boolean isUnlocked(Identifier id)
	{
		if(isOwned(id) && !unlocked.contains(id))
			unlocked.add(id);
		for (Identifier owned : getOwnedList())
		{
			if(UltraRecipeManager.isRecipePresent(owned) && UltraRecipeManager.getRecipe(owned).getUnlocks().contains(id))
				return true;
		}
		return unlocked.contains(id);
	}
	
	@Override
	public List<Identifier> getUnlockedList()
	{
		return unlocked;
	}
	
	@Override
	public void disown(Identifier id)
	{
		owned.remove(id);
	}
	
	@Override
	public void disownAll()
	{
		owned.clear();
	}
	
	@Override
	public void obtain(Identifier id)
	{
		if(!owned.contains(id))
			owned.add(id);
		if(UltraRecipeManager.isRecipePresent(id))
			for (Identifier unlock : UltraRecipeManager.getRecipe(id).getUnlocks())
				unlock(unlock);
	}
	
	@Override
	public void obtainAll()
	{
		for (Identifier id : ENTRIES)
			obtain(id);
	}
	
	@Override
	public boolean isOwned(Identifier id)
	{
		return owned.contains(id);
	}
	
	@Override
	public List<Identifier> getOwnedList()
	{
		return owned;
	}
	
	@Override
	public void reset()
	{
		unlocked = new ArrayList<>();
		owned = new ArrayList<>();
		
		if(provider.getWorld().getGameRules().getBoolean(GameruleRegistry.START_AS_V1))
		{
			unlocked.add(FEEDBACKER);
			unlocked.add(HIVEL);
			unlocked.add(BLOODHEAL);
			unlocked.add(ULTRAHUD);
		}
	}
	
	@Override
	public List<Identifier> getAllGearEntries()
	{
		return ENTRIES;
	}
	
	public void sync()
	{
		UltraComponents.PROGRESSION.sync(provider);
		UltraComponents.LOADOUT.get(provider).setWeaponCountDirty();
	}
	
	@Override
	public void readFromNbt(@NotNull NbtCompound tag)
	{
		this.unlocked.clear();
		this.owned.clear();
		NbtList list = tag.getList("unlocks", NbtElement.STRING_TYPE);
		list.forEach(i -> unlocked.add(Identifier.tryParse(i.asString())));
		list = tag.getList("owned", NbtElement.STRING_TYPE);
		list.forEach(i -> owned.add(Identifier.tryParse(i.asString())));
		if(!tag.contains("patch") && provider.getWorld().getGameRules().getBoolean(GameruleRegistry.START_AS_V1))
		{
			unlock(FEEDBACKER);
			unlock(HIVEL);
			unlock(BLOODHEAL);
			unlock(ULTRAHUD);
		}
	}
	
	@Override
	public void writeToNbt(@NotNull NbtCompound tag)
	{
		NbtList unlocks = new NbtList();
		for (Identifier id : this.unlocked)
			unlocks.add(NbtString.of(id.toString()));
		tag.put("unlocks", unlocks);
		NbtList owned = new NbtList();
		for (Identifier id : this.owned)
			owned.add(NbtString.of(id.toString()));
		tag.put("owned", owned);
		tag.putInt("patch", 1);
	}
	
	@Override
	public void applySyncPacket(PacketByteBuf buf)
	{
		IProgressionComponent.super.applySyncPacket(buf);
		if(provider instanceof WingedPlayerEntity winged && winged.getFocusedTerminal() != null && winged.getFocusedTerminal().getTab() instanceof WeaponsTab weaponsTab)
			weaponsTab.refreshTab();
	}
}
