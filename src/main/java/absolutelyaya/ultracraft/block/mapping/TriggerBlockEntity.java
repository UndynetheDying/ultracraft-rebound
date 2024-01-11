package absolutelyaya.ultracraft.block.mapping;

import absolutelyaya.ultracraft.registry.BlockEntityRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

public class TriggerBlockEntity extends AbstractTriggerBlockEntity
{
	static List<String> attributes = new ArrayList<>();
	
	public TriggerBlockEntity(BlockPos pos, BlockState state)
	{
		super(BlockEntityRegistry.MAP_TRIGGER, pos, state);
	}
	
	@Override
	public Vector4f getColor()
	{
		return new Vector4f(0.25f, 0f, 0.5f, 1f);
	}
	
	@Override
	public Text getAreaLabel()
	{
		return Text.of("T-" + id + " -> " + flag);
	}
	
	@Override
	Class<? extends LivingEntity> getTargetClass()
	{
		return PlayerEntity.class;
	}
	
	@Override
	public String getTexture()
	{
		return "trigger";
	}
	
	@Override
	public List<String> getAttributes()
	{
		return attributes;
	}
	
	@Override
	public void setAttribute(String s, String value)
	{
		if(s.equals("isSelfReset"))
			selfResetting = Boolean.parseBoolean(value);
		else if(s.equals("activationDelay"))
			activateDelay = Math.max(Integer.parseInt(value), 1);
		super.setAttribute(s, value);
	}
	
	static {
		attributes.add("isSelfReset");
		attributes.add("activationDelay");
	}
}
