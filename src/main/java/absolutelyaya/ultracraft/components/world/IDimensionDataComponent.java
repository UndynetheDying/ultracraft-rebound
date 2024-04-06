package absolutelyaya.ultracraft.components.world;

import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;

import java.util.List;
import java.util.Map;

public interface IDimensionDataComponent extends ComponentV3
{
	boolean isFixedStructuresPlaced();
	
	void setFixedStructuresPlaced(boolean b);
	
	Map<String, Integer> getAllFlags();
	
	int getFlag(String id);
	
	void setFlag(String id, int value);
	
	void registerRoomMappingBlock(BlockPos pos);
	
	void removeRoomMappingBlock(BlockPos pos);
	
	List<BlockPos> getAllMappingRooms();
	
	void markRoomInvalid(BlockPos pos);
	
	void clearInvalidRooms();
	
	boolean isPosNotModifiable(PlayerEntity player, BlockPos pos);
}
