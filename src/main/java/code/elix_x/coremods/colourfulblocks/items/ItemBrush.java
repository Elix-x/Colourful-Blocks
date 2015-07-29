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
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import net.minecraftforge.oredict.OreDictionary;
import code.elix_x.coremods.colourfulblocks.ColourfulBlocksBase;
import code.elix_x.coremods.colourfulblocks.brush.BrushMaterial;
import code.elix_x.coremods.colourfulblocks.color.ColourfulBlocksManager;
import code.elix_x.coremods.colourfulblocks.net.ColourfulBlocksGuiHandler;
import code.elix_x.excore.utils.color.RGBA;
import code.elix_x.excore.utils.pos.DimBlockPos;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.internal.FMLNetworkHandler;

public class ItemBrush extends Item {

	public BrushMaterial material;

	public ItemBrush(BrushMaterial mat) {
		material = mat;

		setMaxDamage(material.durability);
		setMaxStackSize(1);

		setUnlocalizedName("blockbrush");
		setCreativeTab(CreativeTabs.tabTools);
	}

	/*
	 * NBT
	 */

	public static void fixTags(ItemStack itemstack){
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
						color.setInteger("buffer", ((ItemBrush) itemstack.getItem()).material.buffer);
					}

				}

				tag.setTag("color", color);

			}

			nbt.setTag("tag", tag);

		}

		itemstack.stackTagCompound = nbt;
	}

	public static NBTTagCompound getColorTag(ItemStack itemstack){
		fixTags(itemstack);
		return itemstack.stackTagCompound.getCompoundTag("tag").getCompoundTag("color");
	}

	public static void setColorTag(ItemStack itemstack, NBTTagCompound nbt){
		fixTags(itemstack);
		itemstack.stackTagCompound.getCompoundTag("tag").setTag("color", nbt);
	}

	public static RGBA getCurrentRGBA(ItemStack itemstack){
		fixTags(itemstack);
		NBTTagCompound nbt = getColorTag(itemstack);
		return new RGBA(nbt.getInteger("r"), nbt.getInteger("g"), nbt.getInteger("b"), nbt.getInteger("a"));
	}

	public static void setCurrentRGBA(ItemStack itemstack, RGBA rgba){
		fixTags(itemstack);
		NBTTagCompound nbt = getColorTag(itemstack);
		nbt.setInteger("r", rgba.r);
		nbt.setInteger("g", rgba.g);
		nbt.setInteger("b", rgba.b);
		nbt.setInteger("a", rgba.a);
		setColorTag(itemstack, nbt);
	}

	public static int getBuffer(ItemStack itemstack){
		fixTags(itemstack);
		return getColorTag(itemstack).getInteger("buffer");
	}

	public static void setBuffer(ItemStack itemstack, int buffer){
		fixTags(itemstack);
		getColorTag(itemstack).setInteger("buffer", buffer);
	}

	public static void defaultBuffer(ItemStack itemstack){
		fixTags(itemstack);
		setBuffer(itemstack, ((ItemBrush) itemstack.getItem()).material.buffer);
	}

	/*
	 * Net
	 */

	public static void updateColour(EntityPlayer player, RGBA rgba) {
		if(player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() instanceof ItemBrush){
			if(hasConsumeDyes(player)){
				defaultBuffer(player.getCurrentEquippedItem());
				setCurrentRGBA(player.getCurrentEquippedItem(), rgba);
			} else {
				notifiyNoDyes(player);
			}
		}
	}

	/*
	 * Right click
	 */

	@Override
	public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer player) {
		//		if(!world.isRemote){
		FMLNetworkHandler.openGui(player, ColourfulBlocksBase.instance, ColourfulBlocksGuiHandler.guiIdBrush, world, 0, 0, 0);
		//		}
		return itemstack;
	}

	/*
	 * Pick color
	 */



	/*
	 * Use
	 */

	@Override
	public boolean onItemUse(ItemStack itemstack, EntityPlayer player, World world, int x, int y, int z, int side, float fx, float fy, float fz) {
		//		if(!player.isSneaking()){
		ColourfulBlocksManager.addRGBA(new DimBlockPos(world.provider.dimensionId, x, y, z), getCurrentRGBA(itemstack));
		itemstack.damageItem(1, player);
		if(getBuffer(itemstack) == 0){
			if(hasConsumeDyes(player)){
				defaultBuffer(itemstack);
			} else {
				notifiyNoDyes(player);
			}
		} else {
			setBuffer(itemstack, getBuffer(itemstack) - 1);
		}
		//		} else {
		//			ColourfullBlocksManager.removeRGBA(new DimBlockPos(world.provider.dimensionId, x, y, z));
		/*setCurrentRGBA(itemstack, ColourfullBlocksManager.getRGBA(new DimBlockPos(world.provider.dimensionId, x, y, z)));
			if(hasConsumeDyes(player)){
				defaultBuffer(itemstack);
			} else {
				notifiyNoDyes(player);
			}*/
		//		}
		return true;
	}

	public static class MainipulatePaintEvent {

		public MainipulatePaintEvent() {

		}

		@SubscribeEvent
		public void manipulate(PlayerInteractEvent event){
			if(event.action == Action.LEFT_CLICK_BLOCK){
				if(event.entityPlayer.getCurrentEquippedItem() != null && event.entityPlayer.getCurrentEquippedItem().getItem() instanceof ItemBrush){
					RGBA rgba = ColourfulBlocksManager.getRGBA(new DimBlockPos(event.world.provider.dimensionId, event.x, event.y, event.z));
					if(rgba != null){
						if(hasConsumeDyes(event.entityPlayer)){
							defaultBuffer(event.entityPlayer.getCurrentEquippedItem());
							setCurrentRGBA(event.entityPlayer.getCurrentEquippedItem(), rgba);
							event.setCanceled(true);
						} else {
							notifiyNoDyes(event.entityPlayer);
						}
					}
				}
			}

			if(event.action == Action.RIGHT_CLICK_BLOCK){
				if(event.entityPlayer.getCurrentEquippedItem() != null && event.entityPlayer.getCurrentEquippedItem().getItem() == Items.potionitem && event.entityPlayer.getCurrentEquippedItem().getItemDamage() == 0){
					if(ColourfulBlocksManager.hasRGBA(new DimBlockPos(event.world.provider.dimensionId, event.x, event.y, event.z))){
						ColourfulBlocksManager.removeRGBA(new DimBlockPos(event.world.provider.dimensionId, event.x, event.y, event.z));
						
						if(ColourfulBlocksBase.consumeWaterOnErase){
							event.entityPlayer.setCurrentItemOrArmor(0, new ItemStack(Items.glass_bottle));
						}
						
						event.setCanceled(true);
					}
				}
			}
		}

	}

	public static boolean hasDyes(EntityPlayer player) {
		if(player.capabilities.isCreativeMode){
			return true;
		}
		
		boolean red = false;
		boolean green = false;
		boolean blue = false;
		boolean water = false;

		for(ItemStack itemstack : player.inventory.mainInventory){
			if(itemstack != null){
				for(int id : OreDictionary.getOreIDs(itemstack)){
					if(!red && OreDictionary.getOreName(id).equals("dyeRed")){
						red = true;
					}
					if(!green && OreDictionary.getOreName(id).equals("dyeGreen")){
						green = true;
					}
					if(!blue && OreDictionary.getOreName(id).equals("dyeBlue")){
						blue = true;
					}
				}
				if(itemstack.getItem() == Items.water_bucket){
					water = true;
				}
			}
		}

		return red && green && blue && water;
	}

	public static boolean hasConsumeDyes(EntityPlayer player) {
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
				//			player.inventory.setInventorySlotContents(rs, player.inventory.getStackInSlot(rs).stackSize > 1 ? new ItemStack(player.inventory.getStackInSlot(rs).getItem(), player.inventory.getStackInSlot(rs).stackSize - 1, player.inventory.getStackInSlot(rs).getItemDamage()) : null);
				//			player.inventory.setInventorySlotContents(gs, player.inventory.getStackInSlot(gs).stackSize > 1 ? new ItemStack(player.inventory.getStackInSlot(gs).getItem(), player.inventory.getStackInSlot(gs).stackSize - 1, player.inventory.getStackInSlot(gs).getItemDamage()) : null);
				//			player.inventory.setInventorySlotContents(bs, player.inventory.getStackInSlot(bs).stackSize > 1 ? new ItemStack(player.inventory.getStackInSlot(bs).getItem(), player.inventory.getStackInSlot(bs).stackSize - 1, player.inventory.getStackInSlot(bs).getItemDamage()) : null);

				//			player.inventory.setInventorySlotContents(rs, new ItemStack(player.inventory.getStackInSlot(rs).getItem(), player.inventory.getStackInSlot(rs).stackSize - 1, player.inventory.getStackInSlot(rs).getItemDamage()));
				//			player.inventory.setInventorySlotContents(gs, new ItemStack(player.inventory.getStackInSlot(gs).getItem(), player.inventory.getStackInSlot(gs).stackSize - 1, player.inventory.getStackInSlot(gs).getItemDamage()));
				//			player.inventory.setInventorySlotContents(bs, new ItemStack(player.inventory.getStackInSlot(bs).getItem(), player.inventory.getStackInSlot(bs).stackSize - 1, player.inventory.getStackInSlot(bs).getItemDamage()));

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
				//				player.inventory.markDirty();
				((EntityPlayerMP) player).sendContainerToPlayer(player.openContainer);
			}

			return red && green && blue && water;
		}
		return false;
	}

	private static void notifiyNoDyes(EntityPlayer player) {
		player.addChatMessage(new ChatComponentText(StatCollector.translateToLocal("message.nodyes")).setChatStyle(new ChatStyle().setColor(EnumChatFormatting.RED)));
	}

	/*
	 * Name
	 */

	@Override
	public String getItemStackDisplayName(ItemStack itemstack) {
		return StatCollector.translateToLocal("brushmaterial." + material.name) + " " + super.getItemStackDisplayName(itemstack);
	}

	/*
	 * IIcons
	 */

	@Override
	public boolean isFull3D() {
		return true;
	}

	private IIcon handle;
	private IIcon top;

	@Override
	public void registerIcons(IIconRegister reg) {
		top = reg.registerIcon(ColourfulBlocksBase.MODID + ":brushtop");
		handle = reg.registerIcon(ColourfulBlocksBase.MODID + ":brushhandle");
	}

	@Override
	public IIcon getIcon(ItemStack stack, int pass) {
		return pass == 0 ? handle : top;
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
			return getCurrentRGBA(itemstack).getHex();
		}
		return 16777215;
	}

}
