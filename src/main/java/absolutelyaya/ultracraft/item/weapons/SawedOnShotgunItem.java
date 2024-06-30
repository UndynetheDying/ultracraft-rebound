package absolutelyaya.ultracraft.item.weapons;

import absolutelyaya.ultracraft.Ultracraft;
import absolutelyaya.ultracraft.accessor.LivingEntityAccessor;
import absolutelyaya.ultracraft.client.GunCooldownManager;
import absolutelyaya.ultracraft.client.rendering.item.SawedOnShotgunRenderer;
import absolutelyaya.ultracraft.components.UltraComponents;
import mod.azure.azurelib.animatable.GeoItem;
import mod.azure.azurelib.animatable.SingletonGeoAnimatable;
import mod.azure.azurelib.animatable.client.RenderProvider;
import mod.azure.azurelib.core.animatable.GeoAnimatable;
import mod.azure.azurelib.core.animatable.instance.AnimatableInstanceCache;
import mod.azure.azurelib.core.animation.AnimatableManager;
import mod.azure.azurelib.core.animation.AnimationController;
import mod.azure.azurelib.core.animation.RawAnimation;
import mod.azure.azurelib.core.object.PlayState;
import mod.azure.azurelib.util.AzureLibUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class SawedOnShotgunItem extends AbstractShotgunItem
{
	protected int approxUseTime = -1;
	private final AnimatableInstanceCache cache = AzureLibUtil.createInstanceCache(this);
	private final Supplier<Object> renderProvider = GeoItem.makeRenderer(this);
	final RawAnimation AnimationSawStart = RawAnimation.begin().thenPlay("saw_start").thenLoop("saw_loop");
	final RawAnimation AnimationSawStartFlip = RawAnimation.begin().thenPlay("saw_start_flip").thenLoop("saw_loop_flip");
	final RawAnimation AnimationSawEnd = RawAnimation.begin().thenPlay("saw_end");
	final RawAnimation AnimationSawEndFlip = RawAnimation.begin().thenPlay("saw_end_flip");
	final RawAnimation AnimationFlip = RawAnimation.begin().thenPlay("saw_flip");
	
	public SawedOnShotgunItem(Settings settings)
	{
		super(settings, 45f, 25f);
		SingletonGeoAnimatable.registerSyncedAnimatable(this);
	}
	
	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand)
	{
		ItemStack stack = user.getStackInHand(hand);
		if(hand.equals(Hand.OFF_HAND))
			return TypedActionResult.fail(stack);
		GunCooldownManager cdm = UltraComponents.WINGED.get(user).getGunCooldownManager();
		if(!cdm.isUsable(this, 0))
			return TypedActionResult.fail(stack);
		user.setCurrentHand(hand);
		if(!world.isClient && stack.hasNbt() && !stack.getNbt().contains("charging"))
		{
			stack.getOrCreateNbt().putBoolean("charging", true);
			triggerAnim(user, GeoItem.getOrAssignId(stack, (ServerWorld)world), getControllerName(), shouldFlip(user) ? "sawStartFlip" : "sawStart");
		}
		return TypedActionResult.pass(stack);
	}
	
	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected)
	{
		super.inventoryTick(stack, world, entity, slot, selected);
		selected = isMainHandstack(stack, entity);
		if(!selected && stack.hasNbt() && stack.getNbt().contains("charging"))
		{
			stack.getNbt().remove("charging");
			if(world.isClient)
				approxUseTime = -1;
			else if(entity instanceof PlayerEntity player)
				triggerAnim(player, GeoItem.getOrAssignId(stack, (ServerWorld)world), getControllerName(), shouldFlip(player) ? "sawEndFlip" : "sawEnd");
		}
		if(world.isClient && stack.hasNbt() && stack.getNbt().contains("charging") &&
				   entity instanceof ClientPlayerEntity player && player.equals(MinecraftClient.getInstance().player))
			approxUseTime++;
	}
	
	@Override
	public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks)
	{
		if(remainingUseTicks == 999)
			return; //pretty much definitely a variant swap - don't fire saw
		NbtCompound nbt = stack.getNbt();
		if(!world.isClient && user instanceof PlayerEntity player)
		{
			float useTime = 1f - MathHelper.clamp(Math.max(remainingUseTicks, 0) / 30f, 0f, 1f);
			//player.getItemCooldownManager().set(this, 50);
			//EjectedCoreEntity bullet = EjectedCoreEntity.spawn(user, world);
			//Vec3d dir = new Vec3d(0f, 0f, 1f);
			//dir = dir.rotateX((float)Math.toRadians(-user.getPitch()));
			//dir = dir.rotateY((float)Math.toRadians(-user.getHeadYaw()));
			//bullet.setVelocity(dir.x, dir.y + ((1f - useTime) * 0.5 + 0.05f), dir.z, Math.max(useTime * 1.25f, 0.25f), 0f);
			//Vec3d vel = bullet.getVelocity();
			//world.addParticle(ParticleTypes.SMOKE, bullet.getX(), bullet.getY(), bullet.getZ(), vel.x, vel.y, vel.z);
			//bullet.setNoGravity(true);
			//world.spawnEntity(bullet);
			triggerAnim(player, GeoItem.getOrAssignId(stack, (ServerWorld)world), getControllerName(), shouldFlip(player) ? "sawEndFlip" : "sawEnd");
			((LivingEntityAccessor)user).addRecoil(altRecoil * useTime);
		}
		if(nbt != null)
			nbt.remove("charging");
		approxUseTime = -1;
	}
	
	@Override
	public boolean isUsedOnRelease(ItemStack stack)
	{
		return true;
	}
	
	@Override
	protected boolean isCanFirePrimary(PlayerEntity user)
	{
		ItemStack stack = user.getMainHandStack();
		if(stack.hasNbt() && stack.getNbt().contains("charging"))
			return false;
		return super.isCanFirePrimary(user);
	}
	
	public int getPelletCount(ItemStack stack)
	{
		return 12;
	}
	
	@Override
	public int getMaxUseTime(ItemStack stack)
	{
		return 20;
	}
	
	public int getApproxUseTime()
	{
		return approxUseTime;
	}
	
	@Override
	public Vector2i getHUDTexture()
	{
		return new Vector2i(2, 1);
	}
	
	@Override
	String getControllerName()
	{
		return "SawShotgun";
	}
	
	@Override
	public void createRenderer(Consumer<Object> consumer)
	{
		consumer.accept(new RenderProvider() {
			private SawedOnShotgunRenderer renderer;
			
			@Override
			public BuiltinModelItemRenderer getCustomRenderer() {
				if (this.renderer == null)
					this.renderer = new SawedOnShotgunRenderer();
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
	public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar)
	{
		controllerRegistrar.add(new AnimationController<>(this, getControllerName(), 1, state -> PlayState.STOP)
										.triggerableAnim("switch", AnimationSwitch)
										.triggerableAnim("switch2", AnimationSwitch2)
										.triggerableAnim("shot", AnimationShotCore)
										.triggerableAnim("shot2", AnimationShotCore2)
										.triggerableAnim("sawStart", AnimationSawStart)
										.triggerableAnim("sawStartFlip", AnimationSawStartFlip)
										.triggerableAnim("sawEnd", AnimationSawEnd)
										.triggerableAnim("sawEndFlip", AnimationSawEndFlip)
										.setSoundKeyframeHandler(this::handleAnimSound),
				new AnimationController<GeoAnimatable>(this, "saw", 1, state -> {
					if(shouldFlip(MinecraftClient.getInstance().player))
					{
						state.setAnimation(AnimationFlip);
						return PlayState.CONTINUE;
					}
					else
						return PlayState.STOP;
				}));
	}
	
	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache()
	{
		return cache;
	}
	
	boolean shouldFlip(PlayerEntity player)
	{
		if(player == null)
			return false;
		return player.getMainArm().equals(Arm.LEFT);
	}
	
	@Override
	protected void appendWeaponInfoTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context)
	{
		super.appendWeaponInfoTooltip(stack, world, tooltip, context);
		tooltip.add(Text.translatable(getTranslationKey() + ".lore2"));
	}
	
	@Override
	public int getItemBarColor(ItemStack stack)
	{
		if(Ultracraft.SERVER_SIDE)
			return 0xdf2828;
		GunCooldownManager cdm = UltraComponents.WINGED.get(MinecraftClient.getInstance().player).getGunCooldownManager();
		if(cdm.isUsable(this, GunCooldownManager.PRIMARY))
			return 0xdfb728;
		return 0xdf2828;
	}
	
	@Override
	protected int getWeaponCooldownStep(ItemStack stack)
	{
		GunCooldownManager cdm = UltraComponents.WINGED.get(MinecraftClient.getInstance().player).getGunCooldownManager();
		if(!cdm.isUsable(this, GunCooldownManager.PRIMARY))
			return (int)(cdm.getCooldownPercent(getCooldownClass(stack), GunCooldownManager.PRIMARY) * 14);
		else
			return (int)((1f - cdm.getCooldownPercent(getCooldownClass(stack), GunCooldownManager.SECONDARY)) * 14);
	}
}
