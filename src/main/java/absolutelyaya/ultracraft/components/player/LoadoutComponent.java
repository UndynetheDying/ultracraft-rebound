package absolutelyaya.ultracraft.components.player;

import absolutelyaya.ultracraft.Weapon;
import absolutelyaya.ultracraft.item.AbstractWeaponItem;
import absolutelyaya.ultracraft.registry.PacketRegistry;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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
		System.out.println(Arrays.toString(ids));
		sync(weapon);
	}
	
	@Override
	public boolean isInLoadout(AbstractWeaponItem weaponItem)
	{
		Identifier[] loadout = loadouts.get(weaponItem.getWeaponType());
		if(loadout == null)
			return false;
		for (Identifier identifier : loadout)
			if (identifier.equals(Registries.ITEM.getId(weaponItem)))
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
	
	}
	
	@Override
	public void writeToNbt(NbtCompound tag)
	{
	
	}
}
