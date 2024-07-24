package absolutelyaya.ultracraft.block.mapping;

import absolutelyaya.ultracraft.components.UltraComponents;
import absolutelyaya.ultracraft.components.player.ILevelStatsComponent;
import absolutelyaya.ultracraft.registry.BlockEntityRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

public class TimerListenerBlockEntity extends AbstractListenerBlockEntity
{
	static List<String> attributes = new ArrayList<>();
	boolean start;
	
	public TimerListenerBlockEntity(BlockPos pos, BlockState state)
	{
		super(BlockEntityRegistry.MAP_TIMER_LISTENER, pos, state);
		id = "timer";
	}
	
	@Override
	public Vector4f getColor()
	{
		return new Vector4f(0.957f, 0.494f, 0.106f, 1f);
	}
	
	@Override
	public String getTexture()
	{
		return "timer";
	}
	
	@Override
	public Text getAreaLabel()
	{
		return Text.of("Time-" + flag + "-> " + (start ? "start" : "stop"));
	}
	
	@Override
	public float getAreaLabelSize()
	{
		return 0f;
	}
	
	@Override
	public List<String> getAttributes()
	{
		return attributes;
	}
	
	@Override
	public void onActivateFlag()
	{
		super.onActivateFlag();
		if(!(world.getBlockEntity(getParent()) instanceof RoomBlockEntity room))
			return;
		room.getContainedPlayers().forEach(e -> {
			ILevelStatsComponent levelStats = UltraComponents.LEVEL_STATS.get(e);
			if(!levelStats.isTimerRunning() && start)
				levelStats.startTimer();
			else if(levelStats.isTimerRunning() && !start)
				levelStats.stopTimer(false);
		});
	}
	
	@Override
	public String getAttribute(String attribute)
	{
		if(attribute.equals("start"))
			return String.valueOf(start);
		return "";
	}
	
	@Override
	protected void writeNbt(NbtCompound nbt)
	{
		super.writeNbt(nbt);
		nbt.putBoolean("start", start);
	}
	
	@Override
	public void readNbt(NbtCompound nbt)
	{
		super.readNbt(nbt);
		if(nbt.contains("start", NbtElement.BYTE_TYPE))
			start = nbt.getBoolean("start");
	}
	
	static {
		attributes.add("start");
	}
}
