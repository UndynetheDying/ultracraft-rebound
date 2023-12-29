package absolutelyaya.ultracraft.client.gui.terminal;

import absolutelyaya.ultracraft.UltraComponents;
import absolutelyaya.ultracraft.Weapon;
import absolutelyaya.ultracraft.block.TerminalBlockEntity;
import absolutelyaya.ultracraft.client.gui.terminal.elements.Button;
import absolutelyaya.ultracraft.client.gui.terminal.elements.Tab;
import absolutelyaya.ultracraft.components.player.ILoadoutComponent;
import absolutelyaya.ultracraft.components.player.IProgressionComponent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.joml.Vector2i;

public class LoadoutTab extends Tab
{
	protected Button returnButton = new Button(Button.RETURN_LABEL, new Vector2i(48, 95 -textRenderer.fontHeight), "weapons", 0, true);
	protected Button nextButton = new Button(">", new Vector2i(49 + returnButton.getSize().x / 3 * 2 + 1, 95 -textRenderer.fontHeight), "next", 0, true);
	protected Button lastButton = new Button("<", new Vector2i(48 - returnButton.getSize().x / 3 * 2 - 1, 95 -textRenderer.fontHeight), "last", 0, true);
	int activeWeaponIdx = 0;
	int[] state = new int[3], order = new int[3];
	ILoadoutComponent loadout;
	IProgressionComponent progression;
	
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
		loadout = UltraComponents.LOADOUT.get(MinecraftClient.getInstance().player);
		progression = UltraComponents.PROGRESSION.get(MinecraftClient.getInstance().player);
		onSwitchWeaponTab();
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
			boolean owned = progression.isOwned(activeWeapon.ids[i]);
			String key;
			if(owned)
				key = Text.translatable("terminal.weapon-short." + activeWeapon.ids[i].getPath()).getString();
			else
				key = "???";
			GUI.drawText(buffers, matrices, key, 2, -65 + 14 * i, 0.01f, Weapon.COLORS[i]);
			if(!owned)
				continue;
			GUI.drawButton(buffers, matrices, new Button(getCharForButton(i), new Vector2i(89, 33 + 14 * i), "toggle", i, false));
			if(state[i] > 0)
				GUI.drawButton(buffers, matrices, new Button(String.valueOf(order[i] + 1), new Vector2i(77, 33 + 14 * i), "order", i, false));
		}
		drawButtons(matrices, terminal, buffers);
	}
	
	void onSwitchWeaponTab()
	{
		Weapon weapon = Weapon.values()[activeWeaponIdx];
		Identifier[] curLoadout = loadout.getLoadoutForWeapon(weapon);
		state[0] = state[1] = state[2] = 0;
		order[0] = order[1] = order[2] = -1;
		for (int i = 0; i < Math.min(3, curLoadout.length); i++)
		{
			boolean alt = false;
			int idx = weapon.getIdxForId(curLoadout[i]);
			if (idx == -1)
				continue;
			if (weapon.altId != null && idx >= weapon.ids.length / 2)
			{
				idx -= weapon.ids.length / 2;
				alt = progression.isOwned(weapon.altId);
			}
			state[idx] = alt ? 2 : 1;
			order[idx] = i;
		}
	}
	
	void saveLoadout()
	{
		Weapon weapon = Weapon.values()[activeWeaponIdx];
		Identifier[] ids = new Identifier[3];
		for (int i = 0; i < Math.min(3, ids.length); i++)
		{
			if(order[i] == -1)
				continue;
			int ii = state[order[i]];
			if(ii == 0)
				continue;
			ids[order[i]] = weapon.ids[ii == 2 ? 3 + i : i];
		}
		loadout.setLoadoutForWeapon(weapon, ids);
	}
	
	String getCharForButton(int i)
	{
		return switch (state[i])
		{
			case 0 -> "N";
			case 1 -> "Y";
			case 2 -> "*";
			default -> "?";
		};
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
					saveLoadout();
					activeWeaponIdx = ii;
					onSwitchWeaponTab();
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
					saveLoadout();
					activeWeaponIdx = ii;
					onSwitchWeaponTab();
					return true;
				}
			}
			return true;
		}
		if(action.equals("toggle"))
		{
			int max = canBeAlternate() ? 3 : 2;
			boolean wasDisabled = state[value] == 0;
			state[value] = (state[value] + 1) % max;
			if(!wasDisabled)
			{
				if(state[value] == 0)
					order[value] = -1;
				return true;
			}
			int newOrder = 0;
			boolean clear = false;
			while(!clear)
			{
				boolean b = true;
				for (int i = 0; i < 3; i++)
				{
					if(order[i] == newOrder)
					{
						b = false;
						break;
					}
				}
				clear = b;
				if(!clear && ++newOrder == 2)
				{
					clear = true;
				}
			}
			order[value] = newOrder;
			return true;
		}
		if(action.equals("order"))
		{
			if(order[value] == 2)
				return true;
			order[value] = order[value] + 1;
			for (int i = 0; i < 3; i++)
			{
				if(order[value] == order[i] && i != value)
					order[i] = order[i] - 1;
			}
			return true;
		}
		return super.onButtonClicked(action, value);
	}
	
	boolean canBeAlternate()
	{
		Weapon weapon = Weapon.values()[activeWeaponIdx];
		return weapon.altId != null && progression.isOwned(weapon.altId);
	}
	
	@Override
	public void onClose(TerminalBlockEntity terminal)
	{
		saveLoadout();
		super.onClose(terminal);
	}
}
