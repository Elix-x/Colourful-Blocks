package code.elix_x.coremods.colourfulblocks.items;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

import java.util.List;

import com.google.common.collect.Lists;

import code.elix_x.coremods.colourfulblocks.ColourfulBlocksBase;
import code.elix_x.coremods.colourfulblocks.color.ColourfulBlocksManager;
import code.elix_x.coremods.colourfulblocks.color.material.ColoringMaterialsManager;
import code.elix_x.coremods.colourfulblocks.color.material.ColoringToolMaterial;
import code.elix_x.coremods.colourfulblocks.color.tool.ColoringTool;
import code.elix_x.coremods.colourfulblocks.color.tool.ColoringToolsManager;
import code.elix_x.coremods.colourfulblocks.color.tool.IColoringTool;
import code.elix_x.coremods.colourfulblocks.net.ColourfulBlocksGuiHandler;
import code.elix_x.excore.utils.color.RGBA;
import code.elix_x.excore.utils.pos.DimBlockPos;
import cpw.mods.fml.common.network.internal.FMLNetworkHandler;

public class ItemBrush extends ColoringTool {

	public ItemBrush(ColoringToolMaterial material) {
		super(material, 7.5, "brush");
	}

	/*
	 * Override
	 */
	
	@Override
	public boolean isAboutToColor(EntityPlayer player, ItemStack itemstack, DimBlockPos pos) {
		MovingObjectPosition mpos = player.rayTrace(5.0, 0);
		return player.worldObj.provider.dimensionId == pos.dimId && mpos.typeOfHit == MovingObjectType.BLOCK && mpos.blockX == pos.x && mpos.blockY == pos.y && mpos.blockZ == pos.z;
	}

	@Override
	public List<DimBlockPos> getBlocksAboutToColor(EntityPlayer player, ItemStack itemstack) {
		MovingObjectPosition mpos = player.rayTrace(5.0, 0);
		return Lists.newArrayList(new DimBlockPos(mpos.blockX, mpos.blockY, mpos.blockZ, player.worldObj.provider.dimensionId));
	}

	@Override
	protected IIcon registerMaterialIcon(IIconRegister reg) {
		return reg.registerIcon(ColourfulBlocksBase.MODID + ":brushhandle");
	}

	@Override
	protected IIcon registerPaintIcon(IIconRegister reg) {
		return reg.registerIcon(ColourfulBlocksBase.MODID + ":brushtop");
	}
	
	/*
	 * Use
	 */
	
	@Override
	public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer player) {
		FMLNetworkHandler.openGui(player, ColourfulBlocksBase.instance, ColourfulBlocksGuiHandler.guiIdBrush, player.worldObj, 0, 0, 0);
		return itemstack;
	}

	@Override
	public boolean onItemUse(ItemStack itemstack, EntityPlayer player, World world, int x, int y, int z, int side, float fx, float fy, float fz) {
		ColourfulBlocksManager.addRGBA(new DimBlockPos(world.provider.dimensionId, x, y, z), getCurrentRGBA(itemstack));
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
