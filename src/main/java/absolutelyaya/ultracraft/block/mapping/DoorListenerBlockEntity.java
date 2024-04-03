package absolutelyaya.ultracraft.block.mapping;

import absolutelyaya.ultracraft.registry.BlockEntityRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

public class DoorListenerBlockEntity extends AbstractListenerBlockEntity
{
	static List<String> attributes = new ArrayList<>();
	Identifier close = new Identifier("spruce_planks"), open = new Identifier("air"), reset = null;
	boolean skull = true;
	
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
	
	public boolean shouldRenderSkull()
	{
		return skull && isActive();
	}
	
	@Override
	public Vector4f getAreaColor()
	{
		return new Vector4f(0.22f, 0.47f, 0.65f, 0.5f);
	}
	
	@Override
	protected void onStateChanged(boolean newState)
	{
		if(world.isClient)
			return;
		Block closedState = Registries.BLOCK.get(close), openState = Registries.BLOCK.get(open);
		forEachBlockInArea(pos -> {
			BlockState lastState = world.getBlockState(pos);
			if(newState && lastState.isOf(openState))
				world.setBlockState(pos, closedState.getDefaultState());
			else if(!newState && world.getBlockState(pos).isOf(closedState))
				world.setBlockState(pos, openState.getDefaultState());
			world.updateListeners(pos, lastState, world.getBlockState(pos), 0);
		});
		super.onStateChanged(newState);
		//TODO: add option to save the areas blocks and restore them when opening the door instead of just filling air
	}
	
	@Override
	public void reset()
	{
		state = nextState = false;
		curActivationDelay = 0;
		Block resetBlock = (reset != null) ? Registries.BLOCK.get(reset) : Registries.BLOCK.get(open);
		forEachBlockInArea(pos -> {
			BlockState state = world.getBlockState(pos);
			if(!state.isOf(resetBlock) && (state.isOf(Registries.BLOCK.get(close)) || state.isOf(Registries.BLOCK.get(open))))
				world.setBlockState(pos, resetBlock.getDefaultState());
		});
		super.onStateChanged(false);
	}
	
	@Override
	public List<String> getAttributes()
	{
		return attributes;
	}
	
	@Override
	public void setAttribute(String s, String value)
	{
		switch (s)
		{
			case "closedBlock" -> close = Identifier.tryParse(value);
			case "openBlock" -> open = Identifier.tryParse(value);
			case "resetBlock" -> {
				if(value.equals("null"))
					reset = null;
				else
					reset = Identifier.tryParse(value);
			}
			case "delay" -> activationDelay = Integer.parseInt(value);
			case "skull" -> skull = Boolean.parseBoolean(value);
		}
		super.setAttribute(s, value);
	}
	
	@Override
	public String getAttribute(String attribute)
	{
		return switch (attribute)
		{
			case "closedBlock" -> String.valueOf(close);
			case "openBlock" -> String.valueOf(open);
			case "resetBlock" -> {
				if(reset == null)
					yield "defaultToOpenBlock";
				yield String.valueOf(reset);
			}
			case "delay" -> String.valueOf(activationDelay);
			case "skull" -> String.valueOf(skull);
			default -> null;
		};
	}
	
	@Override
	public void readNbt(NbtCompound nbt)
	{
		super.readNbt(nbt);
		if(nbt.contains("closedBlock", NbtElement.STRING_TYPE))
			close = Identifier.tryParse(nbt.getString("closedBlock"));
		if(nbt.contains("openBlock", NbtElement.STRING_TYPE))
			open = Identifier.tryParse(nbt.getString("openBlock"));
		if(nbt.contains("resetBlock", NbtElement.STRING_TYPE))
		{
			String v = nbt.getString("resetBlock");
			if(v.equals("null"))
				reset = null;
			else
				reset = Identifier.tryParse(v);
		}
		if(nbt.contains("skull", NbtElement.BYTE_TYPE))
			skull = nbt.getBoolean("skull");
		if(nbt.contains("active", NbtElement.BYTE_TYPE))
			state = nbt.getBoolean("active");
	}
	
	@Override
	protected void writeNbt(NbtCompound nbt)
	{
		super.writeNbt(nbt);
		nbt.putString("closedBlock", close.toString());
		nbt.putString("openBlock", open.toString());
		if(reset != null)
			nbt.putString("resetBlock", reset.toString());
		else
			nbt.putString("resetBlock", "null");
		nbt.putBoolean("skull", skull);
		nbt.putBoolean("active", state);
	}
	
	static {
		attributes.add("closedBlock");
		attributes.add("openBlock");
		attributes.add("resetBlock");
		attributes.add("delay");
		attributes.add("skull");
	}
}
