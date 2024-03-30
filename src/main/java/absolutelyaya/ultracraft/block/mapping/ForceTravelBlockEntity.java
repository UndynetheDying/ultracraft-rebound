package absolutelyaya.ultracraft.block.mapping;

import absolutelyaya.ultracraft.registry.BlockEntityRegistry;
import absolutelyaya.ultracraft.registry.PacketRegistry;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

public class ForceTravelBlockEntity extends AbstractTriggerBlockEntity
{
	List<? extends LivingEntity> lastContained = new ArrayList<>();
	static List<String> attributes = new ArrayList<>();
	boolean openRanking = true;
	
	public ForceTravelBlockEntity(BlockPos pos, BlockState state)
	{
		super(BlockEntityRegistry.MAP_TRAVEL, pos, state);
		id = "travel";
	}
	
	@Override
	public Vector4f getColor()
	{
		return new Vector4f(0.337f, 0.251f, 0.392f, 1f);
	}
	
	@Override
	public String getTexture()
	{
		return "force_travel";
	}
	
	@Override
	public Text getAreaLabel()
	{
		return Text.of("ForceTravel");
	}
	
	@Override
	public List<String> getAttributes()
	{
		return attributes;
	}
	
	@Override
	public String getAttribute(String attribute)
	{
		if(attribute.equals("openRanking"))
			return String.valueOf(openRanking);
		return null;
	}
	
	@Override
	public void setAttribute(String s, String value)
	{
		if(s.equals("openRanking"))
			openRanking = Boolean.parseBoolean(value);
		super.setAttribute(s, value);
	}
	
	@Override
	void tick()
	{
		super.tick();
		if(!world.isClient)
		{
			for (LivingEntity e : containedEntities)
			{
				if(!lastContained.contains(e) && e instanceof ServerPlayerEntity player)
				{
					PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
					buf.writeBoolean(true);
					buf.writeBoolean(openRanking);
					ServerPlayNetworking.send(player, PacketRegistry.TRAVEL_SCREEN_PACKET_ID, buf);
				}
			}
			lastContained = containedEntities;
		}
	}
	
	@Override
	public void readNbt(NbtCompound nbt)
	{
		super.readNbt(nbt);
		if(nbt.contains("openRanking", NbtElement.BYTE_TYPE))
			openRanking = nbt.getBoolean("openRanking");
	}
	
	@Override
	protected void writeNbt(NbtCompound nbt)
	{
		super.writeNbt(nbt);
		nbt.putBoolean("openRanking", openRanking);
	}
	
	@Override
	Class<? extends LivingEntity> getTargetClass()
	{
		return PlayerEntity.class;
	}
	
	static {
		attributes.add("openRanking");
	}
}
