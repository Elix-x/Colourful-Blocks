package code.elix_x.coremods.colorfulblocks.events;

import code.elix_x.coremods.colorfulblocks.ColorfulBlocksBase;
import code.elix_x.coremods.colorfulblocks.api.tools.IColoringTool;
import code.elix_x.coremods.colorfulblocks.color.ColoredBlocksManager;
import code.elix_x.coremods.colorfulblocks.color.tool.ColoringToolsManager;
import code.elix_x.excore.utils.color.RGBA;
import code.elix_x.excore.utils.pos.BlockPos;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickItem;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class MainipulatePaintEvent {

	@SubscribeEvent
	public void manipulate(PlayerInteractEvent event){
		EntityPlayer player = event.getEntityPlayer();
		ItemStack itemstack = event.getItemStack();
		if(itemstack != null && itemstack.getItem() instanceof IColoringTool){
			IColoringTool tool = (IColoringTool) itemstack.getItem();
			if(event instanceof RightClickItem && tool.displayDefaultGui(player, itemstack)){
				ColorfulBlocksBase.proxy.displayGuiSelectColor(itemstack);
				event.setCanceled(true);
			}
			if(event instanceof RightClickBlock && tool.colorBlocksOnRightClick(player, itemstack)){
				for(BlockPos pos : tool.getTargettedBlocks(player, itemstack)){
					if(tool.colorBlockProceed(player, itemstack, pos)){
						ColoredBlocksManager.get(event.getWorld()).addRGBA(pos, tool.getCurrentColor(itemstack));
					}
				}
				player.swingArm(event.getHand());
				event.setCanceled(true);
			}
			if(event instanceof LeftClickBlock && tool.pickColorOnLeftClick(player, itemstack)){
				RGBA rgba = ColoredBlocksManager.get(event.getWorld()).getRGBA(new BlockPos(event.getPos()));
				if(rgba != null){
					ColoringToolsManager.updateColor(player, rgba);
					player.swingArm(event.getHand());
					event.setCanceled(true);
				}
			}
		}

		if(event instanceof RightClickBlock){
			if(itemstack != null && itemstack.getItem() == Items.POTIONITEM && itemstack.getItemDamage() == 0){
				if(ColoredBlocksManager.get(event.getWorld()).hasRGBA(new BlockPos(event.getPos()))){
					ColoredBlocksManager.get(event.getWorld()).removeRGBA(new BlockPos(event.getPos()));
					if(ColorfulBlocksBase.consumeWaterOnErase && !player.capabilities.isCreativeMode){
						player.setItemStackToSlot(event.getHand() == EnumHand.MAIN_HAND ? EntityEquipmentSlot.MAINHAND : EntityEquipmentSlot.OFFHAND, new ItemStack(Items.GLASS_BOTTLE));
					}
					event.setCanceled(true);
				}
			}
		}
	}

}