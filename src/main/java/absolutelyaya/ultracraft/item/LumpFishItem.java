package absolutelyaya.ultracraft.item;

import absolutelyaya.ultracraft.FishPacket;
import absolutelyaya.ultracraft.registry.SoundRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.CodEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class LumpFishItem extends AbstractFishItem
{
	public LumpFishItem(Settings settings)
	{
		super(settings);
	}
	
	@Override
	protected void onSelect()
	{
		sendFishPacket(FishPacket.LUMP_SELECT);
	}
	
	@Override
	protected void onUnselect()
	{
		sendFishPacket(FishPacket.LUMP_UNSELECT);
	}
	
	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand)
	{
		ItemStack stack = user.getStackInHand(hand);
		boolean veryPowerful = false;
		int power = 1;
		if(stack.hasNbt())
		{
			veryPowerful = stack.getNbt().contains("oddValue", NbtElement.BYTE_TYPE) && stack.getNbt().getBoolean("oddValue");
			if(stack.getNbt().contains("power", NbtElement.INT_TYPE))
				power = stack.getNbt().getInt("power");
		}
		if(!veryPowerful)
			user.getItemCooldownManager().set(this, 300);
		if(!world.isClient)
		{
			for (int i = 0; i < power; i++)
				ThrowFish(world, user);
			if(!veryPowerful)
				user.playSound(SoundRegistry.LUMPFISH_USE, SoundCategory.PLAYERS, 1f,  1f);
		}
		return TypedActionResult.success(stack);
	}
	
	void ThrowFish(World world, PlayerEntity user)
	{
		CodEntity cod = new CodEntity(EntityType.COD, world);
		NbtCompound nbt = new NbtCompound();
		cod.writeNbt(nbt);
		nbt.putString("DeathLootTable", "");
		cod.readNbt(nbt);
		cod.disableExperienceDropping();
		cod.setPosition(user.getEyePos());
		cod.setVelocity(user.getRotationVector().multiply(5f));
		cod.setCustomName(Text.translatable(getTranslationKey() + "-soldier"));
		world.spawnEntity(cod);
	}
}
