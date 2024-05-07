package absolutelyaya.ultracraft.block.mapping;

import absolutelyaya.ultracraft.registry.BlockEntityRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

public class SoundListenerBlockEntity extends AbstractListenerBlockEntity
{
	static List<String> attributes = new ArrayList<>();
	Identifier sound = new Identifier("entity.cat.ambient");
	float pitch = 1f, volume = 1f;
	boolean playOnDeactivate;
	
	public SoundListenerBlockEntity(BlockPos pos, BlockState state)
	{
		super(BlockEntityRegistry.MAP_SOUND, pos, state);
		id = "sound";
	}
	
	@Override
	public Vector4f getColor()
	{
		return new Vector4f(0.85f, 0.49f, 0.71f, 0.75f);
	}
	
	@Override
	public String getTexture()
	{
		return "sound_listener";
	}
	
	@Override
	public Text getAreaLabel()
	{
		return Text.of("P-" + flag + "->" + id);
	}
	
	@Override
	public float getAreaLabelSize()
	{
		return 0f;
	}
	
	@Override
	protected void onStateChanged(boolean newState)
	{
		if(newState)
			world.playSound(null, pos, Registries.SOUND_EVENT.get(sound), SoundCategory.BLOCKS, volume, pitch);
		else if(playOnDeactivate)
			world.playSound(null, pos, Registries.SOUND_EVENT.get(sound), SoundCategory.BLOCKS, volume, pitch);
		super.onStateChanged(newState);
	}
	
	@Override
	public List<String> getAttributes()
	{
		return attributes;
	}
	
	@Override
	public void setAttribute(String s, String value) throws AttributeParseException, NumberFormatException
	{
		switch (s)
		{
			case "delay" -> activationDelay = Integer.parseInt(value);
			case "sound" -> sound = parseIdentifier(value);
			case "volume" -> volume = Float.parseFloat(value);
			case "pitch" -> pitch = Float.parseFloat(value);
			case "playOnDeactivate" -> playOnDeactivate = Boolean.parseBoolean(value);
		}
		super.setAttribute(s, value);
	}
	
	@Override
	public String getAttribute(String attribute)
	{
		return switch (attribute)
		{
			case "delay" -> String.valueOf(activationDelay);
			case "sound" -> sound.toString();
			case "volume" -> String.valueOf(volume);
			case "pitch" -> String.valueOf(pitch);
			case "playOnDeactivate" -> String.valueOf(playOnDeactivate);
			default -> null;
		};
	}
	
	@Override
	public void readNbt(NbtCompound nbt)
	{
		super.readNbt(nbt);
		if(nbt.contains("sound", NbtElement.STRING_TYPE))
			sound = Identifier.tryParse(nbt.getString("sound"));
		if(nbt.contains("volume", NbtElement.FLOAT_TYPE))
			volume = nbt.getFloat("volume");
		if(nbt.contains("pitch", NbtElement.FLOAT_TYPE))
			pitch = nbt.getFloat("pitch");
		if(nbt.contains("playOnDeactivate", NbtElement.BYTE_TYPE))
			playOnDeactivate = nbt.getBoolean("playOnDeactivate");
	}
	
	@Override
	protected void writeNbt(NbtCompound nbt)
	{
		super.writeNbt(nbt);
		nbt.putString("sound", sound.toString());
		nbt.putFloat("volume", volume);
		nbt.putFloat("pitch", pitch);
		nbt.putBoolean("playOnDeactivate", playOnDeactivate);
	}
	
	static {
		attributes.add("delay");
		attributes.add("sound");
		attributes.add("volume");
		attributes.add("pitch");
		attributes.add("playOnDeactivate");
	}
}
