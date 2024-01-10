package absolutelyaya.ultracraft.block.mapping;

import absolutelyaya.ultracraft.registry.BlockEntityRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.joml.Vector4f;

public class DoorListenerBlockEntity extends AbstractListenerBlockEntity
{
	Identifier filler = new Identifier("spruce_planks");
	
	public DoorListenerBlockEntity(BlockPos pos, BlockState state)
	{
		super(BlockEntityRegistry.MAP_DOOR, pos, state);
		id = "door";
	}
	
	@Override
	public Text getAreaLabel()
	{
		return Text.of("D-" + flag + "->" + id);
	}
	
	@Override
	public Vector4f getColor()
	{
		return new Vector4f(0.15f, 0.8f, 0.87f, 0.75f).lerp(new Vector4f(0.5f, 1f, 0.5f, 1f), pulseRenderTime / 10f);
	}
	
	@Override
	public String getTexture()
	{
		return "door_listener";
	}
	
	@Override
	public boolean alwaysShowArea()
	{
		return true;
	}
	
	@Override
	public float getAreaLabelSize()
	{
		return 2f;
	}
	
	@Override
	public boolean isAreaModifiable()
	{
		return true;
	}
	
	@Override
	public Vector4f getAreaColor()
	{
		return new Vector4f(0.22f, 0.47f, 0.65f, 0.5f);
	}
	
	@Override
	protected void onStateChanged(boolean newState)
	{
		Block fillerState = Registries.BLOCK.get(filler);
		forEachBlockInArea(pos -> {
			if(newState && world.isAir(pos))
				world.setBlockState(pos, fillerState.getDefaultState());
			else if(!newState && world.getBlockState(pos).isOf(fillerState))
				world.setBlockState(pos, Blocks.AIR.getDefaultState());
		});
		super.onStateChanged(newState);
		//TODO: add option to save the areas blocks and restore them when opening the door instead of just filling air
	}
}
