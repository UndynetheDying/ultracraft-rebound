package absolutelyaya.ultracraft.block.mapping;

import absolutelyaya.ultracraft.cybergrind.CybergrindGame;
import absolutelyaya.ultracraft.cybergrind.CybergrindManager;
import absolutelyaya.ultracraft.registry.BlockEntityRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

public class CybergrindBlockEntity extends AbstractListenerBlockEntity implements FlagBindable, FlagListener
{
	static List<String> attributes = new ArrayList<>();
	String flag, winFlag;
	int rounds = 5;
	CybergrindGame cybergrindGame;
	boolean completed;
	
	public CybergrindBlockEntity(BlockPos pos, BlockState state)
	{
		super(BlockEntityRegistry.MAP_CYBERGRIND, pos, state);
		id = "cybergrind";
	}
	
	@Override
	public Vector4f getColor()
	{
		return new Vector4f(0.15f, 0.8f, 0.87f, 1f);
	}
	
	@Override
	public String getTexture()
	{
		return "cybergrind";
	}
	
	@Override
	public List<String> getAttributes()
	{
		return attributes;
	}
	
	@Override
	public Text getAreaLabel()
	{
		return Text.of(flag + " -> Cybergrind -> " + winFlag);
	}
	
	@Override
	public float getAreaLabelSize()
	{
		return 1.5f;
	}
	
	@Override
	void tick()
	{
		if(completed || world.isClient)
			return;
		if(cybergrindGame != null && cybergrindGame.isOver())
		{
			completed = true;
			if(winFlag != null && !winFlag.isEmpty() && cybergrindGame.isWin() && world.getBlockEntity(getParent()) instanceof RoomBlockEntity room)
				room.setFlag(winFlag, true);
			if(world.isClient)
				return;
			cybergrindGame = null;
		}
	}
	
	@Override
	public void onActivateFlag()
	{
		if(!world.isClient)
			cybergrindGame = CybergrindManager.Instance.startCybergrindAt(getPos(), rounds);
	}
	
	@Override
	public void onDeactivateFlag()
	{
		reset();
	}
	
	@Override
	public void reset()
	{
		super.reset();
		completed = false;
		if(world.isClient)
			return;
		if(cybergrindGame != null && !cybergrindGame.isOver())
			cybergrindGame.end();
		cybergrindGame = null;
	}
	
	@Override
	public String getAttribute(String attribute)
	{
		return switch (attribute)
		{
			case "winFlag" -> winFlag;
			case "rounds" -> String.valueOf(rounds);
			default -> null;
		};
	}
	
	@Override
	public void setAttribute(String s, String value)
	{
		super.setAttribute(s, value);
		switch (s)
		{
			case "winFlag" -> winFlag = value;
			case "rounds" -> rounds = Integer.parseInt(value);
		}
	}
	
	@Override
	public void readNbt(NbtCompound nbt)
	{
		super.readNbt(nbt);
		if(nbt.contains("flag", NbtElement.STRING_TYPE))
			flag = nbt.getString("flag");
		if(nbt.contains("winFlag", NbtElement.STRING_TYPE))
			winFlag = nbt.getString("winFlag");
		if(nbt.contains("rounds", NbtElement.INT_TYPE))
			rounds = Math.max(nbt.getInt("rounds"), 1);
	}
	
	@Override
	protected void writeNbt(NbtCompound nbt)
	{
		super.writeNbt(nbt);
		if(flag != null)
			nbt.putString("flag", flag);
		if(winFlag != null)
			nbt.putString("winFlag", winFlag);
		nbt.putInt("rounds", rounds);
	}
	
	static {
		attributes.add("winFlag");
		attributes.add("rounds");
	}
}
