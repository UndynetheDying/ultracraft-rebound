package absolutelyaya.ultracraft.mixin;

import absolutelyaya.ultracraft.damage.DamageSources;
import absolutelyaya.ultracraft.registry.SoundRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BoatEntity.class)
public abstract class BoatEntityMixin extends Entity
{
	public BoatEntityMixin(EntityType<?> type, World world)
	{
		super(type, world);
	}
	
	@Inject(method = "damage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/vehicle/BoatEntity;discard()V"))
	void onDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir)
	{
		if(source.isOf(DamageSources.SAW_MELEE))
			playSound(SoundRegistry.I_SAWED, 1f, 1f);
	}
}
