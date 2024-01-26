package absolutelyaya.ultracraft.block.mapping;

import absolutelyaya.ultracraft.components.UltraComponents;
import absolutelyaya.ultracraft.components.player.IWingedPlayerComponent;
import absolutelyaya.ultracraft.registry.BlockEntityRegistry;
import absolutelyaya.ultracraft.registry.SoundRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

public class CheckpointBlockEntity extends AbstractTriggerBlockEntity
{
	static List<String> attributes = new ArrayList<>();
	double time;
	
	public CheckpointBlockEntity(BlockPos pos, BlockState state)
	{
		super(BlockEntityRegistry.MAP_CHECKPOINT, pos, state);
		id = "checkpoint";
	}
	
	@Override
	public Vector4f getColor()
	{
		return new Vector4f(0.2f, 1f, 0.6f, 0.75f);
	}
	
	@Override
	public String getTexture()
	{
		return "checkpoint";
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
	Class<? extends LivingEntity> getTargetClass()
	{
		return PlayerEntity.class;
	}
	
	public void onRespawn()
	{
		if(world.getBlockEntity(getParent()) instanceof RoomBlockEntity room)
			room.resetIfEmpty();
	}
	
	@Override
	boolean selfTicking()
	{
		return true;
	}
	
	public static <T extends BlockEntity> void tick(World world, BlockPos blockPos, BlockState state, T t)
	{
		if(world.getBlockEntity(blockPos) instanceof CheckpointBlockEntity checkpoint)
			checkpoint.tick();
	}
	
	@Override
	public float getAreaLabelSize()
	{
		return 0f;
	}
	
	@Override
	void tick()
	{
		super.tick();
		containedEntities.forEach(e -> {
			if(e instanceof PlayerEntity player)
			{
				IWingedPlayerComponent winged = UltraComponents.WINGED.get(player);
				if(!pos.equals(winged.getLastCheckpoint()))
				{
					winged.setLastCheckpoint(pos, world);
					player.playSound(SoundRegistry.CHECKPOINT_GET, 1f, 2f);
					for (int i = 0; i < 16; i++)
					{
						Vec3d pos = player.getPos().add(0, player.getHeight() / 2, 0);
						Vec3d vel = Vec3d.ZERO.addRandom(player.getRandom(), 0.1f);
						world.addParticle(ParticleTypes.END_ROD, pos.x, pos.y, pos.z, vel.x, vel.y, vel.z);
					}
				}
			}
		});
	}
	
	public double getTime()
	{
		return time;
	}
	
	public void progressTime(double delta)
	{
		time += delta;
	}
}
