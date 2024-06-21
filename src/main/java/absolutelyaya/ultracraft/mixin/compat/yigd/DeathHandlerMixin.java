package absolutelyaya.ultracraft.mixin.compat.yigd;

import absolutelyaya.ultracraft.components.UltraComponents;
import absolutelyaya.ultracraft.dimension.LevelManager;
import com.b1n_ry.yigd.DeathHandler;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DeathHandler.class)
public class DeathHandlerMixin
{
	@Inject(method = "onPlayerDeath", at = @At("HEAD"), cancellable = true)
	void onPlayerDeath(ServerPlayerEntity player, ServerWorld world, Vec3d pos, DamageSource deathSource, CallbackInfo ci)
	{
		//You don't drop items in levels or when you have an active checkpoint
		//Since this isn't true for every single player at every point time, the keep inventory gamerule isn't used for this
		//This mixin just prevents the mod from placing a grave and clearing a players inventory anyways
		if(player.getWorld().getRegistryKey().equals(LevelManager.WORLD_KEY) || UltraComponents.WINGED.get(player).getLastCheckpoint() != null)
			ci.cancel();
	}
}
