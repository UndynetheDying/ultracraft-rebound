package absolutelyaya.ultracraft.block.mapping;

import absolutelyaya.ultracraft.registry.BlockEntityRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector4f;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class RoomBlockEntity extends AbstractMappingBlockEntity
{
	static int i = 0;
	Map<BlockPos, AbstractMappingBlockEntity> children = new HashMap<>();
	Map<String, Boolean> flags = new HashMap<>();
	
	public RoomBlockEntity(BlockPos pos, BlockState state)
	{
		super(BlockEntityRegistry.MAP_ROOM, pos, state);
		id = "room-" + i;
		i++;
	}
	
	@Nullable
	@Override
	public Packet<ClientPlayPacketListener> toUpdatePacket()
	{
		return BlockEntityUpdateS2CPacket.create(this);
	}
	
	@Override
	public NbtCompound toInitialChunkDataNbt()
	{
		return createNbt();
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
	
	public void registerChild(BlockPos pos, AbstractMappingBlockEntity blockEntity)
	{
		children.put(pos, blockEntity);
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
		markDirty();
		world.updateListeners(pos, getCachedState(), getCachedState(), 0);
		return true;
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
	}
	
	@Override
	protected void writeNbt(NbtCompound nbt)
	{
		super.writeNbt(nbt);
		NbtCompound flags = new NbtCompound();
		for (String id : getFlags())
			flags.putBoolean(id, checkFlag(id));
		nbt.put("flags", flags);
	}
}
