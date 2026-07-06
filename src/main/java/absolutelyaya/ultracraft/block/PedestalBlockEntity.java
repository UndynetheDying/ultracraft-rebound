package absolutelyaya.ultracraft.block;

import absolutelyaya.ultracraft.registry.BlockEntityRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.math.BlockPos;

public class PedestalBlockEntity extends AbstractPedestalEntity
{
	public PedestalBlockEntity(BlockPos pos, BlockState state)
	{
		super(BlockEntityRegistry.PEDESTAL, pos, state);
		inventory = new SimpleInventory(2);
	}
	
	public void readNbt(NbtCompound nbt)
	{
		super.readNbt(nbt);
		if(nbt.contains("type", NbtElement.STRING_TYPE) && world != null)
		{
			type = nbt.getString("type");
			world.setBlockState(getPos(), getCachedState().with(PedestalBlock.TYPE, AbstractPedestalBlock.Type.valueOf(type.toUpperCase())));
		}
		else
			type = "none";
	}
	
	protected void writeNbt(NbtCompound nbt)
	{
		super.writeNbt(nbt);
		nbt.putString("type", getCachedState().get(PedestalBlock.TYPE).name);
	}
}
