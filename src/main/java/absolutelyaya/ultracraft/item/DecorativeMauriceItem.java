package absolutelyaya.ultracraft.item;

import absolutelyaya.ultracraft.entity.demon.MaliciousFaceEntity;
import absolutelyaya.ultracraft.registry.EntityRegistry;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class DecorativeMauriceItem extends Item
{
	public DecorativeMauriceItem(Settings settings)
	{
		super(settings);
	}
	
	@Override
	public ActionResult useOnBlock(ItemUsageContext context)
	{
		Direction face = context.getSide();
		PlayerEntity player = context.getPlayer();
		if(face != Direction.UP)
			return ActionResult.FAIL;
		World world = context.getWorld();
		MaliciousFaceEntity maurice = new MaliciousFaceEntity(EntityRegistry.MALICIOUS_FACE, world);
		maurice.setPosition(context.getHitPos().add(0f, 0.1f, 0f));
		maurice.setDecorative(true);
		if(player != null)
		{
			maurice.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, player.getEyePos());
			ItemStack stack = context.getStack();
			if(stack != null && !player.isCreative())
				stack.decrement(1);
		}
		world.spawnEntity(maurice);
		return ActionResult.SUCCESS;
	}
}
