package absolutelyaya.ultracraft.block.mapping;

import absolutelyaya.ultracraft.registry.BlockEntityRegistry;
import absolutelyaya.ultracraft.registry.BlockRegistry;
import net.bettercombat.utils.MathHelper;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

public class LightBlockEntity extends AbstractListenerBlockEntity
{
	static final List<String> attributes = new ArrayList<>();
	int level;
	
	public LightBlockEntity(BlockPos pos, BlockState state)
	{
		super(BlockEntityRegistry.MAP_LIGHT, pos, state);
		id = "light";
	}
	
	@Override
	public Vector4f getColor()
	{
		return new Vector4f(1f, 1f, isActive() ? 1f : 0f, 1f);
	}
	
	@Override
	public String getTexture()
	{
		return "light_listener";
	}
	
	@Override
	public Text getAreaLabel()
	{
		return Text.of(flag + " -> " + id);
	}
	
	@Override
	public float getAreaLabelSize()
	{
		return 0f;
	}
	
	@Override
	protected void onStateChanged(boolean newState)
	{
		if(world.getBlockState(pos).isOf(BlockRegistry.MAP_LIGHT))
			world.setBlockState(pos, world.getBlockState(pos).with(LightBlock.ACTIVE, newState).with(LightBlock.LEVEL_15, level));
		super.onStateChanged(newState);
	}
	
	@Override
	public List<String> getAttributes()
	{
		return attributes;
	}
	
	@Override
	public String getAttribute(String attribute)
	{
		if(attribute.equals("level"))
			return String.valueOf(level);
		else if(attribute.equals("delay"))
			return String.valueOf(activationDelay);
		return null;
	}
	
	@Override
	public void setAttribute(String s, String value)
	{
		if(s.equals("level"))
		{
			level = (int)MathHelper.clamp(Integer.parseInt(value), 0, 15);
			world.setBlockState(pos, world.getBlockState(pos).with(LightBlock.LEVEL_15, level));
		}
		else if(s.equals("delay"))
			activationDelay = Integer.parseInt(value);
		super.setAttribute(s, value);
	}
	
	@Override
	public void readNbt(NbtCompound nbt)
	{
		super.readNbt(nbt);
		if(nbt.contains("level", NbtElement.INT_TYPE))
		{
			level = nbt.getInt("level");
			if(world.getBlockState(pos).isOf(BlockRegistry.MAP_LIGHT))
				world.setBlockState(pos, world.getBlockState(pos).with(LightBlock.LEVEL_15, level));
		}
	}
	
	@Override
	protected void writeNbt(NbtCompound nbt)
	{
		super.writeNbt(nbt);
		nbt.putInt("level", level);
	}
	
	public int getLevel()
	{
		return level;
	}
	
	static {
		attributes.add("level");
		attributes.add("delay");
	}
}
