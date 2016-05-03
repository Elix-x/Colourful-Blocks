package code.elix_x.coremods.colorfulblocks.color;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import code.elix_x.coremods.colorfulblocks.ColourfulBlocksBase;
import code.elix_x.coremods.colorfulblocks.net.ColorfulBlocksSyncMessage;
import code.elix_x.excore.utils.color.RGBA;
import code.elix_x.excore.utils.nbt.mbt.MBT;
import code.elix_x.excore.utils.pos.BlockPos;
import code.elix_x.excore.utils.pos.DimBlockPos;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ColoredBlocksManager extends WorldSavedData {

	public static final Logger logger = LogManager.getLogger("Colored Blocks Manager");

	private static final MBT mbt = new MBT();

	public static final String NAME = "Colored Blocks";

	private static Multimap<Integer, Pair<BlockPos, RGBA>> coloredBlocksQueue;

	public static ColoredBlocksManager get(World world){
		ColoredBlocksManager manager = (ColoredBlocksManager) world.getPerWorldStorage().loadData(ColoredBlocksManager.class, NAME);
		if(manager == null){
			manager = new ColoredBlocksManager(NAME);
			manager.dimId = world.provider.getDimension();
			if(coloredBlocksQueue != null && !world.isRemote){
				for(Pair<BlockPos, RGBA> p : coloredBlocksQueue.removeAll(manager.dimId)){
					manager.coloredBlocks.put(p.getKey(), p.getValue());
				}
				manager.markDirty();
				manager.syncWithAll();
			}
			world.getPerWorldStorage().setData(NAME, manager);
		}
		return manager;
	}

	@SideOnly(Side.CLIENT)
	public static int getBlockColor(IBlockAccess world, IBlockState state, BlockPos pos, int original){
		if(world != null && pos != null){
			RGBA rgba = get(world instanceof World ? (World) world : Minecraft.getMinecraft().theWorld).getRGBA(pos);
			if(rgba == null){
				return original;
			} else if(original == -1){
				return rgba.argb();
			} else {
				if(ColourfulBlocksBase.multipyOriginalColor){
					return rgba.multiply(new RGBA(original)).argb();
				} else {
					return rgba.argb();
				}
			}
		} else {
			return original;
		}
	}

	@SideOnly(Side.CLIENT)
	public static void recolorTileEntity(TileEntity tileentity){
		if(tileentity.getWorld() != null) get(tileentity.getWorld()).recolorBlock(tileentity.getPos().getX(), tileentity.getPos().getY(), tileentity.getPos().getZ());
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
	public void recolorBlock(int x, int y, int z){
		RGBA rgba = coloredBlocks.get(new BlockPos(x, y, z));
		if(rgba != null){
			GL11.glColor3f(rgba.r / 255, rgba.g / 255, rgba.b / 255);
		}
	}

	public boolean hasRGBA(BlockPos pos){
		return coloredBlocks.containsKey(pos);	
	}

	public RGBA getRGBA(BlockPos pos){
		return coloredBlocks.get(pos);	
	}

	public void addRGBA(BlockPos pos, RGBA rgba){
		coloredBlocks.put(pos, rgba);
		syncWithAll();
		markDirty();
		if(FMLCommonHandler.instance().getSide() == Side.CLIENT){
			Minecraft.getMinecraft().renderGlobal.markBlockRangeForRenderUpdate(pos.getX(), pos.getY(), pos.getZ(), pos.getX(), pos.getY(), pos.getZ());
		}
	}

	public void removeRGBA(BlockPos pos){
		coloredBlocks.remove(pos);
		syncWithAll();
		markDirty();
		if(FMLCommonHandler.instance().getSide() == Side.CLIENT){
			Minecraft.getMinecraft().renderGlobal.markBlockRangeForRenderUpdate(pos.getX(), pos.getY(), pos.getZ(), pos.getX(), pos.getY(), pos.getZ());
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		dimId = nbt.getInteger("dimId");
		coloredBlocks = mbt.fromNBT(nbt.getTag(NAME), Map.class, BlockPos.class, RGBA.class);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt){
		nbt.setInteger("dimId", dimId);
		nbt.setTag(NAME, mbt.toNBT(coloredBlocks));
	}

	public void syncWith(EntityPlayerMP player){
		NBTTagCompound nbt = new NBTTagCompound();
		writeToNBT(nbt);
		ColourfulBlocksBase.net.sendTo(new ColorfulBlocksSyncMessage(dimId, nbt), player);
	}

	public void syncWithAll(){
		NBTTagCompound nbt = new NBTTagCompound();
		writeToNBT(nbt);
		ColourfulBlocksBase.net.sendToAll(new ColorfulBlocksSyncMessage(dimId, nbt));
	}

}
