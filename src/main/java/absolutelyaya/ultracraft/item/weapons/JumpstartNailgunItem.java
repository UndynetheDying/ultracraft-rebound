package absolutelyaya.ultracraft.item.weapons;

import absolutelyaya.ultracraft.client.GunCooldownManager;
import absolutelyaya.ultracraft.client.rendering.item.JumpstartNailgunRenderer;
import absolutelyaya.ultracraft.components.UltraComponents;
import absolutelyaya.ultracraft.components.player.IWingedPlayerComponent;
import absolutelyaya.ultracraft.registry.SoundRegistry;
import mod.azure.azurelib.animatable.GeoItem;
import mod.azure.azurelib.animatable.SingletonGeoAnimatable;
import mod.azure.azurelib.animatable.client.RenderProvider;
import mod.azure.azurelib.core.animatable.instance.AnimatableInstanceCache;
import mod.azure.azurelib.core.animatable.instance.SingletonAnimatableInstanceCache;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Vector2i;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class JumpstartNailgunItem extends AbstractNailgunItem
{
	final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
	final Supplier<Object> renderProvider = GeoItem.makeRenderer(this);
	boolean b;
	
	public JumpstartNailgunItem(Settings settings)
	{
		super(settings);
		SingletonGeoAnimatable.registerSyncedAnimatable(this);
	}
	
	@Override
	public boolean onPrimaryFire(World world, PlayerEntity user, Vec3d userVelocity)
	{
		if(isCanFirePrimary(user))
		{
			user.playSound(SoundRegistry.NAILGUN_FIRE, 1f, 1.5f + user.getRandom().nextFloat() * 0.1f);
			if(world.isClient)
			{
				super.onPrimaryFire(world, user, userVelocity);
				return true;
			}
			fireNail(world, user, userVelocity, false);
			super.onPrimaryFire(world, user, userVelocity);
			if(!UltraComponents.WINGED.get(user).isHasHookedEntity())
			{
				GunCooldownManager cdm = UltraComponents.WINGED.get(user).getGunCooldownManager();
				cdm.setCooldown(this, 1, GunCooldownManager.PRIMARY);
			}
			return true;
		}
		else
			return false;
	}
	
	@Override
	public void onAltFire(World world, PlayerEntity user)
	{
		user.playSound(SoundRegistry.NAILGUN_MAGNET_FIRE, 1f, 0.8f + user.getRandom().nextFloat() * 0.1f);
		super.onAltFire(world, user);
		//MagnetEntity magnet = MagnetEntity.spawn(user, user.getEyePos(), user.getRotationVector().multiply(1.5f));
		//world.spawnEntity(magnet);
		//TODO: spawn jumpstart entity
		IWingedPlayerComponent winged = UltraComponents.WINGED.get(user);
		if(!world.isClient)
		{
			if(winged.isPrimaryFiring())
				triggerAnim(user, GeoItem.getOrAssignId(user.getMainHandStack(), (ServerWorld)world), getControllerName(), b ? "alt_fire2" : "alt_fire2b");
			else
				triggerAnim(user, GeoItem.getOrAssignId(user.getMainHandStack(), (ServerWorld)world), getControllerName(), b ? "alt_fire" : "alt_fireb");
			b = !b;
		}
	}
	
	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand)
	{
		ItemStack stack = user.getStackInHand(hand);
		if(UltraComponents.WINGED.get(user).isHasHookedEntity())
			return TypedActionResult.fail(stack);
		return super.use(world, user, hand);
	}
	
	@Override
	public Vector2i getHUDTexture()
	{
		return new Vector2i(2, 2);
	}
	
	@Override
	String getControllerName()
	{
		return "jumpstart_nailgun";
	}
	
	@Override
	public void createRenderer(Consumer<Object> consumer)
	{
		consumer.accept(new RenderProvider() {
			private JumpstartNailgunRenderer renderer;
			
			@Override
			public BuiltinModelItemRenderer getCustomRenderer() {
				if (this.renderer == null)
					this.renderer = new JumpstartNailgunRenderer();
				return renderer;
			}
		});
	}
	
	@Override
	public Supplier<Object> getRenderProvider()
	{
		return renderProvider;
	}
	
	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache()
	{
		return cache;
	}
	
	@Override
	protected boolean shouldShowCooldown(ItemStack stack)
	{
		GunCooldownManager cdm = UltraComponents.WINGED.get(MinecraftClient.getInstance().player).getGunCooldownManager();
		return !cdm.isUsable(getCooldownClass(stack), GunCooldownManager.SECONDARY);
	}
	
	@Override
	protected int getWeaponCooldownStep(ItemStack stack)
	{
		GunCooldownManager cdm = UltraComponents.WINGED.get(MinecraftClient.getInstance().player).getGunCooldownManager();
		if(!cdm.isUsable(this, GunCooldownManager.SECONDARY))
			return (int)(cdm.getCooldownPercent(getCooldownClass(stack), GunCooldownManager.SECONDARY) * 14);
		return 0;
	}
	@Override
	public boolean isItemBarVisible(ItemStack stack)
	{
		return getNbt(stack, "jumpstart_cd") > 0;
	}
	
	@Override
	public int getItemBarStep(ItemStack stack)
	{
		int cd = getNbt(stack, "jumpstart_cd");
		if(cd > 0)
			return (int)((1f - cd / 200f) * 14);
		return 0;
	}
	
	@Override
	public int getItemBarColor(ItemStack stack)
	{
		return 0xdf2828;
	}
}
