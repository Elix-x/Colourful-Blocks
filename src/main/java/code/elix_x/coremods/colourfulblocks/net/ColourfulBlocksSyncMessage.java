package code.elix_x.coremods.colourfulblocks.net;

import code.elix_x.coremods.colourfulblocks.color.ColourfulBlocksManager;
import net.minecraft.nbt.NBTTagCompound;
import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class ColourfulBlocksSyncMessage implements IMessage{

	private NBTTagCompound nbt;
	
	public ColourfulBlocksSyncMessage() {
		nbt = new NBTTagCompound();
	}
	
	public ColourfulBlocksSyncMessage(NBTTagCompound tag){
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
	
	public static class ColourfulBlocksSyncMessageHandler implements IMessageHandler<ColourfulBlocksSyncMessage, IMessage>{

		@Override
		public IMessage onMessage(ColourfulBlocksSyncMessage message, MessageContext ctx) {
			ColourfulBlocksManager.readMapFromNBT(message.nbt);
			return null;
		}

	}

}
