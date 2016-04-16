package code.elix_x.coremods.colourfulblocks.items;

import java.util.List;

import com.google.common.collect.Lists;

import code.elix_x.coremods.colourfulblocks.ColourfulBlocksBase;
import code.elix_x.coremods.colourfulblocks.color.ColoredBlocksManager;
import code.elix_x.coremods.colourfulblocks.color.material.ColoringToolMaterial;
import code.elix_x.coremods.colourfulblocks.color.tool.ColoringTool;
import code.elix_x.coremods.colourfulblocks.color.tool.ColoringToolsManager;
import code.elix_x.excore.utils.pos.BlockPos;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
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
		MovingObjectPosition mpos = player.rayTrace(5.0, 0);
		return Lists.newArrayList(new BlockPos(mpos.blockX, mpos.blockY, mpos.blockZ));
	}

	@Override
	protected IIcon registerMaterialIcon(IIconRegister reg){
		return reg.registerIcon(ColourfulBlocksBase.MODID + ":brushhandle");
	}

	@Override
	protected IIcon registerPaintIcon(IIconRegister reg){
		return reg.registerIcon(ColourfulBlocksBase.MODID + ":brushtop");
	}

	/*
	 * Use
	 */

	@Override
	public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer player){
		ColourfulBlocksBase.proxy.displayGuiSelectColor(itemstack);
		return itemstack;
	}

	@Override
	public boolean onItemUse(ItemStack itemstack, EntityPlayer player, World world, int x, int y, int z, int side, float fx, float fy, float fz){
		ColoredBlocksManager.get(world).addRGBA(new BlockPos(x, y, z), getCurrentRGBA(itemstack));
		itemstack.damageItem(1, player);
		if(getBuffer(itemstack) == 0){
			if(hasConsumeDyes(player)){
				defaultBuffer(itemstack);
			} else {
				ColoringToolsManager.notifiyNoDies(player);
			}
		} else {
			setBuffer(itemstack, getBuffer(itemstack) - 1);
		}
		return true;
	}

}
