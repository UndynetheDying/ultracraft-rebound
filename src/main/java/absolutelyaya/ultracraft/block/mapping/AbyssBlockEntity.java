package absolutelyaya.ultracraft.block.mapping;

import absolutelyaya.ultracraft.damage.DamageSources;
import absolutelyaya.ultracraft.registry.BlockEntityRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AbyssBlockEntity extends AbstractTriggerBlockEntity
{
	static List<String> attributes = new ArrayList<>();
	List<? extends LivingEntity> lastContained = new ArrayList<>();
	boolean canKill = false;
	Identifier damagetype = new Identifier("out_of_world");
	float amount = 1f;
	
	public AbyssBlockEntity(BlockPos pos, BlockState state)
	{
		super(BlockEntityRegistry.MAP_ABYSS, pos, state);
	}
	
	@Override
	Class<? extends LivingEntity> getTargetClass()
	{
		return PlayerEntity.class;
	}
	
	@Override
	public Vector4f getColor()
	{
		return new Vector4f(0.2f, 0.05f, 0.25f, 1f);
	}
	
	@Override
	public String getTexture()
	{
		return "abyss";
	}
	
	@Override
	void tick()
	{
		super.tick();
		Registry<DamageType> registry = world.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE);
		DamageType damageType = registry.get(damagetype);
		Optional<RegistryKey<DamageType>> damageTypeKey = registry.getKey(damageType);
		if(damageTypeKey.isEmpty())
			return;
		for (LivingEntity living : containedEntities)
		{
			if(living instanceof PlayerEntity player && (player.isSpectator() || player.isCreative()))
				continue;
			if(lastContained.contains(living))
				continue;
			living.damage(DamageSources.get(world, damageTypeKey.get()), canKill ? amount : Math.min(living.getHealth() - 1, amount));
			BlockPos pos = getPos();
			BlockHitResult bHit = world.raycast(new RaycastContext(pos.toCenterPos(), pos.down(16).toCenterPos(),
					RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, living));
			if(bHit.getType().equals(HitResult.Type.MISS))
				living.teleport(pos.getX(), pos.getY(), pos.getZ());
			else
			{
				Vec3d hitPos = bHit.getPos();
				living.teleport(hitPos.x, hitPos.y, hitPos.z);
			}
		}
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
		return switch (attribute)
		{
			case "damagetype" -> damagetype.toString();
			case "amount" -> String.valueOf(amount);
			case "canKill" -> String.valueOf(canKill);
			default -> null;
		};
	}
	
	@Override
	public void setAttribute(String s, String value) throws AttributeParseException, NumberFormatException
	{
		super.setAttribute(s, value);
		switch (s)
		{
			case "damagetype" -> damagetype =  parseIdentifier(value);
			case "amount" -> amount = Float.parseFloat(value);
			case "canKill" -> canKill = Boolean.parseBoolean(value);
		}
	}
	
	@Override
	public void readNbt(NbtCompound nbt)
	{
		super.readNbt(nbt);
		if(nbt.contains("damageType", NbtElement.STRING_TYPE))
			damagetype = Identifier.tryParse(nbt.getString("damageType"));
		if(nbt.contains("amount", NbtElement.FLOAT_TYPE))
			amount = nbt.getFloat("amount");
		if(nbt.contains("canKill", NbtElement.BYTE_TYPE))
			canKill = nbt.getBoolean("canKill");
	}
	
	@Override
	protected void writeNbt(NbtCompound nbt)
	{
		super.writeNbt(nbt);
		nbt.putString("damageType", damagetype.toString());
		nbt.putFloat("amount", amount);
		nbt.putBoolean("canKill", canKill);
	}
	
	static {
		attributes.add("damagetype");
		attributes.add("amount");
		attributes.add("canKill");
	}
}
