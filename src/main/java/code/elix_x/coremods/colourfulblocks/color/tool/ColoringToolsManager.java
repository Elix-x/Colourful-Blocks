package code.elix_x.coremods.colourfulblocks.color.tool;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import code.elix_x.coremods.colourfulblocks.ColourfulBlocksBase;
import code.elix_x.coremods.colourfulblocks.color.material.ColoringMaterialsManager;
import code.elix_x.coremods.colourfulblocks.color.material.ColoringToolMaterial;
import code.elix_x.coremods.colourfulblocks.items.ItemBrush;
import code.elix_x.excore.utils.color.RGBA;
import code.elix_x.excore.utils.recipes.RecipeStringTranslator;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class ColoringToolsManager {

	public static final Logger logger = LogManager.getLogger("CoB Tools Manager");

	/*
	 * Init
	 */

	private static List<ColoringToolProvider<?>> providers = new ArrayList<ColoringToolProvider<?>>();

	public static void init(){
		Configuration config = ColourfulBlocksBase.mainConfig;
		for(Entry<ColoringToolMaterial, Pair<String, Map<String, Object>>> e : ColoringMaterialsManager.getAllMaterialsAndRecipes().entrySet()){
			for(ColoringToolProvider provider : providers){
				if(config.getBoolean(provider.getConfigOptionName(), "coloring tools", true, "Register " + provider.getConfigOptionName() + " as coloring tools?")){
					Item item = provider.provide(e.getKey());
					GameRegistry.registerItem(item, ((IColoringTool) item).getRegistryPrefix() + "_" + e.getKey().name);
					if(!e.getValue().getKey().equals(ColoringMaterialsManager.RECIPENAMENULL)){
						if(!(e.getValue().getKey().equals(ColoringMaterialsManager.RECIPENAMEVANILLA) && e.getValue().getValue().get(ColoringMaterialsManager.RECIPEENTRYMATERIAL) == null)){
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

	/*
	 * In game
	 */

	public static void updateColor(EntityPlayer player, RGBA rgba) {
		if(player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() instanceof IColoringTool){
			if(((IColoringTool) player.getCurrentEquippedItem().getItem()).hasConsumeDyes(player)){
				((IColoringTool) player.getCurrentEquippedItem().getItem()).setCurrentColor(player.getCurrentEquippedItem(), rgba);
			} else {
				notifiyNoDies(player);
			}
		}
	}

	public static void notifiyNoDies(EntityPlayer player) {
		player.addChatMessage(new ChatComponentText(StatCollector.translateToLocal("message.nodies")).setChatStyle(new ChatStyle().setColor(EnumChatFormatting.RED)));
	}

}
