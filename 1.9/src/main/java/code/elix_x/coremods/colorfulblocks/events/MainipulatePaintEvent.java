package code.elix_x.coremods.colorfulblocks.events;

import code.elix_x.coremods.colorfulblocks.ColourfulBlocksBase;
import code.elix_x.coremods.colorfulblocks.color.ColoredBlocksManager;
import code.elix_x.coremods.colorfulblocks.color.tool.ColoringToolsManager;
import code.elix_x.coremods.colorfulblocks.color.tool.IColoringTool;
import code.elix_x.excore.utils.color.RGBA;
import code.elix_x.excore.utils.pos.BlockPos;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class MainipulatePaintEvent {

	@SubscribeEvent
	public void manipulate(PlayerInteractEvent event){
		if(event instanceof LeftClickBlock){
			if(event.getItemStack() != null && event.getItemStack().getItem() instanceof IColoringTool && ((IColoringTool) event.getItemStack().getItem()).selectColorOnLeftClickBlock()){
				RGBA rgba = ColoredBlocksManager.get(event.getWorld()).getRGBA(new BlockPos(event.getPos()));
				if(rgba != null){
					ColoringToolsManager.updateColor(event.getEntityPlayer(), rgba);
					event.setCanceled(true);
				}
			}
		}

		if(event instanceof RightClickBlock){
			if(event.getItemStack() != null && event.getItemStack().getItem() == Items.potionitem && event.getItemStack().getItemDamage() == 0){
				if(ColoredBlocksManager.get(event.getWorld()).hasRGBA(new BlockPos(event.getPos()))){
					ColoredBlocksManager.get(event.getWorld()).removeRGBA(new BlockPos(event.getPos()));

					if(ColourfulBlocksBase.consumeWaterOnErase && !event.getEntityPlayer().capabilities.isCreativeMode){
						event.getEntityPlayer().setItemStackToSlot(event.getHand() == EnumHand.MAIN_HAND ? EntityEquipmentSlot.MAINHAND : EntityEquipmentSlot.OFFHAND, new ItemStack(Items.glass_bottle));
					}

					event.setCanceled(true);
				}
			}
		}
	}

}