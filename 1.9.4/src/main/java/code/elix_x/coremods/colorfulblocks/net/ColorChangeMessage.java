package code.elix_x.coremods.colorfulblocks.net;

import code.elix_x.excore.utils.color.RGBA;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class ColorChangeMessage implements IMessage {

	public RGBA rgba;

	public ColorChangeMessage(){

	}

	public ColorChangeMessage(RGBA r){
		rgba = r;
	}

	@Override
	public void fromBytes(ByteBuf buf){
		rgba = RGBA.createFromNBT(ByteBufUtils.readTag(buf));
	}

	@Override
	public void toBytes(ByteBuf buf){
		ByteBufUtils.writeTag(buf, rgba.writeToNBT(new NBTTagCompound()));
	}

}
