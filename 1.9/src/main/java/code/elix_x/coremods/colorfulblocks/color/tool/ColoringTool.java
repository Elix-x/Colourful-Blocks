package code.elix_x.coremods.colorfulblocks.color.tool;

import java.util.List;

import code.elix_x.coremods.colorfulblocks.ColourfulBlocksBase;
import code.elix_x.coremods.colorfulblocks.color.material.ColoringMaterialsManager;
import code.elix_x.coremods.colorfulblocks.color.material.ColoringToolMaterial;
import code.elix_x.excore.utils.color.RGBA;
import code.elix_x.excore.utils.nbt.mbt.MBT;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.oredict.OreDictionary;

public abstract class ColoringTool extends Item implements IColoringTool {

	public static final MBT mbt = new MBT();

	protected ColoringToolMaterial material;

	public final double DEFAULTBUFFER;

	public final String NAME;

	public ColoringTool(ColoringToolMaterial material, double defaultBuffer, String name){
		this.material = material;
		this.DEFAULTBUFFER = defaultBuffer;
		this.NAME = name;

		setMaxDamage(material.durability);
		setMaxStackSize(1);

		setCreativeTab(CreativeTabs.TOOLS);
	}

	/*
	 * NBT
	 */

	public ColoringToolData getData(ItemStack itemstack){
		ColoringToolData data = mbt.fromNBT(itemstack.getSubCompound("Coloring Tool Data", true), ColoringToolData.class);
		if(data == null || data.color == null) data = new ColoringToolData(new RGBA(0, 0, 0), defaultBuffer());
		return data;
	}

	public void setData(ItemStack itemstack, ColoringToolData data){
		itemstack.setTagInfo("Coloring Tool Data", mbt.toNBT(data));
	}

	/*
	 * Override
	 */

	@Override
	public ColoringToolMaterial getMaterial(){
		return material;
	}

	@Override
	public boolean selectColorOnLeftClickBlock(){
		return true;
	}

	@Override
	public RGBA getCurrentColor(ItemStack itemstack){
		return getData(itemstack).color;
	}

	@Override
	public void setCurrentColor(ItemStack itemstack, RGBA rgba){
		setData(itemstack, new ColoringToolData(rgba, defaultBuffer()));;
	}

	public double defaultBuffer(){
		return DEFAULTBUFFER * material.bufferMultiplier;
	}

	public double getBuffer(ItemStack itemstack){
		return getData(itemstack).buffer;
	}

	public void setBuffer(ItemStack itemstack, double buffer){
		setData(itemstack, getData(itemstack).setBuffer(buffer));
	}

	public void defaultBuffer(ItemStack itemstack){
		setBuffer(itemstack, defaultBuffer());
	}

	@Override
	public boolean hasConsumeDyes(EntityPlayer player){
		if(!player.worldObj.isRemote){
			if(player.capabilities.isCreativeMode){
				return true;
			}

			boolean red = false;
			boolean green = false;
			boolean blue = false;
			boolean water = false;

			int rs = 0;
			int gs = 0;
			int bs = 0;

			for(int i = 0; i < player.inventory.mainInventory.length; i++){
				ItemStack itemstack = player.inventory.mainInventory[i];
				if(itemstack != null){
					for(int id : OreDictionary.getOreIDs(itemstack)){
						if(!red && OreDictionary.getOreName(id).equals("dyeRed")){
							red = true;
							rs = i;
						}
						if(!green && OreDictionary.getOreName(id).equals("dyeGreen")){
							green = true;
							gs = i;
						}
						if(!blue && OreDictionary.getOreName(id).equals("dyeBlue")){
							blue = true;
							bs = i;
						}
					}
					if(itemstack.getItem() == Items.WATER_BUCKET){
						water = true;
					}
				}
			}

			if(red && green && blue && water){
				{
					ItemStack itemstack = player.inventory.getStackInSlot(rs);
					itemstack.stackSize -= 1;
					if(itemstack.stackSize == 0){
						itemstack = null;
					}
					player.inventory.setInventorySlotContents(rs, itemstack);
				}

				{
					ItemStack itemstack = player.inventory.getStackInSlot(gs);
					itemstack.stackSize -= 1;
					if(itemstack.stackSize == 0){
						itemstack = null;
					}
					player.inventory.setInventorySlotContents(gs, itemstack);
				}

				{
					ItemStack itemstack = player.inventory.getStackInSlot(bs);
					itemstack.stackSize -= 1;
					if(itemstack.stackSize == 0){
						itemstack = null;
					}
					player.inventory.setInventorySlotContents(bs, itemstack);
				}

				if(ColourfulBlocksBase.consumeWaterOnPaint){
					player.inventory.decrStackSize(player.inventory.getSlotFor(new ItemStack(Items.WATER_BUCKET)), 1);
					player.inventory.addItemStackToInventory(new ItemStack(Items.BUCKET));
				}
				((EntityPlayerMP) player).sendContainerToPlayer(player.openContainer);
			}

			return red && green && blue && water;
		}
		return false;
	}

	/*
	 * Rendering
	 */

	@Override
	public boolean isFull3D(){
		return true;
	}

	/*
	 * Name
	 */

	@Override
	public String getItemStackDisplayName(ItemStack itemstack){
		return I18n.translateToLocal(ColoringMaterialsManager.COLORINGTOOLNAMEORDERLANG).replace("%material", I18n.translateToLocal(ColoringMaterialsManager.COLORINGTOOLMATERIALLANG + "." + material.name)).replace("%tool", I18n.translateToLocal(ColoringMaterialsManager.COLORINGTOOLLANG + "." + NAME));
	}

	@Override
	public void addInformation(ItemStack itemstack, EntityPlayer player, List list, boolean shift){
		if(GuiScreen.isShiftKeyDown()){
			list.add(I18n.translateToLocal("coloringtool.desc.color.red") + ": " + getCurrentColor(itemstack).getRF() * 100f + "% (" + getCurrentColor(itemstack).getRI() + "/255)");
			list.add(I18n.translateToLocal("coloringtool.desc.color.green") + ": " + getCurrentColor(itemstack).getGF() * 100f + "% (" + getCurrentColor(itemstack).getGI() + "/255)");
			list.add(I18n.translateToLocal("coloringtool.desc.color.blue") + ": " + getCurrentColor(itemstack).getBF() * 100f + "% (" + getCurrentColor(itemstack).getBI() + "/255)");
			list.add(I18n.translateToLocal("coloringtool.desc.buffer") + ": " + getBuffer(itemstack) / (DEFAULTBUFFER * material.bufferMultiplier) * 100 + "% (" + getBuffer(itemstack) + "/" + DEFAULTBUFFER * material.bufferMultiplier + ")");
			list.add(I18n.translateToLocal("coloringtool.desc.durability") + ": " + (float) (itemstack.getMaxDamage() - itemstack.getItemDamage()) / itemstack.getMaxDamage() * 100  + "% (" + itemstack.getItemDamage() + "/" + itemstack.getMaxDamage() + ")");
		} else {
			list.add(I18n.translateToLocal("coloringtool.desc.color.red") + ": " + getCurrentColor(itemstack).getRF() * 100f + "%");
			list.add(I18n.translateToLocal("coloringtool.desc.color.green") + ": " + getCurrentColor(itemstack).getGF() * 100f + "%");
			list.add(I18n.translateToLocal("coloringtool.desc.color.blue") + ": " + getCurrentColor(itemstack).getBF() * 100f + "%");
			list.add(I18n.translateToLocal("coloringtool.desc.buffer") + ": " + getBuffer(itemstack) / (DEFAULTBUFFER * material.bufferMultiplier) * 100 + "%");
			list.add(I18n.translateToLocal("coloringtool.desc.durability") + ": " + (float) (itemstack.getMaxDamage() - itemstack.getItemDamage()) / itemstack.getMaxDamage() * 100  + "%");
		}
	}

	public static class ColoringToolData {

		public RGBA color;
		public double buffer;

		private ColoringToolData(){

		}

		public ColoringToolData(RGBA color, double buffer){
			this.color = color;
			this.buffer = buffer;
		}

		public RGBA getColor(){
			return color;
		}

		public ColoringToolData setColor(RGBA color){
			this.color = color;
			return this;
		}

		public double getBuffer(){
			return buffer;
		}

		public ColoringToolData setBuffer(double buffer){
			this.buffer = buffer;
			return this;
		}

	}

}
