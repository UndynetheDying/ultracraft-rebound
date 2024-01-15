package absolutelyaya.ultracraft.mixin.client;

import absolutelyaya.ultracraft.accessor.WingedPlayerEntity;
import absolutelyaya.ultracraft.block.HellObserverBlockEntity;
import absolutelyaya.ultracraft.block.TerminalBlockEntity;
import absolutelyaya.ultracraft.client.UltracraftClient;
import absolutelyaya.ultracraft.client.gui.screen.HellObserverScreen;
import absolutelyaya.ultracraft.client.gui.screen.WingCustomizationScreen;
import com.chocohead.mm.api.ClassTinkerers;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Arm;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.RaycastContext;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class CameraMixin
{
	@Shadow public abstract Vec3d getPos();
	
	@Shadow public abstract float getYaw();
	
	@Shadow @Final private Vector3f horizontalPlane;
	
	@Shadow @Final private Vector3f verticalPlane;
	
	@Shadow @Final private Vector3f diagonalPlane;
	
	@Shadow protected abstract void setPos(Vec3d pos);
	
	@Shadow private Vec3d pos;
	
	@Shadow protected abstract void setRotation(float yaw, float pitch);
	
	@Shadow private float yaw;
	
	@Shadow private float cameraY;
	@Shadow private float lastCameraY;
	@Shadow private boolean ready;
	@Shadow private BlockView area;
	@Shadow private Entity focusedEntity;
	@Shadow private boolean thirdPerson;
	@Shadow private float pitch;
	
	Vec3d curOffset;
	float curYaw, baseYaw, curPitch, shakeTime;
	boolean wasWingCustomizationOpen, wasFocusedOnTerminal;
	
	@Inject(method = "update", at = @At("HEAD"), cancellable = true)
	void onBeforeUpdate(BlockView area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo ci)
	{
		if(WingCustomizationScreen.MenuOpen)
		{
			this.ready = true;
			this.area = area;
			this.focusedEntity = focusedEntity;
			this.thirdPerson = thirdPerson;
			setRotation(focusedEntity.getYaw(tickDelta), focusedEntity.getPitch(tickDelta));
			setPos(new Vec3d(MathHelper.lerp(tickDelta, focusedEntity.prevX, focusedEntity.getX()),
					MathHelper.lerp(tickDelta, focusedEntity.prevY, focusedEntity.getY()) + (double)MathHelper.lerp(tickDelta, lastCameraY, cameraY),
					MathHelper.lerp(tickDelta, focusedEntity.prevZ, focusedEntity.getZ())));
			wingCustomizationUpdate(area, focusedEntity, tickDelta);
			ci.cancel();
		}
		else if(focusedEntity instanceof WingedPlayerEntity winged && winged.getFocusedTerminal() != null)
		{
			this.ready = true;
			this.area = area;
			this.focusedEntity = focusedEntity;
			this.thirdPerson = false;
			setRotation(focusedEntity.getYaw(tickDelta), focusedEntity.getPitch(tickDelta));
			setPos(new Vec3d(MathHelper.lerp(tickDelta, focusedEntity.prevX, focusedEntity.getX()),
					MathHelper.lerp(tickDelta, focusedEntity.prevY, focusedEntity.getY()) + (double)MathHelper.lerp(tickDelta, lastCameraY, cameraY),
					MathHelper.lerp(tickDelta, focusedEntity.prevZ, focusedEntity.getZ())));
			TerminalBlockEntity terminal = winged.getFocusedTerminal();
			terminalFocusUpdate(terminal, tickDelta);
			ci.cancel();
		}
		else if (MinecraftClient.getInstance().currentScreen instanceof HellObserverScreen screen && screen.isEditingArea())
		{
			this.ready = true;
			this.area = area;
			this.focusedEntity = focusedEntity;
			this.thirdPerson = false;
			setRotation(focusedEntity.getYaw(tickDelta), focusedEntity.getPitch(tickDelta));
			setPos(new Vec3d(MathHelper.lerp(tickDelta, focusedEntity.prevX, focusedEntity.getX()),
					MathHelper.lerp(tickDelta, focusedEntity.prevY, focusedEntity.getY()) + (double)MathHelper.lerp(tickDelta, lastCameraY, cameraY),
					MathHelper.lerp(tickDelta, focusedEntity.prevZ, focusedEntity.getZ())));
			if(area.getBlockEntity(screen.getObserverPos()) instanceof HellObserverBlockEntity observer)
				hellObserverAreaEditorUpdate(observer, screen, tickDelta);
			ci.cancel();
		}
		else
		{
			curOffset = getPos();
			curYaw = yaw;
			wasWingCustomizationOpen = false;
			wasFocusedOnTerminal = false;
		}
	}
	
	@Inject(method = "update", at = @At("TAIL"))
	void onUpdate(BlockView area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo ci)
	{
		if(!(focusedEntity instanceof PlayerEntity player))
			return;
		float f = UltracraftClient.getConfig().slideCamOffset / 100f;
		if(thirdPerson && f > 0f)
		{
			boolean flip = player.getMainArm().equals(Arm.LEFT);
			if(player instanceof WingedPlayerEntity winged && winged.isSliding())
			{
				Vec3d offset = rotationize(new Vec3d(1.5f * f, f, -1.5f * f * (flip ? -1 : 1)));
				HitResult hitResult = area.raycast(new RaycastContext(getPos(), getPos().add(offset), RaycastContext.ShapeType.VISUAL, RaycastContext.FluidHandling.NONE, focusedEntity));
				if(!hitResult.getType().equals(HitResult.Type.MISS))
					offset = hitResult.getPos().subtract(getPos()).add(rotationize(new Vec3d(0f, 0f, 0.2f * (flip ? -1 : 1))));
				setPos(new Vec3d(pos.x + offset.x, pos.y + offset.y, pos.z + offset.z));
			}
		}
		if(focusedEntity instanceof WingedPlayerEntity winged)
		{
			if(winged.getScreenShake() > 0.001f)
			{
				if(shakeTime == 0)
					shakeTime = focusedEntity.getWorld().getRandom().nextFloat() * 6f;
				applyScreenShake(tickDelta, winged.getScreenShake());
				winged.addScreenshake(-tickDelta * winged.getScreenShake() / 5f);
			}
			else
				shakeTime = 0f;
		}
	}
	
	@ModifyArg(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Camera;setPos(DDD)V"), index = 1)
	double modifyUpdateDeltaTime(double y)
	{
		EntityPose slide = ClassTinkerers.getEnum(EntityPose.class, "SLIDE");
		if(!(focusedEntity instanceof PlayerEntity player) || !player.getPose().equals(slide))
			return y;
		float tickDelta = MinecraftClient.getInstance().getTickDelta();
		return MathHelper.lerp(tickDelta, focusedEntity.prevY, focusedEntity.getY()) + focusedEntity.getEyeHeight(slide);
	}
	
	Vec3d rotationize(Vec3d vec) //aka apply rotation
	{
		double x = horizontalPlane.x() * vec.x + verticalPlane.x() * vec.y + diagonalPlane.x() * vec.z;
		double y = horizontalPlane.y() * vec.x + verticalPlane.y() * vec.y + diagonalPlane.y() * vec.z;
		double z = horizontalPlane.z() * vec.x + verticalPlane.z() * vec.y + diagonalPlane.z() * vec.z;
		return new Vec3d(x, y, z);
	}
	
	void wingCustomizationUpdate(BlockView area, Entity focusedEntity, float tickDelta)
	{
		Vec3d offset = rotationize(WingCustomizationScreen.Instance.getCameraOffset());
		HitResult hitResult = area.raycast(new RaycastContext(getPos(), getPos().add(offset), RaycastContext.ShapeType.VISUAL, RaycastContext.FluidHandling.NONE, focusedEntity));
		if(!hitResult.getType().equals(HitResult.Type.MISS))
			offset = hitResult.getPos().subtract(getPos());
		
		if(!wasWingCustomizationOpen) //set initial transform when opening Menu
		{
			baseYaw = yaw;
			curYaw = yaw + WingCustomizationScreen.Instance.getCameraRotation();
			curOffset = new Vec3d(offset.x, offset.y, offset.z);
			wasWingCustomizationOpen = true;
		}
		else //lerp towards target transform
		{
			setRotation(curYaw = MathHelper.lerp(tickDelta / 10f, curYaw, yaw + WingCustomizationScreen.Instance.getCameraRotation()), 0f);
			curOffset = curOffset.lerp(offset, tickDelta / 10);
			offset = curOffset.rotateY((float)Math.toRadians(-(yaw - baseYaw)));
			setPos(new Vec3d(pos.x + offset.x, pos.y + offset.y, pos.z + offset.z));
		}
	}
	
	void terminalFocusUpdate(TerminalBlockEntity terminal, float tickDelta)
	{
		Vec3d pos = terminal.getPos().toCenterPos();
		Vec3d offset = pos.add(terminal.getCamOffset());
		
		if(!wasFocusedOnTerminal) //set initial transform when opening Menu
		{
			curYaw = yaw;
			curOffset = getPos();
			wasFocusedOnTerminal = true;
		}
		else //lerp towards target transform
		{
			float f = MathHelper.DEGREES_PER_RADIAN;
			Vec3d dir = pos.subtract(offset);
			
			double horizontalLength = Math.sqrt(dir.x * dir.x + dir.z * dir.z);
			curPitch = MathHelper.lerpAngleDegrees(tickDelta / 4f, curPitch, (float)(-(MathHelper.atan2(dir.y, horizontalLength) * f)));
			setRotation(curYaw = MathHelper.lerpAngleDegrees(tickDelta / 4f, curYaw,
					(float)(MathHelper.atan2(dir.z, dir.x) * f) - 90.0f), curPitch);
			curOffset = curOffset.lerp(offset, tickDelta / 4f);
			setPos(curOffset);
		}
		if(focusedEntity instanceof WingedPlayerEntity winged && focusedEntity.squaredDistanceTo(winged.getFocusedTerminal().getPos().toCenterPos()) > 4*4)
			winged.setFocusedTerminal(null);
	}
	
	void hellObserverAreaEditorUpdate(HellObserverBlockEntity observer, HellObserverScreen screen, float tickDelta)
	{
		Vec3d pos = observer.getPos().toCenterPos();
		Vec3d offset = pos.add(observer.getCamOffset(screen.getCamRot()));
		
		if(!wasFocusedOnTerminal) //set initial transform when opening Menu
		{
			curYaw = yaw;
			curOffset = getPos();
			wasFocusedOnTerminal = true;
		}
		else //lerp towards target transform
		{
			float f = MathHelper.DEGREES_PER_RADIAN;
			Vec3d dir = screen.getObserverPos().add(observer.getCheckOffset()).toCenterPos().subtract(offset);
			
			double horizontalLength = Math.sqrt(dir.x * dir.x + dir.z * dir.z);
			curPitch = MathHelper.lerpAngleDegrees(tickDelta / 4f, curPitch, (float)(-(MathHelper.atan2(dir.y, horizontalLength) * f)));
			setRotation(curYaw = MathHelper.lerpAngleDegrees(tickDelta / 4f, curYaw,
					(float)(MathHelper.atan2(dir.z, dir.x) * f) - 90.0f), curPitch);
			curOffset = curOffset.lerp(offset, tickDelta / 4f);
			setPos(curOffset);
		}
	}
	
	void applyScreenShake(float tickDelta, float strength)
	{
		float delta = MinecraftClient.getInstance().getLastFrameDuration();
		shakeTime += delta + strength * 0.65f;
		setRotation(yaw + MathHelper.lerpAngleDegrees(delta * 4f, 0,
						(float)Math.sin(shakeTime + strength * 2.13f) * strength),
				pitch + MathHelper.lerpAngleDegrees(delta * 2f, 0,
						(float)Math.sin(shakeTime + 1.43f + strength * 1.71f) * strength));
		Vec3d dir = new Vec3d(0f, 0f, -1).rotateX((float)Math.toRadians(-pitch)).rotateY((float)Math.toRadians(-yaw));
		setPos(pos.add(dir.multiply(strength / 4f)));
	}
}
