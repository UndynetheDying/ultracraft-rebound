package absolutelyaya.ultracraft.client.gui.terminal;

import absolutelyaya.ultracraft.components.UltraComponents;
import absolutelyaya.ultracraft.Ultracraft;
import absolutelyaya.ultracraft.Weapon;
import absolutelyaya.ultracraft.block.TerminalBlockEntity;
import absolutelyaya.ultracraft.client.gui.terminal.elements.Button;
import absolutelyaya.ultracraft.client.gui.terminal.elements.Sprite;
import absolutelyaya.ultracraft.client.gui.terminal.elements.SpriteListElement;
import absolutelyaya.ultracraft.client.gui.terminal.elements.Tab;
import absolutelyaya.ultracraft.components.player.ILoadoutComponent;
import absolutelyaya.ultracraft.components.player.IProgressionComponent;
import absolutelyaya.ultracraft.data.UltraRecipeManager;
import absolutelyaya.ultracraft.recipe.UltraRecipe;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import org.joml.Vector2f;
import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.List;

public class WeaponsTab extends Tab
{
	static final Identifier TEXTURE = Ultracraft.texIdentifier("textures/gui/weapon_icons");
	static final Vector2i TEXTURE_SIZE = new Vector2i(384, 384);
	
	Button returnButton = new Button(Button.RETURN_LABEL,
			new Vector2i(-6 - textRenderer.getWidth(Text.translatable(Button.RETURN_LABEL).getString()), 96 - textRenderer.fontHeight),
			"mainmenu", 0, false);
	Button craftButton = new Button("terminal.craft", new Vector2i(150, 96 - textRenderer.fontHeight), "craft", 0, true);
	Button blueWeapon = new Button(new Sprite(TEXTURE, new Vector2i(0, 0), 0.001f, new Vector2i(48, 32), new Vector2i(0, 0), TEXTURE_SIZE),
			new Vector2i(50, 23), "weapon", 0, true);
	Button greenWeapon = new Button(new Sprite(TEXTURE, new Vector2i(0, 0), 0.001f, new Vector2i(48, 32), new Vector2i(48, 0), TEXTURE_SIZE),
			new Vector2i(26, 58), "weapon", 1, true);
	Button redWeapon = new Button(new Sprite(TEXTURE, new Vector2i(0, 0), 0.001f, new Vector2i(48, 32), new Vector2i(96, 0), TEXTURE_SIZE),
			new Vector2i(75, 58), "weapon", 2, true);
	Button loadoutButton = new Button("terminal.loadout", new Vector2i(-98, 96 - textRenderer.fontHeight), "loadout", 2, false);
	List<Button> weaponCategoryButtons = new ArrayList<>();
	Weapon selectedCategory = null;
	int selectedWeapon;
	UltraRecipe selectedRecipe;
	SpriteListElement ingredientList = new SpriteListElement(94, 4, 16);
	
	public WeaponsTab()
	{
		super(WEAPONS_ID);
	}
	
	@Override
	public void init(TerminalBlockEntity terminal)
	{
		super.init(terminal);
		Weapon[] weaponCategories = Weapon.values();
		for (int i = 0; i < weaponCategories.length; i++)
		{
			String t = "terminal.weapon." + weaponCategories[i];
			boolean locked = i >= 0 && !unlockedAny(weaponCategories[i]);
			Button b = new Button(t,
					new Vector2i(-6 - textRenderer.getWidth(locked ? "???" : Text.translatable("terminal.weapon." + weaponCategories[i]).getString()),
					2 + (i * (textRenderer.fontHeight + 5))), "select", i, false);
			if(locked)
				b.setClickable(false).setColor(0xff888888).setLabel("???");
			buttons.add(b);
			weaponCategoryButtons.add(b);
		}
		buttons.add(craftButton);
		buttons.add(blueWeapon);
		buttons.add(greenWeapon);
		buttons.add(redWeapon);
		buttons.add(loadoutButton);
		ingredientList.setSelectable(false);
		
		onButtonClicked("select", 0);
	}
	
	boolean unlockedAny(Weapon type)
	{
		return type.isAnyUnlocked(MinecraftClient.getInstance().player);
	}
	
	@Override
	public void drawCustomTab(MatrixStack matrices, TerminalBlockEntity terminal, VertexConsumerProvider buffers)
	{
		GUI.drawTab(matrices, buffers, "terminal.weapons", returnButton);
		GUI.drawBox(buffers, matrices, -1, 0, 1, 100, 0xffffffff, 0.0005f);
		GUI.drawBox(buffers, matrices, 101, 0, 1, 100, 0xffffffff, 0.0005f);
		drawButtons(matrices, terminal, buffers);
		//the two arrows
		GUI.drawSprite(buffers, matrices, TEXTURE, new Vector2i(11, 36), 0.002f, new Vector2i(0, 139), new Vector2i(13, 20), TEXTURE_SIZE, terminal.getTextColor());
		GUI.drawSprite(buffers, matrices, TEXTURE, new Vector2i(76, 36), 0.002f, new Vector2i(14, 139), new Vector2i(13, 20), TEXTURE_SIZE, terminal.getTextColor());
		//ingredients
		if(selectedRecipe != null)
			GUI.drawSpriteList(buffers, matrices, 104, 2, ingredientList);
		else
		{
			String t = Text.translatable("terminal.no-recipe").getString();
			matrices.push();
			matrices.translate(0, 1, 0);
			GUI.drawText(buffers, matrices, t, 152 - textRenderer.getWidth(t) / 2, 40, 0.001f, 0xffdd0000);
			matrices.pop();
		}
	}
	
	@Override
	public boolean onButtonClicked(String action, int value)
	{
		IProgressionComponent progression = UltraComponents.PROGRESSION.get(MinecraftClient.getInstance().player);
		ILoadoutComponent loadout = UltraComponents.LOADOUT.get(MinecraftClient.getInstance().player);
		if(action.equals("select"))
		{
			updateTab(value, progression);
			return true;
		}
		
		Identifier[] selectedWeaponTypeIds = selectedCategory.ids;
		switch(action)
		{
			case "weapon" -> {
				return updateSelectedWeapon(selectedWeaponTypeIds, value, progression);
			}
			case "craft" -> {
				if(selectedRecipe == null)
				{
					craftButton.setClickable(false);
					return true;
				}
				Identifier itemID = selectedCategory.ids[selectedWeapon];
				if(progression.isOwned(itemID))
					loadout.tryDispenseWeapon(selectedCategory, itemID);
				else
				{
					PlayerEntity player = MinecraftClient.getInstance().player;
					if(selectedRecipe.canCraft(player) <= 1)
						return true;
					selectedRecipe.craft(player);
				}
				refreshTab();
				return true;
			}
			default -> {
				return super.onButtonClicked(action, value);
			}
		}
	}
	
	boolean isResultItemInLoadout(ILoadoutComponent loadout)
	{
		Identifier id = selectedCategory.ids[selectedWeapon];
		return loadout.isInLoadout(selectedCategory, id) || loadout.isAltInLoadout(selectedCategory, id);
	}
	
	public void updateTab(int tab, IProgressionComponent progression)
	{
		if(selectedCategory != null)
			weaponCategoryButtons.get(selectedCategory.ordinal()).getPos().add(5, 0);
		weaponCategoryButtons.get(tab).getPos().sub(5, 0);
		selectedCategory = Weapon.values()[tab];
		
		blueWeapon.getSprite().setUv(new Vector2i(0, 32 * tab));
		greenWeapon.getSprite().setUv(new Vector2i(48, 32 * tab));
		redWeapon.getSprite().setUv(new Vector2i(96, 32 * tab));
		
		Identifier[] selectedWeaponTypeIds = selectedCategory.ids;
		updateWeaponButton(blueWeapon, 0, progression);
		updateWeaponButton(greenWeapon, 1, progression);
		updateWeaponButton(redWeapon, 2, progression);
		updateSelectedWeapon(selectedWeaponTypeIds, 0, progression);
	}
	
	public void refreshTab()
	{
		updateTab(selectedCategory.ordinal(), UltraComponents.PROGRESSION.get(MinecraftClient.getInstance().player));
	}
	
	void updateWeaponButton(Button button, int idx, IProgressionComponent progression)
	{
		Identifier[] ids = selectedCategory.ids;
		if(ids.length <= idx || !progression.isUnlocked(ids[idx]))
		{
			button.getSprite().setUv(new Vector2i(144, 160));
			button.setClickable(false).setColor(0xff888888);
		}
		else
			button.setClickable(true).setColor(0xffffffff);
	}
	
	boolean updateSelectedWeapon(Identifier[] selectedWeaponTypeIds, int idx, IProgressionComponent progression)
	{
		if (selectedWeaponTypeIds.length < idx + 1)
			return true;
		ILoadoutComponent loadout = UltraComponents.LOADOUT.get(MinecraftClient.getInstance().player);
		List<Pair<Sprite, String>> ingredients = ingredientList.getElements();
		ingredients.clear();
		Identifier weaponId = null;
		if (idx == -1)
		{
			selectedWeapon = -1;
			selectedRecipe = null;
		}
		else
		{
			if (!progression.isUnlocked(selectedWeaponTypeIds[idx]))
			{
				if (!selectedWeaponTypeIds[idx].equals(selectedCategory.ids[0]))
					refreshTab();
				craftButton.setClickable(selectedRecipe != null);
				return true;
			}
			weaponId = selectedWeaponTypeIds[idx];
			selectedWeapon = selectedCategory.getIdxForId(weaponId);
			selectedRecipe = UltraRecipeManager.getRecipe(weaponId);
			if (selectedRecipe != null)
			{
				List<UltraRecipe.Ingredient> materials = selectedRecipe.getMaterials();
				materials.forEach(i ->
				{
					Identifier id = Registries.ITEM.getId(i.item());
					Identifier sprite = null;
					if(i.spriteOverride() != null)
						sprite = Identifier.tryParse(i.spriteOverride());
					if(sprite == null)
						sprite = new Identifier(id.getNamespace(), "textures/item/" + id.getPath() + ".png");
					ingredients.add(new Pair<>(new Sprite(sprite,
							new Vector2i(0, 0), 0.001f, new Vector2i(16, 16), new Vector2i(0, 0), new Vector2i(16, 16)),
							i.amount() + " " + Text.translatable(i.item().getTranslationKey()).getString() + (i.amount() > 0 ? "s" : "")));
				});
				if(progression.isOwned(weaponId))
				{
					if (loadout.isWeaponTypeHeld(selectedCategory))
						craftButton.setLabel(Text.translatable("terminal.held").getString());
					else if (!isResultItemInLoadout(loadout))
						craftButton.setLabel(Text.translatable("terminal.not-equipped").getString());
				}
				else
					craftButton.setLabel(Text.translatable("terminal." + (progression.isOwned(weaponId) ? "dispense" : "craft")).getString());
			}
		}
		boolean clickable = selectedRecipe != null && !(loadout.isWeaponTypeHeld(selectedCategory) && progression.isOwned(weaponId)) && isResultItemInLoadout(loadout);
		craftButton.setClickable(clickable).setColor(clickable ? 0xffffffff : 0xff888888);
		return true;
	}
	
	@Override
	public Vector2f getSizeOverride()
	{
		return new Vector2f(300, 100);
	}
}
