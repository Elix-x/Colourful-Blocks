package code.elix_x.coremods.colorfulblocks.net;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

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
