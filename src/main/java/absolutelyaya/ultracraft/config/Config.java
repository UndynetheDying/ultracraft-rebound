package absolutelyaya.ultracraft.config;

import absolutelyaya.ultracraft.Ultracraft;
import absolutelyaya.ultracraft.registry.PacketRegistry;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.WorldSavePath;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

public abstract class Config
{
	static final Map<String, Config> configMap = new HashMap<>();
	
	public final List<ConfigEntry<?>> entries = new ArrayList<>();
	final MinecraftServer server;
	final String id;
	
	public Config(MinecraftServer server, String id)
	{
		this.server = server;
		this.id = id;
		configMap.put(id, this);
	}
	
	public static Config getFromID(String configID)
	{
		return configMap.get(configID);
	}
	
	protected abstract String getExportPath();
	
	protected abstract String getFileName();
	
	public void save(MinecraftServer server)
	{
		Path gameDir = server.getSavePath(WorldSavePath.ROOT);
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
	
	public void load(MinecraftServer server)
	{
		Path gameDir = server.getSavePath(WorldSavePath.ROOT);
		Path path = Path.of(gameDir.toString(), getExportPath() + getFileName());
		File file = new File(path.toUri());
		if(!file.exists())
			save(server);
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
	
	public static <V> void onChanged(MinecraftServer server, String configID, ConfigEntry<V> entry)
	{
		server.getPlayerManager().getPlayerList().forEach(p -> onChanged(p, configID, entry));
	}
	
	public static <V> void onChanged(ServerPlayerEntity player, String configId, ConfigEntry<V> entry)
	{
		if(entry instanceof EnumEntry<?> v)
			onChanged(player, configId, v);
		else if(entry instanceof IntegerEntry v)
			onChanged(player, configId, v);
		else if(entry instanceof BooleanEntry v)
			onChanged(player, configId, v);
		else if(entry instanceof FloatEntry v)
			onChanged(player, configId, v);
	}
	
	public static void onChanged(ServerPlayerEntity player, String configId, BooleanEntry entry)
	{
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		buf.writeString(configId);
		buf.writeString(entry.getId());
		buf.writeByte(NbtElement.BYTE_TYPE);
		buf.writeBoolean(entry.getValue());
		ServerPlayNetworking.send(player, PacketRegistry.SYNC_CONFIG_S2C_PACKET_ID, buf);
	}
	
	public static void onChanged(ServerPlayerEntity player, String configId, IntegerEntry entry)
	{
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		buf.writeString(configId);
		buf.writeString(entry.getId());
		buf.writeByte(NbtElement.INT_TYPE);
		buf.writeInt(entry.getValue());
		ServerPlayNetworking.send(player, PacketRegistry.SYNC_CONFIG_S2C_PACKET_ID, buf);
	}
	
	public static void onChanged(ServerPlayerEntity player, String configId, FloatEntry entry)
	{
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		buf.writeString(configId);
		buf.writeString(entry.getId());
		buf.writeByte(NbtElement.FLOAT_TYPE);
		buf.writeFloat(entry.getValue());
		ServerPlayNetworking.send(player, PacketRegistry.SYNC_CONFIG_S2C_PACKET_ID, buf);
	}
	
	public static void onChanged(ServerPlayerEntity player, String configId, EnumEntry<?> entry)
	{
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		buf.writeString(configId);
		buf.writeString(entry.getId());
		buf.writeByte(69);
		buf.writeInt(entry.getValue().ordinal());
		ServerPlayNetworking.send(player, PacketRegistry.SYNC_CONFIG_S2C_PACKET_ID, buf);
	}
	
	public void syncAll(ServerPlayerEntity player)
	{
		entries.forEach(i -> onChanged(player, id, i));
		
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		buf.writeString(id);
		ServerPlayNetworking.send(player, PacketRegistry.FINISH_SYNC_CONFIG_S2C_PACKET_ID, buf);
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
		save(server);
	}
	
	public EnumEntry<?> set(EnumEntry<?> entry, int ordinal)
	{
		for (ConfigEntry<?> i : entries)
		{
			if(i != null && i.getId().equals(entry.id))
				entry.setValue(ordinal);
		}
		save(server);
		return entry;
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
					save(server);
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
	
	public NbtCompound getAsNBT()
	{
		NbtCompound nbt = new NbtCompound();
		for (ConfigEntry<?> entry : entries)
			nbt.putString(entry.getId(), entry.getValue().toString());
		return nbt;
	}
	
	public ConfigEntry<?> getEntry(String id)
	{
		for (ConfigEntry<?> entry : entries)
			if(entry.id.equals(id))
				return entry;
		return null;
	}
}
