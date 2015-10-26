package code.elix_x.coremods.colourfulblocks.color.tool;

import java.util.List;

import code.elix_x.coremods.colourfulblocks.ColourfulBlocksBase;
import code.elix_x.coremods.colourfulblocks.color.material.ColoringMaterialsManager;
import code.elix_x.coremods.colourfulblocks.color.material.ColoringToolMaterial;
import code.elix_x.coremods.colourfulblocks.items.ItemBrush;
import code.elix_x.excore.utils.color.RGBA;
import code.elix_x.excore.utils.pos.DimBlockPos;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraftforge.oredict.OreDictionary;

public abstract class ColoringTool extends Item implements IColoringTool {

	protected ColoringToolMaterial material;

	public final double DEFAULTBUFFER;
	
	public final String NAME;
	
	public ColoringTool(ColoringToolMaterial material, double defaultBuffer, String name) {
		this.material = material;
		this.DEFAULTBUFFER = defaultBuffer;
		this.NAME = name;

		setMaxDamage(material.durability);
		setMaxStackSize(1);

		setCreativeTab(CreativeTabs.tabTools);
	}

	/*
	 * Override
	 */

	@Override
	public ColoringToolMaterial getMaterial() {
		return material;
	}
	
	@Override
	public String getRegistryPrefix() {
		return NAME;
	}

	@Override
	public boolean selectColorOnLeftClickBlock() {
		return true;
	}
	
	@Override
	public RGBA getCurrentColor(ItemStack itemstack) {
		return getCurrentRGBA(itemstack);
	}

	@Override
	public void setCurrentColor(ItemStack itemstack, RGBA rgba) {
		defaultBuffer(itemstack);
		setCurrentRGBA(itemstack, rgba);
	}

	@Override
	public boolean hasConsumeDyes(EntityPlayer player) {
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
					if(itemstack.getItem() == Items.water_bucket){
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
					player.inventory.consumeInventoryItem(Items.water_bucket);
					player.inventory.addItemStackToInventory(new ItemStack(Items.bucket));
				}
				((EntityPlayerMP) player).sendContainerToPlayer(player.openContainer);
			}

			return red && green && blue && water;
		}
		return false;
	}

	/*
	 * NBT
	 */

	public void fixTags(ItemStack itemstack){
		NBTTagCompound nbt = itemstack.stackTagCompound;
		if(nbt == null){
			nbt = new NBTTagCompound();
		}

		{
			NBTTagCompound tag = nbt.getCompoundTag("tag");
			if(tag == null){
				tag = new NBTTagCompound();
			}

			{
				NBTTagCompound color = tag.getCompoundTag("color");
				if(color == null){
					color = new NBTTagCompound();
				}

				{
					if(!color.hasKey("r")){
						color.setInteger("r", 0);
					}
					if(!color.hasKey("g")){
						color.setInteger("g", 0);
					}
					if(!color.hasKey("b")){
						color.setInteger("b", 0);
					}
					if(!color.hasKey("a")){
						color.setInteger("a", 100);
					}

					if(!color.hasKey("buffer")){
						color.setDouble("buffer", material.bufferMultiplier * DEFAULTBUFFER);
					}

				}

				tag.setTag("color", color);

			}

			nbt.setTag("tag", tag);

		}

		itemstack.stackTagCompound = nbt;
	}

	public NBTTagCompound getColorTag(ItemStack itemstack){
		fixTags(itemstack);
		return itemstack.stackTagCompound.getCompoundTag("tag").getCompoundTag("color");
	}

	public void setColorTag(ItemStack itemstack, NBTTagCompound nbt){
		fixTags(itemstack);
		itemstack.stackTagCompound.getCompoundTag("tag").setTag("color", nbt);
	}

	public RGBA getCurrentRGBA(ItemStack itemstack){
		fixTags(itemstack);
		NBTTagCompound nbt = getColorTag(itemstack);
		return new RGBA(nbt.getInteger("r"), nbt.getInteger("g"), nbt.getInteger("b"), nbt.getInteger("a"));
	}

	public void setCurrentRGBA(ItemStack itemstack, RGBA rgba){
		fixTags(itemstack);
		NBTTagCompound nbt = getColorTag(itemstack);
		nbt.setInteger("r", rgba.r);
		nbt.setInteger("g", rgba.g);
		nbt.setInteger("b", rgba.b);
		nbt.setInteger("a", rgba.a);
		setColorTag(itemstack, nbt);
	}

	public double getBuffer(ItemStack itemstack){
		fixTags(itemstack);
		return getColorTag(itemstack).getDouble("buffer");
	}

	public void setBuffer(ItemStack itemstack, double buffer){
		fixTags(itemstack);
		getColorTag(itemstack).setDouble("buffer", buffer);
	}

	public void defaultBuffer(ItemStack itemstack){
		fixTags(itemstack);
		setBuffer(itemstack, material.bufferMultiplier * DEFAULTBUFFER);
	}

	/*
	 * Rendering
	 */

	@Override
	public boolean isFull3D() {
		return true;
	}

	private IIcon materialIcon;
	private IIcon fluidIcon;

	@Override
	public void registerIcons(IIconRegister reg) {
		materialIcon = registerMaterialIcon(reg);
		fluidIcon = registerPaintIcon(reg);
	}

	@Override
	public IIcon getIcon(ItemStack stack, int pass) {
		return pass == 0 ? materialIcon : fluidIcon;
	}

	@Override
	public boolean requiresMultipleRenderPasses() {
		return true;
	}

	@Override
	public int getRenderPasses(int metadata) {
		return 2;
	}

	@Override
	public int getColorFromItemStack(ItemStack itemstack, int pass) {
		if(pass == 0){
			return material.getColor();
		} else if(pass == 1){
			return getCurrentRGBA(itemstack).argb();
		}
		return 16777215;
	}
	
	/*
	 * Name
	 */

	@Override
	public String getItemStackDisplayName(ItemStack itemstack) {
		return StatCollector.translateToLocal(ColoringMaterialsManager.COLORINGTOOLNAMEORDERLANG).replace("%material", StatCollector.translateToLocal(ColoringMaterialsManager.COLORINGTOOLMATERIALLANG + "." + material.name)).replace("%tool", StatCollector.translateToLocal(ColoringMaterialsManager.COLORINGTOOLLANG + "." + NAME));
	}

	@Override
	public void addInformation(ItemStack itemstack, EntityPlayer player, List list, boolean shift) {
		if(GuiScreen.isShiftKeyDown()){
			list.add(StatCollector.translateToLocal("coloringtool.desc.color.red") + ": " + getCurrentRGBA(itemstack).r / 255f * 100f + "% (" + getCurrentRGBA(itemstack).r + "/255)");
			list.add(StatCollector.translateToLocal("coloringtool.desc.color.green") + ": " + getCurrentRGBA(itemstack).g / 255f * 100f + "% (" + getCurrentRGBA(itemstack).r + "/255)");
			list.add(StatCollector.translateToLocal("coloringtool.desc.color.blue") + ": " + getCurrentRGBA(itemstack).b / 255f * 100f + "% (" + getCurrentRGBA(itemstack).r + "/255)");
			list.add(StatCollector.translateToLocal("coloringtool.desc.buffer") + ": " + (double) getBuffer(itemstack) / (DEFAULTBUFFER * material.bufferMultiplier) * 100 + "% (" + getBuffer(itemstack) + "/" + DEFAULTBUFFER * material.bufferMultiplier + ")");
			list.add(StatCollector.translateToLocal("coloringtool.desc.durability") + ": " + (float) (itemstack.getMaxDamage() - itemstack.getItemDamage()) / itemstack.getMaxDamage() * 100  + "% (" + itemstack.getItemDamage() + "/" + itemstack.getMaxDamage() + ")");
		} else {
			list.add(StatCollector.translateToLocal("coloringtool.desc.color.red") + ": " + getCurrentRGBA(itemstack).r / 255f * 100f + "%");
			list.add(StatCollector.translateToLocal("coloringtool.desc.color.green") + ": " + getCurrentRGBA(itemstack).g / 255f * 100f + "%");
			list.add(StatCollector.translateToLocal("coloringtool.desc.color.blue") + ": " + getCurrentRGBA(itemstack).b / 255f * 100f + "%");
			list.add(StatCollector.translateToLocal("coloringtool.desc.buffer") + ": " + (double) getBuffer(itemstack) / (DEFAULTBUFFER * material.bufferMultiplier) * 100 + "%");
			list.add(StatCollector.translateToLocal("coloringtool.desc.durability") + ": " + (float) (itemstack.getMaxDamage() - itemstack.getItemDamage()) / itemstack.getMaxDamage() * 100  + "%");
		}
	}
	
	/*
	 * Abstract
	 */

	protected abstract IIcon registerMaterialIcon(IIconRegister reg);

	protected abstract IIcon registerPaintIcon(IIconRegister reg);

}
