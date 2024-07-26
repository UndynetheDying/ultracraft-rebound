package absolutelyaya.ultracraft.item.weapons;

import absolutelyaya.ultracraft.Ultracraft;
import absolutelyaya.ultracraft.accessor.LivingEntityAccessor;
import absolutelyaya.ultracraft.client.GunCooldownManager;
import absolutelyaya.ultracraft.client.rendering.item.SawedOnShotgunRenderer;
import absolutelyaya.ultracraft.components.UltraComponents;
import absolutelyaya.ultracraft.components.player.IWingedPlayerComponent;
import absolutelyaya.ultracraft.damage.DamageSources;
import absolutelyaya.ultracraft.data.StyleBonusManager;
import absolutelyaya.ultracraft.entity.demon.HideousMassEntity;
import absolutelyaya.ultracraft.entity.demon.HideousPart;
import absolutelyaya.ultracraft.entity.projectile.ChainsawEntity;
import absolutelyaya.ultracraft.item.ISelectionAwareItem;
import absolutelyaya.ultracraft.registry.SoundRegistry;
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
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class SawedOnShotgunItem extends AbstractShotgunItem
{
	static final Identifier MELEE_STYLE_BONUS = Ultracraft.identifier("saw_melee");
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
		IWingedPlayerComponent winged = UltraComponents.WINGED.get(user);
		GunCooldownManager cdm = winged.getGunCooldownManager();
		if(!cdm.isUsable(this, GunCooldownManager.PRIMARY) || !cdm.isUsable(this, GunCooldownManager.SECONDARY))
			return TypedActionResult.fail(stack);
		user.setCurrentHand(hand);
		if(!world.isClient)
		{
			if(!stack.getOrCreateNbt().contains("charging"))
			{
				stack.getOrCreateNbt().putBoolean("charging", true);
				triggerAnim(user, GeoItem.getOrAssignId(stack, (ServerWorld)world), getControllerName(), shouldFlip() ? "sawStartFlip" : "sawStart");
				winged.attachMovingSound("SawActive", SoundRegistry.SHOTGUN_SAW_ACTIVE.getId(), true, 0.6f);
			}
		}
		return TypedActionResult.pass(stack);
	}
	
	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected)
	{
		super.inventoryTick(stack, world, entity, slot, selected);
		selected = isMainHandstack(stack, entity);
		if(stack.getOrCreateNbt().contains("charging"))
		{
			if(!selected && stack.hasNbt())
			{
				stack.getNbt().remove("charging");
				if(world.isClient)
					approxUseTime = -1;
				else if(entity instanceof PlayerEntity player)
					triggerAnim(player, GeoItem.getOrAssignId(stack, (ServerWorld)world), getControllerName(), shouldFlip() ? "sawEndFlip" : "sawEnd");
				return;
			}
			Vec3d pos = entity.getEyePos(), forward = entity.getRotationVector();
			Box check = new Box(pos.x - 0.3f, pos.y - 0.3f, pos.z - 0.3f,
					pos.x + 0.3f, pos.y + 0.3f, pos.z + 0.3f)
								.stretch(forward.multiply(2f));
			DamageSource source = DamageSources.get(world, DamageSources.SAW_MELEE, entity);
			world.getOtherEntities(entity, check, i -> (
					(i instanceof LivingEntity living && !living.isDead()) || i instanceof BoatEntity || i instanceof HideousPart) &&
															   !(i instanceof HideousMassEntity))
					.forEach(i -> {
						i.damage(source, 0.5f);
						if(entity instanceof PlayerEntity player && i instanceof LivingEntity living && living.isDead())
							UltraComponents.STYLE.get(player).styleBonusGet(StyleBonusManager.getBonuses().get(MELEE_STYLE_BONUS));
					});
			BlockHitResult hit = world.raycast(new RaycastContext(entity.getEyePos(), entity.getEyePos().add(entity.getRotationVector().multiply(2f)),
					RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, entity));
			if(hit != null && !hit.getType().equals(HitResult.Type.MISS))
			{
				for (int i = 0; i < 4; i++)
				{
					BlockState state = world.getBlockState(hit.getBlockPos());
					Vec3d p = hit.getPos().addRandom(entity.getWorld().random, 0.2f);
					world.addParticle(new BlockStateParticleEffect(ParticleTypes.BLOCK, state),
							p.x, p.y, p.z, 0f, 0f, 0f);
					if(entity.age % 3 == 0)
						world.playSound(null, hit.getBlockPos(), state.getSoundGroup().getHitSound(),
								SoundCategory.BLOCKS, 1f, 0.9f + world.getRandom().nextFloat() * 0.2f);
				}
			}
		}
		if(world.isClient && stack.hasNbt() && stack.getNbt().contains("charging") &&
				   entity instanceof PlayerEntity player && player.equals(MinecraftClient.getInstance().player))
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
			
			ChainsawEntity saw = ChainsawEntity.spawn(user, world);
			Vec3d dir = player.getRotationVector();
			saw.setVelocity(dir.x, dir.y, dir.z, Math.max(useTime * 1.5f, 0.5f), 0f);
			world.spawnEntity(saw);
			
			triggerAnim(player, GeoItem.getOrAssignId(stack, (ServerWorld)world), getControllerName(), shouldFlip() ? "sawEndFlip" : "sawEnd");
			((LivingEntityAccessor)user).addRecoil(altRecoil * useTime);
			IWingedPlayerComponent winged = UltraComponents.WINGED.get(player);
			GunCooldownManager cdm = winged.getGunCooldownManager();
			cdm.setCooldown(this, 80, GunCooldownManager.SECONDARY);
		}
		if(nbt != null)
			nbt.remove("charging");
		approxUseTime = -1;
		if(user instanceof PlayerEntity player)
		{
			UltraComponents.WINGED.get(player).removeMovingSound("SawActive");
			player.playSound(SoundRegistry.SHOTGUN_SAW_END, 1f, 1f);
		}
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
					if(shouldFlip())
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
	
	boolean shouldFlip()
	{
		if(Ultracraft.SERVER_SIDE)
			return false;
		return MinecraftClient.getInstance().player.getMainArm().equals(Arm.LEFT);
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
	
	@Override
	protected boolean shouldShowCooldown(ItemStack stack)
	{
		GunCooldownManager cdm = UltraComponents.WINGED.get(MinecraftClient.getInstance().player).getGunCooldownManager();
		return super.shouldShowCooldown(stack) || !cdm.isUsable(getCooldownClass(stack), GunCooldownManager.SECONDARY);
	}
	
	@Override
	protected int getNbtDefault(String nbt)
	{
		return super.getNbtDefault(nbt);
	}
	
	@Override
	public void onSelect(PlayerEntity player)
	{
		super.onSelect(player);
		IWingedPlayerComponent winged = UltraComponents.WINGED.get(player);
		winged.attachMovingSound("SawIdle", SoundRegistry.SHOTGUN_SAW_IDLE.getId(), false, 0.6f);
	}
	
	@Override
	public void onUnselect(PlayerEntity player)
	{
		super.onUnselect(player);
		IWingedPlayerComponent winged = UltraComponents.WINGED.get(player);
		winged.removeMovingSound("SawIdle");
		winged.removeMovingSound("SawActive");
	}
}
