package absolutelyaya.ultracraft.mixin;

import absolutelyaya.ultracraft.components.UltraComponents;
import absolutelyaya.ultracraft.block.mapping.CheckpointBlockEntity;
import absolutelyaya.ultracraft.components.player.ILevelStatsComponent;
import absolutelyaya.ultracraft.components.player.IWingedPlayerComponent;
import absolutelyaya.ultracraft.dimension.LevelManager;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
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
			ILevelStatsComponent stats = UltraComponents.LEVEL_STATS.get(player);
			stats.onDeath();
			if(checkpoint.onRespawn() && stats.getCurrentLevelInstance() != null)
			{
				LevelManager.LevelInstance instance = LevelManager.Instance.getInstance(stats.getCurrentLevelInstance());
				if(instance != null)
					instance.restoreLastCheckpointKills();
			}
		}
	}
	
	@WrapOperation(method = "respawnPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;getSpawnPointPosition()Lnet/minecraft/util/math/BlockPos;"))
	BlockPos onGetSpawnPointPosition(ServerPlayerEntity player, Operation<BlockPos> original)
	{
		IWingedPlayerComponent winged = UltraComponents.WINGED.get(player);
		if(winged.getLastCheckpoint() == null || winged.getCheckpointDimension() == null)
			return original.call(player);
		if(player.getWorld().getRegistryKey().equals(winged.getCheckpointDimension()))
			return winged.getLastCheckpoint();
		winged.setLastCheckpoint(null, null);
		return original.call(player);
	}
	
	@WrapOperation(method = "respawnPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;getSpawnPointDimension()Lnet/minecraft/registry/RegistryKey;"))
	RegistryKey<World> onGetSpawnDimension(ServerPlayerEntity player, Operation<RegistryKey<World>> original)
	{
		IWingedPlayerComponent winged = UltraComponents.WINGED.get(player);
		if(winged.getLastCheckpoint() == null || winged.getCheckpointDimension() == null)
			return original.call(player);
		if(player.getWorld().getRegistryKey().equals(winged.getCheckpointDimension()))
			return winged.getCheckpointDimension();
		winged.setLastCheckpoint(null, null);
		return original.call(player);
	}
	
	@WrapOperation(method = "respawnPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;getSpawnAngle()F"))
	float onGetSpawnAngle(ServerPlayerEntity player, Operation<Float> original)
	{
		IWingedPlayerComponent winged = UltraComponents.WINGED.get(player);
		if(winged.getLastCheckpoint() != null && winged.getCheckpointDimension() != null)
			return winged.getCheckpointRotation();
		return original.call(player);
	}
}
