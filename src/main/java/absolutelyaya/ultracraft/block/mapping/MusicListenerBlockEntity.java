package absolutelyaya.ultracraft.block.mapping;

import absolutelyaya.ultracraft.components.UltraComponents;
import absolutelyaya.ultracraft.components.player.ILevelStatsComponent;
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

public class MusicListenerBlockEntity extends AbstractListenerBlockEntity
{
	static List<String> attributes = new ArrayList<>();
	String trackKey = "";
	boolean stopOnDisable;
	
	public MusicListenerBlockEntity(BlockPos pos, BlockState state)
	{
		super(BlockEntityRegistry.MAP_MUSIC_LISTENER, pos, state);
		id = "musicListener";
	}
	
	@Override
	public Vector4f getColor()
	{
		return new Vector4f(0.37f, 0.8f, 0.89f, 1f);
	}
	
	@Override
	public String getTexture()
	{
		return "music_listener";
	}
	
	@Override
	public Text getAreaLabel()
	{
		return Text.of("Ml-" + flag + "->play:" + trackKey);
	}
	
	@Override
	public float getAreaLabelSize()
	{
		 return 0f;
	}
	
	@Override
	void tick()
	{
		super.tick();
		if(!isActive())
			return;
		setAllRoomPlayersSoundtrackKey(trackKey);
	}
	
	@Override
	protected void onStateChanged(boolean newState)
	{
		super.onStateChanged(newState);
		if(newState || !stopOnDisable)
			return;
		setAllRoomPlayersSoundtrackKey("");
	}
	
	void setAllRoomPlayersSoundtrackKey(String key)
	{
		if(world.getBlockEntity(getParent()) instanceof RoomBlockEntity room)
		{
			for (PlayerEntity player : room.getContainedPlayers())
			{
				ILevelStatsComponent stats = UltraComponents.LEVEL_STATS.get(player);
				stats.setCurLevelSoundTrackKey(key);
			}
		}
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
			case "delay" -> activationDelay = Integer.parseInt(value);
			case "trackKey" -> trackKey = value;
			case "stopOnDisable" -> stopOnDisable = Boolean.parseBoolean(value);
		}
		super.setAttribute(s, value);
	}
	
	@Override
	public String getAttribute(String attribute)
	{
		return switch (attribute)
		{
			case "delay" -> String.valueOf(activationDelay);
			case "trackKey" -> trackKey;
			case "stopOnDisable" -> String.valueOf(stopOnDisable);
			default -> null;
		};
	}
	
	@Override
	public void readNbt(NbtCompound nbt)
	{
		super.readNbt(nbt);
		if(nbt.contains("trackKey", NbtElement.STRING_TYPE))
			trackKey = nbt.getString("trackKey");
		if(nbt.contains("stopOnDisable", NbtElement.BYTE_TYPE))
			stopOnDisable = nbt.getBoolean("stopOnDisable");
	}
	
	@Override
	protected void writeNbt(NbtCompound nbt)
	{
		super.writeNbt(nbt);
		nbt.putString("trackKey", trackKey);
		nbt.putBoolean("stopOnDisable", stopOnDisable);
	}
	
	static {
		attributes.add("delay");
		attributes.add("trackKey");
		attributes.add("stopOnDisable");
	}
}
