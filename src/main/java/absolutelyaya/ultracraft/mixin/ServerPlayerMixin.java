package absolutelyaya.ultracraft.mixin;

import absolutelyaya.ultracraft.UltraComponents;
import absolutelyaya.ultracraft.components.player.IWingedPlayerComponent;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerMixin
{
	@Shadow public abstract ServerWorld getServerWorld();
	
	@Inject(method = "getSpawnPointPosition", at = @At("HEAD"), cancellable = true)
	void onGetSpawnPoint(CallbackInfoReturnable<BlockPos> cir)
	{
		IWingedPlayerComponent winged = UltraComponents.WINGED_ENTITY.get(this);
		if(winged.getLastCheckpoint() != null)
		{
			if(getServerWorld().getRegistryKey().equals(winged.getCheckpointDimension()))
				cir.setReturnValue(winged.getLastCheckpoint());
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
