package absolutelyaya.ultracraft.block.mapping;

import absolutelyaya.ultracraft.registry.BlockEntityRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.text.Text;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.joml.Vector4f;

public class TriggerBlockEntity extends AbstractMappingBlockEntity implements FlagBindable
{
	int active, activateDelay = 10;
	String flag;
	boolean selfResetting = true;
	
	public TriggerBlockEntity(BlockPos pos, BlockState state)
	{
		super(BlockEntityRegistry.MAP_TRIGGER, pos, state);
		id = "trigger";
	}
	
	public static <T extends BlockEntity> void tick(World world, BlockPos blockPos, BlockState state, T instance)
	{
		if(instance instanceof TriggerBlockEntity trigger)
		{
			boolean b = world.getEntitiesByType(TypeFilter.instanceOf(PlayerEntity.class), trigger.getAreaBox(), i -> true).size() > 0;
			boolean wasActive = trigger.isActive();
			if(!b && trigger.active > 0 && trigger.selfResetting)
				trigger.active--;
			if(b && trigger.active < trigger.activateDelay * 2)
				trigger.active++;
			
			if(trigger.flag != null && trigger.getParent() != null && world.getBlockEntity(trigger.getParent()) instanceof RoomBlockEntity room)
			{
				if(trigger.isActive() && !wasActive)
					room.setFlag(trigger.flag, true);
				if(!trigger.isActive() && wasActive)
					room.setFlag(trigger.flag, false);
			}
		}
	}
	
	@Override
	public String getFocusKey()
	{
		return "trigger";
	}
	
	@Override
	public Vector4f getColor()
	{
		return new Vector4f(0.25f, 0f, 0.5f, 1f);
	}
	
	public Vector4f getAreaColor()
	{
		Vector4f col = new Vector4f(1f, 0f, 0f, 0.5f).lerp(new Vector4f(0f, 1f, 0f, 0.5f), Math.min((float)active / activateDelay, 1f));
		return col.lerp(new Vector4f(1f, 1f, 1f, 1f), Math.max(1f - Math.abs(active - activateDelay) / 2f, 0f));
	}
	
	public float getAreaLabelSize()
	{
		return 2f;
	}
	
	@Override
	public boolean showCamLine()
	{
		return false;
	}
	
	@Override
	public boolean alwaysShowArea()
	{
		return true;
	}
	
	@Override
	public Text getAreaLabel()
	{
		return Text.of("T-" + id + " -> " + flag);
	}
	
	public boolean isActive()
	{
		return active > activateDelay;
	}
	
	@Override
	public void bindFlag(String flag)
	{
		this.flag = flag;
		markDirty();
		world.updateListeners(pos, getCachedState(), getCachedState(), 0);
	}
	
	@Override
	public String getFlag()
	{
		return flag;
	}
	
	@Override
	public void readNbt(NbtCompound nbt)
	{
		super.readNbt(nbt);
		if(nbt.contains("flag", NbtElement.STRING_TYPE))
			flag = nbt.getString("flag");
		if(nbt.contains("selfReset", NbtElement.BYTE_TYPE))
			selfResetting = nbt.getBoolean("selfReset");
	}
	
	@Override
	protected void writeNbt(NbtCompound nbt)
	{
		super.writeNbt(nbt);
		if(flag != null)
			nbt.putString("flag", flag);
		nbt.putBoolean("selfReset", selfResetting);
	}
}
