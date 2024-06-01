package absolutelyaya.ultracraft.block.mapping;

import absolutelyaya.ultracraft.components.UltraComponents;
import absolutelyaya.ultracraft.components.player.IWingedPlayerComponent;
import absolutelyaya.ultracraft.registry.BlockEntityRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

public class TitleTriggerBlockEntity extends AbstractTriggerBlockEntity
{
	static List<String> attributes = new ArrayList<>();
	List<? extends LivingEntity> lastContained = new ArrayList<>();
	String text = "title.placeholder";
	boolean large;
	
	public TitleTriggerBlockEntity(BlockPos pos, BlockState state)
	{
		super(BlockEntityRegistry.MAP_TITLE, pos, state);
		id = "title";
	}
	
	@Override
	public Vector4f getColor()
	{
		return new Vector4f(0.478f, 0.267f, 0.29f, 1f);
	}
	
	@Override
	public String getTexture()
	{
		return "title";
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
		}
		super.setAttribute(s, value);
	}
	
	@Override
	Class<? extends LivingEntity> getTargetClass()
	{
		return PlayerEntity.class;
	}
	
	@Override
	public Text getAreaLabel()
	{
		if(flag != null)
			return Text.of("Tt-" + flag + "->" + id);
		return Text.of("Tt-" + id);
	}
	
	@Override
	void tick()
	{
		super.tick();
		if(flag != null && world.getBlockEntity(getParent()) instanceof RoomBlockEntity room && !room.checkFlag(flag))
			return;
		containedEntities.forEach(e -> {
			if(e instanceof PlayerEntity player && !lastContained.contains(player))
			{
				IWingedPlayerComponent winged = UltraComponents.WINGED.get(player);
				if(large)
					winged.sendBigTitle(Text.translatable(text));
				else
					winged.sendBoxTitle(Text.translatable(text));
			}
		});
		lastContained = containedEntities;
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
	}
}
