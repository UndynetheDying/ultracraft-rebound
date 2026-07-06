package absolutelyaya.ultracraft.components.player;

import absolutelyaya.ultracraft.Weapon;
import absolutelyaya.ultracraft.components.UltraComponents;
import absolutelyaya.ultracraft.item.weapons.AbstractWeaponItem;
import absolutelyaya.ultracraft.registry.PacketRegistry;
import absolutelyaya.ultracraft.util.InventoryUtil;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;

import java.util.*;

public class LoadoutComponent implements ILoadoutComponent
{
	final PlayerEntity provider;
	private final Map<Weapon, Identifier[]> loadouts = new HashMap<>();
	boolean lastOneWeaponHeld, weaponCountDirty;
	
	public LoadoutComponent(PlayerEntity provider)
	{
		this.provider = provider;
		for (Weapon w : Weapon.values())
			loadouts.put(w, w.getDefaultLoadout());
	}
	
	@Override
	public Identifier[] getLoadoutForWeapon(Weapon weapon)
	{
		return loadouts.getOrDefault(weapon, new Identifier[0]);
	}
	
	public int getOwnedWeaponCountInLoadoutForWeapon(Weapon weapon)
	{
		int count = 0;
		IProgressionComponent progression = UltraComponents.PROGRESSION.get(provider);
		for (Identifier id : loadouts.getOrDefault(weapon, new Identifier[0]))
			if(id != null && progression.isOwned(id))
				count++;
		return count;
	}
	
	@Override
	public void setLoadoutForWeapon(Weapon weapon, Identifier[] ids)
	{
		weaponCountDirty = true;
		loadouts.put(weapon, ids);
		sync(weapon);
	}
	
	@Override
	public boolean isInLoadout(AbstractWeaponItem weaponItem)
	{
		Identifier[] loadout = loadouts.get(weaponItem.getWeaponType());
		if(loadout == null)
			return false;
		for (Identifier identifier : loadout)
			if (identifier != null && identifier.equals(Registries.ITEM.getId(weaponItem)))
				return true;
		return false;
	}
	
	@Override
	public boolean isInLoadout(Weapon weapon, Identifier id)
	{
		Identifier[] loadout = loadouts.get(weapon);
		if(loadout == null)
			return false;
		for (Identifier identifier : loadout)
			if (identifier != null && identifier.equals(id))
				return true;
		return false;
	}
	
	@Override
	public boolean isAltInLoadout(Weapon weapon, Identifier id)
	{
		Identifier[] loadout = loadouts.get(weapon);
		if(loadout == null || weapon.altId == null)
			return false;
		Identifier alt = weapon.getAlt(id);
		if(alt == null)
			return false;
		for (Identifier identifier : loadout)
			if (identifier != null && identifier.equals(alt))
				return true;
		return false;
	}
	
	@Override
	public void addToLoadout(Weapon weapon, Identifier id)
	{
		Identifier[] loadout = loadouts.get(weapon);
		if(loadout == null)
			return;
		List<Identifier> newLoadout = new ArrayList<>();
		for (Identifier i : loadout)
			if(i != null)
				newLoadout.add(i);
		if(!newLoadout.contains(id))
		{
			newLoadout.add(id);
			loadouts.put(weapon, newLoadout.toArray(new Identifier[0]));
		}
	}
	
	@Override
	public boolean isWeaponTypeHeld(Weapon weapon)
	{
		for (Identifier id : weapon.ids)
		{
			PlayerInventory inv = provider.getInventory();
			Item outputItem = Registries.ITEM.get(id);
			if(inv.offHand.get(0).isOf(outputItem))
				return true;
			DefaultedList<ItemStack> invList = inv.main;
			if(InventoryUtil.containsItem(invList, outputItem, 1))
				return true;
		}
		return false;
	}
	
	@Override
	public boolean tryDispenseWeapon(Weapon weapon, Identifier itemID)
	{
		boolean alt = isAltInLoadout(weapon, itemID);
		if(!isWeaponTypeHeld(weapon) && (isInLoadout(weapon, itemID) || alt))
		{
			PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
			buf.writeIdentifier(itemID);
			buf.writeInt(weapon.ordinal());
			buf.writeBoolean(alt);
			ClientPlayNetworking.send(PacketRegistry.TERMINAL_WEAPON_DISPENSE_PACKET_ID, buf);
			provider.giveItemStack(Registries.ITEM.get(alt ? weapon.getAlt(itemID) : itemID).getDefaultStack());
			return true;
		}
		return false;
	}
	
	@Override
	public boolean isMoreThanOneWeaponHeld()
	{
		if(!weaponCountDirty)
			return lastOneWeaponHeld;
		weaponCountDirty = false;
		boolean b = false;
		for (Weapon weapon : Weapon.values())
		{
			if(weapon.ids == null)
				continue;
			boolean isHeld = isWeaponTypeHeld(weapon);
			if(isHeld)
			{
				if(getOwnedWeaponCountInLoadoutForWeapon(weapon) > 1)
					return lastOneWeaponHeld = true;
				if(!b)
					b = true;
				else
					return lastOneWeaponHeld = true;
			}
		}
		return lastOneWeaponHeld = false;
	}
	
	@Override
	public void setWeaponCountDirty()
	{
		weaponCountDirty = true;
	}
	
	@Override
	public void sync(Weapon weapon)
	{
		if(provider.getWorld().isClient)
		{
			PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
			buf.writeInt(weapon.ordinal());
			Identifier[] ids = Arrays.stream(getLoadoutForWeapon(weapon)).filter(Objects::nonNull).toArray(Identifier[]::new);
			buf.writeInt(ids.length);
			for (Identifier id : ids)
				buf.writeIdentifier(id);
			ClientPlayNetworking.send(PacketRegistry.SYNC_LOADOUT_PACKET_ID, buf);
		}
	}
	
	@Override
	public void readFromNbt(NbtCompound tag)
	{
		for (Weapon w : Weapon.values())
		{
			if(!tag.contains(w.toString(), NbtElement.LIST_TYPE))
				continue;
			NbtList list = tag.getList(w.toString(), NbtElement.STRING_TYPE);
			List<Identifier> ids = new ArrayList<>();
			list.forEach(i -> ids.add(Identifier.tryParse(i.asString())));
			loadouts.put(w, ids.toArray(Identifier[]::new));
		}
	}
	
	@Override
	public void writeToNbt(NbtCompound tag)
	{
		for (Weapon w : Weapon.values())
		{
			NbtList list = new NbtList();
			for (Identifier id : getLoadoutForWeapon(w))
				list.add(NbtString.of(id.toString()));
			tag.put(w.toString(), list);
		}
	}
}
