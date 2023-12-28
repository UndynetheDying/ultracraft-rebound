package absolutelyaya.ultracraft.client.gui.terminal;

import absolutelyaya.ultracraft.Weapon;
import absolutelyaya.ultracraft.block.TerminalBlockEntity;
import absolutelyaya.ultracraft.client.gui.terminal.elements.Button;
import absolutelyaya.ultracraft.client.gui.terminal.elements.Tab;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import org.joml.Vector2i;

public class LoadoutTab extends Tab
{
	protected Button returnButton = new Button(Button.RETURN_LABEL, new Vector2i(48, 95 -textRenderer.fontHeight), "weapons", 0, true);
	protected Button nextButton = new Button(">", new Vector2i(49 + returnButton.getSize().x / 3 * 2 + 1, 95 -textRenderer.fontHeight), "next", 0, true);
	protected Button lastButton = new Button("<", new Vector2i(48 - returnButton.getSize().x / 3 * 2 - 1, 95 -textRenderer.fontHeight), "last", 0, true);
	int activeWeaponIdx = 0;
	
	public LoadoutTab()
	{
		super(LOADOUT_ID);
	}
	
	@Override
	public void init(TerminalBlockEntity terminal)
	{
		super.init(terminal);
		buttons.add(nextButton);
		buttons.add(lastButton);
	}
	
	@Override
	public void drawCustomTab(MatrixStack matrices, TerminalBlockEntity terminal, VertexConsumerProvider buffers)
	{
		GUI.drawTab(matrices, buffers, "terminal.loadout", returnButton);
		Weapon activeWeapon = Weapon.values()[activeWeaponIdx];
		String t = Text.translatable("terminal.weapon." + activeWeapon.toString()).getString();
		GUI.drawText(buffers, matrices, t, 50 - textRenderer.getWidth(t) / 2, - 100 + textRenderer.fontHeight * 2, 0.01f);
		for (int i = 0; i < Math.min(3, activeWeapon.ids.length); i++)
		{
			String key = Text.translatable("terminal.weapon-short." + activeWeapon.ids[i].getPath()).getString();
			GUI.drawText(buffers, matrices, key, 2, -65 + 14 * i, 0.01f, Weapon.COLORS[i]);
			GUI.drawButton(buffers, matrices, new Button("A", new Vector2i(89, 33 + 14 * i), "toggle", i, false));
			GUI.drawButton(buffers, matrices, new Button("0", new Vector2i(77, 33 + 14 * i), "order", i, false));
		}
		drawButtons(matrices, terminal, buffers);
	}
	
	@Override
	public boolean onButtonClicked(String action, int value)
	{
		PlayerEntity player = MinecraftClient.getInstance().player;
		Weapon[] weapons = Weapon.values();
		if(action.equals(nextButton.getAction()))
		{
			for (int i = 1; i < weapons.length; i++)
			{
				int ii = (activeWeaponIdx + i) % weapons.length;
				if(weapons[ii].isAnyUnlocked(player))
				{
					activeWeaponIdx = ii;
					return true;
				}
			}
			return true;
		}
		if(action.equals(lastButton.getAction()))
		{
			for (int i = 1; i < weapons.length; i++)
			{
				int ii = (activeWeaponIdx - i) < 0 ? weapons.length - i : activeWeaponIdx - i;
				if(weapons[ii % weapons.length].isAnyUnlocked(player))
				{
					activeWeaponIdx = ii;
					return true;
				}
			}
			return true;
		}
		return super.onButtonClicked(action, value);
	}
}
