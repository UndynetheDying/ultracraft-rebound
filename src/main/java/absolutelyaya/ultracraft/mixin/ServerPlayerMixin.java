package absolutelyaya.ultracraft.mixin;

import absolutelyaya.ultracraft.components.UltraComponents;
import absolutelyaya.ultracraft.components.player.IWingedPlayerComponent;
import absolutelyaya.ultracraft.dimension.LevelManager;
import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stat;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerMixin extends PlayerEntity
{
	public ServerPlayerMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile)
	{
		super(world, pos, yaw, gameProfile);
	}
	
	@Shadow public abstract ServerWorld getServerWorld();
	
	@Shadow public abstract void increaseStat(Stat<?> stat, int amount);
	
	@Shadow public abstract boolean isSpectator();
	
	@Inject(method = "getSpawnPointPosition", at = @At("HEAD"), cancellable = true)
	void onGetSpawnPoint(CallbackInfoReturnable<BlockPos> cir)
	{
		IWingedPlayerComponent winged = UltraComponents.WINGED.get(this);
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
		IWingedPlayerComponent winged = UltraComponents.WINGED.get(this);
		if(winged.getLastCheckpoint() != null && winged.getCheckpointDimension() != null)
			cir.setReturnValue(winged.getCheckpointDimension());
	}
	
	@Inject(method = "worldChanged", at = @At("HEAD"))
	void onWorldChanged(ServerWorld origin, CallbackInfo ci)
	{
		if(!origin.getRegistryKey().equals(LevelManager.WORLD_KEY))
			return;
		UltraComponents.WINGED.get(this).setCurrentLevel(null);
	}
	
	@Redirect(method = "copyFrom", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;isSpectator()Z"))
	boolean shouldKeepInventory(ServerPlayerEntity instance)
	{
		if(instance.getWorld().getRegistryKey().equals(LevelManager.WORLD_KEY))
			return true;
		return isSpectator();
	}
}
