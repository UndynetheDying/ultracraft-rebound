package absolutelyaya.ultracraft.block.mapping;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.math.BlockPos;

public abstract class AbstractListenerBlockEntity extends AbstractMappingBlockEntity implements FlagListener
{
	String flag;
	int pulseRenderTime;
	
	public AbstractListenerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
	{
		super(type, pos, state);
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
		updateNeighbors();
	}
	
	@Override
	public void onDeactivateFlag()
	{
		updateNeighbors();
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
	}
	
	@Override
	protected void writeNbt(NbtCompound nbt)
	{
		super.writeNbt(nbt);
		if(flag != null)
			nbt.putString("flag", flag);
	}
}
