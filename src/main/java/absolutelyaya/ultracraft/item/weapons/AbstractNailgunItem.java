package absolutelyaya.ultracraft.item.weapons;

import absolutelyaya.ultracraft.components.UltraComponents;
import absolutelyaya.ultracraft.Weapon;
import absolutelyaya.ultracraft.components.player.IWingedPlayerComponent;
import absolutelyaya.ultracraft.entity.projectile.NailEntity;
import absolutelyaya.ultracraft.registry.EntityRegistry;
import absolutelyaya.ultracraft.registry.ItemRegistry;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import mod.azure.azurelib.animatable.GeoItem;
import mod.azure.azurelib.core.animation.AnimatableManager;
import mod.azure.azurelib.core.animation.AnimationController;
import mod.azure.azurelib.core.animation.RawAnimation;
import mod.azure.azurelib.core.object.PlayState;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class AbstractNailgunItem extends AbstractWeaponItem implements GeoItem
{
	final RawAnimation AnimationFireLoop = RawAnimation.begin().thenPlay("fire_loop");
	final RawAnimation AnimationFireStop = RawAnimation.begin().thenPlay("fire_stop");
	final RawAnimation AnimationAltFire = RawAnimation.begin().thenPlay("alt_fire");
	final RawAnimation AnimationAltFireB = RawAnimation.begin().thenPlay("alt_fireb");
	final RawAnimation AnimationAltFire2 = RawAnimation.begin().thenPlay("alt_fire2").thenLoop("fire_loop");
	final RawAnimation AnimationAltFire2B = RawAnimation.begin().thenPlay("alt_fire2b").thenLoop("fire_loop");
	
	public AbstractNailgunItem(Settings settings)
	{
		super(settings, 0.5f, 15f);
	}
	
	@Override
	public void onPrimaryFireStart(World world, PlayerEntity user, int slot)
	{
		super.onPrimaryFireStart(world, user, slot);
		if(!world.isClient)
			triggerAnim(user, GeoItem.getOrAssignId(user.getInventory().getStack(slot), (ServerWorld)world), getControllerName(), "fire_loop");
	}
	
	@Override
	public void onPrimaryFireStop(World world, PlayerEntity user, int slot)
	{
		super.onPrimaryFireStop(world, user, slot);
		if(!world.isClient)
			triggerAnim(user, GeoItem.getOrAssignId(user.getInventory().getStack(slot), (ServerWorld)world), getControllerName(), "fire_stop");
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
		selected = isMainHandstack(stack, entity);
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
				setNbt(stack, "heat", Math.min(heat + 3, 100));
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
	
	void fireNail(World world, PlayerEntity user, Vec3d userVelocity, boolean hot)
	{
		NailEntity nail = new NailEntity(EntityRegistry.NAIL, world);
		nail.setPosition(user.getEyePos().subtract(0, 0.25, 0).add(user.getRotationVector().rotateY((float)Math.toRadians(90))
																		   .multiply(user.getMainArm().equals(Arm.RIGHT) ? -0.3 : 0.3)));
		nail.setOwner(user);
		nail.setVelocity(user, user.getPitch(), user.getYaw(), 0f, 2.5f, 7.5f);
		nail.addVelocity(userVelocity);
		nail.setHot(hot);
		world.spawnEntity(nail);
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
										.triggerableAnim("alt_fire", AnimationAltFire)
										.triggerableAnim("alt_fireb", AnimationAltFireB)
										.triggerableAnim("alt_fire2", AnimationAltFire2)
										.triggerableAnim("alt_fire2b", AnimationAltFire2B));
	}
	
	@Override
	public int getNbtDefault(String nbt)
	{
		return switch (nbt)
		{
			case "nails" -> 100;
			case "magnets" -> 3;
			case "heatsinks" -> 2;
			default -> 0;
		};
	}
	
	@Override
	public boolean shouldAim()
	{
		return false;
	}
	
	@Override
	public void onBeforeSwitch(World world, PlayerEntity user, int newSlot)
	{
		super.onBeforeSwitch(world, user, newSlot);
		IWingedPlayerComponent winged = UltraComponents.WINGED.get(user);
		if(winged.isPrimaryFiring())
			onPrimaryFireStop(world, user, newSlot);
	}
	
	@Override
	public void onSwitch(World world, PlayerEntity user, int newSlot)
	{
		super.onSwitch(world, user, newSlot);
		IWingedPlayerComponent winged = UltraComponents.WINGED.get(user);
		if(winged.isPrimaryFiring())
			onPrimaryFireStart(world, user, newSlot);
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
	
	@Override
	protected void appendWeaponInfoTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context)
	{
		tooltip.add(Text.translatable("item.ultracraft.nailgun.lore1"));
		tooltip.add(Text.translatable(getTranslationKey() + ".lore1"));
		tooltip.add(Text.translatable(getTranslationKey() + ".lore2"));
	}
}
