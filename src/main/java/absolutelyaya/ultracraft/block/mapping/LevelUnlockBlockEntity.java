package absolutelyaya.ultracraft.block.mapping;

import absolutelyaya.ultracraft.components.UltraComponents;
import absolutelyaya.ultracraft.data.LevelData;
import absolutelyaya.ultracraft.data.LevelDataManager;
import absolutelyaya.ultracraft.registry.BlockEntityRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

public class LevelUnlockBlockEntity extends AbstractListenerBlockEntity
{
	static List<String> attributes = new ArrayList<>();
	Identifier level;
	
	public LevelUnlockBlockEntity(BlockPos pos, BlockState state)
	{
		super(BlockEntityRegistry.MAP_LEVEL, pos, state);
		id = "unlock";
	}
	
	@Override
	public Vector4f getColor()
	{
		return new Vector4f(0.224f, 0.471f, 0.659f, 1f);
	}
	
	@Override
	public String getTexture()
	{
		return "level_unlock";
	}
	
	@Override
	public List<String> getAttributes()
	{
		return attributes;
	}
	
	@Override
	public String getAttribute(String attribute)
	{
		if(attribute.equals("level"))
			return String.valueOf(level);
		return null;
	}
	
	@Override
	public Text getAreaLabel()
	{
		return Text.of("Level-" + flag + "->" + level);
	}
	
	@Override
	public float getAreaLabelSize()
	{
		return 0f;
	}
	
	@Override
	protected void onStateChanged(boolean newState)
	{
		if(!newState)
			return;
		if(level == null)
			return;
		boolean b = UltraComponents.GLOBAL.get(world.getLevelProperties()).unlockDestination(level);
		if(world.isClient || !b)
			return;
		LevelData data = LevelDataManager.getLevelData(level);
		if(!data.equals(LevelDataManager.ERR_DATA))
			world.getServer().getPlayerManager().getPlayerList()
					.forEach(p -> p.sendMessage(Text.translatable("message.ultracraft.travel.new-destination", data.getTitleText())));
		else if(level.getPath().startsWith("dimension"))
			world.getServer().getPlayerManager().getPlayerList()
					.forEach(p -> p.sendMessage(Text.translatable("message.ultracraft.travel.new-freeroam-destination")));
		super.onStateChanged(newState);
	}
	
	@Override
	public void onActivateFlag()
	{
		super.onActivateFlag();
	}
	
	@Override
	public void setAttribute(String s, String value)
	{
		if(s.equals("level"))
			level = Identifier.tryParse(value);
		super.setAttribute(s, value);
	}
	
	@Override
	protected void writeNbt(NbtCompound nbt)
	{
		super.writeNbt(nbt);
		if(level != null)
			nbt.putString("level", level.toString());
	}
	
	@Override
	public void readNbt(NbtCompound nbt)
	{
		super.readNbt(nbt);
		if(nbt.contains("level", NbtElement.STRING_TYPE))
			level = Identifier.tryParse(nbt.getString("level"));
	}
	
	static {
		attributes.add("level");
	}
}
