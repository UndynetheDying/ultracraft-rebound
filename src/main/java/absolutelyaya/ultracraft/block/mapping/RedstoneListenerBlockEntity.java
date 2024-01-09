package absolutelyaya.ultracraft.block.mapping;

import absolutelyaya.ultracraft.registry.BlockEntityRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.joml.Vector4f;

public class RedstoneListenerBlockEntity extends AbstractListenerBlockEntity
{
	boolean active;
	int maxPulseDuration = 5, pulseDuration;
	
	public RedstoneListenerBlockEntity(BlockPos pos, BlockState state)
	{
		super(BlockEntityRegistry.MAP_REDSTONE, pos, state);
		id = "redstone";
	}
	
	@Override
	public Text getAreaLabel()
	{
		return Text.of("R-" + flag + "->" + id);
	}
	
	@Override
	public float getAreaLabelSize()
	{
		return 0f;
	}
	
	@Override
	public String getFocusKey()
	{
		return "listener";
	}
	
	@Override
	public Vector4f getColor()
	{
		return new Vector4f(0.6f, 0f, 0f, 0.75f).lerp(new Vector4f(0.5f, 1f, 0.5f, 1f), pulseRenderTime / 10f);
	}
	
	@Override
	public void onActivateFlag()
	{
		pulseDuration = maxPulseDuration;
		active = true;
		super.onActivateFlag();
	}
	
	@Override
	public void onDeactivateFlag()
	{
		active = false;
		super.onDeactivateFlag();
	}
	
	public boolean isActive()
	{
		return active && (pulseDuration > 0 || pulseDuration == -1);
	}
	
	@Override
	void tick()
	{
		if(pulseDuration > 0 && !world.isClient)
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
}
