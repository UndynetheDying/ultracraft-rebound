package absolutelyaya.ultracraft.block.mapping;

import absolutelyaya.ultracraft.Ultracraft;
import absolutelyaya.ultracraft.entity.demon.HideousMassEntity;
import absolutelyaya.ultracraft.entity.demon.RodentEntity;
import absolutelyaya.ultracraft.entity.machine.DestinyBondSwordsmachineEntity;
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
	List<Entity> entities = new ArrayList<>();
	Identifier entityType = new Identifier(Ultracraft.MOD_ID, "stray");
	static List<String> attributes = new ArrayList<>();
	
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
				case "ultracraft:destiny_swordsmachine" -> entities.addAll(DestinyBondSwordsmachineEntity.spawn(world, pos.toCenterPos(), 0f));
				case "ultracraft:big_rodent" -> entities.add(RodentEntity.spawn(world, pos.toCenterPos(), 1));
				case "ultracraft:hidden_mass" -> entities.add(HideousMassEntity.spawn(world, pos.toCenterPos(), true));
				default -> entities.add(Registries.ENTITY_TYPE.get(entityType).spawn((ServerWorld)world, pos, SpawnReason.SPAWNER));
			}
		}
		else if(!newState)
		{
			entities.forEach(e -> {
				if(e != null) e.remove(Entity.RemovalReason.DISCARDED);
			});
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
		if(s.equals("entityType"))
			entityType = Identifier.tryParse(value);
		else if(s.equals("delay"))
			activationDelay = Integer.parseInt(value);
		super.setAttribute(s, value);
	}
	
	@Override
	public String getAttribute(String attribute)
	{
		if(attribute.equals("entityType"))
			return String.valueOf(entityType);
		else if(attribute.equals("delay"))
			return String.valueOf(activationDelay);
		return null;
	}
	
	@Override
	public void readNbt(NbtCompound nbt)
	{
		super.readNbt(nbt);
		if(nbt.contains("entityType", NbtElement.STRING_TYPE))
			entityType = Identifier.tryParse(nbt.getString("entityType"));
	}
	
	@Override
	protected void writeNbt(NbtCompound nbt)
	{
		super.writeNbt(nbt);
		nbt.putString("entityType", entityType.toString());
	}
	
	static {
		attributes.add("entityType");
		attributes.add("delay");
	}
}
