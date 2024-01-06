package absolutelyaya.ultracraft.item;

import absolutelyaya.ultracraft.FishPacket;
import absolutelyaya.ultracraft.registry.SoundRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.Random;

public class KillerFishItem extends AbstractFishItem
{
	Random rand = new Random();
	
	public KillerFishItem(Settings settings)
	{
		super(settings);
	}
	
	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand)
	{
		boolean hi = rand.nextInt(500) == 0;
		boolean lo = rand.nextInt(500) == 0;
		user.playSound(SoundRegistry.KILLERFISH_USE, 1, hi ? 2f : lo ? 0.5f : 1f);
		user.getItemCooldownManager().set(this, 10);
		return TypedActionResult.success(user.getStackInHand(hand));
	}
	
	@Override
	protected void onSelect()
	{
		sendFishPacket(FishPacket.KILLER_SELECT);
	}
}
