package absolutelyaya.ultracraft.block.mapping;

import absolutelyaya.ultracraft.registry.BlockEntityRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector4f;

public class RoomBlockEntity extends AbstractMappingBlockEntity
{
	static int i = 0;
	
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
}
