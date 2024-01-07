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

public class LevelBlockEntity extends AbstractMappingBlockEntity
{
	static int i = 0;
	
	public LevelBlockEntity(BlockPos pos, BlockState state)
	{
		super(BlockEntityRegistry.LEVEL, pos, state);
		id = "level-" + i;
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
		return Text.of("L-" + getID());
	}
	
	@Override
	public String getFocusKey()
	{
		return "level";
	}
}
