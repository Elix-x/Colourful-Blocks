package code.elix_x.coremods.colourfulblocks.net;

import code.elix_x.coremods.colourfulblocks.color.tool.ColoringToolsManager;
import code.elix_x.excore.utils.color.RGBA;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;

public class ColorChangeMessage implements IMessage {

	private RGBA rgba;

	public ColorChangeMessage(){

	}

	public ColorChangeMessage(RGBA r){
		rgba = r;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		rgba = RGBA.createFromNBT(ByteBufUtils.readTag(buf));
	}

	@Override
	public void toBytes(ByteBuf buf){
		NBTTagCompound nbt = new NBTTagCompound();
		ByteBufUtils.writeTag(buf, rgba.writeToNBT(new NBTTagCompound()));
	}

	public static class ColorChangeMessageHandler implements IMessageHandler<ColorChangeMessage, IMessage> { 

		@Override
		public IMessage onMessage(ColorChangeMessage message, MessageContext ctx){
			ColoringToolsManager.updateColor(ctx.getServerHandler().playerEntity, message.rgba);
			return null;
		}

	}
}
