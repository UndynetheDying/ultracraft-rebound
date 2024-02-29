package absolutelyaya.ultracraft.block.mapping;

import absolutelyaya.ultracraft.block.CerberusBlock;
import absolutelyaya.ultracraft.components.UltraComponents;
import absolutelyaya.ultracraft.entity.AbstractUltraHostileEntity;
import absolutelyaya.ultracraft.registry.BlockEntityRegistry;
import absolutelyaya.ultracraft.registry.BlockRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtLong;
import net.minecraft.text.Text;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.joml.Vector4f;

import java.util.*;

public class RoomBlockEntity extends AbstractMappingBlockEntity
{
	static List<String> attributes = new ArrayList<>();
	static int i = 0;
	Map<BlockPos, AbstractMappingBlockEntity> children = new HashMap<>();
	Map<String, Boolean> flags = new HashMap<>();
	boolean childCheckPending, active, suppressModifications = true, initialized;
	int curResetCooldown, maxResetCooldown = 6000;
	
	public RoomBlockEntity(BlockPos pos, BlockState state)
	{
		super(BlockEntityRegistry.MAP_ROOM, pos, state);
		id = "room-" + i;
		i++;
	}
	
	public static <T extends BlockEntity> void tick(World world, BlockPos ignored1, BlockState ignored2, T instance)
	{
		if(instance instanceof RoomBlockEntity room)
		{
			if(!room.initialized && world != null)
			{
				UltraComponents.DIMENSION_DATA.get(world).registerRoomMappingBlock(room.pos);
				room.initialized = true;
			}
			if(room.childCheckPending && world != null)
			{
				for (BlockPos pos : room.getChildren())
				{
					if(world.getBlockEntity(pos) instanceof AbstractMappingBlockEntity block && !(block instanceof RoomBlockEntity))
						room.registerChild(pos, block);
					else
						room.removeChild(pos);
				}
				room.childCheckPending = false;
			}
			room.tick();
		}
	}
	
	@Override
	public Text getAreaLabel()
	{
		return Text.of("R-" + getID());
	}
	
	@Override
	public String getFocusKey()
	{
		return "room";
	}
	
	@Override
	public Vector4f getColor()
	{
		return new Vector4f(0.25f, 0.25f, 0.25f, 0.2f);
	}
	
	@Override
	public boolean showCamLine()
	{
		return true;
	}
	
	@Override
	void tick()
	{
		boolean lastActive = active;
		active = world.getEntitiesByType(TypeFilter.instanceOf(PlayerEntity.class), getAreaBox(), i -> i.isAlive() && !i.isSpectator()).size() > 0;
		
		if(active) //tick child blocks
		{
			children.forEach((pos, entity) -> {
				if(world.getBlockEntity(pos.add(getPos())) instanceof AbstractMappingBlockEntity e && !e.selfTicking() && !(e instanceof RoomBlockEntity))
					e.tick();
			});
		}
		else //reset
		{
			if(lastActive)
				curResetCooldown = maxResetCooldown;
			//if the room was not active for the cooldown period, reset all flags to false and notify all listeners
			if(curResetCooldown > 0)
			{
				curResetCooldown--;
				if(curResetCooldown == 0)
					reset();
			}
		}
	}
	
	@Override
	public void reset()
	{
		for (String key : flags.keySet())
			setFlag(key, false);
		for (AbstractMappingBlockEntity child : children.values())
			if(child != null)
				child.reset();
		world.getEntitiesByType(TypeFilter.instanceOf(AbstractUltraHostileEntity.class), getAreaBox(), LivingEntity::isAlive)
				.forEach(e -> {
					if(e.isAlive())
						e.remove(Entity.RemovalReason.DISCARDED);
				});
		forEachBlockInArea(pos -> {
			BlockState state = world.getBlockState(pos);
			if(state.isOf(BlockRegistry.CERBERUS))
				world.setBlockState(pos, state.with(CerberusBlock.EMPTY, false).with(CerberusBlock.SPAWNING, false));
		});
	}
	
	public void registerChild(BlockPos pos, AbstractMappingBlockEntity blockEntity)
	{
		children.put(pos.subtract(getPos()), blockEntity);
		markDirty();
		world.updateListeners(getPos(), getCachedState(), getCachedState(), 0);
		setID(id);
	}
	
	public void removeChild(BlockPos pos)
	{
		children.remove(pos.subtract(getPos()));
		markDirty();
		world.updateListeners(getPos(), getCachedState(), getCachedState(), 0);
		setID(id);
	}
	
	public List<BlockPos> getChildren()
	{
		List<BlockPos> out = new ArrayList<>();
		children.forEach((c, b) -> out.add(c.add(getPos())));
		return out;
	}
	
	public void registerFlag(String id)
	{
		flags.put(id, false);
		markDirty();
		world.updateListeners(pos, getCachedState(), getCachedState(), 0);
	}
	
	public boolean removeFlag(String id)
	{
		boolean b = flags.remove(id) != null;
		if(b)
		{
			markDirty();
			world.updateListeners(pos, getCachedState(), getCachedState(), 0);
		}
		return b;
	}
	
	public boolean checkFlag(String id)
	{
		return flags.getOrDefault(id, false);
	}
	
	public Set<String> getFlags()
	{
		return flags.keySet();
	}
	
	public boolean setFlag(String id, boolean state)
	{
		if(!flags.containsKey(id))
			return false;
		flags.put(id, state);
		for (BlockPos pos : children.keySet())
		{
			if(children.get(pos) instanceof FlagListener listener && listener.getFlag() != null && listener.getFlag().equals(id))
			{
				if(state)
					listener.onActivateFlag();
				else
					listener.onDeactivateFlag();
			}
		}
		markDirty();
		world.updateListeners(pos, getCachedState(), getCachedState(), 0);
		return true;
	}
	
	@Override
	public String getTexture()
	{
		return "root";
	}
	
	@Override
	public List<String> getAttributes()
	{
		return attributes;
	}
	
	@Override
	public void setAttribute(String s, String value)
	{
		if(s.equals("resetCooldown"))
			maxResetCooldown = Math.max(Integer.parseInt(value), 1);
		else if(s.equals("suppressModifications"))
			suppressModifications = Boolean.parseBoolean(value);
		super.setAttribute(s, value);
	}
	
	@Override
	public String getAttribute(String attribute)
	{
		if(attribute.equals("resetCooldown"))
			return String.valueOf(maxResetCooldown);
		else if(attribute.equals("suppressModifications"))
			return String.valueOf(suppressModifications);
		return null;
	}
	
	public void resetIfEmpty()
	{
		if(!active)
			reset();
	}
	
	@Override
	public void readNbt(NbtCompound nbt)
	{
		super.readNbt(nbt);
		if(nbt.contains("flags", NbtElement.COMPOUND_TYPE))
		{
			this.flags = new HashMap<>();
			NbtCompound flags = nbt.getCompound("flags");
			for (String key : flags.getKeys())
				this.flags.put(key, flags.getBoolean(key));
		}
		if(nbt.contains("children", NbtElement.LIST_TYPE))
		{
			this.children = new HashMap<>();
			NbtList list = nbt.getList("children", NbtElement.LONG_TYPE);
			for (NbtElement i : list)
			{
				if(i instanceof NbtLong l)
				{
					BlockPos pos = BlockPos.fromLong(l.longValue());
					if(pos != null)
						children.put(pos, null);
				}
			}
			if(list.size() > 0)
				childCheckPending = true;
		}
		if(nbt.contains("resetCooldown", NbtElement.INT_TYPE))
			maxResetCooldown = nbt.getInt("resetCooldown");
		if(nbt.contains("noMod", NbtElement.BYTE_TYPE))
			suppressModifications = nbt.getBoolean("noMod");
	}
	
	@Override
	protected void writeNbt(NbtCompound nbt)
	{
		super.writeNbt(nbt);
		NbtCompound flags = new NbtCompound();
		for (String id : getFlags())
			flags.putBoolean(id, checkFlag(id));
		nbt.put("flags", flags);
		NbtList children = new NbtList();
		for (BlockPos pos : this.children.keySet())
			children.add(NbtLong.of(pos.asLong()));
		nbt.put("children", children);
		nbt.putInt("resetCooldown", maxResetCooldown);
		nbt.putBoolean("noMod", suppressModifications);
	}
	
	@Override
	public void markRemoved()
	{
		UltraComponents.DIMENSION_DATA.get(world).removeRoomMappingBlock(pos);
		super.markRemoved();
	}
	
	public boolean isSuppressModifications()
	{
		return suppressModifications;
	}
	
	static {
		attributes.add("resetCooldown");
		attributes.add("suppressModifications");
	}
}
