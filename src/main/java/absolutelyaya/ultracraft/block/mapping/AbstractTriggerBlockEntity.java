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

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractTriggerBlockEntity extends AbstractMappingBlockEntity implements FlagBindable
{
	int active, activateDelay = 1, targetThreshold = 0;
	String flag;
	boolean selfResetting = true, inverted;
	List<? extends LivingEntity> containedEntities = new ArrayList<>();
	boolean justReset = inverted;
	
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
		boolean condition = (containedEntities = world.getEntitiesByType(TypeFilter.instanceOf(getTargetClass()), getAreaBox(),
				i -> i.isAlive() && !i.isSpectator())).size() > targetThreshold;
		if(inverted)
			condition = !condition; //invert
		boolean wasActive = isActive();
		if(!condition && active > 0 && (selfResetting || active < activateDelay || (justReset && inverted)))
			active--;
		if(condition && active < activateDelay * 2)
			active++;
		active = MathHelper.clamp(active, 0, activateDelay * 2);
		
		if(flag != null && getParent() != null && world.getBlockEntity(getParent()) instanceof RoomBlockEntity room && isActive() != wasActive)
		{
			justReset = false;
			room.setFlag(flag, isActive());
		}
	}
	
	@Override
	public void reset()
	{
		active = inverted ? activateDelay * 2 : 0;
		justReset = true;
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
		if(nbt.contains("targetThreshold", NbtElement.INT_TYPE))
			targetThreshold = nbt.getInt("targetThreshold");
		if(nbt.contains("invert", NbtElement.BYTE_TYPE))
		{
			inverted = nbt.getBoolean("invert");
			active = inverted ? activateDelay * 2 : 0;
		}
		if(nbt.contains("justReset", NbtElement.BYTE_TYPE))
			justReset = nbt.getBoolean("justReset");
		else
			justReset = inverted;
	}
	
	@Override
	protected void writeNbt(NbtCompound nbt)
	{
		super.writeNbt(nbt);
		if(flag != null)
			nbt.putString("flag", flag);
		nbt.putInt("activateDelay", activateDelay);
		nbt.putBoolean("selfReset", selfResetting);
		nbt.putInt("targetThreshold", targetThreshold);
		nbt.putBoolean("invert", inverted);
		nbt.putBoolean("justReset", justReset);
	}
}
