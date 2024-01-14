package absolutelyaya.ultracraft.item;

import absolutelyaya.ultracraft.UltraComponents;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ProgressionUnlockItem extends SpecialItem
{
	final Identifier progressionEntry;
	
	public ProgressionUnlockItem(Settings settings, Identifier progressionEntry)
	{
		super(settings);
		this.progressionEntry = progressionEntry;
	}
	
	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand)
	{
		UltraComponents.PROGRESSION.get(user).obtain(progressionEntry);
		if(world.isClient)
			user.sendMessage(Text.translatable("message.ultracraft.progression.unlock", getName().getString()), true);
		user.setStackInHand(hand, ItemStack.EMPTY);
		return TypedActionResult.success(user.getStackInHand(hand));
	}
	
	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context)
	{
		super.appendTooltip(stack, world, tooltip, context);
		tooltip.add(Text.translatable("item.ultracraft.progression-item.lore"));
	}
}
