package absolutelyaya.ultracraft.components.world;

import absolutelyaya.ultracraft.block.mapping.RoomBlockEntity;
import absolutelyaya.ultracraft.components.UltraComponents;
import absolutelyaya.ultracraft.dimension.UltraDimensions;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtLong;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DimensionDataComponent implements IDimensionDataComponent
{
	final World provider;
	final List<BlockPos> mappingRooms = new ArrayList<>();
	final List<BlockPos> invalidRooms = new ArrayList<>();
	
	Map<String, Integer> flags = new HashMap<>();
	boolean fixedStructuresPlaced;
	
	public DimensionDataComponent(World provider)
	{
		this.provider = provider;
	}
	
	@Override
	public boolean isFixedStructuresPlaced()
	{
		return fixedStructuresPlaced;
	}
	
	@Override
	public void setFixedStructuresPlaced(boolean b)
	{
		fixedStructuresPlaced = b;
	}
	
	@Override
	public Map<String, Integer> getAllFlags()
	{
		return flags;
	}
	
	@Override
	public int getFlag(String id)
	{
		return flags.getOrDefault(id, -1);
	}
	
	@Override
	public void setFlag(String id, int value)
	{
		flags.put(id, value);
	}
	
	@Override
	public void registerRoomMappingBlock(BlockPos pos)
	{
		if(!mappingRooms.contains(pos))
		{
			mappingRooms.add(pos);
			UltraComponents.DIMENSION_DATA.sync(provider);
		}
	}
	
	@Override
	public void removeRoomMappingBlock(BlockPos pos)
	{
		if(mappingRooms.remove(pos))
			UltraComponents.DIMENSION_DATA.sync(provider);
	}
	
	public List<BlockPos> getAllMappingRooms()
	{
		return mappingRooms;
	}
	
	@Override
	public void markRoomInvalid(BlockPos pos)
	{
		if(!invalidRooms.contains(pos))
			invalidRooms.add(pos);
	}
	
	@Override
	public void clearInvalidRooms()
	{
		invalidRooms.forEach(mappingRooms::remove);
		invalidRooms.clear();
		UltraComponents.DIMENSION_DATA.sync(provider);
	}
	
	public boolean isPosNotModifiable(PlayerEntity player, BlockPos pos)
	{
		if(player.isCreativeLevelTwoOp() || UltraComponents.EDITOR.get(player).isActive())
			return false;
		IDimensionDataComponent data = UltraComponents.DIMENSION_DATA.get(provider);
		for (BlockPos roomPos : data.getAllMappingRooms())
		{
			if(!provider.isChunkLoaded(roomPos))
				continue;
			if(!(provider.getBlockEntity(roomPos) instanceof RoomBlockEntity room))
			{
				data.markRoomInvalid(pos);
				if(UltraDimensions.Instance != null)
					UltraDimensions.Instance.onSuppressedModification(player);
				continue;
			}
			if(room.isSuppressModifications() && room.getAreaBox().contains(pos.toCenterPos()))
				return true;
		}
		data.clearInvalidRooms();
		return !provider.canPlayerModifyAt(player, pos);
	}
	
	@Override
	public void readFromNbt(NbtCompound tag)
	{
		if(tag.contains("fixedStructuresPlaced", NbtElement.BYTE_TYPE))
			fixedStructuresPlaced = tag.getBoolean("fixedStructuresPlaced");
		if(tag.contains("flags", NbtElement.COMPOUND_TYPE))
		{
			NbtCompound flagCompound = tag.getCompound("flags");
			for (String id : flagCompound.getKeys())
			{
				if(flagCompound.contains(id, NbtElement.INT_TYPE))
					flags.put(id, flagCompound.getInt(id));
			}
		}
		if(tag.contains("rooms", NbtElement.LIST_TYPE))
		{
			NbtList list = tag.getList("", NbtElement.COMPOUND_TYPE);
			list.forEach(i -> {
				if(i instanceof NbtLong packed)
					mappingRooms.add(BlockPos.fromLong(packed.longValue()));
			});
		}
	}
	
	@Override
	public void writeToNbt(NbtCompound tag)
	{
		tag.putBoolean("fixedStructuresPlaced", fixedStructuresPlaced);
		NbtCompound flagCompound = new NbtCompound();
		for (String id : flags.keySet())
			flagCompound.putInt(id, flags.get(id));
		tag.put("flags", flagCompound);
		NbtList rooms = new NbtList();
		mappingRooms.forEach(pos -> rooms.add(NbtLong.of(pos.asLong())));
		tag.put("rooms", rooms);
	}
}
