package absolutelyaya.ultracraft.block.mapping;

import absolutelyaya.ultracraft.components.UltraComponents;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.math.BlockPos;

public abstract class AbstractGlobalListenerBlockEntity extends AbstractMappingBlockEntity implements FlagListener
{
	boolean active, wasActive;
	String flag;
	int activationValue = 1;
	
	public AbstractGlobalListenerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
	{
		super(type, pos, state);
	}
	
	@Override
	public String getFocusKey()
	{
		return "listener";
	}
	
	@Override
	public boolean showCamLine()
	{
		return false;
	}
	
	@Override
	public float getAreaLabelSize()
	{
		return 0f;
	}
	
	@Override
	public void bindFlag(String flag)
	{
		this.flag = flag;
		markDirty();
		world.updateListeners(pos, getCachedState(), getCachedState(), 0);
	}
	
	@Override
	public String getFlag()
	{
		return flag;
	}
	
	@Override
	void tick()
	{
		wasActive = active;
		active = UltraComponents.DIMENSION_DATA.get(world).getFlag(flag) == activationValue;
		if(active != wasActive)
		{
			if(active)
				onActivateFlag();
			else
				onDeactivateFlag();
		}
	}
	
	@Override
	public String getAttribute(String attribute)
	{
		if(attribute.equals("activationValue"))
			return String.valueOf(activationValue);
		return null;
	}
	
	@Override
	public void setAttribute(String s, String value) throws AttributeParseException, NumberFormatException
	{
		if(s.equals("activationValue"))
			activationValue = Integer.parseInt(value);
		super.setAttribute(s, value);
	}
	
	@Override
	public void reset()
	{
	
	}
	
	public boolean isActive()
	{
		return active;
	}
	
	@Override
	public boolean isGlobal()
	{
		return true;
	}
	
	@Override
	protected void writeNbt(NbtCompound nbt)
	{
		super.writeNbt(nbt);
		if(flag != null)
			nbt.putString("flag", flag);
		nbt.putInt("activationValue", activationValue);
	}
	
	@Override
	public void readNbt(NbtCompound nbt)
	{
		super.readNbt(nbt);
		if(nbt.contains("flag", NbtElement.STRING_TYPE))
			flag = nbt.getString("flag");
		activationValue = nbt.getInt("activationValue");
	}
}
