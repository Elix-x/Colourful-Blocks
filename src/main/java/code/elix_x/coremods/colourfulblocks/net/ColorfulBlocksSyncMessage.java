package code.elix_x.coremods.colourfulblocks.net;

import code.elix_x.coremods.colourfulblocks.color.ColourfulBlocksManager;
import net.minecraft.nbt.NBTTagCompound;
import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class ColorfulBlocksSyncMessage implements IMessage{

	private NBTTagCompound nbt;
	
	public ColorfulBlocksSyncMessage() {
		nbt = new NBTTagCompound();
	}
	
	public ColorfulBlocksSyncMessage(NBTTagCompound tag){
		nbt = tag;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		nbt = ByteBufUtils.readTag(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeTag(buf, nbt);
	}
	
	public static class ColorfulBlocksSyncMessageHandler implements IMessageHandler<ColorfulBlocksSyncMessage, IMessage>{

		@Override
		public IMessage onMessage(ColorfulBlocksSyncMessage message, MessageContext ctx) {
			ColourfulBlocksManager.readMapFromNBT(message.nbt);
			return null;
		}

	}

}
