package absolutelyaya.ultracraft.client.rendering.item;

import absolutelyaya.ultracraft.components.UltraComponents;
import absolutelyaya.ultracraft.Ultracraft;
import absolutelyaya.ultracraft.client.GunCooldownManager;
import absolutelyaya.ultracraft.item.weapons.PumpShotgunItem;
import absolutelyaya.ultracraft.registry.ItemRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import mod.azure.azurelib.model.DefaultedItemGeoModel;
import mod.azure.azurelib.renderer.GeoItemRenderer;

public class PumpShotgunRenderer extends GeoItemRenderer<PumpShotgunItem>
{
	public PumpShotgunRenderer()
	{
		super(new DefaultedItemGeoModel<>(Ultracraft.identifier("shotgun")));
	}
	
	@Override
	public Identifier getTextureLocation(PumpShotgunItem animatable)
	{
		String tex = "textures/item/pump_shotgun";
		GunCooldownManager cdm = UltraComponents.WINGED.get(MinecraftClient.getInstance().player).getGunCooldownManager();
		float primaryCD = cdm.getCooldownPercent(animatable, 0);
		if(primaryCD > 0.6f)
			return Ultracraft.texIdentifier(tex + 0);
		if(primaryCD > 0.4f)
			return Ultracraft.texIdentifier(tex);
		
		int charge = 0;
		PlayerEntity player = MinecraftClient.getInstance().player;
		if(player != null && player.getMainHandStack().isOf(ItemRegistry.PUMP_SHOTGUN) && player.getMainHandStack().hasNbt())
			charge = player.getMainHandStack().getNbt().getInt("charge");
		if(charge == 3)
		{
			if(player.age % 6 > 2)
				return Ultracraft.texIdentifier(tex + 4);
			else
				return Ultracraft.texIdentifier(tex + 3);
		}
		else if(charge == 2)
			return Ultracraft.texIdentifier(tex + 2);
		else if(charge == 1)
			return Ultracraft.texIdentifier(tex + 1);
		
		return Ultracraft.texIdentifier(tex);
	}
}
