package code.elix_x.coremods.colourfulblocks.color.tool;

import java.util.List;

import code.elix_x.coremods.colourfulblocks.color.material.ColoringToolMaterial;
import code.elix_x.excore.utils.color.RGBA;
import code.elix_x.excore.utils.pos.BlockPos;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface IColoringTool {

	public ColoringToolMaterial getMaterial();

	public String getRegistryPrefix();

	public boolean selectColorOnLeftClickBlock();

	public RGBA getCurrentColor(ItemStack itemstack);

	public void setCurrentColor(ItemStack itemstack, RGBA rgba);

	public boolean hasConsumeDyes(EntityPlayer player);

	public boolean isAboutToColor(EntityPlayer player, ItemStack itemstack, BlockPos pos);

	public List<BlockPos> getBlocksAboutToColor(EntityPlayer player, ItemStack itemstack);

}
