package absolutelyaya.ultracraft.client.gui;

import absolutelyaya.ultracraft.components.UltraComponents;
import absolutelyaya.ultracraft.components.player.IWingedPlayerComponent;
import absolutelyaya.ultracraft.dimension.LevelManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

public class LevelHUD
{
	static long timerStart;
	
	public void render(DrawContext context, float tickDelta)
	{
		PlayerEntity player = MinecraftClient.getInstance().player;
		IWingedPlayerComponent winged = UltraComponents.WINGED.get(player);
		Identifier level = winged.getCurrentLevel();
		if(player.getWorld().getRegistryKey().equals(LevelManager.WORLD_KEY) && level != null)
		{
		
		}
	}
	
	void renderTimer(DrawContext context, float tickDelta)
	{
	
	}
	
	public static void startTimer()
	{
		timerStart = System.currentTimeMillis();
	}
}
