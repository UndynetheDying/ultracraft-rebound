package absolutelyaya.ultracraft.block.mapping;

import absolutelyaya.ultracraft.components.UltraComponents;
import absolutelyaya.ultracraft.components.player.ILevelStatsComponent;
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

public class MusicTriggerBlockEntity extends AbstractTriggerBlockEntity
{
	List<? extends LivingEntity> lastContained = new ArrayList<>();
	static List<String> attributes = new ArrayList<>();
	String trackKey = "";
	
	public MusicTriggerBlockEntity(BlockPos pos, BlockState state)
	{
		super(BlockEntityRegistry.MAP_MUSIC, pos, state);
		id = "musicTrigger";
	}
	
	@Override
	Class<? extends LivingEntity> getTargetClass()
	{
		return PlayerEntity.class;
	}
	
	@Override
	public Vector4f getColor()
	{
		return new Vector4f(0.37f, 0.8f, 0.89f, 1f);
	}
	
	@Override
	public String getTexture()
	{
		return "music_trigger";
	}
	
	@Override
	public Text getAreaLabel()
	{
		if(flag != null)
			return Text.of("Mt-" + flag + "->play:" + trackKey);
		return Text.of("Mt-play:" + trackKey);
	}
	
	@Override
	void tick()
	{
		super.tick();
		if(world.getBlockEntity(getParent()) instanceof RoomBlockEntity room && flag != null && !room.checkFlag(flag))
			return;
		containedEntities.forEach(i -> {
			if(i instanceof PlayerEntity player && !lastContained.contains(player))
			{
				ILevelStatsComponent stats = UltraComponents.LEVEL_STATS.get(player);
				stats.setCurLevelSoundTrackKey(trackKey);
			}
		});
		lastContained = containedEntities;
	}
	
	@Override
	public List<String> getAttributes()
	{
		return attributes;
	}
	
	@Override
	public String getAttribute(String attribute)
	{
		if(attribute.equals("trackKey"))
			return trackKey;
		return null;
	}
	
	@Override
	public void setAttribute(String s, String value) throws AttributeParseException, NumberFormatException
	{
		super.setAttribute(s, value);
		if(s.equals("trackKey"))
			trackKey = value;
	}
	
	@Override
	public void readNbt(NbtCompound nbt)
	{
		super.readNbt(nbt);
		if(nbt.contains("trackKey", NbtElement.STRING_TYPE))
			trackKey = nbt.getString("trackKey");
	}
	
	@Override
	protected void writeNbt(NbtCompound nbt)
	{
		super.writeNbt(nbt);
		nbt.putString("trackKey", trackKey);
	}
	
	static {
		attributes.add("trackKey");
	}
}
