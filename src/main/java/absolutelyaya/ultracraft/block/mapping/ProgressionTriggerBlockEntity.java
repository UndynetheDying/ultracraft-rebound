package absolutelyaya.ultracraft.block.mapping;

import absolutelyaya.ultracraft.components.UltraComponents;
import absolutelyaya.ultracraft.Ultracraft;
import absolutelyaya.ultracraft.components.player.IProgressionComponent;
import absolutelyaya.ultracraft.components.player.IWingedPlayerComponent;
import absolutelyaya.ultracraft.registry.BlockEntityRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

public class ProgressionTriggerBlockEntity extends AbstractTriggerBlockEntity
{
	static List<String> attributes = new ArrayList<>();
	Identifier progressionEntry = Ultracraft.identifier("pierce_revolver");
	boolean giveAsItem, obtain;
	String message = "";
	
	public ProgressionTriggerBlockEntity(BlockPos pos, BlockState state)
	{
		super(BlockEntityRegistry.MAP_PROGRESSION, pos, state);
		id = "progression";
	}
	
	@Override
	public Vector4f getColor()
	{
		return new Vector4f(0.67f, 0.23f, 1f, 0.75f);
	}
	
	@Override
	public String getTexture()
	{
		return "progression";
	}
	
	@Override
	public Text getAreaLabel()
	{
		return Text.of("P-" + id);
	}
	
	@Override
	public List<String> getAttributes()
	{
		return attributes;
	}
	
	@Override
	public String getAttribute(String attribute)
	{
		return switch(attribute) {
			case "entry" -> progressionEntry.toString();
			case "givesItem" -> String.valueOf(giveAsItem);
			case "alsoObtain" -> String.valueOf(obtain);
			case "message" -> message;
			default -> null;
		};
	}
	
	@Override
	public void setAttribute(String s, String value) throws AttributeParseException, NumberFormatException
	{
		switch(s) {
			case "entry" -> progressionEntry = parseIdentifier(value);
			case "givesItem" -> giveAsItem = Boolean.parseBoolean(value);
			case "alsoObtain" -> obtain = Boolean.parseBoolean(value);
			case "message" -> message = value;
		}
		super.setAttribute(s, value);
	}
	
	@Override
	Class<? extends LivingEntity> getTargetClass()
	{
		return PlayerEntity.class;
	}
	
	@Override
	void tick()
	{
		super.tick();
		containedEntities.forEach(e -> {
			if(e instanceof PlayerEntity player)
			{
				IProgressionComponent progression = UltraComponents.PROGRESSION.get(player);
				if(!progression.isUnlocked(progressionEntry))
				{
					progression.unlock(progressionEntry);
					if(obtain)
						progression.obtain(progressionEntry);
					Item item = Registries.ITEM.get(progressionEntry);
					if(giveAsItem && item != null)
						player.giveItemStack(item.getDefaultStack());
					if(message != null && !world.isClient)
					{
						IWingedPlayerComponent winged = UltraComponents.WINGED.get(player);
						winged.sendBoxTitle(Text.translatable(message, item.getName().getString()));
					}
				}
			}
		});
	}
	
	@Override
	protected void writeNbt(NbtCompound nbt)
	{
		super.writeNbt(nbt);
		if(progressionEntry != null)
			nbt.putString("entry", progressionEntry.toString());
		nbt.putBoolean("givesItem", giveAsItem);
		nbt.putBoolean("obtain", obtain);
		if(message != null)
			nbt.putString("message", message);
	}
	
	@Override
	public void readNbt(NbtCompound nbt)
	{
		super.readNbt(nbt);
		if(nbt.contains("entry", NbtElement.STRING_TYPE))
			progressionEntry = Identifier.tryParse(nbt.getString("entry"));
		if(nbt.contains("givesItem", NbtElement.BYTE_TYPE))
			giveAsItem = nbt.getBoolean("givesItem");
		if(nbt.contains("obtain", NbtElement.BYTE_TYPE))
			obtain = nbt.getBoolean("obtain");
		if(nbt.contains("message", NbtElement.STRING_TYPE))
			message = nbt.getString("message");
	}
	
	static {
		attributes.add("entry");
		attributes.add("givesItem");
		attributes.add("alsoObtain");
		attributes.add("message");
	}
}
