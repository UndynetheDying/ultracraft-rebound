package absolutelyaya.ultracraft.block.mapping;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector4f;

import java.util.List;
import java.util.function.Consumer;

public abstract class AbstractMappingBlockEntity extends BlockEntity
{
	protected String id;
	protected BlockPos min, max, parent;
	
	public AbstractMappingBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
	{
		super(type, pos, state);
		min = new BlockPos(0, 0, 0);
		max = new BlockPos(0, 0, 0);
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
	
	public void setID(String id)
	{
		this.id = id;
		markDirty();
		world.updateListeners(pos, getCachedState(), getCachedState(), 0);
	}
	
	public String getID()
	{
		return id;
	}
	
	public void setAreaCorner(int editAreaStep, BlockPos pos)
	{
		if(editAreaStep == 2)
			max = pos.subtract(getPos());
		else if(editAreaStep == 1)
			min = pos.subtract(getPos());
		setID(id); //for some reason the blocks data woN'T SYNC ANY OTHER WAY RAAAA
	}
	
	public void setMin(BlockPos pos)
	{
		min = pos.subtract(getPos());
	}
	
	public void setMax(BlockPos pos)
	{
		max = pos.subtract(getPos());
	}
	
	public BlockPos getMin()
	{
		if(min == null)
			return null;
		return min.add(getPos());
	}
	
	public BlockPos getMax()
	{
		if(max == null)
			return null;
		return max.add(getPos());
	}
	
	public Box getAreaBox()
	{
		return new Box(getMin(), getMax()).expand(0.5f).offset(0.5f, 0.5f, 0.5f);
	}
	
	public Text getAreaLabel()
	{
		return Text.of(id);
	}
	
	public boolean alwaysShowArea()
	{
		return false;
	}
	
	public abstract String getFocusKey();
	
	public abstract Vector4f getColor();
	
	public abstract boolean showCamLine();
	
	public Vector4f getAreaColor()
	{
		return new Vector4f(0.3f, 0.3f, 0.3f, 0.75f);
	}
	
	public float getAreaLabelSize()
	{
		return 4f;
	}
	
	public BlockPos getParent()
	{
		if(parent == null)
			return null;
		return parent.add(getPos());
	}
	
	public void setParent(BlockPos pos)
	{
		if(pos == null)
			parent = null;
		else
			parent = pos.subtract(getPos());
	}
	
	public boolean isAreaModifiable()
	{
		return true;
	}
	
	abstract void tick();
	
	public abstract String getTexture();
	
	void forEachBlockInArea(Consumer<BlockPos> consumer)
	{
		Box box = getAreaBox();
		BlockPos min = new BlockPos((int)box.minX, (int)box.minY, (int)box.minZ);
		BlockPos size = new BlockPos((int)(box.maxX - box.minX), (int)(box.maxY - box.minY), (int)(box.maxZ - box.minZ));
		
		for (int x = 0; x < Math.abs(size.getX()); x++)
			for (int y = 0; y < Math.abs(size.getY()); y++)
				for (int z = 0; z < Math.abs(size.getZ()); z++)
					consumer.accept(min.add(x, y, z));
	}
	
	public abstract List<String> getAttributes();
	
	public void setAttribute(String s, String value)
	{
		markDirty();
		world.updateListeners(pos, getCachedState(), getCachedState(), 0);
	}
	
	public abstract String getAttribute(String attribute);
	
	public abstract void reset();
	
	boolean selfTicking()
	{
		return false;
	}
	
	@Override
	protected void writeNbt(NbtCompound nbt)
	{
		super.writeNbt(nbt);
		if(min != null && max != null)
		{
			nbt.putLong("min", BlockPos.asLong(min.getX(), min.getY(), min.getZ()));
			nbt.putLong("max", BlockPos.asLong(max.getX(), max.getY(), max.getZ()));
		}
		nbt.putString("name", id);
		if(parent != null)
			nbt.putLong("parent", parent.asLong());
	}
	
	@Override
	public void readNbt(NbtCompound nbt)
	{
		super.readNbt(nbt);
		if(nbt.contains("min", NbtElement.LONG_TYPE))
			min = BlockPos.fromLong(nbt.getLong("min"));
		if(nbt.contains("max", NbtElement.LONG_TYPE))
			max = BlockPos.fromLong(nbt.getLong("max"));
		if(nbt.contains("name", NbtElement.STRING_TYPE))
			id = nbt.getString("name");
		if(nbt.contains("parent", NbtElement.LONG_TYPE))
			parent = BlockPos.fromLong(nbt.getLong("parent"));
	}
	
	//TODO: add Scriptable Blocks
}
