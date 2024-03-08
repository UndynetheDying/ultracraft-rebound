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

public class RedstoneListenerBlockEntity extends AbstractListenerBlockEntity
{
	int maxPulseDuration, pulseDuration;
	static List<String> attributes = new ArrayList<>();
	
	public RedstoneListenerBlockEntity(BlockPos pos, BlockState state)
	{
		super(BlockEntityRegistry.MAP_REDSTONE, pos, state);
		id = "redstone";
	}
	
	@Override
	public Text getAreaLabel()
	{
		return Text.of("Ro-" + flag + "->" + id);
	}
	
	@Override
	public float getAreaLabelSize()
	{
		return 0f;
	}
	
	@Override
	public Vector4f getColor()
	{
		return new Vector4f(0.6f, 0f, 0f, 0.75f).lerp(new Vector4f(0.5f, 1f, 0.5f, 1f), pulseRenderTime / 10f);
	}
	
	@Override
	protected void onStateChanged(boolean newState)
	{
		if(newState)
			pulseDuration = maxPulseDuration;
		super.onStateChanged(newState);
	}
	
	@Override
	public boolean isActive()
	{
		return super.isActive() && (pulseDuration > 0 || maxPulseDuration <= 0);
	}
	
	@Override
	void tick()
	{
		if((pulseDuration > 0 || maxPulseDuration <= 0) && !world.isClient)
		{
			pulseDuration--;
			if(pulseDuration == 0)
				updateNeighbors();
		}
		super.tick();
	}
	
	@Override
	public String getTexture()
	{
		return "redstone_listener";
	}
	
	@Override
	public List<String> getAttributes()
	{
		return attributes;
	}
	
	@Override
	public void setAttribute(String s, String value)
	{
		if(s.equals("maxPulseDuration"))
			maxPulseDuration = Integer.parseInt(value);
		else if(s.equals("delay"))
			activationDelay = Integer.parseInt(value);
		super.setAttribute(s, value);
	}
	
	@Override
	public String getAttribute(String attribute)
	{
		if(attribute.equals("maxPulseDuration"))
			return String.valueOf(maxPulseDuration);
		else if(attribute.equals("delay"))
			return String.valueOf(activationDelay);
		return null;
	}
	
	@Override
	public void readNbt(NbtCompound nbt)
	{
		super.readNbt(nbt);
		if(nbt.contains("pulseDuration", NbtElement.INT_TYPE))
			maxPulseDuration = nbt.getInt("pulseDuration");
	}
	
	@Override
	protected void writeNbt(NbtCompound nbt)
	{
		super.writeNbt(nbt);
		nbt.putInt("pulseDuration", maxPulseDuration);
	}
	
	static {
		attributes.add("maxPulseDuration");
		attributes.add("delay");
	}
}
