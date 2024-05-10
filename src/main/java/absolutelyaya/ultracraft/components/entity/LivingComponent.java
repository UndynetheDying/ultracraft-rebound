package absolutelyaya.ultracraft.components.entity;

import absolutelyaya.ultracraft.accessor.Enrageable;
import absolutelyaya.ultracraft.components.UltraComponents;
import absolutelyaya.ultracraft.registry.SoundRegistry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;

public class LivingComponent implements ILivingComponent
{
	final LivingEntity provider;
	boolean cancerous, enraged;
	
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
	public boolean isEnraged()
	{
		return enraged;
	}
	
	@Override
	public void setEnraged(boolean v)
	{
		enraged = v;
		UltraComponents.LIVING.sync(provider);
		if(!(provider instanceof Enrageable))
			provider.playSound(SoundRegistry.GENERIC_ENRAGE, 1.5f, 0.9f);
	}
	
	@Override
	public void readFromNbt(NbtCompound tag)
	{
		if(tag.contains("cancerous", NbtElement.BYTE_TYPE))
			cancerous = tag.getBoolean("cancerous");
		if(tag.contains("enraged", NbtElement.BYTE_TYPE))
			enraged = tag.getBoolean("enraged");
	}
	
	@Override
	public void writeToNbt(NbtCompound tag)
	{
		tag.putBoolean("cancerous", cancerous);
		tag.putBoolean("enraged", enraged);
	}
}
