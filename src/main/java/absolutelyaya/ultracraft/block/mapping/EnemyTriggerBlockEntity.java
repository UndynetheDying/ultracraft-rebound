package absolutelyaya.ultracraft.block.mapping;

import absolutelyaya.ultracraft.block.CerberusBlock;
import absolutelyaya.ultracraft.registry.BlockEntityRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.text.Text;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class EnemyTriggerBlockEntity extends AbstractTriggerBlockEntity
{
	static List<String> attributes = new ArrayList<>();
	
	public EnemyTriggerBlockEntity(BlockPos pos, BlockState state)
	{
		super(BlockEntityRegistry.MAP_ENEMY_TRIGGER, pos, state);
		id = "enemyTrigger";
	}
	
	@Override
	public Vector4f getColor()
	{
		return new Vector4f(0.5f, 0f, 0.25f, 1f);
	}
	
	@Override
	Class<? extends LivingEntity> getTargetClass()
	{
		return HostileEntity.class;
	}
	
	@Override
	public Text getAreaLabel()
	{
		return Text.of("E-" + id + " -> " + flag);
	}
	
	@Override
	public String getTexture()
	{
		return "enemy_trigger";
	}
	
	@Override
	void tick()
	{
		boolean condition = (containedEntities = world.getEntitiesByType(TypeFilter.instanceOf(getTargetClass()), getAreaBox(),
				this::isValidTarget)).size() > targetThreshold;
		AtomicBoolean cerbs = new AtomicBoolean();
		forEachBlockInArea(pos -> {
			BlockState state = world.getBlockState(pos);
			if(state.getBlock() instanceof CerberusBlock && state.get(CerberusBlock.SPAWNING))
				cerbs.set(true);
		});
		condition = condition || cerbs.get();
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
	public List<String> getAttributes()
	{
		return attributes;
	}
	
	@Override
	public void setAttribute(String s, String value)
	{
		switch (s)
		{
			case "isSelfReset" -> selfResetting = Boolean.parseBoolean(value);
			case "activationDelay" -> activateDelay = Math.max(Integer.parseInt(value), 1);
			case "targetThreshold" -> targetThreshold = Math.max(Integer.parseInt(value), 0);
			case "invert" -> inverted = Boolean.parseBoolean(value);
		}
		super.setAttribute(s, value);
	}
	
	@Override
	public String getAttribute(String attribute)
	{
		return switch (attribute)
		{
			case "isSelfReset" -> String.valueOf(selfResetting);
			case "activationDelay" -> String.valueOf(activateDelay);
			case "targetThreshold" -> String.valueOf(targetThreshold);
			case "invert" -> String.valueOf(inverted);
			default -> null;
		};
	}
	
	static {
		attributes.add("isSelfReset");
		attributes.add("activationDelay");
		attributes.add("targetThreshold");
		attributes.add("invert");
	}
}
