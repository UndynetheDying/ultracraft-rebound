package absolutelyaya.ultracraft.components.player;

import absolutelyaya.ultracraft.Weapon;
import absolutelyaya.ultracraft.item.AbstractWeaponItem;
import absolutelyaya.ultracraft.registry.PacketRegistry;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.*;

public class LoadoutComponent implements ILoadoutComponent
{
	final PlayerEntity provider;
	private final Map<Weapon, Identifier[]> loadouts = new HashMap<>();;
	
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
	
	@Override
	public void setLoadoutForWeapon(Weapon weapon, Identifier[] ids)
	{
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
