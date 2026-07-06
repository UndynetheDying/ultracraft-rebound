package absolutelyaya.ultracraft.item;

import absolutelyaya.ultracraft.Ultracraft;
import absolutelyaya.ultracraft.components.UltraComponents;
import absolutelyaya.ultracraft.components.player.IProgressionComponent;
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
		if(isTrinket() && Ultracraft.TRINKETS)
			return TypedActionResult.fail(user.getStackInHand(hand));
		IProgressionComponent progression = UltraComponents.PROGRESSION.get(user);
		if(progression.isUnlocked(progressionEntry))
			return TypedActionResult.fail(user.getStackInHand(hand));
		UltraComponents.PROGRESSION.get(user).unlock(progressionEntry);
		if(world.isClient)
			UltraComponents.WINGED.get(user).sendBoxTitle(Text.translatable("message.ultracraft.progression.unlock", getName().getString()), 10f);
		TypedActionResult<ItemStack> result = TypedActionResult.success(user.getStackInHand(hand));
		if(!user.isCreative())
			user.getStackInHand(hand).decrement(1);
		return result;
	}
	
	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context)
	{
		super.appendTooltip(stack, world, tooltip, context);
		if(isTrinket() && Ultracraft.TRINKETS)
			return;
		tooltip.add(Text.translatable("item.ultracraft.progression-item.lore"));
		if(context.isAdvanced() && context.isCreative())
			tooltip.add(Text.translatable("item.ultracraft.progression-item.hidden-lore", "§8" + progressionEntry.toString()));
	}
}
