package code.elix_x.coremods.colorfulblocks.events;

import java.io.File;
import java.io.IOException;

import code.elix_x.coremods.colorfulblocks.color.ColoredBlocksManager;
import code.elix_x.excore.utils.color.RGBA;
import code.elix_x.excore.utils.pos.DimBlockPos;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.world.WorldEvent.Load;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class SyncColoredBlocksEvent {

	@SubscribeEvent
	public void load(Load event){
		if(event.getWorld().provider.getDimension() == 0){
			File file = new File(event.getWorld().getSaveHandler().getWorldDirectory(), "coloredBlocks.dat");
			if(file.exists()){
				NBTTagCompound nbt = null;
				try {
					nbt = CompressedStreamTools.read(file);
					if(nbt != null){
						NBTTagList list = (NBTTagList) nbt.getTag("list");
						for(int i = 0; i < list.tagCount(); i++){
							NBTTagCompound tag = list.getCompoundTagAt(i);
							ColoredBlocksManager.enqueueOldColoredBlock(DimBlockPos.createFromNBT(tag), RGBA.createFromNBT(tag));
						}
					}
				} catch (IOException e){
					ColoredBlocksManager.logger.error("Caught exception while reading file: ", e);
				} finally {
					file.delete();
				}
			}
		}
	}

	@SubscribeEvent
	public void join(EntityJoinWorldEvent event){
		if(!event.getWorld().isRemote){
			if(event.getEntity() instanceof EntityPlayerMP){
				ColoredBlocksManager.get(event.getWorld()).syncWith((EntityPlayerMP) event.getEntity());
			}
		}
	}

}