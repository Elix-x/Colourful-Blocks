package code.elix_x.coremods.colourfulblocks.color;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;

import code.elix_x.coremods.colourfulblocks.ColourfulBlocksBase;
import code.elix_x.coremods.colourfulblocks.net.ColorfulBlocksSyncMessage;
import code.elix_x.excore.utils.color.RGBA;
import code.elix_x.excore.utils.nbt.mbt.MBT;
import code.elix_x.excore.utils.pos.BlockPos;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.world.WorldEvent.Load;

public class ColoredBlocksManager extends WorldSavedData {

	public static final String NAME = "Colored Blocks";

	public static final int DEFAULTCOLOR = 16777215;

	public static final Logger logger = LogManager.getLogger("Colored Blocks Manager");

	public static ColoredBlocksManager get(World world){
		ColoredBlocksManager manager = (ColoredBlocksManager) world.perWorldStorage.loadData(ColoredBlocksManager.class, NAME);
		if(manager == null){
			manager = new ColoredBlocksManager(NAME);
			manager.dimId = world.provider.dimensionId;
			world.perWorldStorage.setData(NAME, manager);
		}
		return manager;
	}

	@SideOnly(Side.CLIENT)
	public static int getBlockColor(IBlockAccess world, int x, int y, int z) {
		if(world instanceof World) return get((World) world).getBlockColor(x, y, z);
		return DEFAULTCOLOR;
	}

	@SideOnly(Side.CLIENT)
	public static void recolorTileEntity(TileEntity tileentity) {
		get(tileentity.getWorldObj()).recolorBlock(tileentity.xCoord, tileentity.yCoord, tileentity.zCoord);
	}

	@SideOnly(Side.CLIENT)
	public static void recolorBlock(RenderBlocks renderblocks, int x, int y, int z) {
		if(renderblocks.blockAccess instanceof World) get((World) renderblocks.blockAccess).recolorBlock(x, y, z);
	}

	private final MBT mbt = new MBT();

	private int dimId;

	private Map<BlockPos, RGBA> coloredBlocks = new HashMap<BlockPos, RGBA>();

	public ColoredBlocksManager(String name){
		super(name);
	}

	@SideOnly(Side.CLIENT)
	public int getBlockColor(int x, int y, int z) {
		RGBA rgba = getRGBA(new BlockPos(x, y, z));
		try{
			if(Loader.isModLoaded("powerofbreathing") && (Boolean) Class.forName("code.elix_x.mods.powerofbreathing.events.NyanEvents").getMethod("isGoing").invoke(null)){
				rgba = (RGBA) Class.forName("code.elix_x.mods.powerofbreathing.events.NyanEvents").getMethod("colorBlock").invoke(null);
			}
		} catch(Exception e){

		}
		if(rgba != null){
			return rgba.argb();
		}
		return DEFAULTCOLOR;
	}

	@SideOnly(Side.CLIENT)
	public void recolorBlock(int x, int y, int z){
		RGBA rgba = coloredBlocks.get(new BlockPos(x, y, z));
		if(rgba != null){
			GL11.glColor3f(rgba.r / 255, rgba.g / 255, rgba.b / 255);
		}
	}

	public boolean hasRGBA(BlockPos pos) {
		return coloredBlocks.containsKey(pos);	
	}

	public RGBA getRGBA(BlockPos pos) {
		return coloredBlocks.get(pos);	
	}

	public void addRGBA(BlockPos pos, RGBA rgba) {
		coloredBlocks.put(pos, rgba);
		syncMapWith(null);
		markDirty();
		if(FMLCommonHandler.instance().getSide() == Side.CLIENT){
			Minecraft.getMinecraft().renderGlobal.markBlockForRenderUpdate(pos.getX(), pos.getY(), pos.getZ());
		}
	}

	public void removeRGBA(BlockPos pos) {
		coloredBlocks.remove(pos);
		syncMapWith(null);
		markDirty();
		if(FMLCommonHandler.instance().getSide() == Side.CLIENT){
			Minecraft.getMinecraft().renderGlobal.markBlockForRenderUpdate(pos.getX(), pos.getY(), pos.getZ());
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		dimId = nbt.getInteger("dimId");
		coloredBlocks = mbt.fromNBT(nbt.getTag(NAME), Map.class, BlockPos.class, RGBA.class);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		nbt.setInteger("dimId", dimId);
		nbt.setTag(NAME, mbt.toNBT(coloredBlocks));
	}

	public void syncMapWith(EntityPlayerMP player){
		NBTTagCompound nbt = new NBTTagCompound();
		writeToNBT(nbt);
		if(player != null){
			if(!player.worldObj.isRemote) ColourfulBlocksBase.net.sendTo(new ColorfulBlocksSyncMessage(dimId, nbt), player);
		} else {
			ColourfulBlocksBase.net.sendToAll(new ColorfulBlocksSyncMessage(dimId, nbt));
		}
	}

	public static class Events{

		public Events() {

		}

		@SubscribeEvent
		public void load(Load event){
			if(event.world.provider.dimensionId == 0){
				File file = new File(event.world.getSaveHandler().getWorldDirectory(), "coloredBlocks.dat");
				if(file.exists()){
					/*NBTTagCompound nbt = null;
					try {
						nbt = CompressedStreamTools.read(file);
					} catch (IOException e) {
						logger.error("Caught exception while reading file: ", e);
					}
					if(nbt != null){
						NBTTagList list = (NBTTagList) nbt.getTag("list");
						for(int i = 0; i < list.tagCount(); i++){
							NBTTagCompound tag = list.getCompoundTagAt(i);
							get(MinecraftServer.getServer().worldServerForDimension(DimBlockPos.createFromNBT(tag).dimId)).coloredBlocks.put(BlockPos.createFromNBT(tag), RGBA.createFromNBT(tag));
						}
					}
					file.delete();*/
				}
			}
		}

		@SubscribeEvent
		public void join(EntityJoinWorldEvent event){
			if(!event.world.isRemote){
				if(event.entity instanceof EntityPlayerMP){
					get(event.world).syncMapWith((EntityPlayerMP) event.entity);
				}
			}
		}

	}

}
