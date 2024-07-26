package absolutelyaya.ultracraft.block.mapping;

import absolutelyaya.ultracraft.Ultracraft;
import absolutelyaya.ultracraft.components.UltraComponents;
import absolutelyaya.ultracraft.components.player.IProgressionComponent;
import absolutelyaya.ultracraft.registry.BlockEntityRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

public class ProgressionCheckTriggerBlockEntity extends AbstractTriggerBlockEntity
{
	static List<String> attributes = new ArrayList<>();
	Identifier progressionEntry = Ultracraft.identifier("pierce_revolver");
	boolean checkUnlocked = true, checkObtained;
	
	public ProgressionCheckTriggerBlockEntity(BlockPos pos, BlockState state)
	{
		super(BlockEntityRegistry.MAP_PROGRESSION_CHECK, pos, state);
		id = "progressionCheck";
	}
	
	@Override
	Class<? extends LivingEntity> getTargetClass()
	{
		return PlayerEntity.class;
	}
	
	@Override
	public Vector4f getColor()
	{
		return new Vector4f(0.25f, 0f, 0.5f, 1f);
	}
	
	@Override
	public String getTexture()
	{
		return "progression";
	}
	
	@Override
	public Text getAreaLabel()
	{
		return Text.of("Pc-" + id + "->" + flag);
	}
	
	@Override
	public List<String> getAttributes()
	{
		return attributes;
	}
	
	@Override
	public String getAttribute(String attribute)
	{
		return switch (attribute)
		{
			case "targetThreshold" -> String.valueOf(targetThreshold);
			case "activationDelay" -> String.valueOf(activateDelay);
			case "invert" -> String.valueOf(inverted);
			case "entry" -> progressionEntry.toString();
			case "checkUnlocked" -> String.valueOf(checkUnlocked);
			case "checkObtained" -> String.valueOf(checkObtained);
			default -> "";
		};
	}
	
	@Override
	public void setAttribute(String s, String value) throws AttributeParseException, NumberFormatException
	{
		super.setAttribute(s, value);
		if(s.equals("targetThreshold"))
			targetThreshold = Integer.parseInt(value);
		if(s.equals("invert"))
			inverted = Boolean.parseBoolean(value);
		if(s.equals("activationDelay"))
			activateDelay = Integer.parseInt(value);
		if(s.equals("entry"))
			progressionEntry = Identifier.tryParse(value);
		if(s.equals("checkUnlocked"))
			checkUnlocked = Boolean.parseBoolean(value);
		if(s.equals("checkObtained"))
			checkObtained = Boolean.parseBoolean(value);
	}
	
	@Override
	boolean isValidTarget(LivingEntity entity)
	{
		if(super.isValidTarget(entity) && entity instanceof PlayerEntity player)
		{
			IProgressionComponent progression = UltraComponents.PROGRESSION.get(player);
			return (!checkUnlocked || progression.isUnlocked(progressionEntry)) && (!checkObtained || progression.isOwned(progressionEntry));
		}
		else
			return false;
	}
	
	@Override
	protected void writeNbt(NbtCompound nbt)
	{
		super.writeNbt(nbt);
		nbt.putString("entry", progressionEntry.toString());
		nbt.putBoolean("checkUnlocked", checkUnlocked);
		nbt.putBoolean("checkObtained", checkObtained);
	}
	
	@Override
	public void readNbt(NbtCompound nbt)
	{
		super.readNbt(nbt);
		if(nbt.contains("entry", NbtElement.STRING_TYPE))
			progressionEntry = Identifier.tryParse(nbt.getString("entry"));
		if(nbt.contains("checkUnlocked", NbtElement.BYTE_TYPE))
			checkUnlocked = nbt.getBoolean("checkUnlocked");
		if(nbt.contains("checkObtained", NbtElement.BYTE_TYPE))
			checkObtained = nbt.getBoolean("checkObtained");
	}
	
	static {
		attributes.add("targetThreshold");
		attributes.add("activationDelay");
		attributes.add("invert");
		attributes.add("entry");
		attributes.add("checkUnlocked");
		attributes.add("checkObtained");
	}
}
