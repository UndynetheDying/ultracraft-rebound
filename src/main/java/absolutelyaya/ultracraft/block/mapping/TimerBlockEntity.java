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

public class TimerBlockEntity extends AbstractTriggerBlockEntity
{
	static List<String> attributes = new ArrayList<>();
	boolean start;
	
	public TimerBlockEntity(BlockPos pos, BlockState state)
	{
		super(BlockEntityRegistry.MAP_TIMER, pos, state);
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
		return Text.of("Time-" + id + "-> " + (start ? "start" : "stop"));
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
	public String getAttribute(String attribute)
	{
		if(attribute.equals("start"))
			return String.valueOf(start);
		return null;
	}
	
	@Override
	public void setAttribute(String s, String value)
	{
		if(s.equals("start"))
			start = Boolean.parseBoolean(value);
		super.setAttribute(s, value);
	}
	
	@Override
	Class<? extends LivingEntity> getTargetClass()
	{
		return PlayerEntity.class;
	}
	
	@Override
	void tick()
	{
		super.tick();
		containedEntities.forEach(e -> {
			if(e instanceof PlayerEntity player)
			{
				IWingedPlayerComponent winged = UltraComponents.WINGED.get(player);
				if(!winged.isTimerRunning() && start)
					winged.startTimer();
				else if(winged.isTimerRunning() && !start)
					winged.stopTimer(false);
			}
		});
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
