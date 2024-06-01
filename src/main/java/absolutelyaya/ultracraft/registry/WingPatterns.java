package absolutelyaya.ultracraft.registry;

import absolutelyaya.ultracraft.Ultracraft;
import absolutelyaya.ultracraft.client.UltracraftClient;
import com.google.common.collect.ImmutableMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.CoreShaderRegistrationCallback;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class WingPatterns
{
	private static ShaderProgram gamerProgram, sunburstProgram, monochromeProgram, ripplesProgram, ripplesClrProgram;
	private static ShaderProgram gamerPreviewProgram, sunburstPreviewProgram, monochromePreviewProgram, ripplesPreviewProgram, ripplesClrPreviewProgram;
	private static final Map<String, Pattern> patterns;
	private static final Map<String, Overlay> overlays;
	
	public static void init()
	{
		CoreShaderRegistrationCallback.EVENT.register((callback) -> {
			callback.register(new Identifier(Ultracraft.MOD_ID, "wing-patterns/gamer"), VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, (program) -> {
				program.getUniform("MetalColor");
				program.getUniform("WingColor");
				program.markUniformsDirty();
				gamerProgram = program;
			});
			callback.register(new Identifier(Ultracraft.MOD_ID, "wing-patterns/sunburst"), VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, (program) -> {
				program.getUniform("MetalColor");
				program.getUniform("WingColor");
				program.markUniformsDirty();
				sunburstProgram = program;
			});
			callback.register(new Identifier(Ultracraft.MOD_ID, "wing-patterns/monochrome"), VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, (program) -> {
				program.getUniform("MetalColor");
				program.getUniform("WingColor");
				program.markUniformsDirty();
				monochromeProgram = program;
			});
			callback.register(new Identifier(Ultracraft.MOD_ID, "wing-patterns/ripples"), VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, (program) -> {
				program.getUniform("MetalColor");
				program.getUniform("WingColor");
				program.markUniformsDirty();
				ripplesProgram = program;
			});
			callback.register(new Identifier(Ultracraft.MOD_ID, "wing-patterns/ripples-clr"), VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, (program) -> {
				program.getUniform("MetalColor");
				program.getUniform("WingColor");
				program.markUniformsDirty();
				ripplesClrProgram = program;
			});
			callback.register(new Identifier(Ultracraft.MOD_ID, "wing-patterns/preview/gamer"), VertexFormats.POSITION_TEXTURE_COLOR, (program) -> {
				program.getUniform("MetalColor");
				program.getUniform("WingColor");
				program.markUniformsDirty();
				gamerPreviewProgram = program;
			});
			callback.register(new Identifier(Ultracraft.MOD_ID, "wing-patterns/preview/sunburst"), VertexFormats.POSITION_TEXTURE_COLOR, (program) -> {
				program.getUniform("MetalColor");
				program.getUniform("WingColor");
				program.markUniformsDirty();
				sunburstPreviewProgram = program;
			});
			callback.register(new Identifier(Ultracraft.MOD_ID, "wing-patterns/preview/monochrome"), VertexFormats.POSITION_TEXTURE_COLOR, (program) -> {
				program.getUniform("MetalColor");
				program.getUniform("WingColor");
				program.markUniformsDirty();
				monochromePreviewProgram = program;
			});
			callback.register(new Identifier(Ultracraft.MOD_ID, "wing-patterns/preview/ripples"), VertexFormats.POSITION_TEXTURE_COLOR, (program) -> {
				program.getUniform("MetalColor");
				program.getUniform("WingColor");
				program.markUniformsDirty();
				ripplesPreviewProgram = program;
			});
			callback.register(new Identifier(Ultracraft.MOD_ID, "wing-patterns/preview/ripples-clr"), VertexFormats.POSITION_TEXTURE_COLOR, (program) -> {
				program.getUniform("MetalColor");
				program.getUniform("WingColor");
				program.markUniformsDirty();
				ripplesClrPreviewProgram = program;
			});
		});
	}
	
	public static Pattern getAnimated(String id)
	{
		return patterns.get(id);
	}
	
	public static List<String> getAllAnimatedIDs()
	{
		return patterns.keySet().stream().toList();
	}
	
	public static Overlay getOverlay(String id)
	{
		return overlays.get(id);
	}
	
	public static List<String> getAllOverlayIDs()
	{
		return overlays.keySet().stream().toList();
	}
	
	public static ShaderProgram getGamerShaderProgram()
	{
		return gamerProgram;
	}
	
	public static ShaderProgram getGamerPreviewShaderProgram()
	{
		return gamerPreviewProgram;
	}
	
	public static ShaderProgram getSunburstShaderProgram()
	{
		return sunburstProgram;
	}
	
	public static ShaderProgram getSunburstPreviewShaderProgram()
	{
		return sunburstPreviewProgram;
	}
	
	public static ShaderProgram getMonochromeShaderProgram()
	{
		return monochromeProgram;
	}
	
	public static ShaderProgram getMonochromePreviewShaderProgram()
	{
		return monochromePreviewProgram;
	}
	
	public static ShaderProgram getRipplesShaderProgram()
	{
		return ripplesProgram;
	}
	
	public static ShaderProgram getRipplesPreviewShaderProgram()
	{
		return ripplesPreviewProgram;
	}
	
	public static ShaderProgram getRipplesClrShaderProgram()
	{
		return ripplesClrProgram;
	}
	
	public static ShaderProgram getRipplesClrPreviewShaderProgram()
	{
		return ripplesClrPreviewProgram;
	}
	
	public static Supplier<ShaderProgram> getProgram(String id)
	{
		if(!patterns.containsKey(id))
			return patterns.get("none").program;
		return patterns.get(id).program;
	}
	
	static
	{
		float divider = 1f / 255f;
		ImmutableMap.Builder<String, Pattern> patternBuilder = ImmutableMap.builder();
		patternBuilder.put("none", new Pattern(UltracraftClient::getWingsColoredShaderProgram, UltracraftClient::getWingsColoredUIShaderProgram,
				new Vec3d(255, 255, 255).multiply(divider), false));
		patternBuilder.put("gamer", new Pattern(WingPatterns::getGamerShaderProgram, WingPatterns::getGamerPreviewShaderProgram,
				new Vec3d(255, 255, 255).multiply(divider), true));
		patternBuilder.put("sunburst", new Pattern(WingPatterns::getSunburstShaderProgram, WingPatterns::getSunburstPreviewShaderProgram,
				new Vec3d(184, 0, 0).multiply(divider), true));
		patternBuilder.put("monochrome", new Pattern(WingPatterns::getMonochromeShaderProgram, WingPatterns::getMonochromePreviewShaderProgram,
				new Vec3d(255, 255, 255).multiply(divider), true));
		patternBuilder.put("ripples", new Pattern(WingPatterns::getRipplesShaderProgram, WingPatterns::getRipplesPreviewShaderProgram,
				new Vec3d(255, 255, 255).multiply(divider), true));
		patternBuilder.put("ripples-clr", new Pattern(WingPatterns::getRipplesClrShaderProgram, WingPatterns::getRipplesClrPreviewShaderProgram,
				new Vec3d(255, 255, 255).multiply(divider), true));
		patterns = patternBuilder.build();
		
		ImmutableMap.Builder<String, Overlay> overlayBuilder = ImmutableMap.builder();
		overlayBuilder.put("rainbow", new Overlay(new Vec3d(255, 255, 255).multiply(divider), false));
		overlayBuilder.put("trans", new Overlay(new Vec3d(108, 191, 242).multiply(divider), false));
		overlayBuilder.put("bi", new Overlay(new Vec3d(105, 120, 255).multiply(divider), false));
		overlayBuilder.put("pan", new Overlay(new Vec3d(255, 105, 79).multiply(divider), false));
		overlayBuilder.put("enby", new Overlay(new Vec3d(255, 170, 43).multiply(divider), false));
		overlayBuilder.put("queer", new Overlay(new Vec3d(92, 155, 233).multiply(divider), false));
		overlayBuilder.put("lesbian", new Overlay(new Vec3d(224, 133, 219).multiply(divider), false));
		overlayBuilder.put("aromantic", new Overlay(new Vec3d(42, 168, 36).multiply(divider), false));
		overlayBuilder.put("ace", new Overlay(new Vec3d(145, 147, 255).multiply(divider), false));
		overlayBuilder.put("fluid", new Overlay(new Vec3d(249, 61, 70).multiply(divider), false));
		overlayBuilder.put("androgyne", new Overlay(new Vec3d(231, 161, 233).multiply(divider), false));
		overlayBuilder.put("poly", new Overlay(new Vec3d(165, 141, 224).multiply(divider), false));
		overlays = overlayBuilder.build();
	}
	
	public record Pattern(Supplier<ShaderProgram> program, Supplier<ShaderProgram> previewProgram, Vec3d textColor, boolean hasFlavor) {}
	
	public record Overlay(Vec3d textColor, boolean hasFlavor) {}
}
