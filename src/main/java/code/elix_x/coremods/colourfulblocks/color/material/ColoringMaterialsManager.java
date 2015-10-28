package code.elix_x.coremods.colourfulblocks.color.material;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import code.elix_x.coremods.colourfulblocks.ColourfulBlocksBase;
import code.elix_x.coremods.colourfulblocks.color.material.ColoringMaterialsManager.GsonConversionOldBrushes.GsonConversionOldBrush;
import code.elix_x.coremods.colourfulblocks.color.material.ColoringMaterialsManager.GsonMaterialsConversion.GsonMaterialConversion;
import code.elix_x.coremods.colourfulblocks.color.material.ColoringMaterialsManager.GsonMaterialsConversion.GsonMaterialConversion.GsonConversionRecipeEntry;
import code.elix_x.coremods.colourfulblocks.color.material.ColoringMaterialsManager.GsonRecipesConversion.GsonRecipeHandlerConversion;
import code.elix_x.coremods.colourfulblocks.color.material.ColoringMaterialsManager.GsonRecipesConversion.GsonRecipeHandlerConversion.GsonRecipeConversion;
import code.elix_x.excore.utils.color.RGBA;
import code.elix_x.excore.utils.items.ItemStackStringTranslator;
import code.elix_x.excore.utils.math.AdvancedMathUtils;
import code.elix_x.excore.utils.recipes.RecipeStringTranslator;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;

public class ColoringMaterialsManager {

	public static final String RECIPENAMENULL = "NULL";
	public static final String RECIPENAMEVANILLA = "VANILLA";
	public static final String RECIPETYPEBRUSH = "BRUSH";
	public static final String RECIPEENTRYMATERIAL = "<MATERIAL>";
	public static final String COLORINGTOOLLANG = "item.coloringtool";
	public static final String COLORINGTOOLNAMEORDERLANG = COLORINGTOOLLANG + ".nameorder";
	public static final String COLORINGTOOLMATERIALLANG = "coloringtoolmaterial";

	public static final Logger logger = LogManager.getLogger("CoB Materials Manager");

	public static Gson gson = new GsonBuilder().disableHtmlEscaping().create();

	public static File extensionsDir;

	private static Map<Pair<String, String>, String[]> recipes = new HashMap<Pair<String,String>, String[]>();

	private static Map<ColoringToolMaterial, Pair<String, Map<String, Object>>> materialRecipe = new HashMap<ColoringToolMaterial, Pair<String, Map<String, Object>>>();

	public static void init(){
		extensionsDir = new File(ColourfulBlocksBase.configFolder, "extensions");
		if(!extensionsDir.exists()){
			extensionsDir.mkdirs();
			initMaterials();
			initRecipes();
			initLocalisations();
		}
		updateOldStuff();
		if(FMLCommonHandler.instance().getSide() == Side.CLIENT && ColourfulBlocksBase.mainConfig.getBoolean("fixColors", "json", true, "Fix color for items with color \"0\" or \"0:0:0\" or \"0:0:0:0\", but with valid crafting item.")){
			fixColors();
		}
		loadMaterials();
		loadRecipes();
		loadLocalisations();
	}

	@Deprecated
	private static void updateOldStuff() {
		File oldFile = new File(ColourfulBlocksBase.configFolder, "brushes.json");
		if(oldFile.exists()){
			try {
				JsonReader reader = new JsonReader(new FileReader(oldFile));
				GsonConversionOldBrushes brushesOld = gson.fromJson(reader, GsonConversionOldBrushes.class);
				GsonMaterialsConversion brushesNew = new GsonMaterialsConversion(new ArrayList<GsonMaterialConversion>());
				for(GsonConversionOldBrush oldBrush : brushesOld.brushes){
					brushesNew.materials.add(new GsonMaterialConversion(oldBrush.name, oldBrush.durability, oldBrush.buffer, oldBrush.color, RECIPENAMEVANILLA, new GsonConversionRecipeEntry(RECIPEENTRYMATERIAL, oldBrush.ingredient.replace("oredictionary:", ItemStackStringTranslator.OREDICT + ":"))));
				}
				reader.close();
				File portedDir = new File(extensionsDir, "ported");
				portedDir.mkdir();
				File newFile = new File(portedDir, "materials.json");
				newFile.createNewFile();
				JsonWriter writer = new JsonWriter(new FileWriter(newFile));
				gson.toJson(brushesNew, GsonMaterialsConversion.class, writer);
				writer.close();
				oldFile.delete();
				File oldLangDir = new File(ColourfulBlocksBase.configFolder, "lang");
				oldLangDir.mkdir();
				File newLangDir = new File(portedDir, "lang");
				newLangDir.mkdir();
				for(File oldLangFile : oldLangDir.listFiles(new FileFilter() {

					@Override
					public boolean accept(File file) {
						return file.getName().endsWith(".lang");
					}

				})){
					File newLangFile = new File(newLangDir, oldLangFile.getName());
					newLangFile.createNewFile();
					FileUtils.writeLines(newLangFile, Lists.transform(FileUtils.readLines(oldLangFile), new Function<String, String>(){

						@Override
						public String apply(String input) {
							return input.replace("brushmaterial", COLORINGTOOLMATERIALLANG);
						}
					}));
				}
				FileUtils.deleteDirectory(oldLangDir);
			} catch(IOException e){
				logger.error("Caught exception while porting old configuration: ", e);
			}
		}
	}

	@Deprecated
	public static class GsonConversionOldBrushes {

		private List<GsonConversionOldBrush> brushes;

		@Deprecated
		public static class GsonConversionOldBrush {

			private String name;
			private int durability;
			private int buffer;
			private String color;
			private String ingredient;

		}

	}

	/*
	 * Static getters
	 */

	public static Map<ColoringToolMaterial, Pair<String, Map<String, Object>>> getAllMaterialsAndRecipes() {
		return materialRecipe;
	}

	public static String[] getRecipe(String name, String type){
		return recipes.get(new ImmutablePair<String, String>(name, type));
	}

	/*
	 * Helpers
	 */

	private static ItemStack recognizeRepairItem(ToolMaterial material) {
		return material.getRepairItemStack();
	}

	@SideOnly(Side.CLIENT)
	private static String recognizeColorToString(ToolMaterial material) {
		return recognizeColorToString(recognizeRepairItem(material));
	}

	@SideOnly(Side.CLIENT)
	private static String recognizeColorToString(ItemStack itemstack) {
		RGBA rgba = recognizeColorToRGBA(itemstack);
		return rgba.r + ":" + rgba.g + ":" + rgba.b;
	}

	@SideOnly(Side.CLIENT)
	private static RGBA recognizeColorToRGBA(ToolMaterial material) {
		return recognizeColorToRGBA(recognizeRepairItem(material));
	}

	@SideOnly(Side.CLIENT)
	private static RGBA recognizeColorToRGBA(ItemStack itemstack) {
		RGBA color = new RGBA(0, 0, 0, 0);
		if(itemstack != null){
			ResourceLocation texture = null;
			if(Block.getBlockFromItem(itemstack.getItem()) != Blocks.air){
				Block block = Block.getBlockFromItem(itemstack.getItem());
				String textureName = ObfuscationReflectionHelper.getPrivateValue(Block.class, block, "textureName", "field_149768_d");
				if(textureName != null){
					if(textureName.split(":").length == 1){
						textureName = "minecraft:" + textureName;
					}
					texture = new ResourceLocation(textureName.split(":")[0], "textures/blocks/" + textureName.split(":")[1] + ".png");
				}
			} else {
				Item item = itemstack.getItem();
				String textureName = ObfuscationReflectionHelper.getPrivateValue(Item.class, item, "iconString");
				if(textureName != null){
					if(textureName.split(":").length == 1){
						textureName = "minecraft:" + textureName;
					}
					texture = new ResourceLocation(textureName.split(":")[0], "textures/items/" + textureName.split(":")[1] + ".png");
				}
			}
			if(texture != null){
				int[] colorBuff = new int[]{};
				try {
					colorBuff = TextureUtil.readImageData(Minecraft.getMinecraft().getResourceManager(), texture);
				} catch (IOException e) {
					logger.error("Caught exception while parsing texture to get color: ", e);
				}
				int[] red = new int[]{};
				int[] green = new int[]{};
				int[] blue = new int[]{};
				for(int c : colorBuff){
					Color col = new Color(c);
					if(col.getAlpha() > 0){
						red = ArrayUtils.add(red, col.getRed());
						green = ArrayUtils.add(green, col.getGreen());
						blue = ArrayUtils.add(blue, col.getBlue());
					}
				}
				if(red.length > 0 && green.length > 0 && blue.length > 0){
					int r = AdvancedMathUtils.average(red[0], ArrayUtils.subarray(red, 1, red.length));
					int g = AdvancedMathUtils.average(green[0], ArrayUtils.subarray(green, 1, green.length));
					int b = AdvancedMathUtils.average(blue[0], ArrayUtils.subarray(blue, 1, blue.length));
					color = new RGBA(r, g, b);
				}
			}
		}
		return color;
	}

	/*
	 * Init Default
	 */

	private static void initMaterials() {
		try {
			initDefaultMaterials();
		} catch (IOException e) {
			logger.error("Caught exception while generating vanilla extension materials: ", e);
		}
		try {
			initGenMaterials();
		} catch (IOException e) {
			logger.error("Caught exception while generating generated extension: ", e);
		}
	}

	private static void initDefaultMaterials() throws IOException {
		File vanillaDir = new File(extensionsDir, "vanilla");
		vanillaDir.mkdirs();

		GsonMaterialsConversion conversion = new GsonMaterialsConversion(
				new GsonMaterialConversion(ToolMaterial.WOOD.name(), ToolMaterial.WOOD.getMaxUses(), ToolMaterial.WOOD.getHarvestLevel(), (110 + ":" + 65 + ":" + 43), RECIPENAMEVANILLA, new GsonConversionRecipeEntry(RECIPEENTRYMATERIAL, "oreDict:plankWood")),
				new GsonMaterialConversion(ToolMaterial.STONE.name(), ToolMaterial.STONE.getMaxUses(), ToolMaterial.STONE.getHarvestLevel(), (77 + ":" + 77 + ":" + 77), RECIPENAMEVANILLA, new GsonConversionRecipeEntry(RECIPEENTRYMATERIAL, "oreDict:cobblestone")),
				new GsonMaterialConversion(ToolMaterial.IRON.name(), ToolMaterial.IRON.getMaxUses(), ToolMaterial.IRON.getHarvestLevel(), (153 + ":" + 153 + ":" + 153), RECIPENAMEVANILLA, new GsonConversionRecipeEntry(RECIPEENTRYMATERIAL, "oreDict:ingotIron")),
				new GsonMaterialConversion(ToolMaterial.GOLD.name(), ToolMaterial.GOLD.getMaxUses(), ToolMaterial.GOLD.getHarvestLevel(), (186 + ":" + 154 + ":" + 9), RECIPENAMEVANILLA, new GsonConversionRecipeEntry(RECIPEENTRYMATERIAL, "oreDict:ingotGold")),
				new GsonMaterialConversion("DIAMOND", ToolMaterial.EMERALD.getMaxUses(), ToolMaterial.EMERALD.getHarvestLevel(), (39 + ":" + 207 + ":" + 230), RECIPENAMEVANILLA, new GsonConversionRecipeEntry(RECIPEENTRYMATERIAL, "oreDict:gemDiamond"))
				);

		File materials = new File(vanillaDir, "materials.json");
		materials.createNewFile();
		JsonWriter writer = new JsonWriter(new FileWriter(materials));
		writer.setIndent("	");
		gson.toJson(conversion, conversion.getClass(), writer);
		writer.close();
	}

	private static void initGenMaterials() throws IOException {
		File generatedDir = new File(extensionsDir, "generated");
		generatedDir.mkdirs();

		GsonMaterialsConversion conversion = new GsonMaterialsConversion(new ArrayList<ColoringMaterialsManager.GsonMaterialsConversion.GsonMaterialConversion>());
		for(ToolMaterial mat : ToolMaterial.values()){
			if(mat != ToolMaterial.WOOD && mat != ToolMaterial.STONE && mat != ToolMaterial.IRON && mat != ToolMaterial.GOLD && mat != ToolMaterial.EMERALD){
				conversion.materials.add(new GsonMaterialConversion(mat.name(), mat.getMaxUses(), mat.getHarvestLevel(), FMLCommonHandler.instance().getSide() == Side.CLIENT ? recognizeColorToString(mat) : "0:0:0", RECIPENAMEVANILLA, new GsonConversionRecipeEntry(RECIPEENTRYMATERIAL, ItemStackStringTranslator.toString(recognizeRepairItem(mat)))));
			}
		}

		File materials = new File(generatedDir, "materials.json");
		materials.createNewFile();
		JsonWriter writer = new JsonWriter(new FileWriter(materials));
		writer.setIndent("	");
		gson.toJson(conversion, conversion.getClass(), writer);
		writer.close();
	}

	private static void initRecipes() {
		try {
			initDefaultRecipes();
		} catch (IOException e) {
			logger.error("Caught exception while generating vanilla extension recipes: ", e);
		}
	}

	private static void initDefaultRecipes() throws IOException {
		File vanillaDir = new File(extensionsDir, "vanilla");
		vanillaDir.mkdirs();

		Map<String, String> map = new HashMap<String, String>();
		map.put(RECIPEENTRYMATERIAL, RECIPEENTRYMATERIAL);
		GsonRecipesConversion conversion = new GsonRecipesConversion(
				new GsonRecipeHandlerConversion(RECIPENAMEVANILLA, 
						new GsonRecipeConversion(RECIPETYPEBRUSH, RecipeStringTranslator.toString(map, "  #", " % ", "$  ", '#', Blocks.wool, '%', RECIPEENTRYMATERIAL, '$', "stickWood"))
						)
				);
		File materials = new File(vanillaDir, "recipes.json");
		materials.createNewFile();
		JsonWriter writer = new JsonWriter(new FileWriter(materials));
		writer.setIndent("	");
		gson.toJson(conversion, conversion.getClass(), writer);
		writer.close();
	}

	private static void initLocalisations() {
		try {
			initDefaultLocalisations();
		} catch (IOException e) {
			logger.error("Caught exception while generating vanilla extension localisations: ", e);
		}
		try {
			initGenLocalisations();
		} catch (IOException e) {
			logger.error("Caught exception while generating generated extension localisations: ", e);
		}
	}

	private static void initDefaultLocalisations() throws IOException {
		File vanillaDir = new File(extensionsDir, "vanilla");
		vanillaDir.mkdirs();
		File lang = new File(vanillaDir, "lang");
		lang.mkdir();
		File en_US = new File(lang, "en_US.lang");
		en_US.createNewFile();
		String s = "";
		s += COLORINGTOOLMATERIALLANG + ".WOOD=Wooden";
		s += "\n";
		s += COLORINGTOOLMATERIALLANG + ".STONE=Stone";
		s += "\n";
		s += COLORINGTOOLMATERIALLANG + ".IRON=Iron";
		s += "\n";
		s += COLORINGTOOLMATERIALLANG + ".GOLD=Golden";
		s += "\n";
		s += COLORINGTOOLMATERIALLANG + ".DIAMOND=Diamond";
		FileWriter writer = new FileWriter(en_US);
		writer.write(s);
		writer.close();
	}

	private static void initGenLocalisations() throws IOException {
		File generatedDir = new File(extensionsDir, "generated");
		generatedDir.mkdirs();
		File lang = new File(generatedDir, "lang");
		lang.mkdir();
		File en_US = new File(lang, "en_US.lang");
		en_US.createNewFile();
		String s = "";
		for(ToolMaterial mat : ToolMaterial.values()){
			if(mat != ToolMaterial.WOOD && mat != ToolMaterial.STONE && mat != ToolMaterial.IRON && mat != ToolMaterial.GOLD && mat != ToolMaterial.EMERALD){
				s += COLORINGTOOLMATERIALLANG + "." + mat.name();
				s += "=";
				s += mat.name().charAt(0) + mat.name().substring(1, mat.name().length()).toLowerCase();
				s += "\n";
			}
		}
		if(s.length() > 0){
			s = s.substring(0, s.length() - 1);
		}
		FileWriter writer = new FileWriter(en_US);
		writer.write(s);
		writer.close();
	}

	/*
	 * Fix
	 */

	private static void fixColors() {
		for(File file : extensionsDir.listFiles(new FileFilter() {

			@Override
			public boolean accept(File file) {
				return file.isDirectory();
			}

		})){
			File json = new File(file, "materials.json");
			if(json.exists()){
				try{
					JsonReader reader = new JsonReader(new FileReader(json));
					GsonMaterialsConversion mats = gson.fromJson(reader, GsonMaterialsConversion.class);
					for(GsonMaterialConversion mat : mats.materials){
						if(mat.color.equals("0") || mat.color.equals("0:0:0") || mat.color.equals("0:0:0:0")){
							if(!mat.ingredients.isEmpty()){
								int[] r = new int[0];
								int[] g = new int[0];
								int[] b = new int[0];
								for(GsonConversionRecipeEntry ing : mat.ingredients){
									if(ItemStackStringTranslator.isValidItemstack(ing.value)){
										RGBA rgba = recognizeColorToRGBA(ItemStackStringTranslator.fromString(ing.value));
										r = ArrayUtils.add(r, rgba.r);
										g = ArrayUtils.add(g, rgba.g);
										b = ArrayUtils.add(b, rgba.b);
									}
								}
								mat.color = AdvancedMathUtils.average(r[0], ArrayUtils.subarray(r, 1, r.length)) + ":" + AdvancedMathUtils.average(g[0], ArrayUtils.subarray(g, 1, g.length)) + ":" + AdvancedMathUtils.average(b[0], ArrayUtils.subarray(b, 1, b.length));
							}
						}
					}
					reader.close();
					JsonWriter writer = new JsonWriter(new FileWriter(json));
					writer.setIndent("	");
					gson.toJson(mats, GsonMaterialsConversion.class, writer);
					writer.close();
				} catch(IOException e){
					logger.error("Caught exception while reading recipes.json. It will be ignored!");
				}
			}
		}
	}

	/*
	 * Load
	 */

	private static void loadMaterials() {
		for(File file : extensionsDir.listFiles(new FileFilter() {

			@Override
			public boolean accept(File file) {
				return file.isDirectory();
			}

		})){
			File json = new File(file, "materials.json");
			if(json.exists()){
				try{
					JsonReader reader = new JsonReader(new FileReader(json));
					GsonMaterialsConversion mats = gson.fromJson(reader, GsonMaterialsConversion.class);
					for(GsonMaterialConversion mat : mats.materials){
						ColoringToolMaterial material;
						try{
							material = new ColoringToolMaterial(mat.name, mat.durability, Integer.parseInt(mat.color), mat.bufferMultiplier);
						} catch(NumberFormatException e){
							String[] s = mat.color.split(":");
							if(s.length == 3){
								material = new ColoringToolMaterial(mat.name, mat.durability, new RGBA(Integer.parseInt(s[0]), Integer.parseInt(s[1]), Integer.parseInt(s[2])), mat.bufferMultiplier);
							} else {
								material = new ColoringToolMaterial(mat.name, mat.durability, new RGBA(Integer.parseInt(s[0]), Integer.parseInt(s[1]), Integer.parseInt(s[2]), Integer.parseInt(s[3])), mat.bufferMultiplier);
							}
						}
						Map<String, Object> map = new HashMap<String, Object>();
						for(GsonConversionRecipeEntry r : mat.ingredients){
							map.put(r.name, ItemStackStringTranslator.fromStringAdvanced(r.value));
						}
						materialRecipe.put(material, new ImmutablePair<String, Map<String,Object>>(mat.recipe, map));
					}
					reader.close();
				} catch(IOException e){
					logger.error("Caught exception while reading recipes.json. It will be ignored!");
				}
			}
		}
	}

	private static void loadRecipes() {
		for(File file : extensionsDir.listFiles(new FileFilter() {

			@Override
			public boolean accept(File file) {
				return file.isDirectory();
			}

		})){
			File json = new File(file, "recipes.json");
			if(json.exists()){
				try{
					JsonReader reader = new JsonReader(new FileReader(json));
					GsonRecipesConversion recs = gson.fromJson(reader, GsonRecipesConversion.class);
					for(GsonRecipeHandlerConversion hand : recs.recipes){
						for(GsonRecipeConversion rec : hand.types){
							recipes.put(new ImmutablePair<String, String>(hand.name, rec.type), rec.recipe);
						}
					}
					reader.close();
				} catch(IOException e){
					logger.error("Caught exception while reading recipes.json. It will be ignored!");
				}
			}
		}
	}

	private static void loadLocalisations() {
		for(File file : extensionsDir.listFiles(new FileFilter() {

			@Override
			public boolean accept(File file) {
				return file.isDirectory();
			}

		})){
			File langDir = new File(file, "lang");
			for(File lang : langDir.listFiles(new FileFilter() {
				@Override
				public boolean accept(File file) {
					return !file.isDirectory() && file.getName().endsWith(".lang");
				}
			})){
				try{
					String language = lang.getName().substring(0, lang.getName().length() - 5);
					HashMap<String, String> map = new HashMap<String, String>();

					BufferedReader br = new BufferedReader(new FileReader(lang)) ;

					for(String line; (line = br.readLine()) != null;) {
						try{
							map.put(line.split("=")[0], line.split("=")[1]);
						} catch(IndexOutOfBoundsException e){

						}
					}

					br.close();

					LanguageRegistry.instance().injectLanguage(language, map);
				} catch(Exception e){
					logger.error("Caught exception while parsing lang file: ", e);
				}
			}

		}
	}

	/*
	 * Gson Conversion
	 */

	public static class GsonMaterialsConversion {

		private List<GsonMaterialConversion> materials;

		public GsonMaterialsConversion() {

		}		

		public GsonMaterialsConversion(List<GsonMaterialConversion> materials) {
			this.materials = materials;
		}

		public GsonMaterialsConversion(GsonMaterialConversion... materials){
			this.materials = Lists.newArrayList(materials);
		}

		public static class GsonMaterialConversion {

			private String name;
			private int durability;
			private double bufferMultiplier;
			private String color;
			private String recipe;
			private List<GsonConversionRecipeEntry> ingredients;

			public GsonMaterialConversion() {

			}

			public GsonMaterialConversion(String name, int durability, double bufferMultiplier, String color, String recipe, List<GsonConversionRecipeEntry> ingredients) {
				this.name = name;
				this.durability = durability;
				this.bufferMultiplier = bufferMultiplier;
				this.color = color;
				this.recipe = recipe;
				this.ingredients = ingredients;
			}

			public GsonMaterialConversion(String name, int durability, double bufferMultiplier, String color, String recipe, GsonConversionRecipeEntry... ingredients) {
				this(name, durability, bufferMultiplier, color, recipe, Lists.newArrayList(ingredients));
			}

			public static class GsonConversionRecipeEntry {

				private String name;
				private String value;

				public GsonConversionRecipeEntry() {

				}

				public GsonConversionRecipeEntry(String name, String value) {
					this.name = name;
					this.value = value;
				}

			}

		}

	}

	public static class GsonRecipesConversion {

		private List<GsonRecipeHandlerConversion> recipes;

		public GsonRecipesConversion() {

		}

		public GsonRecipesConversion(List<GsonRecipeHandlerConversion> recipes) {
			this.recipes = recipes;
		}

		public GsonRecipesConversion(GsonRecipeHandlerConversion... recipes){
			this(Lists.newArrayList(recipes));
		}

		public static class GsonRecipeHandlerConversion {

			private String name;
			private List<GsonRecipeConversion> types;

			public GsonRecipeHandlerConversion() {

			}

			public GsonRecipeHandlerConversion(String name, List<GsonRecipeConversion> types) {
				this.name = name;
				this.types = types;
			}

			public GsonRecipeHandlerConversion(String name, GsonRecipeConversion... types) {
				this(name, Lists.newArrayList(types));
			}

			public static class GsonRecipeConversion {

				private String type;
				private String[] recipe;

				public GsonRecipeConversion() {

				}

				public GsonRecipeConversion(String type, String... recipe) {
					this.type = type;
					this.recipe = recipe;
				}

			}
		}

	}

}
