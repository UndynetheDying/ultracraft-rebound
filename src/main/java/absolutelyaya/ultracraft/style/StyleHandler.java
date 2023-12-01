package absolutelyaya.ultracraft.style;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

public class StyleHandler
{
	//TODO: track staleness
	public static void applyStyleBonus(PlayerEntity player, StyleBonus bonus)
	{
		player.sendMessage(Text.of("style bonus get!! " + bonus.translationKey));
	}
}
