package absolutelyaya.ultracraft.mixin;

import absolutelyaya.ultracraft.components.UltraComponents;
import absolutelyaya.ultracraft.components.player.IWingedPlayerComponent;
import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerMixin extends PlayerEntity
{
	public ServerPlayerMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile)
	{
		super(world, pos, yaw, gameProfile);
	}
	
	@Shadow public abstract ServerWorld getServerWorld();
	
	@Inject(method = "getSpawnPointPosition", at = @At("HEAD"), cancellable = true)
	void onGetSpawnPoint(CallbackInfoReturnable<BlockPos> cir)
	{
		IWingedPlayerComponent winged = UltraComponents.WINGED_ENTITY.get(this);
		if(winged.getLastCheckpoint() != null)
		{
			if(getServerWorld().getRegistryKey().equals(winged.getCheckpointDimension()))
			{
				BlockPos pos = winged.getLastCheckpoint();
				BlockHitResult hit = getServerWorld().raycast(new RaycastContext(pos.toCenterPos(), pos.add(0, -32, 0).toCenterPos(),
						RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, this));
				cir.setReturnValue(BlockPos.ofFloored(hit.getPos().add(0f, 0.1f, 0f)));
			}
			else
				winged.setLastCheckpoint(null, null);
		}
	}
	
	@Inject(method = "getSpawnPointDimension", at = @At("HEAD"), cancellable = true)
	void onGetSpawnDimension(CallbackInfoReturnable<RegistryKey<World>> cir)
	{
		IWingedPlayerComponent winged = UltraComponents.WINGED_ENTITY.get(this);
		if(winged.getLastCheckpoint() != null && winged.getCheckpointDimension() != null)
			cir.setReturnValue(winged.getCheckpointDimension());
	}
}
