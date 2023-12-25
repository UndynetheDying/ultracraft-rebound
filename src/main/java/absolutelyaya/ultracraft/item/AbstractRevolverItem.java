package absolutelyaya.ultracraft.item;

import absolutelyaya.ultracraft.ServerHitscanHandler;
import absolutelyaya.ultracraft.UltraComponents;
import absolutelyaya.ultracraft.client.GunCooldownManager;
import absolutelyaya.ultracraft.damage.DamageSources;
import absolutelyaya.ultracraft.registry.ItemRegistry;
import absolutelyaya.ultracraft.registry.SoundRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import mod.azure.azurelib.animatable.GeoItem;
import mod.azure.azurelib.core.animation.Animation;
import mod.azure.azurelib.core.animation.RawAnimation;

public abstract class AbstractRevolverItem extends AbstractWeaponItem implements GeoItem
{
	boolean b; //toggled on every shot; decides purely which shot animation should be used to allow for rapid firing
	final RawAnimation AnimationStop = RawAnimation.begin().then("nothing", Animation.LoopType.LOOP);
	final RawAnimation AnimationCharge = RawAnimation.begin().thenPlay("charging").thenLoop("charged");
	final RawAnimation AnimationSpin = RawAnimation.begin().thenPlay("spinup").thenLoop("spinning");
	final RawAnimation AnimationAltSpin = RawAnimation.begin().thenLoop("spinning");
	final RawAnimation AnimationDischarge = RawAnimation.begin().thenPlay("discharge");
	final RawAnimation AnimationShot = RawAnimation.begin().thenPlay("shot");
	final RawAnimation AnimationShot2 = RawAnimation.begin().thenPlay("shot2");
	final RawAnimation AnimationSlabShot = RawAnimation.begin().thenPlay("slabshot").thenPlay("hammerpull");
	final RawAnimation AnimationHammerPull = RawAnimation.begin().thenPlay("hammerpull");
	final RawAnimation AnimationHammerPull2 = RawAnimation.begin().thenPlay("hammerpull2");
	
	public AbstractRevolverItem(Settings settings, float recoil, float altRecoil)
	{
		super(settings, recoil, altRecoil);
	}
	
	@Override
	public boolean onPrimaryFire(World world, PlayerEntity user, Vec3d userVelocity)
	{
		GunCooldownManager cdm = UltraComponents.WINGED_ENTITY.get(user).getGunCooldownManager();
		if(isCanFirePrimary(user))
		{
			if(world.isClient)
			{
				super.onPrimaryFire(world, user, userVelocity);
				return true;
			}
			if(isAlternate())
			{
				float pitch = 1f + (user.getRandom().nextFloat() - 0.5f) * 0.2f;
				world.playSound(null, user.getBlockPos(), SoundRegistry.SLAB_REVOLVER_FIRE, SoundCategory.PLAYERS, 0.9f, pitch);
				world.playSound(null, user.getBlockPos(), SoundRegistry.SLAB_REVOLVER_FIRE_DING, SoundCategory.PLAYERS, 0.8f, 1f);
				triggerAnim(user, GeoItem.getOrAssignId(user.getMainHandStack(), (ServerWorld)world), getControllerName(), "slabshot");
				setNbt(user.getMainHandStack(), getHammerId(), 0);
			}
			else
			{
				world.playSound(null, user.getBlockPos(), SoundRegistry.REVOLVER_FIRE, SoundCategory.PLAYERS, 0.75f,
						0.9f + (user.getRandom().nextFloat() - 0.5f) * 0.2f);
				triggerAnim(user, GeoItem.getOrAssignId(user.getMainHandStack(), (ServerWorld)world), getControllerName(), b ? "shot" : "shot2");
			}
			
			if(isAlternate())
				ServerHitscanHandler.makeBasicHitscan(user, ServerHitscanHandler.SLAB, 2 * getPrimaryDamage(), DamageSources.GUN)
						.semiPierce(2, getPrimaryDamage())
						.explosion(new ServerHitscanHandler.HitscanExplosionData(2f, 0f, 0f, true)).perform();
			else
				ServerHitscanHandler.performHitscan(user, ServerHitscanHandler.NORMAL, getPrimaryDamage());
			cdm.setCooldown(this, getPrimaryCooldown(), GunCooldownManager.PRIMARY);
			b = !b;
			return true;
		}
		else
			return false;
	}
	
	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected)
	{
		if(!(entity instanceof PlayerEntity player))
			return;
		super.inventoryTick(stack, world, entity, slot, selected);
		GunCooldownManager cdm = UltraComponents.WINGED_ENTITY.get(player).getGunCooldownManager();
		//Marksman Coin Tick
		int coins = getNbt(stack, "coins");
		if(coins < 4 && cdm.isUsable(this, GunCooldownManager.SECONDARY))
		{
			setNbt(stack, "coins", coins + 1);
			if(coins + 1 < 4)
				cdm.setCooldown(this, 200, GunCooldownManager.SECONDARY);
			player.playSound(SoundRegistry.REVOLVER_ALT_CHARGE, 0.1f, 1.75f);
		}
		//Sharpshooter Charges Tick
		int charges = getNbt(stack, "charges");
		if(charges < (isAlternate() ? 1 : 3) && cdm.isUsable(this, GunCooldownManager.TRITARY))
		{
			setNbt(stack, "charges", charges + 1);
			cdm.setCooldown(this, getSharpshooterRechargeTime(), GunCooldownManager.TRITARY);
			player.playSound(SoundRegistry.REVOLVER_ALT_CHARGE, 0.1f, 1.5f);
		}
	}
	
	@Override
	Item[] getVariants()
	{
		return new Item[]{ItemRegistry.ALTERNATE_PIERCER, ItemRegistry.ALTERNATE_MARKSMAN, ItemRegistry.ALTERNATE_SHARPSHOOTER};
	}
	
	@Override
	int getSwitchCooldown(ItemStack stack)
	{
		return 4;
	}
	
	@Override
	public Class<? extends AbstractWeaponItem> getCooldownClass()
	{
		return AbstractRevolverItem.class;
	}
	
	@Override
	public int getNbtDefault(String nbt)
	{
		return switch (nbt)
		{
			case "charges" -> isAlternate() ? 1 : 3;
			case "coins" -> 4;
			case "hammer1", "hammer2", "hammer3" -> 1;
			default -> 0;
		};
	}
	
	protected int getPrimaryCooldown()
	{
		return isAlternate() ? 32 : 9;
	}
	
	protected float getPrimaryDamage()
	{
		return isAlternate() ? 2.5f : 1f;
	}
	
	protected int getSharpshooterRechargeTime()
	{
		return isAlternate() ? 170 : 120;
	}
	
	protected boolean isAlternate()
	{
		return false;
	}
	
	protected String getHammerId()
	{
		return null;
	}
}
