package absolutelyaya.ultracraft.block.mapping;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.math.BlockPos;

public abstract class AbstractListenerBlockEntity extends AbstractMappingBlockEntity implements FlagListener
{
	boolean nextState, state;
	String flag;
	int pulseRenderTime, activationDelay, curActivationDelay;
	
	public AbstractListenerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
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
	public void onActivateFlag()
	{
		pulseRenderTime = 10;
		if(!nextState)
			curActivationDelay = activationDelay;
		nextState = true;
	}
	
	@Override
	public void onDeactivateFlag()
	{
		if(nextState)
			curActivationDelay = activationDelay;
		nextState = false;
	}
	
	protected void onStateChanged(boolean newState)
	{
		updateNeighbors();
	}
	
	public boolean isActive()
	{
		return state;
	}
	
	@Override
	public boolean isAreaModifiable()
	{
		return false;
	}
	
	@Override
	void tick()
	{
		if(pulseRenderTime > 0)
			pulseRenderTime--;
		if(curActivationDelay > 0)
			curActivationDelay--;
		if(curActivationDelay == 0 && state != nextState)
			onStateChanged(state = nextState);
	}
	
	protected void updateNeighbors()
	{
		world.updateNeighbors(getPos(), world.getBlockState(getPos()).getBlock());
	}
	
	@Override
	public void readNbt(NbtCompound nbt)
	{
		super.readNbt(nbt);
		if(nbt.contains("flag", NbtElement.STRING_TYPE))
			flag = nbt.getString("flag");
		if(nbt.contains("delay", NbtElement.INT_TYPE))
			activationDelay = nbt.getInt("delay");
	}
	
	@Override
	protected void writeNbt(NbtCompound nbt)
	{
		super.writeNbt(nbt);
		if(flag != null)
			nbt.putString("flag", flag);
		nbt.putInt("delay", activationDelay);
	}
}
