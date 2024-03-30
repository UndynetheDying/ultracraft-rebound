package absolutelyaya.ultracraft.block.mapping;

import absolutelyaya.ultracraft.components.UltraComponents;
import absolutelyaya.ultracraft.components.player.IWingedPlayerComponent;
import absolutelyaya.ultracraft.registry.BlockEntityRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

public class GlobalTitleListenerBlockEntity extends AbstractGlobalListenerBlockEntity
{
	static List<String> attributes = new ArrayList<>();
	String text = "title.placeholder";
	boolean large;
	
	public GlobalTitleListenerBlockEntity(BlockPos pos, BlockState state)
	{
		super(BlockEntityRegistry.MAP_GLOBAL_TITLE, pos, state);
		id = "gTitle";
	}
	
	@Override
	public Vector4f getColor()
	{
		return new Vector4f(0.478f, 0.267f, 0.29f, 1f);
	}
	
	@Override
	public String getTexture()
	{
		return "global_title";
	}
	
	@Override
	public Text getAreaLabel()
	{
		return Text.of("gT-" + flag + "->" + id);
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
			default -> super.getAttribute(attribute);
		};
	}
	
	@Override
	public void setAttribute(String s, String value)
	{
		switch (s)
		{
			case "large" -> large = Boolean.parseBoolean(value);
			case "text" -> text = value;
		}
		super.setAttribute(s, value);
	}
	
	@Override
	public void onActivateFlag()
	{
		world.getPlayers().forEach(p -> {
			IWingedPlayerComponent winged = UltraComponents.WINGED.get(p);
			if(large)
				winged.sendBigTitle(Text.translatable(text));
			else
				winged.sendBoxTitle(Text.translatable(text));
		});
	}
	
	@Override
	public void onDeactivateFlag()
	{
	
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
		attributes.add("activationValue");
		attributes.add("large");
		attributes.add("text");
	}
}
