package absolutelyaya.ultracraft.block;

import absolutelyaya.ultracraft.registry.BlockEntityRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public class SkyBlockEntity extends BlockEntity
{
	SkyType type = SkyType.DAY;
	
	public SkyBlockEntity(BlockPos pos, BlockState state)
	{
		super(BlockEntityRegistry.SKY, pos, state);
	}
	
	public SkyType getSkyType()
	{
		return type;
	}
	
	@Override
	public void readNbt(NbtCompound nbt)
	{
		super.readNbt(nbt);
		if(nbt.contains("type", NbtElement.STRING_TYPE))
		{
			type = switch(nbt.getString("type"))
			{
				default -> SkyType.DAY;
				case "fire" -> SkyType.FIRE;
				case "evening" -> SkyType.EVENING;
				case "night" -> SkyType.NIGHT;
				case "luna" -> SkyType.LUNA;
			};
		}
	}
	
	@Override
	protected void writeNbt(NbtCompound nbt)
	{
		super.writeNbt(nbt);
		nbt.putString("type", type.toString().toLowerCase(Locale.ROOT));
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
	
	public enum SkyType
	{
		DAY,
		FIRE,
		EVENING,
		NIGHT,
		LUNA(new String[]{null, "night1", "night2", "night3", "night4", "night5"});
		
		public final String[] textures;
		
		SkyType()
		{
			this.textures = null;
		}
		
		SkyType(String[] textures)
		{
			this.textures = textures;
		}
		
		@Override
		public String toString()
		{
			return super.toString().toLowerCase(Locale.ROOT);
		}
	}
}
