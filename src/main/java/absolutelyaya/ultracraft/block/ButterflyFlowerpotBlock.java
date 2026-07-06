package absolutelyaya.ultracraft.block;

import absolutelyaya.ultracraft.registry.ParticleRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FlowerPotBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class ButterflyFlowerpotBlock extends FlowerPotBlock
{
	public ButterflyFlowerpotBlock(Block content, Settings settings)
	{
		super(content, settings);
	}
	
	@Override
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random)
	{
		super.randomDisplayTick(state, world, pos, random);
		if(random.nextFloat() < 0.05f)
		{
			Vec3d pos2 = pos.up().toCenterPos().addRandom(random, 3);
			world.addParticle(ParticleRegistry.BUTTERFLY, pos2.x, pos2.y, pos2.z, 0f, 0f, 0f);
		}
	}
}
