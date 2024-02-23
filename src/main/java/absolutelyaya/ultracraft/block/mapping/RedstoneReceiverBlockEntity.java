package absolutelyaya.ultracraft.block.mapping;

import absolutelyaya.ultracraft.registry.BlockEntityRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

public class RedstoneReceiverBlockEntity extends AbstractMappingBlockEntity implements FlagBindable
{
	static List<String> attributes = new ArrayList<>();
	String flag;
	boolean active, wasActive;
	
	public RedstoneReceiverBlockEntity(BlockPos pos, BlockState state)
	{
		super(BlockEntityRegistry.MAP_RECEIVER, pos, state);
		id = "receiver";
	}
	
	@Override
	public String getFocusKey()
	{
		return "trigger";
	}
	
	@Override
	public boolean isAreaModifiable()
	{
		return false;
	}
	
	@Override
	public Vector4f getColor()
	{
		return new Vector4f(0.6f, 0f, 0f, 0.75f);
	}
	
	@Override
	public boolean showCamLine()
	{
		return false;
	}
	
	@Override
	void tick()
	{
		if(flag != null && getParent() != null && world.getBlockEntity(getParent()) instanceof RoomBlockEntity room && active != wasActive)
			room.setFlag(flag, active);
		wasActive = active;
	}
	
	@Override
	public String getTexture()
	{
		return "redstone_receiver";
	}
	
	@Override
	public float getAreaLabelSize()
	{
		return 0f;
	}
	
	@Override
	public Text getAreaLabel()
	{
		return Text.of("Ri-" + id + "->" + flag);
	}
	
	@Override
	public List<String> getAttributes()
	{
		return attributes;
	}
	
	@Override
	public String getAttribute(String attribute)
	{
		return null;
	}
	
	@Override
	public void reset()
	{
	
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
	protected void writeNbt(NbtCompound nbt)
	{
		super.writeNbt(nbt);
		if(flag != null)
			nbt.putString("flag", flag);
		nbt.putBoolean("active", active);
	}
	
	@Override
	public void readNbt(NbtCompound nbt)
	{
		super.readNbt(nbt);
		if(nbt.contains("flag", NbtElement.STRING_TYPE))
			flag = nbt.getString("flag");
		active = nbt.getBoolean("active");
	}
}
