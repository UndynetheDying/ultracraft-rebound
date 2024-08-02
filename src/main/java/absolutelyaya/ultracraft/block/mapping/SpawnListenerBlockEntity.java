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
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockStateRaycastContext;
import net.minecraft.world.BlockView;
import net.minecraft.world.RaycastContext;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

public class SpawnListenerBlockEntity extends AbstractListenerBlockEntity
{
	static List<String> attributes = new ArrayList<>();
	List<Entity> entities = new ArrayList<>();
	Identifier entityType = Ultracraft.identifier("stray");
	float yaw;
	boolean noAI, onGround;
	
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
			Vec3d pos = this.pos.toCenterPos();
			switch(entityType.toString())
			{
				case "ultracraft:swordsmachine_wave1" -> entities.add(SwordsmachineEntity.spawnAsBoss(world, pos, 1));
				case "ultracraft:swordsmachine_wave2" -> entities.add(SwordsmachineEntity.spawnAsBoss(world, pos, 2));
				case "ultracraft:regular_swordsmachine" -> entities.add(SwordsmachineEntity.spawnAsNonBoss(world, pos));
				case "ultracraft:destiny_swordsmachine" -> entities.addAll(DestinyBondSwordsmachineEntity.spawn(world, pos, yaw));
				case "ultracraft:very_rodent" -> entities.add(RodentEntity.spawn(world, pos, 1));
				case "ultracraft:hidden_mass" -> entities.add(HideousMassEntity.spawn(world, pos, true));
				case "ultracraft:regular_mass" -> entities.add(HideousMassEntity.spawnAsNonBoss(world, pos));
				case "ultracraft:malicious_boss" -> entities.add(MaliciousFaceEntity.spawnAsBoss(world, pos));
				case "ultracraft:cerberus_boss" -> entities.add(CerberusEntity.spawnAsBoss(world, pos, false));
				case "ultracraft:half_cerberus_boss" -> entities.add(CerberusEntity.spawnAsBoss(world, pos, true));
				default -> entities.add(Registries.ENTITY_TYPE.get(entityType).spawn((ServerWorld)world, this.pos, SpawnReason.SPAWNER));
			}
			entities.forEach(e -> {
				e.setYaw(yaw);
				e.setBodyYaw(yaw);
				e.setHeadYaw(yaw);
				e.prevYaw = yaw;
				if(noAI && e instanceof MobEntity mob)
				{
					mob.setAiDisabled(true);
					mob.setPosition(mob.getPos().subtract(0f, 0.5f, 0f));
				}
				if(onGround)
				{
					HitResult hit = world.raycast(new RaycastContext(pos, pos.subtract(0f, 16f, 0f),
							RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, e));
					if(hit != null && !hit.getType().equals(HitResult.Type.MISS))
					{
						Vec3d hitPos = hit.getPos();
						e.refreshPositionAndAngles(hitPos.x, hitPos.y, hitPos.z, e.getYaw(), e.getPitch());
						e.setOnGround(true);
					}
				}
			});
		}
		else if(!newState)
		{
			entities.forEach(e -> {
				if(e != null && e.isAlive() && !e.isRemoved())
					e.setRemoved(Entity.RemovalReason.DISCARDED);
			});
			entities.removeIf(Entity::isRemoved);
		}
		super.onStateChanged(newState);
	}
	
	@Override
	public void reset()
	{
		super.reset();
		entities.forEach(e -> {
			if(e != null && e.isAlive() && !e.isRemoved())
				e.setRemoved(Entity.RemovalReason.DISCARDED);
		});
		entities.removeIf(Entity::isRemoved);
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
	public void setAttribute(String s, String value) throws AttributeParseException, NumberFormatException
	{
		switch (s)
		{
			case "entityType" -> entityType = parseIdentifier(value);
			case "delay" -> activationDelay = Integer.parseInt(value);
			case "yaw" -> yaw = Float.parseFloat(value);
			case "noAI" -> noAI = Boolean.parseBoolean(value);
			case "onGround" -> onGround = Boolean.parseBoolean(value);
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
			case "noAI" -> String.valueOf(noAI);
			case "onGround" -> String.valueOf(onGround);
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
		if(nbt.contains("noAI", NbtElement.BYTE_TYPE))
			noAI = nbt.getBoolean("noAI");
		if(nbt.contains("onGround", NbtElement.BYTE_TYPE))
			onGround = nbt.getBoolean("onGround");
	}
	
	@Override
	protected void writeNbt(NbtCompound nbt)
	{
		super.writeNbt(nbt);
		nbt.putString("entityType", entityType.toString());
		nbt.putFloat("yaw", yaw);
		nbt.putBoolean("noAI", noAI);
		nbt.putBoolean("onGround", onGround);
	}
	
	static {
		attributes.add("entityType");
		attributes.add("delay");
		attributes.add("yaw");
		attributes.add("noAI");
		attributes.add("onGround");
	}
}
