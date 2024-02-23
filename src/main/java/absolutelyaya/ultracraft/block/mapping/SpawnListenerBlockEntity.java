package absolutelyaya.ultracraft.block.mapping;

import absolutelyaya.ultracraft.Ultracraft;
import absolutelyaya.ultracraft.entity.demon.CerberusEntity;
import absolutelyaya.ultracraft.entity.demon.HideousMassEntity;
import absolutelyaya.ultracraft.entity.demon.MaliciousFaceEntity;
import absolutelyaya.ultracraft.entity.demon.RodentEntity;
import absolutelyaya.ultracraft.entity.husk.FilthEntity;
import absolutelyaya.ultracraft.entity.machine.DestinyBondSwordsmachineEntity;
import absolutelyaya.ultracraft.entity.machine.SwordsmachineEntity;
import absolutelyaya.ultracraft.registry.BlockEntityRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

public class SpawnListenerBlockEntity extends AbstractListenerBlockEntity
{
	static List<String> attributes = new ArrayList<>();
	List<Entity> entities = new ArrayList<>();
	Identifier entityType = new Identifier(Ultracraft.MOD_ID, "stray");
	float yaw;
	
	public SpawnListenerBlockEntity(BlockPos pos, BlockState state)
	{
		super(BlockEntityRegistry.MAP_SPAWNER, pos, state);
		id = "spawner";
	}
	
	@Override
	public Vector4f getColor()
	{
		return new Vector4f(0.5f, 0.43f, 0.61f, 0.75f);
	}
	
	@Override
	public Text getAreaLabel()
	{
		return Text.of("S-" + flag + "->" + id);
	}
	
	@Override
	protected void onStateChanged(boolean newState)
	{
		if(newState && !world.isClient)
		{
			switch(entityType.toString())
			{
				case "ultracraft:dummy" -> entities.add(FilthEntity.spawnWithoutAI(world, pos.toCenterPos().subtract(0f, 0.5f, 0f)));
				case "ultracraft:regular_swordsmachine" -> entities.add(SwordsmachineEntity.spawnAsNonBoss(world, pos.toCenterPos()));
				case "ultracraft:destiny_swordsmachine" -> entities.addAll(DestinyBondSwordsmachineEntity.spawn(world, pos.toCenterPos(), yaw));
				case "ultracraft:big_rodent" -> entities.add(RodentEntity.spawn(world, pos.toCenterPos(), 1));
				case "ultracraft:hidden_mass" -> entities.add(HideousMassEntity.spawn(world, pos.toCenterPos(), true));
				case "ultracraft:regular_mass" -> entities.add(HideousMassEntity.spawnAsNonBoss(world, pos.toCenterPos()));
				case "ultracraft:malicious_boss" -> entities.add(MaliciousFaceEntity.spawnAsBoss(world, pos.toCenterPos()));
				case "ultracraft:cerberus_boss" -> entities.add(CerberusEntity.spawnAsBoss(world, pos.toCenterPos(), false));
				case "ultracraft:half_cerberus_boss" -> entities.add(CerberusEntity.spawnAsBoss(world, pos.toCenterPos(), true));
				default -> entities.add(Registries.ENTITY_TYPE.get(entityType).spawn((ServerWorld)world, pos, SpawnReason.SPAWNER));
			}
			entities.forEach(e -> {
				e.setYaw(yaw);
				e.setBodyYaw(yaw);
				e.setHeadYaw(yaw);
			});
		}
		else if(!newState)
		{
			entities.forEach(e -> {
				if(e != null && e.isAlive())
					e.remove(Entity.RemovalReason.DISCARDED);
			});
			entities.clear();
		}
		super.onStateChanged(newState);
	}
	
	@Override
	public float getAreaLabelSize()
	{
		return 0f;
	}
	
	@Override
	public String getTexture()
	{
		return "spawn_listener";
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
			case "entityType" -> entityType = Identifier.tryParse(value);
			case "delay" -> activationDelay = Integer.parseInt(value);
			case "yaw" -> yaw = Float.parseFloat(value);
		}
		super.setAttribute(s, value);
	}
	
	@Override
	public String getAttribute(String attribute)
	{
		return switch (attribute)
		{
			case "entityType" -> String.valueOf(entityType);
			case "delay" -> String.valueOf(activationDelay);
			case "yaw" -> String.valueOf(yaw);
			default -> null;
		};
	}
	
	@Override
	public void readNbt(NbtCompound nbt)
	{
		super.readNbt(nbt);
		if(nbt.contains("entityType", NbtElement.STRING_TYPE))
			entityType = Identifier.tryParse(nbt.getString("entityType"));
		if(nbt.contains("yaw", NbtElement.FLOAT_TYPE))
			yaw = nbt.getFloat("yaw");
	}
	
	@Override
	protected void writeNbt(NbtCompound nbt)
	{
		super.writeNbt(nbt);
		nbt.putString("entityType", entityType.toString());
		nbt.putFloat("yaw", yaw);
	}
	
	static {
		attributes.add("entityType");
		attributes.add("delay");
		attributes.add("yaw");
	}
}
