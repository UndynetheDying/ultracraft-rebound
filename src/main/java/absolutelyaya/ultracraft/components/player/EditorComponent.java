package absolutelyaya.ultracraft.components.player;

import absolutelyaya.ultracraft.UltraComponents;
import absolutelyaya.ultracraft.block.mapping.AbstractMappingBlockEntity;
import absolutelyaya.ultracraft.block.mapping.RoomBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;

public class EditorComponent implements IEditorComponent
{
	PlayerEntity provider;
	HashMap<String, BlockPos> focus = new HashMap<>();
	BlockPos editAreaCore, rebindingParent;
	int editAreaStep;
	boolean active;
	
	public EditorComponent(PlayerEntity provider)
	{
		this.provider = provider;
	}
	
	@Override
	public void toggleEditMode()
	{
		setActive(!active);
	}
	
	@Override
	public boolean isActive()
	{
		return active;
	}
	
	@Override
	public void setActive(boolean active)
	{
		this.active = active;
		
		provider.noClip = provider.getAbilities().flying = active;
		if(active)
			provider.getAbilities().allowFlying = true;
		else
			provider.getAbilities().allowFlying = provider.isCreative() || provider.isSpectator();
		provider.getAbilities().setFlySpeed(active ? 0.2f : 0.05f);
		UltraComponents.EDITOR.sync(provider);
	}
	
	@Override
	public void setEditFocus(String key, BlockPos pos)
	{
		focus.put(key, pos);
	}
	
	@Override
	public BlockPos getEditFocus(String key)
	{
		return focus.get(key);
	}
	
	@Override
	public HashMap<String, BlockPos> getEditFocus()
	{
		return focus;
	}
	
	@Override
	public void clearEditFocus()
	{
		focus = new HashMap<>();
	}
	
	@Override
	public void clearEditFocus(String key)
	{
		focus.remove(key);
	}
	
	@Override
	public void setEditAreaStep(int i)
	{
		editAreaStep = i;
	}
	
	@Override
	public void setEditAreaCore(BlockPos pos)
	{
		editAreaCore = pos;
	}
	
	@Override
	public int getEditAreaStep()
	{
		return editAreaStep;
	}
	
	@Override
	public ActionResult useBlock(PlayerEntity player, BlockPos pos)
	{
		if(player.getWorld().isClient)
			return ActionResult.PASS;
		if(editAreaStep > 0 && editAreaCore != null && player.getWorld().getBlockEntity(editAreaCore) instanceof AbstractMappingBlockEntity e)
		{
			e.setAreaCorner(editAreaStep, pos);
			setEditAreaStep(editAreaStep - 1);
			if(editAreaStep == 0)
			{
				editAreaCore = null;
				player.sendMessage(Text.of("Editing Area Complete."));
			}
			return ActionResult.SUCCESS;
		}
		else if(editAreaStep > 0)
			editAreaCore = null;
		return ActionResult.PASS;
	}
	
	@Override
	public void setRebindingParent(BlockPos pos)
	{
		rebindingParent = pos;
	}
	
	@Override
	public BlockPos getRebindingParent()
	{
		return rebindingParent;
	}
	
	@Override
	public void sync()
	{
		UltraComponents.EDITOR.sync(provider);
	}
	
	@Override
	public void readFromNbt(NbtCompound tag)
	{
		if(tag.contains("active", NbtElement.BYTE_TYPE))
			setActive(tag.getBoolean("active"));
		if(tag.contains("areaStep", NbtElement.INT_TYPE))
			editAreaStep = tag.getInt("areaStep");
		if(tag.contains("areaCore", NbtElement.LONG_TYPE))
			editAreaCore = BlockPos.fromLong(tag.getLong("areaCore"));
	}
	
	@Override
	public void writeToNbt(NbtCompound tag)
	{
		tag.putBoolean("active", active);
		tag.putInt("areaStep", editAreaStep);
		if(editAreaCore != null)
			tag.putLong("areaCore", BlockPos.asLong(editAreaCore.getX(), editAreaCore.getY(), editAreaCore.getZ()));
	}
}
