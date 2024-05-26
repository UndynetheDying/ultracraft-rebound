package absolutelyaya.ultracraft.components.player;

import absolutelyaya.ultracraft.client.rendering.EditModeRenderer;
import absolutelyaya.ultracraft.components.UltraComponents;
import absolutelyaya.ultracraft.block.mapping.AbstractMappingBlockEntity;
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
	boolean active, showAreaOwner = true, noClip = true, ghost = false, wasFlyingBeforeEditing = false, recursiveRooms = false, showRelations = true;
	float flySpeed = 3f;
	
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
		
		if(active)
		{
			wasFlyingBeforeEditing = provider.getAbilities().flying;
			provider.getAbilities().flying = provider.getAbilities().allowFlying = true;
			provider.setOnGround(false);
		}
		else
		{
			provider.getAbilities().flying = (provider.isCreative() && wasFlyingBeforeEditing) || provider.isSpectator();
			provider.getAbilities().allowFlying = provider.isCreative() || provider.isSpectator();
		}
		
		provider.getAbilities().setFlySpeed(active ? flySpeed / 20f : 0.05f);
		UltraComponents.EDITOR.sync(provider);
	}
	
	@Override
	public void setEditFocus(String key, BlockPos pos)
	{
		focus.put(key, pos);
		if(provider.getWorld().isClient && key.equals("room") && !EditModeRenderer.Instance.isKnown(pos))
			EditModeRenderer.Instance.addKnownRoom(pos);
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
	public boolean isShowAreaOwner()
	{
		return showAreaOwner;
	}
	
	@Override
	public void setShowAreaOwner(boolean v)
	{
		showAreaOwner = v;
	}
	
	@Override
	public boolean toggleShowAreaOwner()
	{
		return showAreaOwner = !showAreaOwner;
	}
	
	@Override
	public boolean isNoClip()
	{
		return active && noClip;
	}
	
	@Override
	public void setNoClip(boolean v)
	{
		noClip = v;
	}
	
	@Override
	public boolean toggleNoClip()
	{
		return noClip = !noClip;
	}
	
	@Override
	public float getFlySpeed()
	{
		return flySpeed;
	}
	
	@Override
	public void setFlySpeed(float v)
	{
		flySpeed = v;
		if(active)
			provider.getAbilities().setFlySpeed(v / 20f);
	}
	
	@Override
	public void setGhost(boolean v)
	{
		ghost = v;
	}
	
	@Override
	public boolean isGhost()
	{
		return active && ghost;
	}
	
	@Override
	public boolean toggleGhost()
	{
		return ghost = !ghost;
	}
	
	@Override
	public void setAllowRecursiveRooms(boolean v)
	{
		recursiveRooms = v;
	}
	
	@Override
	public boolean isAllowRecursiveRooms()
	{
		return recursiveRooms;
	}
	
	@Override
	public boolean toggleRecursiveRooms()
	{
		return recursiveRooms = !recursiveRooms;
	}
	
	@Override
	public void setShowRelations(boolean v)
	{
		showRelations = v;
	}
	
	@Override
	public boolean isShowRelations()
	{
		return showRelations;
	}
	
	@Override
	public boolean toggleShowRelations()
	{
		return showRelations = !showRelations;
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
		if(tag.contains("showAreaOwner", NbtElement.BYTE_TYPE))
			setShowAreaOwner(tag.getBoolean("showAreaOwner"));
		if(tag.contains("noclip", NbtElement.BYTE_TYPE))
			setNoClip(tag.getBoolean("noclip"));
		if(tag.contains("flySpeed", NbtElement.FLOAT_TYPE))
			setFlySpeed(tag.getFloat("flySpeed"));
		if(tag.contains("ghost", NbtElement.BYTE_TYPE))
			setGhost(tag.getBoolean("ghost"));
		if(tag.contains("recursiveRooms", NbtElement.BYTE_TYPE))
			setAllowRecursiveRooms(tag.getBoolean("recursiveRooms"));
		if(tag.contains("showRelations", NbtElement.BYTE_TYPE))
			setShowRelations(tag.getBoolean("showRelations"));
		if(tag.contains("rebindingParent", NbtElement.LONG_TYPE))
			rebindingParent = BlockPos.fromLong(tag.getLong("rebindingParent"));
		else
			rebindingParent = null;
	}
	
	@Override
	public void writeToNbt(NbtCompound tag)
	{
		tag.putBoolean("active", active);
		tag.putInt("areaStep", editAreaStep);
		if(editAreaCore != null)
			tag.putLong("areaCore", BlockPos.asLong(editAreaCore.getX(), editAreaCore.getY(), editAreaCore.getZ()));
		tag.putBoolean("showAreaOwner", showAreaOwner);
		tag.putBoolean("noclip", noClip);
		tag.putFloat("flySpeed", flySpeed);
		tag.putBoolean("ghost", ghost);
		tag.putBoolean("recursiveRooms", recursiveRooms);
		tag.putBoolean("showRelations", showRelations);
		if(rebindingParent != null)
			tag.putLong("rebindingParent", rebindingParent.asLong());
	}
}
