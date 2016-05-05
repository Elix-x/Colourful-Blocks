package code.elix_x.coremods.colorfulblocks.items;

import java.util.List;

import com.google.common.collect.Lists;

import code.elix_x.coremods.colorfulblocks.ColourfulBlocksBase;
import code.elix_x.coremods.colorfulblocks.color.ColoredBlocksManager;
import code.elix_x.coremods.colorfulblocks.color.material.ColoringToolMaterial;
import code.elix_x.coremods.colorfulblocks.color.tool.ColoringTool;
import code.elix_x.coremods.colorfulblocks.color.tool.ColoringToolsManager;
import code.elix_x.excore.utils.pos.BlockPos;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class ItemBrush extends ColoringTool {

	public ItemBrush(ColoringToolMaterial material){
		super(material, 7.5, "brush");
	}

	/*
	 * Override
	 */

	@Override
	public List<BlockPos> getBlocksAboutToBeColored(EntityPlayer player, ItemStack itemstack){
		return Lists.newArrayList(new BlockPos(player.rayTrace(5.0, 0).getBlockPos()));
	}

	/*
	 * Use
	 */

	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack itemstack, World world, EntityPlayer player, EnumHand hand){
		ColourfulBlocksBase.proxy.displayGuiSelectColor(itemstack);
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemstack);
	}

	@Override
	public EnumActionResult onItemUse(ItemStack itemstack, EntityPlayer player, World world, net.minecraft.util.math.BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ){
		boolean buffer = getBuffer(itemstack) > 0;
		if(getBuffer(itemstack) == 0){
			if(hasConsumeDyes(player)){
				defaultBuffer(itemstack);
				buffer = true;
			} else {
				ColoringToolsManager.notifiyNoDyes(player);
				return EnumActionResult.FAIL;
			}
		}
		if(buffer){
			itemstack.damageItem(1, player);
			setBuffer(itemstack, getBuffer(itemstack) - 1);
			ColoredBlocksManager.get(world).addRGBA(new BlockPos(pos), getCurrentRGBA(itemstack));
			return EnumActionResult.SUCCESS;
		} else {
			return EnumActionResult.FAIL;
		}
	}

}
