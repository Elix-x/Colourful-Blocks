package code.elix_x.coremods.colourfulblocks.color;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;

import code.elix_x.coremods.colourfulblocks.ColourfulBlocksBase;
import code.elix_x.coremods.colourfulblocks.net.ColourfulBlocksSyncMessage;
import code.elix_x.excore.utils.color.RGBA;
import code.elix_x.excore.utils.pos.DimBlockPos;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.world.WorldEvent.Load;
import net.minecraftforge.event.world.WorldEvent.Save;

public class ColourfulBlocksManager {

	//	private static Map<Integer, Map<Integer, Map<Integer, Map<Integer, RGBO>>>> map = new HashMap<Integer, Map<Integer, Map<Integer, Map<Integer,RGBO>>>>();
	//	private static Map<Integer, Integer, Integer, Integer, RGBO> map;
	//	private static Map<DimBlockPos, RGBO> map = new HashMap<DimBlockPos, RGBO>();

	public static final Logger logger = LogManager.getLogger("Colourfull Blocks Manager");

	private static Map<DimBlockPos, RGBA> map = new HashMap<DimBlockPos, RGBA>();

	private static boolean gl = false;;

	@SideOnly(Side.CLIENT)
	public static int getBlockColor(IBlockAccess world, int x, int y, int z) {
		synchronized (map) {
			RGBA rgba = map.get(new DimBlockPos(Minecraft.getMinecraft().thePlayer.worldObj.provider.dimensionId, x, y, z));
			try{
				if(Loader.isModLoaded("powerofbreathing") && (Boolean) Class.forName("code.elix_x.mods.powerofbreathing.events.NyanEvents").getMethod("isGoing").invoke(null)){
					rgba = (RGBA) Class.forName("code.elix_x.mods.powerofbreathing.events.NyanEvents").getMethod("colorBlock").invoke(null);
				}
			} catch(Exception e){

			}
			if(rgba != null){
				return rgba.getHex();
			}
			return 16777215;
			//		return 10994515;
		}
	}

	@SideOnly(Side.CLIENT)
	public static void recolorTileEntity(TileEntity tileentity) {
		synchronized (map) {
			RGBA rgba = map.get(new DimBlockPos(tileentity.getWorldObj().provider.dimensionId, tileentity.xCoord, tileentity.yCoord, tileentity.zCoord));
			try{
				if(Loader.isModLoaded("powerofbreathing") && (Boolean) Class.forName("code.elix_x.mods.powerofbreathing.events.NyanEvents").getMethod("isGoing").invoke(null)){
					rgba = (RGBA) Class.forName("code.elix_x.mods.powerofbreathing.events.NyanEvents").getMethod("colorBlock").invoke(null);
				}
			} catch(Exception e){

			}
			if(rgba != null){
				GL11.glColor3f(rgba.r / 255, rgba.g / 255, rgba.b / 255);
			}
		}
	}

	@SideOnly(Side.CLIENT)
	public static void recolorBlock(RenderBlocks renderblocks, int x, int y, int z){
		synchronized (map) {
			IBlockAccess blockAccess = renderblocks.blockAccess;
			if(blockAccess instanceof World){
				World world = (World) blockAccess;
				RGBA rgba = map.get(new DimBlockPos(world.provider.dimensionId, x, y, z));
				try{
					if(Loader.isModLoaded("powerofbreathing") && (Boolean) Class.forName("code.elix_x.mods.powerofbreathing.events.NyanEvents").getMethod("isGoing").invoke(null)){
						rgba = (RGBA) Class.forName("code.elix_x.mods.powerofbreathing.events.NyanEvents").getMethod("colorBlock").invoke(null);
					}
				} catch(Exception e){

				}
				if(rgba != null){
					GL11.glColor3f(rgba.r / 255, rgba.g / 255, rgba.b / 255);
				}
			}
		}
	}

	public static boolean hasRGBA(DimBlockPos pos) {
		synchronized (map) {
			return map.containsKey(pos);	
		}
	}

	public static RGBA getRGBA(DimBlockPos pos) {
		synchronized (map) {
			return map.get(pos);	
		}
	}

	public static void addRGBA(DimBlockPos pos, RGBA rgba) {
		synchronized (map) {
			map.put(pos, rgba);
			syncMapWith(null);
			if(FMLCommonHandler.instance().getSide() == Side.CLIENT){
				Minecraft.getMinecraft().renderGlobal.markBlockForRenderUpdate(pos.getX(), pos.getY(), pos.getZ());
			}
		}
	}

	public static void removeRGBA(DimBlockPos pos) {
		synchronized (map){
			map.remove(pos);
			syncMapWith(null);
		}
	}

	public static void readMapFromNBT(NBTTagCompound nbt){
		synchronized (map) {
			map.clear();
		}

		NBTTagList list = (NBTTagList) nbt.getTag("list");
		for(int i = 0; i < list.tagCount(); i++){
			NBTTagCompound tag = list.getCompoundTagAt(i);
			map.put(DimBlockPos.createFromNBT(tag), RGBA.createFromNBT(tag));
		}
	}

	public static NBTTagCompound writeMapToNBT(NBTTagCompound nbt){
		NBTTagList list = new NBTTagList();
		synchronized (map) {
			for(Entry<DimBlockPos, RGBA> entry : map.entrySet()){
				NBTTagCompound tag = new NBTTagCompound();
				tag = entry.getKey().writeToNBT(tag);
				tag = entry.getValue().writeToNBT(tag);
				list.appendTag(tag);
			}
		}

		nbt.setTag("list", list);
		return nbt;
	}

	public static void syncMapWith(EntityPlayer player){
		synchronized (map) {
			if(player != null){
				if(!player.worldObj.isRemote){
					ColourfulBlocksBase.net.sendTo(new ColourfulBlocksSyncMessage(writeMapToNBT(new NBTTagCompound())), (EntityPlayerMP) player);
				}
			} else {
				ColourfulBlocksBase.net.sendToAll(new ColourfulBlocksSyncMessage(writeMapToNBT(new NBTTagCompound())));
			}
		}
	}

	public static void onStarting(FMLServerStartingEvent event){
		/*	synchronized (map) {
			map.clear();

//			File file = new File(event.world.getSaveHandler().getWorldDirectory(), "coloredBlocks.dat");
			File file = new File(event.getServer().getFolderName(), "coloredBlocks.dat");
			try {
				file.createNewFile();
			} catch (IOException e) {
				logger.error("Caught exception while creating save file: ", e);
			}

			NBTTagCompound nbt = null;
			try {
				nbt = CompressedStreamTools.read(file);
			} catch (IOException e) {
				logger.error("Caught exception while reading file: ", e);
			}
			if(nbt != null){
				readMapFromNBT(nbt);
			}
		}*/
	}

	public static void onStopping(FMLServerStoppingEvent event){
		/*synchronized (map) {
//			File file = new File(event.world.getSaveHandler().getWorldDirectory(), "coloredBlocks.dat");
			File file = new File(MinecraftServer.getServer().getFolderName(), "coloredBlocks.dat");
			try {
				file.createNewFile();
			} catch (IOException e) {
				logger.error("Caught exception while creating save file: ", e);
			}

			try {
				CompressedStreamTools.safeWrite(writeMapToNBT(new NBTTagCompound()), file);
			} catch (IOException e) {
				logger.error("Caught exception while saving file: ", e);
			}
		}*/
	}

	public static void stopped(FMLServerStoppedEvent event){

	}

	public static class Events{

		public Events() {

		}

		@SubscribeEvent
		public void save(Save event){
			if(event.world.provider.dimensionId == 0){
				synchronized (map) {
					File file = new File(event.world.getSaveHandler().getWorldDirectory(), "coloredBlocks.dat");
					try {
						file.createNewFile();
					} catch (IOException e) {
						logger.error("Caught exception while creating save file: ", e);
					}

					try {
						CompressedStreamTools.safeWrite(writeMapToNBT(new NBTTagCompound()), file);
					} catch (IOException e) {
						logger.error("Caught exception while saving file: ", e);
					}
				}
			}
		}

		@SubscribeEvent
		public void load(Load event){
			if(event.world.provider.dimensionId == 0){
				synchronized (map) {
					map.clear();

					File file = new File(event.world.getSaveHandler().getWorldDirectory(), "coloredBlocks.dat");
					try {
						file.createNewFile();
					} catch (IOException e) {
						logger.error("Caught exception while creating save file: ", e);
					}

					NBTTagCompound nbt = null;
					try {
						nbt = CompressedStreamTools.read(file);
					} catch (IOException e) {
						logger.error("Caught exception while reading file: ", e);
					}
					if(nbt != null){
						readMapFromNBT(nbt);
					}
				}
			}
		}

		@SubscribeEvent
		public void join(EntityJoinWorldEvent event){
			if(!event.world.isRemote){
				if(event.entity instanceof EntityPlayer){
					syncMapWith((EntityPlayer) event.entity);
				}
			}
		}

	}

}
