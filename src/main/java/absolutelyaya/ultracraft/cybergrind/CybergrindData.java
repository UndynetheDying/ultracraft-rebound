package absolutelyaya.ultracraft.cybergrind;

import absolutelyaya.ultracraft.Ultracraft;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import org.joml.Vector4i;

public class CybergrindData
{
	public static final byte FULL_SYNC = 0, PARTIAL_SYNC = 1, DESTROY_SYNC = 2;
	
	final Vector4i arenaBounds;
	final int waves;
	
	int enemies, currentWave;
	boolean solidBounds;
	
	public CybergrindData(int waves, Vector4i arenaBounds)
	{
		this.waves = waves;
		this.arenaBounds = arenaBounds;
	}
	
	public int getEnemies()
	{
		return enemies;
	}
	
	public void setEnemies(int enemies)
	{
		this.enemies = enemies;
	}
	
	public int getCurrentWave()
	{
		return currentWave;
	}
	
	public void setCurrentWave(int currentWave)
	{
		this.currentWave = currentWave;
	}
	
	public int getWaves()
	{
		return waves;
	}
	
	public Vector4i getArenaBounds()
	{
		return arenaBounds;
	}
	
	public boolean isSolidBounds()
	{
		return solidBounds;
	}
	
	public void setSolidBounds(boolean solidBounds)
	{
		this.solidBounds = solidBounds;
	}
	
	public NbtCompound serialize()
	{
		NbtCompound nbt = new NbtCompound();
		
		NbtCompound bounds = new NbtCompound();
		bounds.putInt("minX", arenaBounds.x);
		bounds.putInt("minZ", arenaBounds.y);
		bounds.putInt("maxX", arenaBounds.z);
		bounds.putInt("maxZ", arenaBounds.w);
		bounds.putBoolean("solid", solidBounds);
		
		nbt.put("arenaBounds", bounds);
		nbt.putInt("currentWave", currentWave);
		nbt.putInt("waves", waves);
		nbt.putInt("enemies", enemies);
		return nbt;
	}
	
	public static CybergrindData fromNbt(NbtCompound nbt)
	{
		int waves;
		Vector4i bounds;
		boolean solidBounds;
		if (nbt.contains("waves", NbtElement.INT_TYPE) && nbt.contains("arenaBounds", NbtElement.COMPOUND_TYPE))
		{
			waves = nbt.getInt("waves");
			NbtCompound compound = nbt.getCompound("arenaBounds");
			bounds = new Vector4i(compound.getInt("minX"), compound.getInt("minZ"), compound.getInt("maxX"), compound.getInt("maxZ"));
			solidBounds = compound.getBoolean("solid");
		}
		else
		{
			Ultracraft.LOGGER.error("couldn't deserialize cybergrind game; data: " + nbt);
			return null;
		}
		CybergrindData val = new CybergrindData(waves, bounds);
		val.setSolidBounds(solidBounds);
		if (nbt.contains("enemies", NbtElement.INT_TYPE))
			val.setEnemies(nbt.getInt("enemies"));
		if (nbt.contains("currentWave", NbtElement.INT_TYPE))
			val.setCurrentWave(nbt.getInt("currentWave"));
		return val;
	}
}
