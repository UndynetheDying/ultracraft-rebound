package absolutelyaya.ultracraft.mixin.client;

import absolutelyaya.ultracraft.components.UltraComponents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerInventory.class)
public class PlayerInventoryMixin
{
	@Shadow @Final public PlayerEntity player;
	
	@Shadow public int selectedSlot;
	
	@Inject(method = "scrollInHotbar", at = @At("HEAD"))
	void onScrollHotbar(double scrollAmount, CallbackInfo ci)
	{
		int i = (int)Math.signum(scrollAmount);
		if(i == 0)
			return;
		int slot = (selectedSlot - i) % 9;
		if(slot < 0)
			slot += 9;
		UltraComponents.WINGED.get(player).onUpdateActiveSlot(selectedSlot, slot);
	}
}
