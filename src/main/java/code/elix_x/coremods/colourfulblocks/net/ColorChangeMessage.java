package code.elix_x.coremods.colourfulblocks.net;

import io.netty.buffer.ByteBuf;

import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import code.elix_x.coremods.colourfulblocks.color.tool.ColoringToolsManager;
import code.elix_x.coremods.colourfulblocks.items.ItemBrush;
import code.elix_x.excore.utils.color.RGBA;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class ColorChangeMessage implements IMessage{

	private RGBA rgba;
	
	public ColorChangeMessage() {
		
	}
	
	public ColorChangeMessage(RGBA r) {
		rgba = r;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		NBTTagCompound nbt = ByteBufUtils.readTag(buf);
		rgba = RGBA.createFromNBT(nbt);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt = rgba.writeToNBT(nbt);
		ByteBufUtils.writeTag(buf, nbt);
	}

	public static class ColorChangeMessageHandler implements IMessageHandler<ColorChangeMessage, IMessage>{

		@Override
		public IMessage onMessage(ColorChangeMessage message, MessageContext ctx) {
			ColoringToolsManager.updateColor(ctx.getServerHandler().playerEntity, message.rgba);
			return null;
		}
		
	}
}
