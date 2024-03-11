package absolutelyaya.ultracraft.mixin;

import absolutelyaya.ultracraft.components.UltraComponents;
import absolutelyaya.ultracraft.components.player.IWingedPlayerComponent;
import absolutelyaya.ultracraft.cybergrind.CybergrindGame;
import absolutelyaya.ultracraft.cybergrind.CybergrindManager;
import absolutelyaya.ultracraft.dimension.LevelManager;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.authlib.GameProfile;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerMixin extends PlayerEntity
{
	public ServerPlayerMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile)
	{
		super(world, pos, yaw, gameProfile);
	}
	
	@Shadow public abstract void increaseStat(Stat<?> stat, int amount);
	
	@Shadow public abstract boolean isSpectator();
	
	@Shadow public abstract void onRecipeCrafted(Recipe<?> recipe, List<ItemStack> ingredients);
	
	@ModifyReturnValue(method = "getSpawnPointPosition", at = @At("RETURN"))
	BlockPos onGetSpawnPoint(BlockPos original)
	{
		IWingedPlayerComponent winged = UltraComponents.WINGED.get(this);
		if(winged.getLastCheckpoint() == null || winged.getCheckpointDimension() == null)
			return original;
		if(getWorld().getRegistryKey().equals(winged.getCheckpointDimension()))
			return winged.getLastCheckpoint();
		winged.setLastCheckpoint(null, null);
		return original;
	}
	
	@ModifyReturnValue(method = "getSpawnPointDimension", at = @At("RETURN"))
	RegistryKey<World> onGetSpawnDimension(RegistryKey<World> original)
	{
		IWingedPlayerComponent winged = UltraComponents.WINGED.get(this);
		if(winged.getLastCheckpoint() == null || winged.getCheckpointDimension() == null)
			return original;
		if(getWorld().getRegistryKey().equals(winged.getCheckpointDimension()))
			return winged.getCheckpointDimension();
		winged.setLastCheckpoint(null, null);
		return original;
	}
	
	@ModifyReturnValue(method = "getSpawnAngle", at = @At("RETURN"))
	float onGetSpawnAngle(float original)
	{
		IWingedPlayerComponent winged = UltraComponents.WINGED.get(this);
		if(winged.getLastCheckpoint() != null && winged.getCheckpointDimension() != null)
			return winged.getCheckpointRotation();
		return original;
	}
	
	@Inject(method = "worldChanged", at = @At("HEAD"))
	void onWorldChanged(ServerWorld origin, CallbackInfo ci)
	{
		if(origin.getRegistryKey() != null && !origin.getRegistryKey().equals(LevelManager.WORLD_KEY))
			return;
		UltraComponents.WINGED.get(this).enterLevel(null, null);
		CybergrindGame cybergrind = CybergrindManager.Instance.getActiveGame();
		if(cybergrind != null)
			cybergrind.removeParticipant(this);
	}
	
	@Inject(method = "onDeath", at = @At("HEAD"))
	void onDeath(DamageSource damageSource, CallbackInfo ci)
	{
		CybergrindGame cybergrind = CybergrindManager.Instance.getActiveGame();
		if(cybergrind != null)
			cybergrind.removeParticipant(this);
	}
	
	@WrapOperation(method = "copyFrom", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;isSpectator()Z"))
	boolean shouldKeepInventory(ServerPlayerEntity instance, Operation<Boolean> original)
	{
		if(instance.getWorld().getRegistryKey().equals(LevelManager.WORLD_KEY))
			return true;
		return original.call(instance);
	}
}
