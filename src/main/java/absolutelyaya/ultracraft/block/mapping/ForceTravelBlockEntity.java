package absolutelyaya.ultracraft.block.mapping;

import absolutelyaya.ultracraft.client.gui.screen.TravelScreen;
import absolutelyaya.ultracraft.registry.BlockEntityRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

public class ForceTravelBlockEntity extends AbstractTriggerBlockEntity
{
	static List<String> attributes = new ArrayList<>();
	
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
		return null;
	}
	
	@Override
	void tick()
	{
		super.tick();
		if(!world.isClient)
			return;
		MinecraftClient client = MinecraftClient.getInstance();
		if(containedEntities.contains(client.player) && !(client.currentScreen instanceof TravelScreen))
			client.setScreen(new TravelScreen(false, true));
	}
	
	@Override
	Class<? extends LivingEntity> getTargetClass()
	{
		return PlayerEntity.class;
	}
}
