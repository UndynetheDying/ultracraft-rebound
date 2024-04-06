package absolutelyaya.ultracraft.mixin;

import absolutelyaya.ultracraft.Ultracraft;
import absolutelyaya.ultracraft.accessor.ServerWorldAccessor;
import absolutelyaya.ultracraft.block.PortalBlock;
import absolutelyaya.ultracraft.block.mapping.RoomBlockEntity;
import absolutelyaya.ultracraft.components.UltraComponents;
import absolutelyaya.ultracraft.components.world.IDimensionDataComponent;
import absolutelyaya.ultracraft.entity.demon.HideousPart;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerWorld.class)
public class ServerWorldMixin implements ServerWorldAccessor
{
	Int2ObjectMap<HideousPart> hideousParts = new Int2ObjectOpenHashMap<>();
	
	@Inject(method = "tickEntity", at = @At("HEAD"), cancellable = true)
	void onTickEntity(Entity entity, CallbackInfo ci)
	{
		if(Ultracraft.isTimeFrozen())
			ci.cancel();
	}
	
	@Override
	public Int2ObjectMap<HideousPart> getHideousParts()
	{
		return hideousParts;
	}
	
	@ModifyReturnValue(method = "getDragonPart", at = @At("RETURN"))
	Entity onGetDragonPart(Entity original, int id)
	{
		if(original == null)
			return hideousParts.get(id);
		return original;
	}
}
