package absolutelyaya.ultracraft.components.player;

import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;

public interface IEditorComponent extends ComponentV3, AutoSyncedComponent
{
	void toggleEditMode();
	
	boolean isActive();
	
	void setActive(boolean state);
	
	void setEditFocus(String key, BlockPos pos);
	
	BlockPos getEditFocus(String key);
	
	void setEditAreaStep(int i);
	
	void setEditAreaCore(BlockPos pos);
	
	int getEditAreaStep();
	
	ActionResult useBlock(PlayerEntity player, BlockPos pos);
	
	void sync();
}
