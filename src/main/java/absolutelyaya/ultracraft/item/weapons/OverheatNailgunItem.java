package absolutelyaya.ultracraft.item.weapons;

import absolutelyaya.ultracraft.components.UltraComponents;
import absolutelyaya.ultracraft.client.GunCooldownManager;
import absolutelyaya.ultracraft.client.rendering.item.OverheatNailgunRenderer;
import absolutelyaya.ultracraft.registry.SoundRegistry;
import mod.azure.azurelib.animatable.GeoItem;
import mod.azure.azurelib.animatable.SingletonGeoAnimatable;
import mod.azure.azurelib.animatable.client.RenderProvider;
import mod.azure.azurelib.core.animatable.instance.AnimatableInstanceCache;
import mod.azure.azurelib.core.animatable.instance.SingletonAnimatableInstanceCache;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Vector2i;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class OverheatNailgunItem extends AbstractNailgunItem
{
	final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
	final Supplier<Object> renderProvider = GeoItem.makeRenderer(this);
	
	public OverheatNailgunItem(Settings settings)
	{
		super(settings);
		SingletonGeoAnimatable.registerSyncedAnimatable(this);
	}
	
	@Override
	public boolean onPrimaryFire(World world, PlayerEntity user, Vec3d userVelocity)
	{
		ItemStack stack = user.getMainHandStack();
		if(isCanFirePrimary(user))
		{
			boolean heatsinkActive = getNbt(stack, "heatsinking") == 1;
			GunCooldownManager cdm = UltraComponents.WINGED.get(user).getGunCooldownManager();
			user.playSound(SoundRegistry.NAILGUN_FIRE, 1f, 1.5f + user.getRandom().nextFloat() * 0.1f);
			if(world.isClient)
			{
				super.onPrimaryFire(world, user, userVelocity);
				return true;
			}
			int heat = getNbt(stack, "heat");
			if(!heatsinkActive)
			{
				fireNail(world, user, userVelocity, false);
				if(getNbt(stack, "heatsinks") == 0)
					cdm.setCooldown(this, 8, GunCooldownManager.PRIMARY);
				else
					cdm.setCooldown(this, (int)Math.floor(heat / 25f), GunCooldownManager.PRIMARY);
			}
			else
			{
				for (int i = 0; i < 5; i++)
					fireNail(world, user, userVelocity, true);
				if(heat > 0)
					setNbt(stack, "heat", Math.max(heat - 5, 0));
				else
					setNbt(stack, "heatsinking", 0);
			}
			super.onPrimaryFire(world, user, userVelocity);
			return true;
		}
		else
			return false;
	}
	
	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand)
	{
		ItemStack stack = user.getStackInHand(hand);
		int sinks = getNbt(stack, "heatsinks");
		if(sinks <= 0 || getNbt(stack, "heatsinking") == 1 || getNbt(stack, "heat") <= 0)
			return TypedActionResult.fail(stack);
		setNbt(stack, "heatsinking", 1);
		setNbt(stack, "heatsinks", sinks - 1);
		if(getNbt(stack, "heatsink_cd") == -1)
			setNbt(stack, "heatsink_cd", 160);
		return super.use(world, user, hand);
	}
	
	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected)
	{
		super.inventoryTick(stack, world, entity, slot, selected);
	}
	
	@Override
	public Vector2i getHUDTexture()
	{
		return new Vector2i(1, 2);
	}
	
	@Override
	String getControllerName()
	{
		return "overheat_nailgun";
	}
	
	@Override
	public void createRenderer(Consumer<Object> consumer)
	{
		consumer.accept(new RenderProvider() {
			private OverheatNailgunRenderer renderer;
			
			@Override
			public BuiltinModelItemRenderer getCustomRenderer() {
				if (this.renderer == null)
					this.renderer = new OverheatNailgunRenderer();
				
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
	public String getTopOverlayString(ItemStack stack)
	{
		return Formatting.GOLD + String.valueOf(getNbt(stack, "heat"));
	}
	
	@Override
	public String getCountString(ItemStack stack)
	{
		return Formatting.AQUA + String.valueOf(getNbt(stack, "heatsinks"));
	}
	
	@Override
	public boolean isItemBarVisible(ItemStack stack)
	{
		return shouldShowCooldown(stack) || getNbt(stack, "heatsink_cd") > 0;
	}
	
	@Override
	public int getItemBarStep(ItemStack stack)
	{
		int cd = getNbt(stack, "heatsink_cd");
		if(cd > 0)
			return (int)((1f - cd / 160f) * 14);
		return super.getItemBarStep(stack);
	}
	
	@Override
	protected boolean shouldShowCooldown(ItemStack stack)
	{
		GunCooldownManager cdm = UltraComponents.WINGED.get(MinecraftClient.getInstance().player).getGunCooldownManager();
		return cdm.getCooldown(getCooldownClass(stack), GunCooldownManager.PRIMARY) > 4;
	}
	
	@Override
	public int getItemBarColor(ItemStack stack)
	{
		return 0x28df53;
	}
}
