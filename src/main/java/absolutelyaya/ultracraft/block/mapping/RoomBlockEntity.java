package absolutelyaya.ultracraft.block.mapping;

import absolutelyaya.ultracraft.registry.BlockEntityRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtLong;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.joml.Vector4f;

import java.util.*;

public class RoomBlockEntity extends AbstractMappingBlockEntity
{
	static int i = 0;
	Map<BlockPos, AbstractMappingBlockEntity> children = new HashMap<>();
	Map<String, Boolean> flags = new HashMap<>();
	boolean childCheckPending;
	
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
			if(room.childCheckPending)
			{
				for (BlockPos pos : room.getChildren())
				{
					if(world.getBlockEntity(pos) instanceof AbstractMappingBlockEntity block && !(block instanceof RoomBlockEntity))
						room.registerChild(pos.subtract(room.getPos()), block);
					else
						room.removeChild(pos.subtract(room.getPos()));
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
	void tick() //just execute child block ticks
	{
		children.forEach((pos, entity) -> {
			if(world.getBlockEntity(pos.add(getPos())) instanceof AbstractMappingBlockEntity e && !(e instanceof RoomBlockEntity))
				e.tick();
		});
	}
	
	public void registerChild(BlockPos pos, AbstractMappingBlockEntity blockEntity)
	{
		children.put(pos.subtract(getPos()), blockEntity);
		markDirty();
		world.updateListeners(pos, getCachedState(), getCachedState(), 0);
		setID(id);
	}
	
	public void removeChild(BlockPos pos)
	{
		children.remove(pos.subtract(getPos()));
		markDirty();
		world.updateListeners(pos, getCachedState(), getCachedState(), 0);
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
			if(children.get(pos) instanceof FlagListener listener)
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
	}
}
