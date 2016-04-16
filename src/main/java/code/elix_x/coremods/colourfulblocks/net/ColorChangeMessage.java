package code.elix_x.coremods.colourfulblocks.net;

import code.elix_x.excore.utils.color.RGBA;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;

public class ColorChangeMessage implements IMessage {

	public RGBA rgba;

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
		ByteBufUtils.writeTag(buf, rgba.writeToNBT(new NBTTagCompound()));
	}

}
