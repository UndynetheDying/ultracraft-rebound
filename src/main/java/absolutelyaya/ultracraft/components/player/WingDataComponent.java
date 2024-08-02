package absolutelyaya.ultracraft.components.player;

import absolutelyaya.ultracraft.components.UltraComponents;
import absolutelyaya.ultracraft.Ultracraft;
import absolutelyaya.ultracraft.accessor.WingedPlayerEntity;
import absolutelyaya.ultracraft.client.UltracraftClient;
import absolutelyaya.ultracraft.client.rendering.UltraHudRenderer;
import absolutelyaya.ultracraft.registry.WingPatterns;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import org.joml.Vector3f;

public class WingDataComponent implements IWingDataComponent, AutoSyncedComponent
{
	Vector3f[] colors = new Vector3f[] { new Vector3f(247f / 255f, 1f, 154f / 255f), new Vector3f(117f / 255f, 154f / 255f, 1f) };
	String pattern = "", overlay = "";
	boolean active;
	PlayerEntity provider;
	
	public WingDataComponent(PlayerEntity entity)
	{
		provider = entity;
	}
	
	@Override
	public Vector3f[] getColors()
	{
		if(UltracraftClient.getConfig().blockedPlayers.contains(provider.getUuid()))
			return UltracraftClient.getDefaultWingColors();
		return colors;
	}
	
	@Override
	public void setColor(Vector3f val, int idx)
	{
		colors[idx] = val;
	}
	
	@Override
	public String getPattern()
	{
		return pattern;
	}
	
	@Override
	public void setPattern(String id)
	{
		pattern = id;
	}
	
	@Override
	public String getOverlay()
	{
		if(provider.getWorld().isClient && !isOverlayExists(overlay))
			overlay = "";
		return overlay;
	}
	
	@Override
	public void setOverlay(String id)
	{
		if(provider.getWorld().isClient && !isOverlayExists(id))
		{
			overlay = "";
			return;
		}
		overlay = id;
	}
	
	@Override
	public boolean isOverlayExists(String id)
	{
		return WingPatterns.getAllOverlayIDs().contains(id);
	}
	
	@Override
	public boolean isActive()
	{
		return active;
	}
	
	@Override
	public void setActive(boolean b)
	{
		active = b;
		if(provider.isMainPlayer())
			UltraHudRenderer.onUpdateWingsActive();
		if(provider instanceof WingedPlayerEntity winged)
			winged.updateSpeedConfig(b);
		if(b)
		{
			provider.setSprinting(false);
			provider.setSneaking(false);
		}
	}
	
	public void sync()
	{
		UltraComponents.WING_DATA.sync(provider);
	}
	
	NbtCompound serializeColor(Vector3f color)
	{
		NbtCompound c = new NbtCompound();
		c.putFloat("r", color.x);
		c.putFloat("g", color.y);
		c.putFloat("b", color.z);
		return c;
	}
	
	Vector3f deserializeColor(NbtCompound nbt)
	{
		return new Vector3f(nbt.getFloat("r"), nbt.getFloat("g"), nbt.getFloat("b"));
	}
	
	@Override
	public void readFromNbt(NbtCompound tag)
	{
		NbtCompound colors = tag.getCompound("colors");
		this.colors[0] = deserializeColor(colors.getCompound("wings"));
		this.colors[1] = deserializeColor(colors.getCompound("metal"));
		pattern = Ultracraft.checkSupporter(provider.getUuid(), provider.getWorld().isClient) ? tag.getString("pattern") : "";
		overlay = tag.getString("overlay");
		active = tag.getBoolean("visible");
	}
	
	@Override
	public void writeToNbt(NbtCompound tag)
	{
		NbtCompound colors = new NbtCompound();
		colors.put("wings", serializeColor(this.colors[0]));
		colors.put("metal", serializeColor(this.colors[1]));
		tag.put("colors", colors);
		tag.putString("pattern", pattern);
		tag.putString("overlay", overlay);
		tag.putBoolean("visible", active);
	}
}
