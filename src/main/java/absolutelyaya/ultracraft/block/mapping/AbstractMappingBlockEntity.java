package absolutelyaya.ultracraft.block.mapping;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public abstract class AbstractMappingBlockEntity extends BlockEntity
{
	protected String id;
	protected BlockPos min, max;
	
	public AbstractMappingBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
	{
		super(type, pos, state);
		min = pos;
		max = pos;
	}
	
	public void setID(String id)
	{
		this.id = id;
		markDirty();
		world.updateListeners(pos, getCachedState(), getCachedState(), 0);
	}
	
	public String getID()
	{
		return id;
	}
	
	public void setAreaCorner(int editAreaStep, BlockPos pos)
	{
		if(editAreaStep == 2)
			max = pos;
		else if(editAreaStep == 1)
			min = pos;
		setID(id); //for some reason the blocks data woN'T SYNC ANY OTHER WAY RAAAA
	}
	
	public BlockPos getMin(BlockPos pos)
	{
		if(min == null)
			return null;
		return min.subtract(pos);
	}
	
	public BlockPos getMax(BlockPos pos)
	{
		if(max == null)
			return null;
		return max.subtract(pos);
	}
	
	public Text getAreaLabel()
	{
		return Text.of(id);
	}
	
	public boolean alwaysShowArea()
	{
		return false;
	}
	
	public abstract String getFocusKey();
	
	@Override
	protected void writeNbt(NbtCompound nbt)
	{
		super.writeNbt(nbt);
		if(min != null && max != null)
		{
			nbt.putLong("min", BlockPos.asLong(min.getX(), min.getY(), min.getZ()));
			nbt.putLong("max", BlockPos.asLong(max.getX(), max.getY(), max.getZ()));
		}
		nbt.putString("name", id);
	}
	
	@Override
	public void readNbt(NbtCompound nbt)
	{
		super.readNbt(nbt);
		if(nbt.contains("min", NbtElement.LONG_TYPE))
			min = BlockPos.fromLong(nbt.getLong("min"));
		if(nbt.contains("max", NbtElement.LONG_TYPE))
			max = BlockPos.fromLong(nbt.getLong("max"));
		if(nbt.contains("name", NbtElement.STRING_TYPE))
			id = nbt.getString("name");
	}
}
