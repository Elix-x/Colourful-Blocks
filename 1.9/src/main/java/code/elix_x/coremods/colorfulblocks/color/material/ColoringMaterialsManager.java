package code.elix_x.coremods.colorfulblocks.color.material;

import java.awt.Color;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import code.elix_x.coremods.colorfulblocks.ColourfulBlocksBase;
import code.elix_x.coremods.colorfulblocks.color.material.ColoringMaterialsManager.GsonMaterialsConversion.GsonMaterialConversion;
import code.elix_x.coremods.colorfulblocks.color.material.ColoringMaterialsManager.GsonMaterialsConversion.GsonMaterialConversion.GsonConversionRecipeEntry;
import code.elix_x.coremods.colorfulblocks.color.material.ColoringMaterialsManager.GsonRecipesConversion.GsonRecipeHandlerConversion;
import code.elix_x.coremods.colorfulblocks.color.material.ColoringMaterialsManager.GsonRecipesConversion.GsonRecipeHandlerConversion.GsonRecipeConversion;
import code.elix_x.excore.utils.color.RGBA;
import code.elix_x.excore.utils.items.ItemStackStringTranslator;
import code.elix_x.excore.utils.math.AdvancedMathUtils;
import code.elix_x.excore.utils.recipes.RecipeStringTranslator;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
			logger.info("Generating extensions");
			initMaterials();
			initRecipes();
			initLocalisations();
		}
		logger.info("Fixing extensions");
		if(FMLCommonHandler.instance().getSide() == Side.CLIENT && ColourfulBlocksBase.mainConfig.getBoolean("fixColors", "json", true, "Fix color for items with color \"0\" or \"0:0:0\" or \"0:0:0:0\", but with valid crafting item.")){
			fixColors();
		}
		logger.info("Loading extensions");
		loadMaterials();
		loadRecipes();
		loadLocalisations();
	}

	/*
	 * Static getters
	 */

	public static Map<ColoringToolMaterial, Pair<String, Map<String, Object>>> getAllMaterialsAndRecipes(){
		return materialRecipe;
	}

	public static String[] getRecipe(String name, String type){
		return recipes.get(new ImmutablePair<String, String>(name, type));
	}

	/*
	 * Helpers
	 */

	private static ItemStack recognizeRepairItem(ToolMaterial material){
		return material.getRepairItemStack();
	}

	@SideOnly(Side.CLIENT)
	private static RGBA recognizeColorToRGBA(ToolMaterial material){
		return recognizeColorToRGBA(recognizeRepairItem(material));
	}

	@SideOnly(Side.CLIENT)
	private static RGBA recognizeColorToRGBA(ItemStack itemstack){
		RGBA color = new RGBA(0, 0, 0, 0);
		if(itemstack != null && itemstack.getItem() != null){
			ResourceLocation texture = null;
			if(Block.getBlockFromItem(itemstack.getItem()) != Blocks.AIR){
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
				String textureName = ObfuscationReflectionHelper.getPrivateValue(Item.class, item, "iconString", "field_111218_cA");
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
				} catch (IOException e){
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
					int r = AdvancedMathUtils.average(red);
					int g = AdvancedMathUtils.average(green);
					int b = AdvancedMathUtils.average(blue);
					color = new RGBA(r, g, b);
				}
			}
		}
		return color;
	}

	/*
	 * Init Default
	 */

	private static void initMaterials(){
		try {
			logger.info("Generating vanilla extension materials");
			initDefaultMaterials();
		} catch (IOException e){
			logger.error("Caught exception while generating vanilla extension materials: ", e);
		}
		try {
			logger.info("Generating generated extension materials");
			initGenMaterials();
		} catch (IOException e){
			logger.error("Caught exception while generating generated extension materials: ", e);
		}
	}

	private static void initDefaultMaterials() throws IOException {
		File vanillaDir = new File(extensionsDir, "vanilla");
		vanillaDir.mkdirs();

		GsonMaterialsConversion conversion = new GsonMaterialsConversion(
				new GsonMaterialConversion(ToolMaterial.WOOD.name(), ToolMaterial.WOOD.getMaxUses(), ToolMaterial.WOOD.getHarvestLevel(), new RGBA(110, 65, 43), RECIPENAMEVANILLA, new GsonConversionRecipeEntry(RECIPEENTRYMATERIAL, "oreDict:plankWood")),
				new GsonMaterialConversion(ToolMaterial.STONE.name(), ToolMaterial.STONE.getMaxUses(), ToolMaterial.STONE.getHarvestLevel(), new RGBA(77, 77, 77), RECIPENAMEVANILLA, new GsonConversionRecipeEntry(RECIPEENTRYMATERIAL, "oreDict:cobblestone")),
				new GsonMaterialConversion(ToolMaterial.IRON.name(), ToolMaterial.IRON.getMaxUses(), ToolMaterial.IRON.getHarvestLevel(), new RGBA(153, 153, 153), RECIPENAMEVANILLA, new GsonConversionRecipeEntry(RECIPEENTRYMATERIAL, "oreDict:ingotIron")),
				new GsonMaterialConversion(ToolMaterial.GOLD.name(), ToolMaterial.GOLD.getMaxUses(), ToolMaterial.GOLD.getHarvestLevel(), new RGBA(186, 154, 9), RECIPENAMEVANILLA, new GsonConversionRecipeEntry(RECIPEENTRYMATERIAL, "oreDict:ingotGold")),
				new GsonMaterialConversion(ToolMaterial.DIAMOND.name(), ToolMaterial.DIAMOND.getMaxUses(), ToolMaterial.DIAMOND.getHarvestLevel(), new RGBA(39, 207, 230), RECIPENAMEVANILLA, new GsonConversionRecipeEntry(RECIPEENTRYMATERIAL, "oreDict:gemDiamond"))
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
			if(mat != ToolMaterial.WOOD && mat != ToolMaterial.STONE && mat != ToolMaterial.IRON && mat != ToolMaterial.GOLD && mat != ToolMaterial.DIAMOND){
				logger.debug("Found modded tool material. Generating coloring tool material from it.");
				conversion.materials.add(new GsonMaterialConversion(mat.name(), mat.getMaxUses(), mat.getHarvestLevel(), FMLCommonHandler.instance().getSide() == Side.CLIENT ? recognizeColorToRGBA(mat) : new RGBA(0, 0, 0), RECIPENAMEVANILLA, new GsonConversionRecipeEntry(RECIPEENTRYMATERIAL, ItemStackStringTranslator.toString(recognizeRepairItem(mat)))));
			}
		}

		File materials = new File(generatedDir, "materials.json");
		materials.createNewFile();
		JsonWriter writer = new JsonWriter(new FileWriter(materials));
		writer.setIndent("	");
		gson.toJson(conversion, conversion.getClass(), writer);
		writer.close();
	}

	private static void initRecipes(){
		try {
			logger.info("Generating vanilla extension recipes");
			initDefaultRecipes();
		} catch (IOException e){
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
						new GsonRecipeConversion(RECIPETYPEBRUSH, RecipeStringTranslator.toString(map, "  #", " % ", "$  ", '#', Blocks.WOOL, '%', RECIPEENTRYMATERIAL, '$', "stickWood"))
						)
				);
		File materials = new File(vanillaDir, "recipes.json");
		materials.createNewFile();
		JsonWriter writer = new JsonWriter(new FileWriter(materials));
		writer.setIndent("	");
		gson.toJson(conversion, conversion.getClass(), writer);
		writer.close();
	}

	private static void initLocalisations(){
		try {
			logger.info("Generating vanilla extension localisations");
			initDefaultLocalisations();
		} catch (IOException e){
			logger.error("Caught exception while generating vanilla extension localisations: ", e);
		}
		try {
			logger.info("Generating generated extension localisations");
			initGenLocalisations();
		} catch (IOException e){
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
			if(mat != ToolMaterial.WOOD && mat != ToolMaterial.STONE && mat != ToolMaterial.IRON && mat != ToolMaterial.GOLD && mat != ToolMaterial.DIAMOND){
				logger.debug("Found modded tool material. Generating coloring tool material localisations from it.");
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

	private static void fixColors(){
		logger.info("Fixing colors.");
		for(File file : extensionsDir.listFiles(new FileFilter(){

			@Override
			public boolean accept(File file){
				return file.isDirectory();
			}

		})){
			File json = new File(file, "materials.json");
			if(json.exists()){
				try{
					logger.info("Fixing colors in extension " + file.getName());
					JsonReader reader = new JsonReader(new FileReader(json));
					GsonMaterialsConversion mats = gson.fromJson(reader, GsonMaterialsConversion.class);
					for(GsonMaterialConversion mat : mats.materials){
						if(mat.color.equals("0") || mat.color.equals("0:0:0") || mat.color.equals("0:0:0:0")){
							logger.debug("Found material that needs color fixing: " + mat.name);
							if(!mat.ingredients.isEmpty()){
								int[] r = new int[0];
								int[] g = new int[0];
								int[] b = new int[0];
								for(GsonConversionRecipeEntry ing : mat.ingredients){
									if(ItemStackStringTranslator.isValidItemstack(ing.value)){
										RGBA rgba = recognizeColorToRGBA(ItemStackStringTranslator.fromString(ing.value));
										r = ArrayUtils.add(r, rgba.getRI());
										g = ArrayUtils.add(g, rgba.getGI());
										b = ArrayUtils.add(b, rgba.getBI());
									}
								}
								if(r.length > 0 && g.length > 0 && b.length > 0){
									mat.color = new RGBA(AdvancedMathUtils.average(r), AdvancedMathUtils.average(g), AdvancedMathUtils.average(b));
								}
							}
						}
						if(mat.color.equals("0") || mat.color.equals("0:0:0") || mat.color.equals("0:0:0:0")){
							logger.debug("Could not fix color for material: " + mat.name);
						} else {
							logger.debug("Successfully fixed color for material: " + mat.name);
						}
					}
					reader.close();
					JsonWriter writer = new JsonWriter(new FileWriter(json));
					writer.setIndent("	");
					gson.toJson(mats, GsonMaterialsConversion.class, writer);
					writer.close();
				} catch(IOException e){
					logger.error("Caught exception while reading materials.json. It will be ignored!");
				}
			}
		}
	}

	/*
	 * Load
	 */

	private static void loadMaterials(){
		logger.info("Loading materials");
		for(File file : extensionsDir.listFiles(new FileFilter(){

			@Override
			public boolean accept(File file){
				return file.isDirectory();
			}

		})){
			File json = new File(file, "materials.json");
			if(json.exists()){
				try{
					logger.info("Loading materials from extension " + file.getName());
					JsonReader reader = new JsonReader(new FileReader(json));
					GsonMaterialsConversion mats = gson.fromJson(reader, GsonMaterialsConversion.class);
					for(GsonMaterialConversion mat : mats.materials){
						ColoringToolMaterial material = new ColoringToolMaterial(mat.name, mat.durability, mat.color, mat.bufferMultiplier);
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

	private static void loadRecipes(){
		logger.info("Loading recipes");
		for(File file : extensionsDir.listFiles(new FileFilter(){

			@Override
			public boolean accept(File file){
				return file.isDirectory();
			}

		})){
			File json = new File(file, "recipes.json");
			if(json.exists()){
				try{
					logger.info("Loading recipes from extension " + file.getName());
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

	private static void loadLocalisations(){
		ColourfulBlocksBase.proxy.loadLocalisations();
	}

	/*
	 * Gson Conversion
	 */

	public static class GsonMaterialsConversion {

		private List<GsonMaterialConversion> materials;

		public GsonMaterialsConversion(){

		}		

		public GsonMaterialsConversion(List<GsonMaterialConversion> materials){
			this.materials = materials;
		}

		public GsonMaterialsConversion(GsonMaterialConversion... materials){
			this.materials = Lists.newArrayList(materials);
		}

		public static class GsonMaterialConversion {

			private String name;
			private int durability;
			private double bufferMultiplier;
			private RGBA color;
			private String recipe;
			private List<GsonConversionRecipeEntry> ingredients;

			public GsonMaterialConversion(){

			}

			public GsonMaterialConversion(String name, int durability, double bufferMultiplier, RGBA color, String recipe, List<GsonConversionRecipeEntry> ingredients){
				this.name = name;
				this.durability = durability;
				this.bufferMultiplier = bufferMultiplier;
				this.color = color;
				this.recipe = recipe;
				this.ingredients = ingredients;
			}

			public GsonMaterialConversion(String name, int durability, double bufferMultiplier, RGBA color, String recipe, GsonConversionRecipeEntry... ingredients){
				this(name, durability, bufferMultiplier, color, recipe, Lists.newArrayList(ingredients));
			}

			public static class GsonConversionRecipeEntry {

				private String name;
				private String value;

				public GsonConversionRecipeEntry(){

				}

				public GsonConversionRecipeEntry(String name, String value){
					this.name = name;
					this.value = value;
				}

			}

		}

	}

	public static class GsonRecipesConversion {

		private List<GsonRecipeHandlerConversion> recipes;

		public GsonRecipesConversion(){

		}

		public GsonRecipesConversion(List<GsonRecipeHandlerConversion> recipes){
			this.recipes = recipes;
		}

		public GsonRecipesConversion(GsonRecipeHandlerConversion... recipes){
			this(Lists.newArrayList(recipes));
		}

		public static class GsonRecipeHandlerConversion {

			private String name;
			private List<GsonRecipeConversion> types;

			public GsonRecipeHandlerConversion(){

			}

			public GsonRecipeHandlerConversion(String name, List<GsonRecipeConversion> types){
				this.name = name;
				this.types = types;
			}

			public GsonRecipeHandlerConversion(String name, GsonRecipeConversion... types){
				this(name, Lists.newArrayList(types));
			}

			public static class GsonRecipeConversion {

				private String type;
				private String[] recipe;

				public GsonRecipeConversion(){

				}

				public GsonRecipeConversion(String type, String... recipe){
					this.type = type;
					this.recipe = recipe;
				}

			}
		}

	}

}
