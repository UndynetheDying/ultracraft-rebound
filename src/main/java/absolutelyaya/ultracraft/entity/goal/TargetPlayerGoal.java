package absolutelyaya.ultracraft.entity.goal;

import absolutelyaya.ultracraft.entity.AbstractUltraHostileEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.attribute.EntityAttributes;

public class TargetPlayerGoal extends Goal
{
	final AbstractUltraHostileEntity mob;
	final float distance;
	
	public TargetPlayerGoal(AbstractUltraHostileEntity mob)
	{
		this.mob = mob;
		this.distance = (float)mob.getAttributeBaseValue(EntityAttributes.GENERIC_FOLLOW_RANGE);
	}
	
	@Override
	public boolean canStart()
	{
		return mob.getTarget() == null;
	}
	
	@Override
	public void start()
	{
		mob.setTarget(mob.getWorld().getClosestPlayer(TargetPredicate.createAttackable().setBaseMaxDistance(distance).ignoreVisibility(), mob));
	}
	
	@Override
	public boolean shouldContinue()
	{
		return mob.getTarget() != null && mob.getTarget().canTakeDamage();
	}
	
	@Override
	public void stop()
	{
		if(mob.getTarget() != null)
			mob.setTarget(null);
	}
}
