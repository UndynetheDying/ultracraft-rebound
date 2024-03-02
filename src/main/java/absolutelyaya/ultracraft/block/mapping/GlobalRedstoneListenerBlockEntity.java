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

public class GlobalRedstoneListenerBlockEntity extends AbstractMappingBlockEntity implements FlagListener
{
	static List<String> attributes = new ArrayList<>();
	String flag;
	int activationValue;
	boolean active, wasActive;
	
	public GlobalRedstoneListenerBlockEntity(BlockPos pos, BlockState state)
	{
		super(BlockEntityRegistry.MAP_GLOBAL_REDSTONE, pos, state);
		id = "gRedstone";
	}
	
	@Override
	public String getFocusKey()
	{
		return "listener";
	}
	
	@Override
	public Text getAreaLabel()
	{
		return Text.of("gRo-" + flag + "->" + id);
	}
	
	@Override
	public float getAreaLabelSize()
	{
		return 0f;
	}
	
	@Override
	void tick()
	{
		wasActive = active;
		active = UltraComponents.DIMENSION_DATA.get(world).getFlag(flag) == activationValue;
		if(active != wasActive)
			world.updateNeighbors(getPos(), world.getBlockState(getPos()).getBlock());
	}
	
	@Override
	public String getTexture()
	{
		return "global_redstone_listener";
	}
	
	@Override
	public List<String> getAttributes()
	{
		return attributes;
	}
	
	@Override
	public String getAttribute(String attribute)
	{
		if(attribute.equals("activationValue"))
			return String.valueOf(activationValue);
		return null;
	}
	
	@Override
	public void setAttribute(String s, String value)
	{
		if(s.equals("activationValue"))
			activationValue = Integer.parseInt(value);
		super.setAttribute(s, value);
	}
	
	@Override
	public void reset()
	{
	
	}
	
	@Override
	public Vector4f getColor()
	{
		return new Vector4f(0.6f, 0f, 0f, 0.75f).lerp(new Vector4f(0.5f, 1f, 0.5f, 1f), isActive() ? 1f : 0f);
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
	}
	
	@Override
	public String getFlag()
	{
		return flag;
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
	public void onActivateFlag()
	{
	
	}
	
	@Override
	public void onDeactivateFlag()
	{
	
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
	
	static {
		attributes.add("activationValue");
	}
}
