package absolutelyaya.ultracraft.item;

import absolutelyaya.ultracraft.registry.SoundRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
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
	public void onSelect(PlayerEntity player)
	{
		player.getWorld().playSound(null, player.getBlockPos(), SoundRegistry.KILLERFISH_SELECT, SoundCategory.PLAYERS, 1f, 1f);
	}
	
	@Override
	public void onUnselect(PlayerEntity player)
	{
	
	}
}
