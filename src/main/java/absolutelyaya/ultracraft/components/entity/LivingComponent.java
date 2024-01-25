package absolutelyaya.ultracraft.components.entity;

import absolutelyaya.ultracraft.components.UltraComponents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;

public class LivingComponent implements ILivingComponent
{
	final LivingEntity provider;
	boolean cancerous;
	
	public LivingComponent(LivingEntity provider)
	{
		this.provider = provider;
	}
	
	@Override
	public boolean isCancerous()
	{
		return cancerous;
	}
	
	@Override
	public void setCanerous(boolean v)
	{
		cancerous = v;
		UltraComponents.LIVING.sync(provider);
	}
	
	@Override
	public void readFromNbt(NbtCompound tag)
	{
		if(tag.contains("cancerous", NbtElement.BYTE_TYPE))
			cancerous = tag.getBoolean("cancerous");
	}
	
	@Override
	public void writeToNbt(NbtCompound tag)
	{
		tag.putBoolean("cancerous", cancerous);
	}
}
