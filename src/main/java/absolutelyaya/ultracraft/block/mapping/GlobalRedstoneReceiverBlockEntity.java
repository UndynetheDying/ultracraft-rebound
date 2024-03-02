package absolutelyaya.ultracraft.block.mapping;

import absolutelyaya.ultracraft.components.UltraComponents;
import absolutelyaya.ultracraft.registry.BlockEntityRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

public class GlobalRedstoneReceiverBlockEntity extends AbstractMappingBlockEntity implements FlagBindable
{
	static List<String> attributes = new ArrayList<>();
	String flag;
	boolean active, wasActive;
	int activeValue = 1, inactiveValue;
	
	public GlobalRedstoneReceiverBlockEntity(BlockPos pos, BlockState state)
	{
		super(BlockEntityRegistry.MAP_GLOBAL_RECEIVER, pos, state);
		id = "gReceiver";
	}
	
	@Override
	public String getFocusKey()
	{
		return "trigger";
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
	public float getAreaLabelSize()
	{
		return 0f;
	}
	
	@Override
	public Text getAreaLabel()
	{
		return Text.of("gRi-" + id + "->" + flag);
	}
	
	@Override
	void tick()
	{
		if(flag != null && active != wasActive)
			UltraComponents.DIMENSION_DATA.get(world).setFlag(flag, active ? activeValue : inactiveValue);
		wasActive = active;
	}
	
	@Override
	public String getTexture()
	{
		return "global_redstone_receiver";
	}
	
	@Override
	public List<String> getAttributes()
	{
		return attributes;
	}
	
	@Override
	public String getAttribute(String attribute)
	{
		if(attribute.equals("activeValue"))
			return String.valueOf(activeValue);
		else if(attribute.equals("inactiveValue"))
			return String.valueOf(inactiveValue);
		return null;
	}
	
	@Override
	public void setAttribute(String s, String value)
	{
		if(s.equals("activeValue"))
			this.activeValue = Integer.parseInt(value);
		else if(s.equals("inactiveValue"))
			this.inactiveValue = Integer.parseInt(value);
		super.setAttribute(s, value);
	}
	
	@Override
	public void reset()
	{
	
	}
	
	@Override
	public void bindFlag(String flag)
	{
		this.flag = flag;
	}
	
	@Override
	public String getFlag()
	{
		return flag;
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
		nbt.putInt("activeValue", activeValue);
		nbt.putInt("inactiveValue", inactiveValue);
	}
	
	@Override
	public void readNbt(NbtCompound nbt)
	{
		super.readNbt(nbt);
		if(nbt.contains("flag", NbtElement.STRING_TYPE))
			flag = nbt.getString("flag");
		activeValue = nbt.getInt("activeValue");
		inactiveValue = nbt.getInt("inactiveValue");
	}
	
	static {
		attributes.add("activeValue");
		attributes.add("inactiveValue");
	}
}
