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
import net.minecraft.util.math.BlockPos;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DamageBlockEntity extends AbstractTriggerBlockEntity
{
	static final List<String> attributes = new ArrayList<>();
	List<? extends LivingEntity> lastContained = new ArrayList<>();
	Identifier damagetype = new Identifier("out_of_world");
	float amount = 1f;
	boolean perTick;
	
	public DamageBlockEntity(BlockPos pos, BlockState state)
	{
		super(BlockEntityRegistry.MAP_DAMAGE, pos, state);
		id = "damage";
	}
	
	@Override
	public Vector4f getColor()
	{
		return new Vector4f(0.19f, 0.02f, 0.01f, 1f);
	}
	
	@Override
	public String getTexture()
	{
		return "damage";
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
			case "perTick" -> String.valueOf(perTick);
			default -> null;
		};
	}
	
	@Override
	public void setAttribute(String s, String value)
	{
		super.setAttribute(s, value);
		switch (s)
		{
			case "damagetype" -> damagetype =  Identifier.tryParse(value);
			case "amount" -> amount = Float.parseFloat(value);
			case "perTick" -> perTick = Boolean.parseBoolean(value);
		};
	}
	
	@Override
	Class<? extends LivingEntity> getTargetClass()
	{
		return LivingEntity.class;
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
			if(lastContained.contains(living) && !perTick)
				continue;
			living.damage(DamageSources.get(world, damageTypeKey.get()), amount);
		}
		lastContained = containedEntities;
	}
	
	@Override
	public void readNbt(NbtCompound nbt)
	{
		super.readNbt(nbt);
		if(nbt.contains("damageType", NbtElement.STRING_TYPE))
			damagetype = Identifier.tryParse(nbt.getString("damageType"));
		if(nbt.contains("amount", NbtElement.FLOAT_TYPE))
			amount = nbt.getFloat("amount");
		if(nbt.contains("perTick", NbtElement.BYTE_TYPE))
			perTick = nbt.getBoolean("perTick");
	}
	
	@Override
	protected void writeNbt(NbtCompound nbt)
	{
		super.writeNbt(nbt);
		nbt.putString("damageType", damagetype.toString());
		nbt.putFloat("amount", amount);
		nbt.putBoolean("perTick", perTick);
	}
	
	static {
		attributes.add("damagetype");
		attributes.add("amount");
		attributes.add("perTick");
	}
}
