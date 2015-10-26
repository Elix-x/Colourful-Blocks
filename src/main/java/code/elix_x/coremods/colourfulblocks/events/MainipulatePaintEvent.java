package code.elix_x.coremods.colourfulblocks.events;

import code.elix_x.coremods.colourfulblocks.ColourfulBlocksBase;
import code.elix_x.coremods.colourfulblocks.color.ColourfulBlocksManager;
import code.elix_x.coremods.colourfulblocks.color.tool.ColoringToolsManager;
import code.elix_x.coremods.colourfulblocks.color.tool.IColoringTool;
import code.elix_x.coremods.colourfulblocks.items.ItemBrush;
import code.elix_x.coremods.colourfulblocks.net.ColourfulBlocksGuiHandler;
import code.elix_x.excore.utils.color.RGBA;
import code.elix_x.excore.utils.pos.DimBlockPos;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.internal.FMLNetworkHandler;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;

public class MainipulatePaintEvent {

	public MainipulatePaintEvent() {

	}

	@SubscribeEvent
	public void manipulate(PlayerInteractEvent event){
		if(event.action == Action.LEFT_CLICK_BLOCK){
			if(event.entityPlayer.getCurrentEquippedItem() != null && event.entityPlayer.getCurrentEquippedItem().getItem() instanceof IColoringTool && ((IColoringTool) event.entityPlayer.getCurrentEquippedItem().getItem()).selectColorOnLeftClickBlock()){
				RGBA rgba = ColourfulBlocksManager.getRGBA(new DimBlockPos(event.world.provider.dimensionId, event.x, event.y, event.z));
				if(rgba != null){
					ColoringToolsManager.updateColor(event.entityPlayer, rgba);
					event.setCanceled(true);
				}
			}
		}

		if(event.action == Action.RIGHT_CLICK_BLOCK){
			if(event.entityPlayer.getCurrentEquippedItem() != null && event.entityPlayer.getCurrentEquippedItem().getItem() == Items.potionitem && event.entityPlayer.getCurrentEquippedItem().getItemDamage() == 0){
				if(ColourfulBlocksManager.hasRGBA(new DimBlockPos(event.world.provider.dimensionId, event.x, event.y, event.z))){
					ColourfulBlocksManager.removeRGBA(new DimBlockPos(event.world.provider.dimensionId, event.x, event.y, event.z));

					if(ColourfulBlocksBase.consumeWaterOnErase){
						event.entityPlayer.setCurrentItemOrArmor(0, new ItemStack(Items.glass_bottle));
					}

					event.setCanceled(true);
				}
			}
		}
	}

}