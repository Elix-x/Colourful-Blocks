package code.elix_x.coremods.colorfulblocks.color.tool;

import java.util.List;

import code.elix_x.coremods.colorfulblocks.color.material.ColoringToolMaterial;
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

	public List<BlockPos> getBlocksAboutToBeColored(EntityPlayer player, ItemStack itemstack);

}
