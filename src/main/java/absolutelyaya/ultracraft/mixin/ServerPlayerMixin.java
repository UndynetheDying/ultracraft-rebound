package absolutelyaya.ultracraft.mixin;

import absolutelyaya.ultracraft.components.UltraComponents;
import absolutelyaya.ultracraft.cybergrind.CybergrindGame;
import absolutelyaya.ultracraft.cybergrind.CybergrindManager;
import absolutelyaya.ultracraft.dimension.LevelManager;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.authlib.GameProfile;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stat;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

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
	
	@Shadow public abstract boolean isCreative();
	
	@Inject(method = "worldChanged", at = @At("HEAD"))
	void onWorldChanged(ServerWorld origin, CallbackInfo ci)
	{
		UltraComponents.WINGED.get(this).setLastCheckpoint(null, null);
		if(origin.getRegistryKey() != null && !origin.getRegistryKey().equals(LevelManager.WORLD_KEY))
			return;
		UltraComponents.LEVEL_STATS.get(this).enterLevel(null, null);
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
		if(instance.getWorld().getRegistryKey().equals(LevelManager.WORLD_KEY) || UltraComponents.WINGED.get(instance).getLastCheckpoint() != null)
			return true;
		return original.call(instance);
	}
	
	@Inject(method = "changeGameMode", at = @At("TAIL"))
	void onSetGameMode(GameMode gameMode, CallbackInfoReturnable<Boolean> cir)
	{
		UltraComponents.LEVEL_STATS.get(this).setInvalid();
	}
}
