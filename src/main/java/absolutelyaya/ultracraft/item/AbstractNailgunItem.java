package absolutelyaya.ultracraft.item;

import absolutelyaya.ultracraft.components.UltraComponents;
import absolutelyaya.ultracraft.Weapon;
import absolutelyaya.ultracraft.components.player.IWingedPlayerComponent;
import absolutelyaya.ultracraft.registry.ItemRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import mod.azure.azurelib.animatable.GeoItem;
import mod.azure.azurelib.core.animation.AnimatableManager;
import mod.azure.azurelib.core.animation.AnimationController;
import mod.azure.azurelib.core.animation.RawAnimation;
import mod.azure.azurelib.core.object.PlayState;

public abstract class AbstractNailgunItem extends AbstractWeaponItem implements GeoItem
{
	final RawAnimation AnimationFireLoop = RawAnimation.begin().thenPlay("fire_loop");
	final RawAnimation AnimationFireStop = RawAnimation.begin().thenPlay("fire_stop");
	final RawAnimation AnimationAltFire = RawAnimation.begin().thenPlay("alt_fire");
	
	public AbstractNailgunItem(Settings settings)
	{
		super(settings, 0.5f, 15f);
	}
	
	@Override
	public void onPrimaryFireStart(World world, PlayerEntity user)
	{
		super.onPrimaryFireStart(world, user);
		if(!world.isClient)
			triggerAnim(user, GeoItem.getOrAssignId(user.getMainHandStack(), (ServerWorld)world), getControllerName(), "fire_loop");
	}
	
	@Override
	public void onPrimaryFireStop(World world, PlayerEntity user)
	{
		super.onPrimaryFireStop(world, user);
		if(!world.isClient)
			triggerAnim(user, GeoItem.getOrAssignId(user.getMainHandStack(), (ServerWorld)world), getControllerName(), "fire_stop");
	}
	
	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand)
	{
		ItemStack stack = user.getStackInHand(hand);
		if(hand.equals(Hand.OFF_HAND))
			return TypedActionResult.fail(stack);
		onAltFire(world, user);
		return super.use(world, user, hand);
	}
	
	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected)
	{
		super.inventoryTick(stack, world, entity, slot, selected);
		if(world.isClient)
			return;
		boolean inactive = (!selected || (entity instanceof PlayerEntity player && !UltraComponents.WINGED.get(player).isPrimaryFiring()));
		int nails = getNbt(stack, "nails");
		if(nails < 100 && entity.age % 5 == 0 && (inactive || nails == 0))
			setNbt(stack, "nails", nails + 1);
		int heat = getNbt(stack, "heat");
		if(heat > 0  && entity.age % 3 == 0 && inactive)
			setNbt(stack, "heat", heat - 1);
		else if(!inactive && entity.age % 2 == 0)
			if(heat < 100 && !(stack.isOf(ItemRegistry.OVERHEAT_NAILGUN) && getNbt(stack, "heatsinking") == 1) && getNbt(stack, "heatsinks") > 0)
				setNbt(stack, "heat", Math.min(heat + 2, 100));
		int heatsinkCD = getNbt(stack, "heatsink_cd");
		int heatsinks = getNbt(stack, "heatsinks");
		if(heatsinkCD > 0 && inactive)
			setNbt(stack, "heatsink_cd", heatsinkCD - 1);
		if(heatsinkCD == 0)
		{
			if(heatsinks < 2)
				setNbt(stack, "heatsinks", ++heatsinks);
			if(heatsinks >= 2)
				setNbt(stack, "heatsink_cd", -1);
			else
				setNbt(stack, "heatsink_cd", 160);
		}
	}
	
	
	@Override
	int getSwitchCooldown(ItemStack stack)
	{
		return 10;
	}
	
	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar)
	{
		controllerRegistrar.add(new AnimationController<>(this, getControllerName(), 1, state -> PlayState.STOP)
										.triggerableAnim("fire_loop", AnimationFireLoop)
										.triggerableAnim("fire_stop", AnimationFireStop)
										.triggerableAnim("alt_fire", AnimationAltFire));
	}
	
	@Override
	public int getNbtDefault(String nbt)
	{
		if(nbt.equals("nails"))
			return 100;
		if(nbt.equals("magnets"))
			return 3;
		if(nbt.equals("heatsinks"))
			return 2;
		return 0;
	}
	
	@Override
	public boolean shouldAim()
	{
		return false;
	}
	
	@Override
	protected void onBeforeSwitch(PlayerEntity user, World world)
	{
		super.onBeforeSwitch(user, world);
		IWingedPlayerComponent winged = UltraComponents.WINGED.get(user);
		if(winged.isPrimaryFiring())
			onPrimaryFireStop(world, user);
	}
	
	@Override
	protected void onSwitch(PlayerEntity user, World world)
	{
		super.onSwitch(user, world);
		IWingedPlayerComponent winged = UltraComponents.WINGED.get(user);
		if(winged.isPrimaryFiring())
			onPrimaryFireStart(world, user);
	}
	
	@Override
	public boolean canHoldUse()
	{
		return false;
	}
	
	@Override
	public Weapon getWeaponType()
	{
		return Weapon.NAILGUN;
	}
}
