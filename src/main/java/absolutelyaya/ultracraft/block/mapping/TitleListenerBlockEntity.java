package absolutelyaya.ultracraft.block.mapping;

import absolutelyaya.ultracraft.components.UltraComponents;
import absolutelyaya.ultracraft.components.player.IWingedPlayerComponent;
import absolutelyaya.ultracraft.registry.BlockEntityRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

public class TitleListenerBlockEntity extends AbstractListenerBlockEntity
{
	static List<String> attributes = new ArrayList<>();
	String text = "title.placeholder";
	boolean large;
	
	public TitleListenerBlockEntity(BlockPos pos, BlockState state)
	{
		super(BlockEntityRegistry.MAP_TITLE_LISTENER, pos, state);
		id = "title";
	}
	
	@Override
	public Text getAreaLabel()
	{
		return Text.of("Tl-" + flag + "->" + id);
	}
	
	@Override
	public float getAreaLabelSize()
	{
		return 0f;
	}
	
	@Override
	protected void onStateChanged(boolean newState)
	{
		if(!newState)
		{
			super.onStateChanged(false);
			return;
		}
		if(world.getBlockEntity(getParent()) instanceof RoomBlockEntity room)
		{
			for (PlayerEntity player : room.getContainedPlayers())
			{
				IWingedPlayerComponent winged = UltraComponents.WINGED.get(player);
				if(large)
					winged.sendBigTitle(Text.translatable(text));
				else
					winged.sendBoxTitle(Text.translatable(text));
			}
		}
		super.onStateChanged(true);
	}
	
	@Override
	public Vector4f getColor()
	{
		return new Vector4f(0.478f, 0.267f, 0.29f, 1f);
	}
	
	@Override
	public String getTexture()
	{
		return "title_listener";
	}
	
	@Override
	public List<String> getAttributes()
	{
		return attributes;
	}
	
	@Override
	public String getAttribute(String attribute)
	{
		return switch (attribute)
		{
			case "large" -> String.valueOf(large);
			case "text" -> text;
			case "delay" -> String.valueOf(activationDelay);
			default -> null;
		};
	}
	
	@Override
	public void setAttribute(String s, String value) throws AttributeParseException, NumberFormatException
	{
		switch (s)
		{
			case "large" -> large = Boolean.parseBoolean(value);
			case "text" -> text = value;
			case "delay" -> activationDelay = Integer.parseInt(value);
		}
		super.setAttribute(s, value);
	}
	
	@Override
	protected void writeNbt(NbtCompound nbt)
	{
		super.writeNbt(nbt);
		if(text != null)
			nbt.putString("text", text);
		nbt.putBoolean("large", large);
	}
	
	@Override
	public void readNbt(NbtCompound nbt)
	{
		super.readNbt(nbt);
		if(nbt.contains("text", NbtElement.STRING_TYPE))
			text = nbt.getString("text");
		if(nbt.contains("large", NbtElement.BYTE_TYPE))
			large = nbt.getBoolean("large");
	}
	
	static {
		attributes.add("large");
		attributes.add("text");
		attributes.add("delay");
	}
}
