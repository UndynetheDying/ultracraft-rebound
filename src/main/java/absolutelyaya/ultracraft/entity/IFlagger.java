package absolutelyaya.ultracraft.entity;

import net.minecraft.util.math.BlockPos;

import java.util.List;

public interface IFlagger
{
	default boolean isRoomListener(BlockPos pos)
	{
		return getListenerRooms().stream().anyMatch(i -> i.equals(pos));
	}
	
	List<BlockPos> getListenerRooms();
	
	void bindListenerRoom(BlockPos pos);
}
