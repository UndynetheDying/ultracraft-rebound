package absolutelyaya.ultracraft.block.mapping;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import org.joml.Vector4f;

public abstract class AbstractTriggerBlockEntity extends AbstractMappingBlockEntity implements FlagBindable
{
	int active, activateDelay = 10;
	String flag;
	boolean selfResetting = true;
	
	public AbstractTriggerBlockEntity(BlockEntityType<? extends AbstractTriggerBlockEntity> type, BlockPos pos, BlockState state)
	{
		super(type, pos, state);
		id = "trigger";
	}
	
	@Override
	public String getFocusKey()
	{
		return "trigger";
	}
	
	public Vector4f getAreaColor()
	{
		Vector4f col = new Vector4f(1f, 0f, 0f, 0.5f).lerp(new Vector4f(0f, 1f, 0f, 0.5f), Math.min((float)active / activateDelay, 1f));
		return col.lerp(new Vector4f(1f, 1f, 1f, 1f), activateDelay > 1 ? Math.max(1f - Math.abs(active - activateDelay) / 2f, 0f) : 0f);
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
	void tick()
	{
		boolean b = world.getEntitiesByType(TypeFilter.instanceOf(getTargetClass()), getAreaBox(), i -> true).size() > 0;
		boolean wasActive = isActive();
		if(!b && active > 0 && (selfResetting || active < activateDelay))
			active--;
		if(b && active < activateDelay * 2)
			active++;
		active = MathHelper.clamp(active, 0, activateDelay * 2);
		
		if(flag != null && getParent() != null && world.getBlockEntity(getParent()) instanceof RoomBlockEntity room)
		{
			if(isActive() && !wasActive)
				room.setFlag(flag, true);
			if(!isActive() && wasActive)
				room.setFlag(flag, false);
		}
	}
	
	@Override
	public void reset()
	{
		active = 0;
	}
	
	abstract Class<? extends LivingEntity> getTargetClass();
	
	@Override
	public void readNbt(NbtCompound nbt)
	{
		super.readNbt(nbt);
		if(nbt.contains("flag", NbtElement.STRING_TYPE))
			flag = nbt.getString("flag");
		if(nbt.contains("activateDelay", NbtElement.INT_TYPE))
			activateDelay = nbt.getInt("activateDelay");
		if(nbt.contains("selfReset", NbtElement.BYTE_TYPE))
			selfResetting = nbt.getBoolean("selfReset");
	}
	
	@Override
	protected void writeNbt(NbtCompound nbt)
	{
		super.writeNbt(nbt);
		if(flag != null)
			nbt.putString("flag", flag);
		nbt.putInt("activateDelay", activateDelay);
		nbt.putBoolean("selfReset", selfResetting);
	}
}
