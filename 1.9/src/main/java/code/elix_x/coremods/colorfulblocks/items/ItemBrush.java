package code.elix_x.coremods.colorfulblocks.items;

import java.util.List;

import com.google.common.collect.Lists;

import code.elix_x.coremods.colorfulblocks.color.material.ColoringToolMaterial;
import code.elix_x.coremods.colorfulblocks.color.tool.ColoringTool;
import code.elix_x.excore.utils.pos.BlockPos;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class ItemBrush extends ColoringTool {

	public ItemBrush(ColoringToolMaterial material){
		super(material, 7.5, "brush");
	}

	/*
	 * Override
	 */

	@Override
	public List<BlockPos> getTargettedBlocks(EntityPlayer player, ItemStack itemstack){
		return Lists.newArrayList(new BlockPos(player.rayTrace(5.0, 0).getBlockPos()));
	}

}
