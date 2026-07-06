package absolutelyaya.ultracraft.recipe;

import absolutelyaya.ultracraft.components.UltraComponents;
import absolutelyaya.ultracraft.components.player.IProgressionComponent;
import absolutelyaya.ultracraft.registry.PacketRegistry;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.Pair;

import java.util.List;
import java.util.Map;

public class UltraRecipe
{
	final Identifier id;
	final List<Ingredient> material;
	final Identifier result;
	final List<ItemStack> extraOutput;
	final List<Identifier> unlocks;
	
	public UltraRecipe(Identifier id, List<Ingredient> material, Identifier result, List<ItemStack> extraOutput, List<Identifier> unlocks)
	{
		this.id = id;
		this.material = material;
		this.result = result;
		this.extraOutput = extraOutput;
		this.unlocks = unlocks;
	}
	
	public int canCraft(PlayerEntity player)
	{
		if(UltraComponents.PROGRESSION.get(player).isOwned(result))
		{
			player.sendMessage(Text.translatable("terminal.craft.err1"), true);
			return 1; //result is already owned
		}
		if(player.isCreativeLevelTwoOp())
			return 2;
		int result = 2;
		for(Ingredient ingredient : material)
		{
			int count = 0;
			for (ItemStack stack : player.getInventory().main)
			{
				if(stack.isOf(ingredient.item))
				{
					count += stack.getCount();
					if(stack.hasCustomName() || stack.hasEnchantments())
						result = 3; //can craft, but some items that could get consumed might not be intended to be used for it
					if(count >= ingredient.amount)
						break; //item count is sufficient, lets stop counting
				}
			}
			if(count < ingredient.amount)
			{
				player.sendMessage(Text.translatable("terminal.craft.err0"), true);
				return 0; //an item is insufficient; cannot craft
			}
		}
		return result;
	}
	
	public void craft(PlayerEntity player)
	{
		if(canCraft(player) <= 1)
			return;
		if(player.getWorld().isClient)
		{
			PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
			buf.writeIdentifier(id);
			ClientPlayNetworking.send(PacketRegistry.TERMINAL_WEAPON_CRAFT_PACKET_ID, buf);
			return;
		}
		//grant owned progression Entry
		IProgressionComponent progression = UltraComponents.PROGRESSION.get(player);
		progression.obtain(result);
		for (Identifier unlockID : unlocks)
			progression.unlock(unlockID);
		progression.sync();
		//dispense extra output
		for (ItemStack stack : extraOutput)
			player.giveItemStack(stack);
		if(player.isCreativeLevelTwoOp())
			return;
		//consume items
		for(Ingredient ingredient : material)
		{
			int count = ingredient.amount;
			for (ItemStack stack : player.getInventory().main)
			{
				if(!stack.isOf(ingredient.item))
					continue;
				int decrement = Math.min(count, stack.getCount());
				stack.decrement(decrement);
				count -= decrement;
			}
		}
	}
	
	public static UltraRecipe deserialize(Identifier id, JsonObject json)
	{
		JsonArray materialsIn = json.getAsJsonArray("materials");
		ImmutableList.Builder<Ingredient> materialsBuilder = ImmutableList.builder();
		materialsIn.forEach(i -> {
			JsonObject object = i.getAsJsonObject();
			if(object.has("item") && object.has("count"))
			{
				String spriteOverride = null;
				if(object.has("sprite-override"))
					spriteOverride = JsonHelper.getString(object, "sprite-override");
				materialsBuilder.add(new Ingredient(JsonHelper.getItem(object, "item"), JsonHelper.getInt(object, "count"), spriteOverride));
			}
		});
		Identifier result = Identifier.tryParse(json.get("result").getAsString());
		JsonArray extraOutputsIn = json.getAsJsonArray("extra-outputs");
		ImmutableList.Builder<ItemStack> extraOutputsBuilder = ImmutableList.builder();
		if(extraOutputsIn != null)
		{
			extraOutputsIn.forEach(i -> {
				JsonObject object = i.getAsJsonObject();
				if(object.has("item") && object.has("count"))
					extraOutputsBuilder.add(new ItemStack(JsonHelper.getItem(object, "item"), JsonHelper.getInt(object, "count")));
			});
		}
		JsonArray unlocksIn = json.getAsJsonArray("unlocks");
		ImmutableList.Builder<Identifier> unlocksBuilder = ImmutableList.builder();
		if(unlocksIn != null)
			unlocksIn.forEach(i -> unlocksBuilder.add(Identifier.tryParse(i.getAsString())));
		return new UltraRecipe(id, materialsBuilder.build(), result, extraOutputsBuilder.build(), unlocksBuilder.build());
	}
	
	public JsonObject serialize()
	{
		JsonObject json = new JsonObject();
		JsonArray materials = new JsonArray();
		for (Ingredient entry : this.material)
		{
			JsonObject object = new JsonObject();
			object.add("item", new JsonPrimitive(Registries.ITEM.getId(entry.item).toString()));
			object.add("count", new JsonPrimitive(entry.amount));
			object.add("sprite-override", new JsonPrimitive(entry.spriteOverride));
		}
		json.add("materials", materials);
		json.add("result", new JsonPrimitive(result.toString()));
		JsonArray extraOutputs = new JsonArray();
		for (ItemStack entry : this.extraOutput)
		{
			JsonObject object = new JsonObject();
			object.add("item", new JsonPrimitive(Registries.ITEM.getId(entry.getItem()).toString()));
			object.add("count", new JsonPrimitive(entry.getCount()));
		}
		json.add("extra-outputs", extraOutputs);
		return json;
	}
	
	public static void serialize(PacketByteBuf buf, Map.Entry<Identifier, UltraRecipe> pair)
	{
		UltraRecipe recipe = pair.getValue();
		NbtCompound nbt = new NbtCompound();
		nbt.putString("id", pair.getKey().toString());
		NbtList materials = new NbtList();
		for (Ingredient entry : recipe.material)
		{
			NbtCompound mat = new NbtCompound();
			mat.putString("item", Registries.ITEM.getId(entry.item).toString());
			mat.putInt("count", entry.amount);
			if(entry.spriteOverride != null)
				mat.putString("sprite-override", entry.spriteOverride);
			materials.add(mat);
		}
		nbt.put("materials", materials);
		nbt.putString("result", recipe.result.toString());
		NbtList extraOutputs = new NbtList();
		for (ItemStack entry : recipe.extraOutput)
		{
			NbtCompound stack = new NbtCompound();
			entry.writeNbt(stack);
			extraOutputs.add(stack);
		}
		nbt.put("extra-outputs", extraOutputs);
		NbtList unlocks = new NbtList();
		for (Identifier entry : recipe.unlocks)
			unlocks.add(NbtString.of(entry.toString()));
		nbt.put("unlocks", unlocks);
		buf.writeNbt(nbt);
	}
	
	public static Pair<Identifier, UltraRecipe> deserialize(PacketByteBuf buf)
	{
		NbtCompound nbt = buf.readNbt();
		Identifier id = Identifier.tryParse(nbt.getString("id"));
		NbtList materialsIn = nbt.getList("materials", NbtElement.COMPOUND_TYPE);
		ImmutableList.Builder<Ingredient> materialsBuilder = ImmutableList.builder();
		materialsIn.forEach(i -> {
			NbtCompound object = (NbtCompound)i;
			if(object.contains("item") && object.contains("count"))
			{
				String spriteOverride = null;
				if(object.contains("sprite-override"))
					spriteOverride = object.getString("sprite-override");
				materialsBuilder.add(new Ingredient(Registries.ITEM.get(Identifier.tryParse(object.getString("item"))), object.getInt("count"), spriteOverride));
			}
		});
		Identifier result = Identifier.tryParse(nbt.getString("result"));
		NbtList extraOutputsIn = nbt.getList("extra-outputs", NbtElement.COMPOUND_TYPE);
		ImmutableList.Builder<ItemStack> extraOutputsBuilder = ImmutableList.builder();
		extraOutputsIn.forEach(i -> {
			NbtCompound object = (NbtCompound)i;
			if(object.contains("item"))
				extraOutputsBuilder.add(ItemStack.fromNbt(object.getCompound("item")));
		});
		NbtList unlocksIn = nbt.getList("unlocks", NbtElement.STRING_TYPE);
		ImmutableList.Builder<Identifier> unlocksBuilder = ImmutableList.builder();
		if(unlocksIn != null)
			unlocksIn.forEach(i -> unlocksBuilder.add(Identifier.tryParse(i.asString())));
		return new Pair<>(id, new UltraRecipe(id, materialsBuilder.build(), result, extraOutputsBuilder.build(), unlocksBuilder.build()));
	}
	
	public List<Ingredient> getMaterials()
	{
		return material;
	}
	
	public List<Identifier> getUnlocks()
	{
		return unlocks;
	}
	
	public record Ingredient(Item item, int amount, String spriteOverride)
	{
	
	}
}
