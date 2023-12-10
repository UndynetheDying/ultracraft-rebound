package absolutelyaya.ultracraft.components.player;

import absolutelyaya.ultracraft.UltraComponents;
import absolutelyaya.ultracraft.config.HivelConfig;
import absolutelyaya.ultracraft.registry.SoundRegistry;
import absolutelyaya.ultracraft.registry.StatusEffectRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;

public class HivelComponent implements IHivelComponent
{
	private final PlayerEntity provider;
	
	boolean sliding, slamming, ignoreSlowdown, airControlIncreased, dirty;
	float stamina, lastStamina, staminaRegen = 1.5f, maxNoSlowdownVelocity;
	int dashingTicks = -2, slamDamageCooldown;
	
	public HivelComponent(PlayerEntity provider)
	{
		this.provider = provider;
	}
	
	@Override
	public void setSliding(boolean v)
	{
		sliding = v;
	}
	
	@Override
	public boolean isSliding()
	{
		return UltraComponents.WING_DATA.get(provider).isActive() && sliding;
	}
	
	@Override
	public void onDash()
	{
		dashingTicks = HivelConfig.INSTANCE.dashTicks.getValue();
		provider.getWorld().playSound(null, provider.getBlockPos(), SoundRegistry.DASH, SoundCategory.PLAYERS, 0.75f, 1.6f);
		ignoreSlowdown = false;
	}
	
	@Override
	public void cancelDash()
	{
		dashingTicks = -2;
	}
	
	@Override
	public void onDashJump()
	{
		dashingTicks = -2;
		provider.getWorld().playSound(null, provider.getBlockPos(), SoundRegistry.DASH_JUMP, SoundCategory.PLAYERS, 0.75f, 1.6f);
	}
	
	@Override
	public boolean isDashing()
	{
		return dashingTicks > 0;
	}
	
	@Override
	public boolean wasDashing()
	{
		return dashingTicks + 1 >= 0;
	}
	
	@Override
	public boolean wasDashing(int i)
	{
		return dashingTicks + i >= 0;
	}
	
	@Override
	public int getDashingTicks()
	{
		return dashingTicks;
	}
	
	@Override
	public float getStamina()
	{
		return stamina;
	}
	
	@Override
	public boolean consumeStamina()
	{
		if(provider.isCreative())
			return true;
		if(stamina >= 30)
		{
			stamina = Math.max(stamina - 30, 0);
			return true;
		}
		else
			provider.playSound(SoundRegistry.NO_STAMINA, 0.5f, 1.8f);
		return false;
	}
	
	@Override
	public void replenishStamina(int i)
	{
		stamina = Math.min(stamina + 30 * i, 90);
	}
	
	@Override
	public void setSlamming(boolean b)
	{
		slamming = b;
	}
	
	@Override
	public boolean isSlamming()
	{
		return slamming;
	}
	
	@Override
	public boolean shouldIgnoreSlowdown()
	{
		return ignoreSlowdown;
	}
	
	@Override
	public void setIgnoreSlowdown(boolean b)
	{
		ignoreSlowdown = b;
		maxNoSlowdownVelocity = (float)provider.getVelocity().horizontalLength();
	}
	
	@Override
	public void setAirControlIncreased(boolean b)
	{
		airControlIncreased = b;
	}
	
	@Override
	public boolean isAirControlIncreased()
	{
		return airControlIncreased;
	}
	
	@Override
	public float getSlamDamageCooldown()
	{
		return slamDamageCooldown;
	}
	
	@Override
	public void setSlamDamageCooldown(int i)
	{
		slamDamageCooldown = i;
	}
	
	@Override
	public float getMaxNoSlowdownVelocity()
	{
		return maxNoSlowdownVelocity;
	}
	
	@Override
	public void markDirty()
	{
		dirty = true;
	}
	
	@Override
	public void readFromNbt(NbtCompound tag)
	{
	
	}
	
	@Override
	public void writeToNbt(NbtCompound tag)
	{
	
	}
	
	@Override
	public void tick()
	{
		if(dashingTicks > -60)
			dashingTicks--;
		if(slamDamageCooldown > 0)
			slamDamageCooldown--;
		StatusEffectInstance chilled = provider.getStatusEffect(StatusEffectRegistry.CHILLED);
		if(stamina < 90 && !isSliding() && !(chilled != null && provider.age % (chilled.getAmplifier() + 1) != 0))
		{
			lastStamina = stamina;
			stamina += staminaRegen;
			if(lastStamina % 30f > stamina % 30f)
				provider.playSound(SoundRegistry.STAMINA_REGEN, 0.2f, 1f + stamina / 30f * 0.1f);
			dirty = true;
		}
		if(dirty)
		{
			UltraComponents.HIVEL.sync(provider);
			dirty = false;
		}
	}
}
