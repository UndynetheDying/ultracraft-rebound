package absolutelyaya.ultracraft.registry;

import absolutelyaya.ultracraft.components.UltraComponents;
import absolutelyaya.ultracraft.accessor.LivingEntityAccessor;
import absolutelyaya.ultracraft.accessor.WingedPlayerEntity;
import absolutelyaya.ultracraft.block.IPunchableBlock;
import absolutelyaya.ultracraft.client.UltracraftClient;
import absolutelyaya.ultracraft.client.gui.screen.WingCustomizationScreen;
import absolutelyaya.ultracraft.compat.PlayerAnimator;
import absolutelyaya.ultracraft.components.player.IArmComponent;
import absolutelyaya.ultracraft.item.AbstractWeaponItem;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.block.BellBlock;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class KeybindRegistry
{
	public static final KeyBinding PUNCH = KeyBindingHelper.registerKeyBinding(
			new KeyBinding("key.ultracraft.punch", InputUtil.Type.KEYSYM,
					GLFW.GLFW_KEY_F, "category.ultracraft"));
	public static final KeyBinding PUNCH_FEEDBACKER = KeyBindingHelper.registerKeyBinding(
			new KeyBinding("key.ultracraft.punch_feedbacker", InputUtil.Type.KEYSYM,
					GLFW.GLFW_KEY_UNKNOWN, "category.ultracraft"));
	public static final KeyBinding PUNCH_KNUCKLE = KeyBindingHelper.registerKeyBinding(
			new KeyBinding("key.ultracraft.punch_knuckle", InputUtil.Type.KEYSYM,
					GLFW.GLFW_KEY_UNKNOWN, "category.ultracraft"));
	public static final KeyBinding HIGH_VELOCITY_TOGGLE = KeyBindingHelper.registerKeyBinding(
			new KeyBinding("key.ultracraft.hivel_toggle", InputUtil.Type.KEYSYM,
			GLFW.GLFW_KEY_V, "category.ultracraft"));
	public static final KeyBinding WING_CUSTOMIZATION = KeyBindingHelper.registerKeyBinding(
			new KeyBinding("key.ultracraft.wing_customization", InputUtil.Type.KEYSYM,
					GLFW.GLFW_KEY_APOSTROPHE, "category.ultracraft"));
	public static final KeyBinding WEAPON_CYCLE = KeyBindingHelper.registerKeyBinding(
			new KeyBinding("key.ultracraft.weapon_cycle", InputUtil.Type.KEYSYM,
					GLFW.GLFW_KEY_R, "category.ultracraft"));
	public static final KeyBinding ARM_CYCLE = KeyBindingHelper.registerKeyBinding(
			new KeyBinding("key.ultracraft.arm_cycle", InputUtil.Type.KEYSYM,
					GLFW.GLFW_KEY_G, "category.ultracraft"));
	
	public static final KeyBinding DASH = KeyBindingHelper.registerKeyBinding(
			new KeyBinding("key.ultracraft.dash", InputUtil.Type.KEYSYM,
					GLFW.GLFW_KEY_LEFT_SHIFT, "category.ultracraft"));
	public static final KeyBinding SLIDE = KeyBindingHelper.registerKeyBinding(
			new KeyBinding("key.ultracraft.slide", InputUtil.Type.KEYSYM,
					GLFW.GLFW_KEY_LEFT_CONTROL, "category.ultracraft"));
	
	static boolean hivelPressed = false, punchPressed = false, wingCustomizationPressed = false, weaponCyclePressed = false, armCyclePressed = false;
	
	public static void register()
	{
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while(HIGH_VELOCITY_TOGGLE.wasPressed() && !hivelPressed)
			{
				UltracraftClient.toggleHiVelEnabled();
				hivelPressed = true;
			}
			while(HIGH_VELOCITY_TOGGLE.wasPressed()); //remove stored hivel toggle presses
			hivelPressed = HIGH_VELOCITY_TOGGLE.isPressed();
		});
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			boolean isAnyPunchPressed = (PUNCH.isPressed() || PUNCH_FEEDBACKER.isPressed() || PUNCH_KNUCKLE.isPressed());
			if(punchPressed != isAnyPunchPressed)
			{
				PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
				buf.writeBoolean(isAnyPunchPressed);
				ClientPlayNetworking.send(PacketRegistry.PUNCH_PRESSED_PACKET_ID, buf);
			}
			while(PUNCH.wasPressed() && !punchPressed)
			{
				if(!performPunch(client, (byte)-1))
					continue;
				punchPressed = true;
			}
			while(PUNCH_FEEDBACKER.wasPressed() && !punchPressed)
			{
				if(!performPunch(client, (byte)0))
					continue;
				punchPressed = true;
			}
			while(PUNCH_KNUCKLE.wasPressed() && !punchPressed)
			{
				if(!performPunch(client, (byte)1))
					continue;
				punchPressed = true;
			}
			while(PUNCH.wasPressed() || PUNCH_FEEDBACKER.wasPressed() || PUNCH_KNUCKLE.wasPressed()); //remove stored punch presses
			punchPressed = isAnyPunchPressed;
		});
		ClientTickEvents.END_CLIENT_TICK.register(client ->
		{
			while (WING_CUSTOMIZATION.wasPressed() && !wingCustomizationPressed)
				client.setScreen(new WingCustomizationScreen(null));
			while(WING_CUSTOMIZATION.wasPressed()); //remove stored presses
			wingCustomizationPressed = WING_CUSTOMIZATION.isPressed();
		});
		ClientTickEvents.END_CLIENT_TICK.register(client ->
		{
			while (WEAPON_CYCLE.wasPressed() && !weaponCyclePressed)
			{
				ClientPlayerEntity player = client.player;
				if(player.getMainHandStack().getItem() instanceof AbstractWeaponItem)
					AbstractWeaponItem.cycleVariant(player);
			}
			while(WEAPON_CYCLE.wasPressed()); //remove stored presses
			weaponCyclePressed = WEAPON_CYCLE.isPressed();
		});
		ClientTickEvents.END_CLIENT_TICK.register(client ->
		{
			while (ARM_CYCLE.wasPressed() && !armCyclePressed)
			{
				ClientPlayerEntity player = client.player;
				UltraComponents.ARMS.get(player).cycleArms();
				client.gameRenderer.firstPersonRenderer.resetEquipProgress(Hand.OFF_HAND);
				PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
				ClientPlayNetworking.send(PacketRegistry.ARM_CYCLE_PACKET_ID, buf);
			}
			while(ARM_CYCLE.wasPressed()); //remove stored presses
			armCyclePressed = ARM_CYCLE.isPressed();
		});
	}
	
	static boolean performPunch(MinecraftClient client, byte arm)
	{
		ClientPlayerEntity player = client.player;
		if(player == null || player.isSpectator())
			return false;
		if(arm != -1)
		{
			IArmComponent armComponent = UltraComponents.ARMS.get(player);
			armComponent.setActiveArm(arm);
		}
		if(!((LivingEntityAccessor)player).punch())
			return false;
		if(player.isMainPlayer())
		{
			boolean flip = player.getMainArm().equals(Arm.LEFT);
			int anim;
			if(player instanceof WingedPlayerEntity winged && winged.isSliding())
				anim = flip ? PlayerAnimator.SLIDE_PUNCH_FLIPPED : PlayerAnimator.SLIDE_PUNCH;
			else
				anim = flip ? PlayerAnimator.PUNCH_FLIPPED : PlayerAnimator.PUNCH;
			PlayerAnimator.playAnimation(player, anim, 0, false);
		}
		
		HitResult crosshairTarget = client.crosshairTarget;
		Entity entity = null;
		if(crosshairTarget != null)
		{
			if(crosshairTarget.getType().equals(HitResult.Type.ENTITY))
				entity = ((EntityHitResult)crosshairTarget).getEntity();
			else if(crosshairTarget.getType().equals(HitResult.Type.BLOCK))
			{
				BlockHitResult hit = ((BlockHitResult)crosshairTarget);
				BlockState state = player.getWorld().getBlockState(hit.getBlockPos());
				if(state.getBlock() instanceof IPunchableBlock || state.isIn(TagRegistry.FRAGILE) || state.getBlock() instanceof BellBlock)
				{
					PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
					buf.writeBlockPos(hit.getBlockPos());
					buf.writeBoolean(false);
					ClientPlayNetworking.send(PacketRegistry.PUNCH_BLOCK_PACKET_ID, buf);
					if (state.getBlock() instanceof IPunchableBlock punchable && punchable.onPunch(player, hit.getBlockPos(), false))
						return true; //if punch interaction was successful, don't display break particles and stuff
				}
				Vec3d pos = hit.getPos();
				for (int i = 0; i < 6; i++)
					player.getWorld().addParticle(new BlockStateParticleEffect(ParticleTypes.BLOCK, state), pos.x, pos.y, pos.z, 0f, 0f, 0f);
				player.playSound(state.getSoundGroup().getHitSound(), 1f, 1f);
			}
		}
		boolean b = entity != null && !(entity instanceof ProjectileEntity);
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		buf.writeBoolean(b);
		if(b)
			buf.writeVarInt(entity.getId());
		buf.writeByte(arm);
		buf.writeVector3f(player.getVelocity().toVector3f());
		buf.writeBoolean(UltracraftClient.getConfig().showPunchArea);
		ClientPlayNetworking.send(PacketRegistry.PUNCH_PACKET_ID, buf);
		return true;
	}
}
