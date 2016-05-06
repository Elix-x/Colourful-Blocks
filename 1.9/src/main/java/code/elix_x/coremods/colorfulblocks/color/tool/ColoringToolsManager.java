package code.elix_x.coremods.colorfulblocks.color.tool;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import code.elix_x.coremods.colorfulblocks.ColorfulBlocksBase;
import code.elix_x.coremods.colorfulblocks.api.ColorfulBlocksAPI;
import code.elix_x.coremods.colorfulblocks.api.materials.ColoringToolMaterial;
import code.elix_x.coremods.colorfulblocks.api.tools.ColoringToolProvider;
import code.elix_x.coremods.colorfulblocks.api.tools.IColoringTool;
import code.elix_x.coremods.colorfulblocks.color.material.ColoringMaterialsManager;
import code.elix_x.excore.utils.color.RGBA;
import code.elix_x.excore.utils.recipes.RecipeStringTranslator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ColoringToolsManager {

	public static final Logger logger = LogManager.getLogger("CoB Tools Manager");

	/*
	 * Init
	 */

	private static List<ColoringToolProvider<?>> providers = new ArrayList<ColoringToolProvider<?>>();

	private static Multimap<ColoringToolProvider, Item> providersItems = HashMultimap.create();

	public static void init(){
		logger.info("Creating items and recipes");
		Configuration config = ColorfulBlocksBase.mainConfig;
		for(ColoringToolProvider provider : providers){
			if(config.getBoolean(provider.getConfigOptionName(), "Coloring Tools", true, "Register " + provider.getConfigOptionName() + " as coloring tools?")){
				logger.info("Creating " + provider.getConfigOptionName());
				for(Entry<ColoringToolMaterial, Pair<String, Map<String, Object>>> e : ColoringMaterialsManager.getAllMaterialsAndRecipes().entrySet()){
					logger.debug("Registering underlying tool for material: " + e.getKey().name);
					Item item = provider.provide(e.getKey());
					GameRegistry.register(item);
					providersItems.put(provider, item);
					if(!e.getValue().getKey().equals(ColorfulBlocksAPI.RECIPENAMENULL)){
						if(!(e.getValue().getKey().equals(ColorfulBlocksAPI.RECIPENAMEVANILLA) && e.getValue().getValue().get(ColorfulBlocksAPI.RECIPEENTRYMATERIAL) == null)){
							GameRegistry.addRecipe(RecipeStringTranslator.fromString(new ItemStack(item), e.getValue().getValue(), ColoringMaterialsManager.getRecipe(e.getValue().getKey(), provider.getRecipeType())));
						} else {
							logger.warn("Coloring tool material " + e.getKey().name + " has recipe set to vanilla, but crafting item to null. Please define crafting item or set recipe to NULL.");
						}
					}
				}
			}
		}
	}

	public static void registerProvider(ColoringToolProvider provider){
		providers.add(provider);
	}

	public static List<ColoringToolProvider<?>> getProviders(){
		return providers;
	}

	public static <I extends Item & IColoringTool> Collection<I> getAllItems(ColoringToolProvider<I> provider){
		Collection<Item> c = providersItems.get(provider);
		Collection<I> ic = new HashSet<I>();
		for(Item i : c) ic.add((I) i);
		return ic;
	}

	/*
	 * In game
	 */

	public static void updateColor(EntityPlayer player, RGBA rgba){
		if(player.getHeldItemMainhand() != null && player.getHeldItemMainhand().getItem() instanceof IColoringTool){
			if(((IColoringTool) player.getHeldItemMainhand().getItem()).hasConsumeDyes(player)){
				((IColoringTool) player.getHeldItemMainhand().getItem()).setCurrentColor(player.getHeldItemMainhand(), rgba);
			} else {
				notifyNoDyes(player);
			}
		}
	}

	public static void notifyNoDyes(EntityPlayer player){
		player.addChatMessage(new TextComponentString(I18n.translateToLocal("message.nodies")).setStyle(new Style().setColor(TextFormatting.RED)));
	}

}
