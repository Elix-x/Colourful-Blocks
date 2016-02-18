package code.elix_x.coremods.colourfulblocks.net;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;

public class ColorfulBlocksSyncMessage implements IMessage {

	public int dimId;
	public NBTTagCompound nbt;

	public ColorfulBlocksSyncMessage() {
		nbt = new NBTTagCompound();
	}

	public ColorfulBlocksSyncMessage(int dimId, NBTTagCompound nbt) {
		this.dimId = dimId;
		this.nbt = nbt;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		dimId = buf.readInt();
		nbt = ByteBufUtils.readTag(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(dimId);
		ByteBufUtils.writeTag(buf, nbt);
	}

}
