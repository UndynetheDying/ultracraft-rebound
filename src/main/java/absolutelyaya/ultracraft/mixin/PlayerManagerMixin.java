package absolutelyaya.ultracraft.mixin;

import absolutelyaya.ultracraft.components.UltraComponents;
import absolutelyaya.ultracraft.block.mapping.CheckpointBlockEntity;
import absolutelyaya.ultracraft.components.player.IWingedPlayerComponent;
import absolutelyaya.ultracraft.dimension.LevelManager;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.RaycastContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin
{
	@Inject(method = "respawnPlayer", at = @At(value = "HEAD"))
	void onRespawn(ServerPlayerEntity player, boolean alive, CallbackInfoReturnable<ServerPlayerEntity> cir)
	{
		IWingedPlayerComponent winged = UltraComponents.WINGED.get(player);
		BlockPos pos = winged.getLastCheckpoint();
		if(pos != null && player.getWorld().getRegistryKey().equals(winged.getCheckpointDimension()) &&
				   player.getWorld().getBlockEntity(winged.getLastCheckpoint()) instanceof CheckpointBlockEntity checkpoint)
		{
			winged.removePerfect();
			checkpoint.onRespawn();
		}
	}
}
