package code.elix_x.coremods.colourfulblocks.color;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import code.elix_x.coremods.colourfulblocks.ColourfulBlocksBase;
import code.elix_x.coremods.colourfulblocks.net.ColorfulBlocksSyncMessage;
import code.elix_x.excore.utils.color.RGBA;
import code.elix_x.excore.utils.nbt.mbt.MBT;
import code.elix_x.excore.utils.pos.BlockPos;
import code.elix_x.excore.utils.pos.DimBlockPos;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
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

public class ColoredBlocksManager extends WorldSavedData {

	public static final Logger logger = LogManager.getLogger("Colored Blocks Manager");

	private static final MBT mbt = new MBT();

	public static final String NAME = "Colored Blocks";

	public static final int DEFAULTCOLOR = 16777215;

	private static Multimap<Integer, Pair<BlockPos, RGBA>> coloredBlocksQueue;

	public static ColoredBlocksManager get(World world){
		ColoredBlocksManager manager = (ColoredBlocksManager) world.perWorldStorage.loadData(ColoredBlocksManager.class, NAME);
		if(manager == null){
			manager = new ColoredBlocksManager(NAME);
			manager.dimId = world.provider.dimensionId;
			if(coloredBlocksQueue != null && !world.isRemote){
				for(Pair<BlockPos, RGBA> p : coloredBlocksQueue.removeAll(manager.dimId)){
					manager.coloredBlocks.put(p.getKey(), p.getValue());
				}
				manager.markDirty();
				manager.syncMapWith(null);
			}
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

	public static void enqueueOldColoredBlock(DimBlockPos pos, RGBA rgba){
		if(coloredBlocksQueue == null) coloredBlocksQueue = HashMultimap.create();
		coloredBlocksQueue.put(pos.dimId, new ImmutablePair(new BlockPos(pos.x, pos.y, pos.z), rgba));
	}

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

}
