package code.elix_x.coremods.colourfulblocks.net;

import io.netty.buffer.ByteBuf;

import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import code.elix_x.coremods.colourfulblocks.items.ItemBrush;
import code.elix_x.excore.utils.color.RGBA;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class ColourChangeMessage implements IMessage{

	private RGBA rgba;
	private EntityPlayer player;
	private int dimId;
	
	public ColourChangeMessage() {
		
	}
	
	public ColourChangeMessage(RGBA r, EntityPlayer p, int d) {
		rgba = r;
		player = p;
		dimId = d;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		NBTTagCompound nbt = ByteBufUtils.readTag(buf);
		rgba = RGBA.createFromNBT(nbt);
		dimId = nbt.getInteger("dimId");
		player = MinecraftServer.getServer().worldServerForDimension(dimId).func_152378_a(UUID.fromString(nbt.getString("player")));
	}

	@Override
	public void toBytes(ByteBuf buf) {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt = rgba.writeToNBT(nbt);
		nbt.setInteger("dimId", dimId);
		nbt.setString("player", player.func_146094_a(player.getGameProfile()).toString());
		ByteBufUtils.writeTag(buf, nbt);
	}

	public static class ColourChangeMessageHandler implements IMessageHandler<ColourChangeMessage, IMessage>{

		@Override
		public IMessage onMessage(ColourChangeMessage message, MessageContext ctx) {
			ItemBrush.updateColour(message.player, message.rgba);
			return null;
		}
		
	}
}
