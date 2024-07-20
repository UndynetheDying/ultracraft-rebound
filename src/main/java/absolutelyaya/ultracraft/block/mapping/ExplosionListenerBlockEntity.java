package absolutelyaya.ultracraft.block.mapping;

import absolutelyaya.ultracraft.ExplosionHandler;
import absolutelyaya.ultracraft.damage.DamageSources;
import absolutelyaya.ultracraft.registry.BlockEntityRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

public class ExplosionListenerBlockEntity extends AbstractListenerBlockEntity
{
	static List<String> attributes = new ArrayList<>();
	float explosionDamage = 0.01f, explosionRadius = 3f;
	boolean justKnockback;
	
	public ExplosionListenerBlockEntity(BlockPos pos, BlockState state)
	{
		super(BlockEntityRegistry.MAP_EXPLOSION, pos, state);
		id = "explosion";
	}
	
	@Override
	public Vector4f getColor()
	{
		return new Vector4f(0.95f, 0.7f, 0.1f, 0.75f);
	}
	
	@Override
	public String getTexture()
	{
		return "explosion_listener";
	}
	
	@Override
	public Text getAreaLabel()
	{
		return Text.of("X-" + flag + "->" + id);
	}
	
	@Override
	public float getAreaLabelSize()
	{
		 return 0f;
	}
	
	@Override
	protected void onStateChanged(boolean newState)
	{
		if(newState && !world.isClient)
		{
			if(justKnockback)
			{
				getWorld().getOtherEntities(null, new Box(getPos()).expand(explosionRadius), e -> true).forEach(e -> {
					float dist = (float)getPos().toCenterPos().distanceTo(e.getPos());
					e.addVelocity(e.getPos().subtract(getPos().toCenterPos()).normalize().multiply((1f - (dist / explosionRadius)) * explosionDamage));
				});
			}
			else
				ExplosionHandler.explosion(null, world, getPos().toCenterPos(), DamageSources.get(world, DamageSources.EXPLOSION),
						explosionDamage, 0f, explosionRadius, false);
		}
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
			case "damage" -> explosionDamage = Float.parseFloat(value);
			case "radius" -> explosionRadius = Float.parseFloat(value);
			case "justKnockback" -> justKnockback = Boolean.parseBoolean(value);
		}
		super.setAttribute(s, value);
	}
	
	@Override
	public String getAttribute(String attribute)
	{
		return switch (attribute)
		{
			case "delay" -> String.valueOf(activationDelay);
			case "damage" -> String.valueOf(explosionDamage);
			case "radius" -> String.valueOf(explosionRadius);
			case "justKnockback" -> String.valueOf(justKnockback);
			default -> null;
		};
	}
	
	@Override
	public void readNbt(NbtCompound nbt)
	{
		super.readNbt(nbt);
		if(nbt.contains("damage", NbtElement.FLOAT_TYPE))
			explosionDamage = nbt.getFloat("damage");
		if(nbt.contains("radius", NbtElement.FLOAT_TYPE))
			explosionRadius = nbt.getFloat("radius");
		if(nbt.contains("justKnockback", NbtElement.BYTE_TYPE))
			justKnockback = nbt.getBoolean("justKnockback");
	}
	
	@Override
	protected void writeNbt(NbtCompound nbt)
	{
		super.writeNbt(nbt);
		nbt.putFloat("damage", explosionDamage);
		nbt.putFloat("radius", explosionRadius);
		nbt.putBoolean("justKnockback", justKnockback);
	}
	
	static {
		attributes.add("delay");
		attributes.add("damage");
		attributes.add("radius");
		attributes.add("justKnockback");
	}
}
