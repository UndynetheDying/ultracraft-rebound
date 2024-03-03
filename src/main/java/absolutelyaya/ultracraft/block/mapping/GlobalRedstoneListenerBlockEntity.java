package absolutelyaya.ultracraft.block.mapping;

import absolutelyaya.ultracraft.registry.BlockEntityRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

public class GlobalRedstoneListenerBlockEntity extends AbstractGlobalListenerBlockEntity
{
	static List<String> attributes = new ArrayList<>();
	String flag;
	
	public GlobalRedstoneListenerBlockEntity(BlockPos pos, BlockState state)
	{
		super(BlockEntityRegistry.MAP_GLOBAL_REDSTONE, pos, state);
		id = "gRedstone";
	}
	
	@Override
	public Text getAreaLabel()
	{
		return Text.of("gRo-" + flag + "->" + id);
	}
	
	@Override
	public void onActivateFlag()
	{
		world.updateNeighbors(getPos(), world.getBlockState(getPos()).getBlock());
	}
	
	@Override
	public void onDeactivateFlag()
	{
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
	public Vector4f getColor()
	{
		return new Vector4f(0.6f, 0f, 0f, 0.75f).lerp(new Vector4f(0.5f, 1f, 0.5f, 1f), isActive() ? 1f : 0f);
	}
	
	static {
		attributes.add("activationValue");
	}
}
