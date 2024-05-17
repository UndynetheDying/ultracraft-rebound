package absolutelyaya.ultracraft.block.mapping;

import absolutelyaya.ultracraft.components.UltraComponents;
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
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

public class ForceTravelBlockEntity extends AbstractTriggerBlockEntity
{
	List<? extends LivingEntity> lastContained = new ArrayList<>();
	static List<String> attributes = new ArrayList<>();
	boolean openRanking = true, rankingTitleSuffix = true;
	Identifier forceDestination = null;
	
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
		return switch (attribute)
		{
			case "openRanking" -> String.valueOf(openRanking);
			case "rankingTitleSuffix" -> String.valueOf(rankingTitleSuffix);
			case "forceDestination" -> forceDestination == null ? "none" : forceDestination.toString();
			default -> null;
		};
	}
	
	@Override
	public void setAttribute(String s, String value) throws AttributeParseException, NumberFormatException
	{
		switch (s)
		{
			case "openRanking" -> openRanking = Boolean.parseBoolean(value);
			case "rankingTitleSuffix" -> rankingTitleSuffix = Boolean.parseBoolean(value);
			case "forceDestination" -> {
				if(value.equalsIgnoreCase("none") || value.equalsIgnoreCase("null") || value.equalsIgnoreCase("default"))
					forceDestination = null;
				else
					forceDestination = Identifier.tryParse(value);
			}
		}
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
					buf.writeBoolean(rankingTitleSuffix);
					buf.writeNullable(forceDestination, PacketByteBuf::writeIdentifier);
					ServerPlayNetworking.send(player, PacketRegistry.TRAVEL_SCREEN_PACKET_ID, buf);
					UltraComponents.LEVEL_STATS.get(player).onFinishLevel();
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
		if(nbt.contains("rankingTitleSuffix", NbtElement.BYTE_TYPE))
			rankingTitleSuffix = nbt.getBoolean("rankingTitleSuffix");
		if(nbt.contains("forceDestination", NbtElement.STRING_TYPE))
		{
			String val = nbt.getString("forceDestination");
			if(val.equals("none"))
				forceDestination = null;
			else
				forceDestination = Identifier.tryParse(val);
		}
	}
	
	@Override
	protected void writeNbt(NbtCompound nbt)
	{
		super.writeNbt(nbt);
		nbt.putBoolean("openRanking", openRanking);
		nbt.putBoolean("rankingTitleSuffix", rankingTitleSuffix);
		nbt.putString("forceDestination", forceDestination != null ? forceDestination.toString() : "none");
	}
	
	@Override
	Class<? extends LivingEntity> getTargetClass()
	{
		return PlayerEntity.class;
	}
	
	static {
		attributes.add("openRanking");
		attributes.add("rankingTitleSuffix");
		attributes.add("forceDestination");
	}
}
