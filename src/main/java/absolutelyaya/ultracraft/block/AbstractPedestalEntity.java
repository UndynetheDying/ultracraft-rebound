package absolutelyaya.ultracraft.block;

import absolutelyaya.ultracraft.client.gui.screen.PedestalScreenHandler;
import absolutelyaya.ultracraft.registry.PacketRegistry;
import absolutelyaya.ultracraft.registry.ScreenHandlerRegistry;
import absolutelyaya.ultracraft.registry.SoundRegistry;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class AbstractPedestalEntity extends BlockEntity implements NamedScreenHandlerFactory
{
	
	protected Inventory inventory;
	protected String type;
	protected boolean decorative, sacrificial, sacrificePending, sacrificeSuccess;
	
	public AbstractPedestalEntity(BlockEntityType<? extends AbstractPedestalEntity> entity, BlockPos pos, BlockState state)
	{
		super(entity, pos, state);
		inventory = new SimpleInventory(2);
	}
	
	public boolean onPunch(PlayerEntity player, boolean mainHand)
	{
		if(decorative)
			return false;
		if(sacrificial && sacrificePending)
			return false;
		markDirty();
		PlayerInventory playerInventory = player.getInventory();
		ItemStack held = inventory.getStack(0);
		if((player.getOffHandStack().isEmpty() && !mainHand) || (mainHand && player.getMainHandStack().isEmpty()))
		{
			if(!held.isEmpty())
			{
				(mainHand ? playerInventory.main : playerInventory.offHand).set(mainHand ? playerInventory.selectedSlot : 0, held.copy());
				inventory.setStack(0, ItemStack.EMPTY);
			}
			else
				return false;
		}
		else if(held.isEmpty())
		{
			inventory.setStack(0, mainHand ? player.getMainHandStack().copy() : player.getOffHandStack().copy());
			if(!player.isCreative())
				(mainHand ? playerInventory.main : playerInventory.offHand).set(mainHand ? playerInventory.selectedSlot : 0, ItemStack.EMPTY);
		}
		if(world != null && sacrificial && !getKey().isEmpty() && getHeld().getItem().equals(getKey().getItem()))
		{
			sacrificePending = true;
			if(world.getBlockState(getPos()).getBlock() instanceof AbstractPedestalBlock pedestalBlock)
				world.scheduleBlockTick(getPos(), pedestalBlock, 20);
		}
		if(world != null)
			world.updateListeners(pos, getCachedState(), getCachedState(), Block.NOTIFY_ALL);
		//if both stacks are not empty, do nothing, but don't count it as punching a regular block.
		return true;
	}
	
	public void readNbt(NbtCompound nbt)
	{
		super.readNbt(nbt);
		if (nbt.contains("holding", NbtElement.COMPOUND_TYPE))
			inventory.setStack(0, ItemStack.fromNbt(nbt.getCompound("holding")));
		else
			inventory.setStack(0, ItemStack.EMPTY);
		if (nbt.contains("key", NbtElement.COMPOUND_TYPE))
			inventory.setStack(1, ItemStack.fromNbt(nbt.getCompound("key")));
		else
			inventory.setStack(1, ItemStack.EMPTY);
		if(nbt.contains("locked", NbtElement.BYTE_TYPE) && world != null)
			world.setBlockState(getPos(), getCachedState().with(AbstractPedestalBlock.LOCKED, nbt.getBoolean("locked")));
		if(nbt.contains("decorative", NbtElement.BYTE_TYPE))
			decorative = nbt.getBoolean("decorative");
		if(nbt.contains("sacrificial", NbtElement.BYTE_TYPE))
			sacrificial = nbt.getBoolean("sacrificial");
		if(nbt.contains("sacrificeSuccess", NbtElement.BYTE_TYPE))
			sacrificeSuccess = nbt.getBoolean("sacrificeSuccess");
	}
	
	protected void writeNbt(NbtCompound nbt)
	{
		super.writeNbt(nbt);
		nbt.put("holding", getHeld().writeNbt(new NbtCompound()));
		nbt.put("key", getKey().writeNbt(new NbtCompound()));
		nbt.putBoolean("fancy", getCachedState().get(AbstractPedestalBlock.FANCY));
		nbt.putBoolean("locked", getCachedState().get(AbstractPedestalBlock.LOCKED));
		nbt.putBoolean("decorative", decorative);
		nbt.putBoolean("sacrificial", sacrificial);
		nbt.putBoolean("sacrificeSuccess", sacrificeSuccess);
	}
	
	@Override
	public Packet<ClientPlayPacketListener> toUpdatePacket()
	{
		return BlockEntityUpdateS2CPacket.create(this);
	}
	
	@Override
	public NbtCompound toInitialChunkDataNbt()
	{
		return createNbt();
	}
	
	public ItemStack getHeld()
	{
		return inventory.getStack(0);
	}
	
	public ItemStack getKey()
	{
		return inventory.getStack(1);
	}
	
	public boolean isFancy()
	{
		return getCachedState().get(AbstractPedestalBlock.FANCY);
	}
	
	public boolean isSacrificeSuccess()
	{
		return sacrificeSuccess;
	}
	
	public void sacrifice()
	{
		if(world == null)
			return;
		if(!world.isClient)
		{
			List<PlayerEntity> nearby = getWorld().getEntitiesByType(TypeFilter.instanceOf(PlayerEntity.class), new Box(getPos()).expand(32), e -> true);
			PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
			buf.writeFloat(20f);
			buf.writeDouble(getPos().getX() + 0.5f);
			buf.writeDouble(getPos().up().getY());
			buf.writeDouble(getPos().getZ() + 0.5f);
			buf.writeDouble(0f);
			buf.writeBoolean(false);
			for (PlayerEntity player : nearby)
				ServerPlayNetworking.send((ServerPlayerEntity)player, PacketRegistry.BLEED_PACKET_ID, buf);
		}
		world.playSound(null, getPos(), SoundRegistry.SACRIFICE, SoundCategory.HOSTILE, 1f, 1f);
		inventory.setStack(0, ItemStack.EMPTY);
		world.updateListeners(pos, getCachedState(), getCachedState(), Block.NOTIFY_ALL);
		sacrificeSuccess = true;
	}
	
	@Override
	public Text getDisplayName()
	{
		return Text.translatable("screen.ultracraft.pedestal.title");
	}
	
	@Nullable
	@Override
	public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player)
	{
		return new PedestalScreenHandler(ScreenHandlerRegistry.PEDESTAL, syncId, playerInventory, inventory, getPos());
	}
}
