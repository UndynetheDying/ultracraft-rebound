package absolutelyaya.ultracraft.config;

import absolutelyaya.ultracraft.Ultracraft;
import absolutelyaya.ultracraft.registry.PacketRegistry;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public abstract class Config
{
	public final List<ConfigEntry<?>> entries = new ArrayList<>();
	
	protected abstract String getExportPath();
	
	protected abstract String getFileName();
	
	public void save()
	{
		Path gameDir = FabricLoader.getInstance().getGameDir();
		try
		{
			Path.of(gameDir.toString(), getExportPath()).toFile().mkdirs();
			File file = Path.of(gameDir.toString(), getExportPath(), getFileName()).toFile();
			file.createNewFile();
			try (FileWriter writer = new FileWriter(file))
			{
				for (ConfigEntry<?> entry : entries)
					writer.write(entry.serialize() + "\n");
			}
			catch (IOException e)
			{
				throw new RuntimeException(e);
			}
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	public void load()
	{
		Path gameDir = FabricLoader.getInstance().getGameDir();
		Path path = Path.of(gameDir.toString(), getExportPath() + getFileName());
		File file = new File(path.toUri());
		if(!file.exists())
		{
			save();
		}
		try (Scanner reader = new Scanner(file))
		{
			while(reader.hasNextLine())
			{
				String line = reader.nextLine();
				if(line.startsWith("#") || line.isEmpty())
					continue;
				String[] segments = line.split(":");
				if(segments.length != 2)
					continue;
				try
				{
					for (ConfigEntry<?> entry : entries)
					{
						if(entry.id.equals(segments[0]))
							entry.deserialize(segments[1]);
					}
				}
				catch (Exception e)
				{
					Ultracraft.LOGGER.error("An Exception occurred trying to read Server Config Entry '" + segments[0] + "'.");
					e.printStackTrace();
				}
			}
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	public static <V> void onChanged(MinecraftServer server, ConfigEntry<V> entry)
	{
		server.getPlayerManager().getPlayerList().forEach(p -> onChanged(p, entry));
	}
	
	public static <V> void onChanged(ServerPlayerEntity player, ConfigEntry<V> entry)
	{
		V value = entry.getValue();
		Class<?> clazz = value.getClass();
		if (clazz.equals(Integer.class))
			onChanged(player, entry.getId(), (int)value);
		if (clazz.equals(Boolean.class))
			onChanged(player, entry.getId(), (boolean)value);
		if (clazz.equals(Float.class))
			onChanged(player, entry.getId(), (float)value);
	}
	
	public static void onChanged(MinecraftServer server, String id, int val)
	{
		server.getPlayerManager().getPlayerList().forEach(p -> {
			PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
			buf.writeString(id);
			buf.writeByte(NbtElement.INT_TYPE);
			buf.writeInt(val);
			ServerPlayNetworking.send(p, PacketRegistry.SYNC_RULE_PACKET_ID, buf);
		});
	}
	
	public static void onChanged(ServerPlayerEntity player, String id, boolean val)
	{
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		buf.writeString(id);
		buf.writeByte(NbtElement.BYTE_TYPE);
		buf.writeBoolean(val);
		ServerPlayNetworking.send(player, PacketRegistry.SYNC_RULE_PACKET_ID, buf);
	}
	
	public static void onChanged(ServerPlayerEntity player, String id, int val)
	{
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		buf.writeString(id);
		buf.writeByte(NbtElement.INT_TYPE);
		buf.writeInt(val);
		ServerPlayNetworking.send(player, PacketRegistry.SYNC_RULE_PACKET_ID, buf);
	}
	
	public static void onChanged(ServerPlayerEntity player, String id, float val)
	{
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		buf.writeString(id);
		buf.writeByte(NbtElement.FLOAT_TYPE);
		buf.writeFloat(val);
		ServerPlayNetworking.send(player, PacketRegistry.SYNC_RULE_PACKET_ID, buf);
	}
	
	public void syncAll(ServerPlayerEntity player)
	{
		entries.forEach(i -> onChanged(player, i));
		
	}
	
	public void syncAll(MinecraftServer server)
	{
		server.getPlayerManager().getPlayerList().forEach(this::syncAll);
	}
	
	public <V> void set(ConfigEntry<V> entry, V value)
	{
		for (ConfigEntry<?> i : entries)
		{
			if(i != null && i.getId().equals(entry.id))
				entry.setValue(value);
		}
	}
	
	public <V> ConfigEntry<V> set(String id, V value)
	{
		for (ConfigEntry<?> entry : entries)
		{
			if(entry != null && entry.getId().equals(id))
			{
				try
				{
					ConfigEntry<V> vEntry = ((ConfigEntry<V>)entry);
					vEntry.setValue(value);
					return vEntry;
				}
				catch (Exception e)
				{
					Ultracraft.LOGGER.error("Exception encountered when trying to set Config Value '" + id + "'");
					e.printStackTrace();
				}
			}
		}
		return null;
	}
}
