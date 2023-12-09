package absolutelyaya.ultracraft.mixin.client;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(KeyBinding.class)
public abstract class KeybindMixin
{
	@Shadow public InputUtil.Key boundKey;
	@Shadow @Final private static Map<String, KeyBinding> KEYS_BY_ID;
	@Shadow @Final private static Map<InputUtil.Key, KeyBinding> KEY_TO_BINDINGS;
	
	@Shadow public abstract boolean equals(KeyBinding other);
	
	@Shadow private int timesPressed;
	
	@Shadow public abstract void setPressed(boolean pressed);
	
	private static final Multimap<InputUtil.Key, KeyBinding> binds = ArrayListMultimap.create();
	
	@Inject(method = "<init>(Ljava/lang/String;Lnet/minecraft/client/util/InputUtil$Type;ILjava/lang/String;)V", at = @At("TAIL"))
	void onInit(String translationKey, InputUtil.Type type, int code, String category, CallbackInfo ci)
	{
		binds.put(boundKey, (KeyBinding)(Object)this);
	}
	
	@Inject(method = "updateKeysByCode", at = @At(value = "HEAD"))
	private static void onUpdateKeysByCode(CallbackInfo ci)
	{
		binds.clear();
		for (KeyBinding bind : KEYS_BY_ID.values())
			binds.put(bind.boundKey, bind);
	}
	
	@Inject(method = "setKeyPressed", at = @At(value = "TAIL"))
	private static void onSetKeyPressed(InputUtil.Key key, boolean pressed, CallbackInfo ci)
	{
		for (KeyBinding i : binds.get(key))
		{
			if(i != null)
				((KeybindMixin)(Object)i).setPressed(pressed);
		}
	}
	
	@Inject(method = "onKeyPressed", at = @At(value = "TAIL"))
	private static void onKeyPressed(InputUtil.Key key, CallbackInfo ci)
	{
		for (KeyBinding i : binds.get(key))
		{
			if(i != null)
				((KeybindMixin)(Object)i).timesPressed++;
		}
	}
}
